package org.luke.decut.app.timeline.controls.snap;

import org.luke.decut.app.home.Home;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class SnapPointsFactory implements Function<Home, List<Double>> {
    public static final SnapPointsFactory SECOND = new SnapPointsFactory() {
        @Override
        public List<Double> apply(Home owner) {
            return IntStream.iterate(
                    0,
                    i -> i < owner.durationProperty().get(),
                    i -> i + 1
            ).mapToDouble(
                    i -> i
            ).boxed().toList();
        }
    };

    public static final SnapPointsFactory CLIPS = new SnapPointsFactory() {
        @Override
        public List<Double> apply(Home owner) {
            return IntStream.iterate(
                    0,
                    i -> i < owner.durationProperty().get(),
                    i -> i + 1
            ).mapToDouble(
                    i -> i
            ).boxed().toList();
        }
    };

    public static final SnapPointsFactory FIFTH = new SnapPointsFactory() {
        @Override
        public List<Double> apply(Home owner) {
            return IntStream.iterate(
                    0,
                    i -> i < owner.durationProperty().get(),
                    i -> i + 1
            ).mapToDouble(
                    i -> i
            ).boxed().toList();
        }
    };

    public static final SnapPointsFactory FRAME = new SnapPointsFactory() {
        @Override
        public List<Double> apply(Home owner) {
            return IntStream.iterate(
                    0,
                    i -> i < owner.durationProperty().get(),
                    i -> i + 1
            ).mapToDouble(
                    i -> i
            ).boxed().toList();
        }
    };
}
