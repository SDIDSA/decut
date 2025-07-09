package org.luke.decut.app.home;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.menubar.edit.EditMenu;
import org.luke.decut.app.home.menubar.FileMenu;
import org.luke.decut.app.inspector.Inspector;
import org.luke.decut.app.lib.*;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.preview.Preview;
import org.luke.decut.app.timeline.controls.snap.SnapStrategy;
import org.luke.decut.app.timeline.TimelinePane;
import org.luke.decut.app.timeline.tracks.Tracks;
import org.luke.decut.app.timeline.viewport.Viewport;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.file.DecutProject;
import org.luke.decut.render.TimelineRenderer;
import org.luke.gui.controls.button.MenuBarButton;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;

import java.awt.*;
import java.io.File;

public class Home extends Page {

    private final MenuBarButton file;
    private final EditMenu edit;

    private final VBox root;
    private final HBox top;

    private final Library library;
    private final TimelinePane timeline;
    private final Preview preview;
    private final Inspector inspector;

    private final TimelineRenderer renderer;

    private final DoubleProperty duration;
    private final DoubleProperty frameRate;
    private final DoubleProperty canvasWidth;
    private final DoubleProperty canvasHeight;
    private SnapStrategy snapStrategy = SnapStrategy.SECOND;

    private double initVSplit;
    private final DoubleProperty vSplit;
    private double initHSplit1;
    private final DoubleProperty hSplit1;
    private double initHSplit2;
    private DoubleProperty hSplit2;

    public Home(Window window) {
        super(window, new Dimension(500 * 2 + 15 * 4 + 30, 600));

        file = new FileMenu(this);
        edit = new EditMenu(this);

        vSplit = new SimpleDoubleProperty(0.5);
        hSplit1 = new SimpleDoubleProperty(0.3);
        hSplit2 = new SimpleDoubleProperty(0.7);
        root = new VBox();

        duration = new SimpleDoubleProperty(300);
        frameRate = new SimpleDoubleProperty(30);
        canvasWidth = new SimpleDoubleProperty(1280);
        canvasHeight = new SimpleDoubleProperty(720);

        library = new Library(this);
        LibraryTab assets = new LibraryTab(this,"Assets", "assets", Assets.class);
        library.addTab(assets);
        library.addTab(new LibraryTab(this, "Text", "text", Text.class));
        library.addTab(new LibraryTab(this, "Effects", "effects", Effects.class));

        Platform.runLater(assets::select);

        top = new HBox();
        timeline = new TimelinePane(this);
        preview = new Preview(this);
        inspector = new Inspector(this);

        renderer = new TimelineRenderer(this);

        double thickness = 7;

        root.setPadding(new Insets(thickness));

        top.setMinHeight(USE_PREF_SIZE);
        top.setMaxHeight(USE_PREF_SIZE);
        top.prefHeightProperty().bind(heightProperty().subtract(thickness * 3).multiply(vSplit));

        timeline.setMinHeight(USE_PREF_SIZE);
        timeline.setMaxHeight(USE_PREF_SIZE);
        timeline.setMinWidth(USE_PREF_SIZE);
        timeline.setMaxWidth(USE_PREF_SIZE);
        timeline.prefHeightProperty().bind(heightProperty().subtract(thickness * 3).multiply(vSplit.negate().add(1)));
        timeline.prefWidthProperty().bind(widthProperty().subtract(thickness * 2));

        library.setMinWidth(USE_PREF_SIZE);
        library.setMaxWidth(USE_PREF_SIZE);
        library.prefWidthProperty().bind(widthProperty().subtract(thickness * 4).multiply(hSplit1));
        library.setMinHeight(USE_PREF_SIZE);
        library.setMaxHeight(USE_PREF_SIZE);
        library.prefHeightProperty().bind(top.heightProperty());

        preview.setMinWidth(USE_PREF_SIZE);
        preview.setMaxWidth(USE_PREF_SIZE);
        preview.prefWidthProperty().bind(widthProperty().subtract(thickness * 4).multiply(hSplit2.subtract(hSplit1)));
        preview.setMinHeight(USE_PREF_SIZE);
        preview.setMaxHeight(USE_PREF_SIZE);
        preview.prefHeightProperty().bind(top.heightProperty());

        inspector.setMinWidth(USE_PREF_SIZE);
        inspector.setMaxWidth(USE_PREF_SIZE);
        inspector.prefWidthProperty().bind(widthProperty().subtract(thickness * 4).multiply(hSplit2.negate().add(1)));
        inspector.setMinHeight(USE_PREF_SIZE);
        inspector.setMaxHeight(USE_PREF_SIZE);
        inspector.prefHeightProperty().bind(top.heightProperty());

        Resizer hResizer1 = new Resizer(this, Orientation.VERTICAL, thickness);
        hResizer1.setOnInit(() -> initHSplit1 = hSplit1.get());
        hResizer1.setOnDrag((byPx) -> {
            double newVal = initHSplit1 + byPx / getWidth();
            hSplit1.set(Math.min(0.4, Math.max(0.1, newVal)));
        });

        Resizer hResizer2 = new Resizer(this, Orientation.VERTICAL, thickness);
        hResizer2.setOnInit(() -> initHSplit2 = hSplit2.get());
        hResizer2.setOnDrag((byPx) -> {
            double newVal = initHSplit2 + byPx / getWidth();
            hSplit2.set(Math.min(0.9, Math.max(0.6, newVal)));
        });
        top.getChildren().addAll(library, hResizer1, preview, hResizer2, inspector);

        Resizer vResizer = new Resizer(this, Orientation.HORIZONTAL, thickness);
        vResizer.setOnInit(() -> initVSplit = vSplit.get());
        vResizer.setOnDrag((byPx) -> {
            double newVal = initVSplit + byPx / getHeight();
            vSplit.set(Math.min(0.9, Math.max(0.1, newVal)));
        });

        root.getChildren().addAll(top, vResizer, timeline);

        getChildren().add(root);

        applyStyle(window.getStyl());
    }

