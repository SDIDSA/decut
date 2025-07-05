package org.luke.decut.app.lib.assets.data;

import javafx.scene.image.*;

import java.nio.ByteBuffer;

public class ImageUtils {
    public static Image cropCenter(Image originalImage) {
        int thumbnailSize = (int) Math.min(originalImage.getWidth(), originalImage.getHeight());

        double sourceWidth = originalImage.getWidth();
        double sourceHeight = originalImage.getHeight();

        double sourceRatio = sourceWidth / sourceHeight;
        double targetRatio = (double) thumbnailSize / thumbnailSize;

        double cropX = 0;
        double cropY = 0;
        double cropWidth = sourceWidth;
        double cropHeight = sourceHeight;

        if (sourceRatio > targetRatio) {
            cropWidth = sourceHeight * targetRatio;
            cropX = (sourceWidth - cropWidth) / 2;
        } else if (sourceRatio < targetRatio) {
            cropHeight = sourceWidth / targetRatio;
            cropY = (sourceHeight - cropHeight) / 2;
        }

        PixelReader pixelReader = originalImage.getPixelReader();

        WritableImage thumbnail = new WritableImage(thumbnailSize, thumbnailSize);
        PixelWriter pixelWriter = thumbnail.getPixelWriter();

        WritablePixelFormat<ByteBuffer> pf = WritablePixelFormat.getByteBgraInstance();

        ByteBuffer buffer = ByteBuffer.allocate((int) cropWidth * (int) cropHeight * 4);

        pixelReader.getPixels(
                (int) cropX, (int) cropY,
                (int) cropWidth, (int) cropHeight,
                pf,
                buffer,
                (int) cropWidth * 4
        );

        buffer.rewind();

        pixelWriter.setPixels(
                0, 0,
                (int) cropWidth, (int) cropHeight,
                pf,
                buffer,
                (int) cropWidth * 4
        );

        return thumbnail;
    }

}
