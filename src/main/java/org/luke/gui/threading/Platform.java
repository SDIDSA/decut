package org.luke.gui.threading;

import org.luke.gui.exception.ErrorHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Platform {
    private static int threadIndex = 0;
    private static final Function<String, ThreadFactory> threadMaker =
            (name) ->
                    (runnable) -> new Thread(runnable, name + "_" + threadIndex++);

    private static final Function<String, ExecutorService> poolMaker =
            (name) -> Executors.newCachedThreadPool(threadMaker.apply(name));

    private static final ExecutorService back = poolMaker.apply("back_thread");
    private static final ExecutorService wait = poolMaker.apply("wait_thread");

    public static void runLater(Runnable r) {
        javafx.application.Platform.runLater(safeRunnable(r));
    }

    public static void runAfter(Runnable r, long after) {
        wait.execute(() -> {
            sleep(after);
            runLater(r);
        });
    }

    public static void runAfterBack(Runnable action, Runnable post, long after) {
        wait.execute(() -> {
            sleep(after);
            safeRunnable(action).run();
            if (post != null) safeRunnable(post).run();
        });
    }

    public static <T> void runAfterBack(Supplier<T> action, Consumer<T> post, long after) {
        wait.execute(() -> {
            sleep(after);
            T result = safeSupplier(action).get();
            if (post != null) {
                runLater(() -> post.accept(result));
            }
        });
    }

    public static void runPeriodicWhile(Runnable action, long interval,
                                        Supplier<Boolean> condition) {
        wait.execute(() -> {
            while (condition.get()) {
                runLater(action);
                sleep(interval);
            }
        });
    }

    public static void runPeriodicBackWhile(Runnable action, long interval,
                                        Supplier<Boolean> condition, Runnable post) {
        wait.execute(() -> {
            while (condition.get()) {
                safeRunnable(action).run();
                sleep(interval);
            }
            if(post != null) safeRunnable(post).run();
        });
    }

    public static void runPeriodicUntil(Runnable action, long interval, Supplier<Boolean> condition) {
        wait.execute(() -> {
            while (!condition.get()) {
                runLater(action);
                sleep(interval);
            }
        });
    }

    public static void waitWhile(Supplier<Boolean> condition) {
        waitWhile(condition, -1);
    }

    public static void waitWhileNot(Supplier<Boolean> condition) {
        waitWhileNot(condition, -1);
    }

    public static boolean waitWhile(Supplier<Boolean> condition, long timeout) {
        long start = System.currentTimeMillis();
        while (condition.get() && (timeout < 0 || System.currentTimeMillis() - start < timeout)) {
            sleep(10);
        }
        return !condition.get();
    }

    public static boolean waitWhileNot(Supplier<Boolean> condition, long timeout) {
        long start = System.currentTimeMillis();
        while (!condition.get() && (timeout < 0 || System.currentTimeMillis() - start < timeout)) {
            sleep(10);
        }
        return condition.get();
    }

    public static void waitWhile(Supplier<Boolean> condition, Runnable post) {
        waitWhile(condition, post, -1);
    }

    public static void waitWhileNot(Supplier<Boolean> condition, Runnable post) {
        waitWhileNot(condition, post, -1);
    }

    public static void waitWhile(Supplier<Boolean> condition, Runnable post, long timeout) {
        wait.execute(() -> {
            if (waitWhile(condition, timeout))
                runLater(post);
        });
    }

    public static void waitWhileNot(Supplier<Boolean> condition, Runnable post, long timeout) {
        wait.execute(() -> {
            if (waitWhileNot(condition, timeout))
                runLater(post);
        });
    }

    public static void sleep(long duration) {
        try {
            long dur = Math.max(duration, 0);
            Thread.sleep(dur);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        }
    }

    public static void runBack(Runnable action, Runnable post) {
        back.execute(() -> {
            safeRunnable(action).run();
            if (post != null) safeRunnable(post).run();
        });
    }

    public static <T> void runBack(Supplier<T> action, Consumer<T> post) {
        back.execute(() -> {
            T result = safeSupplier(action).get();
            if (post != null) {
                runLater(() -> post.accept(result));
            }
        });
    }

    public static <U, V> void runBack(List<U> input, Function<U, V> transform, Consumer<List<V>> post) {
        List<CompletableFuture<V>> futures = input.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> safeFunction(transform).apply(item), back))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .thenAccept(results -> {
                    if (post != null) runLater(() -> post.accept(results));
                });
    }

    public static void runBack(Runnable action) {
        runBack(action, null);
    }

    private static Runnable safeRunnable(Runnable action) {
        return () -> {
            try {
                action.run();
            } catch (Exception x) {
                ErrorHandler.handle(x, "(do something) in thread " + Thread.currentThread().getName());
            }
        };
    }

    public static <T> Supplier<T> safeSupplier(Supplier<T> action) {
        return () -> {
            try {
                return action.get();
            } catch (Exception x) {
                ErrorHandler.handle(x, "(do something) in thread " + Thread.currentThread().getName());
                return null;
            }
        };
    }

    public static <U, V> Function<U, V> safeFunction(Function<U, V> action) {
        return (in) -> {
            try {
                return action.apply(in);
            } catch (Exception x) {
                ErrorHandler.handle(x, "(do something) in thread " + Thread.currentThread().getName());
                return null;
            }
        };
    }
}