    public void perform(String name, Runnable action, Runnable inverse, boolean ffmpeg) {
        edit.perform(name, action, inverse, ffmpeg);
    }

    public void perform(String name, Runnable action, Runnable inverse) {
        perform(name, action, inverse, false);
    }

    public FfmpegCommand render(File file) {
        return renderer.generateRenderCommand(file);
    }

    public SnapStrategy getSnapStrategy() {
        return snapStrategy;
    }

    public void setSnapStrategy(SnapStrategy snapStrategy) {
        this.snapStrategy = snapStrategy;
    }

    public DoubleProperty framerateProperty() {
        return frameRate;
    }

    public DoubleProperty durationProperty() {
        return duration;
    }

    public DoubleProperty timeScaleProperty() {
        return timeline.timeScale();
    }

    public DoubleProperty timeScaleSource() {
        return timeline.timeScaleSource();
    }

    public DoubleProperty ppsProperty() {
        return timeline.ppsProperty();
    }

    public DoubleProperty atProperty() {
        return timeline.atProperty();
    }

    public Tracks getTracks() {
        return timeline.getTracks();
    }

    public Viewport getViewPort() {
        return timeline.getViewPort();
    }

    public DoubleProperty canvasWidthProperty() {
        return canvasWidth;
    }

    public DoubleProperty canvasHeightProperty() {
        return canvasHeight;
    }

    public void loadLibraryContent(LibraryTab tab) {
        library.loadContent(tab);
    }

    public int timeToFrame(double seconds) {
        return (int) (seconds * frameRate.get());
    }

    public double pixelToTime(double pixels) {
        return snapToFrame(pixels / ppsProperty().get());
    }

    public double snapToFrame(double seconds) {
        return Math.floor(seconds * frameRate.get()) / frameRate.get();
    }

    public double snapToNextFrame(double seconds) {
        return Math.ceil(seconds * frameRate.get()) / frameRate.get();
    }

    public double snapDrag(double seconds) {
        return snapStrategy.snap(this, seconds);
    }

    public DecutProject save() {
        DecutProject proj = new DecutProject();
        Assets assets = LibraryContent.getInstance(this, Assets.class);
        proj.addAssets(assets.getGrid().getData().toArray(new AssetData[0]));
        return proj;
    }

    public void load(DecutProject proj) {
        Assets assets = LibraryContent.getInstance(this, Assets.class);
        assets.getGrid().importFiles(proj.getAssets());
    }

    @Override
    public void setup() {
        super.setup();
        getWindow().getMenuBar().getChildren().setAll(file, edit);
    }

    @Override
    public void destroy() {
        super.destroy();
        getWindow().getMenuBar().getChildren().clear();
    }

    @Override
    public void applyStyle(Style style) {
        //root.setBackground(Backgrounds.make(style.getAccent()));
    }
}
