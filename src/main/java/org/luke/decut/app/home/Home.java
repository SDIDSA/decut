package org.luke.decut.app.home;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import org.luke.decut.app.home.menubar.FileMenu;
import org.luke.decut.app.home.menubar.edit.EditMenu;
import org.luke.decut.app.inspector.Inspector;
import org.luke.decut.app.lib.*;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.preview.Preview;
import org.luke.decut.app.timeline.TimelinePane;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.controls.snap.SnapStrategy;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.app.timeline.tracks.Tracks;
import org.luke.decut.app.timeline.viewport.Viewport;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.file.FileDealer;
import org.luke.decut.file.project.DecutProject;
import org.luke.decut.render.SegmentRenderer;
import org.luke.decut.render.TimelineRenderer;
import org.luke.gui.controls.button.MenuBarButton;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
    private final SegmentRenderer previewer;
    private final DoubleProperty duration;
    private final DoubleProperty frameRate;
    private final DoubleProperty canvasWidth;
    private final DoubleProperty canvasHeight;
    private final DoubleProperty vSplit;
    private final DoubleProperty hSplit1;
    private File openProject;
    private SnapStrategy snapStrategy = SnapStrategy.SECOND;
    private double initVSplit;
    private double initHSplit1;
    private double initHSplit2;
    private final DoubleProperty hSplit2;

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
        LibraryTab assets = new LibraryTab(this, "Assets", "assets", Assets.class);
        library.addTab(assets);
        library.addTab(new LibraryTab(this, "Text", "text", Text.class));
        library.addTab(new LibraryTab(this, "Effects", "effects", Effects.class));

        Platform.runLater(assets::select);

        top = new HBox();
        timeline = new TimelinePane(this);
        preview = new Preview(this);
        inspector = new Inspector(this);

        renderer = new TimelineRenderer(this);
        previewer = new SegmentRenderer(this);

        double thickness = 7;

        root.setPadding(new Insets(0, thickness, thickness, thickness));

        top.setMinHeight(USE_PREF_SIZE);
        top.setMaxHeight(USE_PREF_SIZE);
        top.prefHeightProperty().bind(heightProperty().subtract(thickness * 2).multiply(vSplit));

        timeline.setMinHeight(USE_PREF_SIZE);
        timeline.setMaxHeight(USE_PREF_SIZE);
        timeline.setMinWidth(USE_PREF_SIZE);
        timeline.setMaxWidth(USE_PREF_SIZE);
        timeline.prefHeightProperty().bind(heightProperty().subtract(thickness * 2).multiply(vSplit.negate().add(1)));
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

    public void perform(String name, Runnable action, Runnable inverse, boolean ffmpeg, boolean ffprobe) {
        edit.perform(name, () -> {
            action.run();
            pausePlayback();
            clearPreviewCache();
        }, () -> {
            inverse.run();
            pausePlayback();
            clearPreviewCache();
        }, ffmpeg, ffprobe);
    }

    public void perform(String name, Runnable action, Runnable inverse) {
        perform(name, action, inverse, false, false);
    }

    public FfmpegCommand render(File file) {
        return renderer.generateRenderCommand(file);
    }

    public FfmpegCommand previewFrames(File file, double startTime, double duration, double qualityFactor) {
        return previewer.renderSegmentFrames(file, startTime, duration, qualityFactor);
    }

    public FfmpegCommand previewAudio(File file, double startTime, double duration) {
        return previewer.renderSegmentAudio(file, startTime, duration);
    }

    public void setPreviewQuality(double qualityFactor) {
        if (preview == null) {
            Platform.waitWhile(() -> preview == null, () -> preview.setQualityFactor(qualityFactor), 5000);
        } else {
            preview.setQualityFactor(qualityFactor);
        }
    }

    public void playPreview() {
        preview.play();
    }

    public void pausePreview() {
        preview.pause();
    }

    public void pausePlayback() {
        timeline.pausePlayback();
    }

    public void clearPreviewCache() {
        preview.clearCache();
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

    public double pixelToTimeNoSnap(double pixels) {
        return pixels / ppsProperty().get();
    }

    public double snapToFrame(double seconds) {
        return Math.round(seconds * frameRate.get()) / frameRate.get();
    }

    public double snapToNextFrame(double seconds) {
        return Math.ceil(seconds * frameRate.get()) / frameRate.get();
    }

    public double snapDrag(double seconds) {
        return snapStrategy.snap(this, seconds);
    }

    public DecutProject save() {
        DecutProject proj = new DecutProject();
        proj.save(this);
        return proj;
    }

    public void zip(File root) {
        Assets assets = LibraryContent.getInstance(this, Assets.class);
        assets.getGrid().getData().forEach(asset -> {
            if (isAssetUsed(asset)) {
                try {
                    Files.copy(asset.getFile().toPath(), new File(root, asset.getFile().getName()).toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean isAssetUsed(AssetData asset) {
        for (Track track : getTracks().getTracks()) {
            for (TimelineClip clip : track.getContent().getClips()) {
                if (clip.getSourceAsset() == asset || clip.getSourceAsset().getParent() == asset) {
                    return true;
                }
            }
        }
        return false;
    }

    private void load(DecutProject proj) {
        proj.load(this);
    }

    public void load(File projectFile, boolean zipped) {
        openProject = projectFile;
        DecutProject proj = new DecutProject();
        proj.deserialize(projectFile, new JSONObject(FileDealer.read(projectFile)), zipped);
        load(proj);
    }

    public File getOpenProject() {
        return openProject;
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
