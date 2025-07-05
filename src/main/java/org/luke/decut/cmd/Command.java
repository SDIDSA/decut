package org.luke.decut.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.luke.decut.crossplatform.Os;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.threading.Platform;

public class Command {
    private final String[] command;

    private OutputStreamWriter input;

    private final ArrayList<Consumer<String>> inputHandlers;
    private final ArrayList<Consumer<String>> errorHandlers;

    private final ArrayList<Consumer<Integer>> onExit;

    private boolean urlDecode = false;

    public Command(Consumer<String> inputHandler, Consumer<String> errorHandler, String... command) {
        this.command = Os.fromSystem().addCommandPrefix(command);
        this.inputHandlers = new ArrayList<>();
        this.errorHandlers = new ArrayList<>();
        this.onExit = new ArrayList<>();

        if (inputHandler != null)
            inputHandlers.add(inputHandler);
        if (errorHandler != null)
            errorHandlers.add(errorHandler);
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlDecode = urlDecode;
    }

    public Command(Consumer<String> inputHandler, String... command) {
        this(inputHandler, null, command);
    }

    public Command(String... command) {
        this(null, null, command);
    }

    public Command addInputHandler(Consumer<String> inputHandler) {
        inputHandlers.add(inputHandler);
        return this;
    }

    public Command addErrorHandler(Consumer<String> errorHandler) {
        errorHandlers.add(errorHandler);
        return this;
    }

    public void write(String b) {
        try {
            input.append(b);
            input.append(System.lineSeparator());
            input.flush();
        } catch (IOException e) {
            ErrorHandler.handle(e, "write to process input");
        }
    }

    public Command addOnExit(Consumer<Integer> r) {
        onExit.add(r);
        return this;
    }

    public Process execute(File root) {
        if(urlDecode) {
            for (int i = 0; i < command.length; i++) {
                command[i] = URLDecoder.decode(command[i], Charset.defaultCharset());
            }
        }
        try {
            Process process = new ProcessBuilder(command).directory(root).start();

            input = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);

            registerHandler(process.getInputStream(), inputHandlers);
            registerHandler(process.getErrorStream(), errorHandlers);

            Platform.runBack(() -> {
                try {
                    int exitCode = process.waitFor();
                    onExit.forEach(oe -> oe.accept(exitCode));
                } catch (InterruptedException e) {
                    ErrorHandler.handle(e, "wait for command to finish");
                    Thread.currentThread().interrupt();
                }
            });

            return process;
        } catch (IOException e) {
            ErrorHandler.handle(e, "executing process");
            return null;
        }
    }

    public void executeAndJoin(File root) {
        Process process = execute(root);
        ArrayList<Consumer<Integer>> onExits = new ArrayList<>(onExit);
        onExit.clear();
        try {
            int exitCode = process.waitFor();
            onExits.forEach(oe -> oe.accept(exitCode));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerHandler(InputStream stream, List<Consumer<String>> handlers) {
        Platform.runBack(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String fline = line.trim();
                    handlers.forEach(handler -> {
                        if (handler != null)
                            handler.accept(fline);
                    });
                }
            } catch (IOException e) {
                ErrorHandler.handle(e, "handling output");
            }
        });
    }
}