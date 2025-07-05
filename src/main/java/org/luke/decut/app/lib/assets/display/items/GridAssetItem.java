package org.luke.decut.app.lib.assets.display.items;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.dragdrop.AssetDrag;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.SplineInterpolator;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.recycle.grid.GridCell;
import org.luke.gui.controls.recycle.grid.GridView;
import org.luke.gui.controls.text.unkeyed.Label;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class GridAssetItem extends GridCell<AssetData> implements Styleable, AssetItem {

    private final ColoredIcon typeIcon;
    private final Label name;

    private final ColoredIcon thumb;

    private final Timeline dragStart, dragEnd;
    private AssetData loaded;

    public GridAssetItem(Home owner, GridView<AssetData> gv) {
        super(gv);
        typeIcon = new ColoredIcon(owner.getWindow(), "empty", 14,
                Style::getBackgroundTertiary,
                Style::getTextNormal);
        typeIcon.setPadding(6);
        typeIcon.setRadius(new CornerRadii(0, 0, 5, 0, false));
        typeIcon.setTranslateX(-3);
        typeIcon.setTranslateY(-3);

        name = new Label("", new Font(12));
        name.setTextAlignment(TextAlignment.CENTER);
        name.setPadding(new Insets(5));
        name.setMinWidth(USE_PREF_SIZE);
        name.setMaxWidth(USE_PREF_SIZE);
        name.opacityProperty().unbind();

        name.setTranslateY(15);
        name.setOpacity(0);

        Timeline enter = new Timeline(
                new KeyFrame(Duration.seconds(.15),
                        new KeyValue(name.translateYProperty(), 0),
                        new KeyValue(name.opacityProperty(), 1)
                )
        );

        Timeline exit = new Timeline(
                new KeyFrame(Duration.seconds(.15),
                        new KeyValue(name.translateYProperty(), 15),
                        new KeyValue(name.opacityProperty(), 0)
                )
        );

        setCursor(Cursor.HAND);
        setOnMouseEntered(_ -> {
            exit.stop();
            enter.playFromStart();
        });
        setOnMouseExited(_ -> {
            enter.stop();
            exit.playFromStart();
        });

        StackPane.setAlignment(typeIcon, Pos.TOP_LEFT);
        StackPane.setAlignment(name, Pos.BOTTOM_CENTER);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        setClip(clip);

        thumb = new ColoredIcon(owner.getWindow(), "empty", 14, Style::getTextMuted);

        StackPane root = new StackPane();
        root.getChildren().addAll(thumb, typeIcon, name);

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        root.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        root.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        name.prefWidthProperty().bind(SIZE);
        root.prefWidthProperty().bind(SIZE);
        root.prefHeightProperty().bind(SIZE);

        clip.widthProperty().bind(SIZE);
        clip.heightProperty().bind(SIZE);

        dragStart = new Timeline(new KeyFrame(Duration.seconds(0.2),
                new KeyValue(thumb.scaleXProperty(), 1.4, SplineInterpolator.EASE_OUT),
                new KeyValue(thumb.scaleYProperty(), 1.4, SplineInterpolator.EASE_OUT)));

        dragEnd = new Timeline(new KeyFrame(Duration.seconds(0.2),
                new KeyValue(thumb.scaleXProperty(), 1, SplineInterpolator.EASE_OUT),
                new KeyValue(thumb.scaleYProperty(), 1, SplineInterpolator.EASE_OUT)));

        setOnDragDetected(event -> {
            gv.getSelectionModel().clearSelection();
            gv.getSelectionModel().select(loaded);

            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            WritableImage snapshot = snapshot(null, null);
            db.setDragView(snapshot);

            AssetDrag dc = new AssetDrag(owner, loaded);
            dc.putContent(db);

            dragEnd.stop();
            dragStart.playFromStart();
            event.consume();
        });

        setOnDragDone(e -> {
            dragStart.stop();
            dragEnd.playFromStart();
            setPressed(false);
        });

        getChildren().add(root);

        applyStyle(owner.getWindow().getStyl());
    }

    @Override
    protected void updateContent(AssetData item) {
        loaded = item;
        typeIcon.setImage(item.getType().getIcon());
        name.setText(item.getFile().getName());
        thumb.setImage(item.getThumb(), AssetItem.SIZE);
        thumb.setColored(item.isThumbColored());
    }

    @Override
    public void applyStyle(Style style, boolean selected) {
        if (name == null) return;
        name.setFill(style.getTextNormal());
        name.setBackground(Backgrounds.make(style.getBackgroundTertiaryOr()
                .deriveColor(0, 1, 1, 0.8)));
        setBackground(Backgrounds.make(style.getBackgroundTertiaryOr(), 5));
        thumb.setOpacity(selected ? 1 : 0.2);
    }
}
