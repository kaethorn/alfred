package de.wasenweg.alfred.scanner;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Thumbnail {

    private static final int BOUND_WIDTH = 200;
    private static final int BOUND_HEIGHT = 300;

    private static Dimension calculateTargetDimension(final int srcWidth, final int srcHeight) {
        int dstWidth = srcWidth;
        int dstHeight = srcHeight;

        if (srcWidth > BOUND_WIDTH) {
            dstWidth = BOUND_WIDTH;
            dstHeight = (dstWidth * srcHeight) / srcWidth;
        }

        if (dstHeight > BOUND_HEIGHT) {
            dstHeight = BOUND_HEIGHT;
            dstWidth = (dstHeight * srcWidth) / srcHeight;
        }

        return new Dimension(dstWidth, dstHeight);
    }

    public static ByteArrayOutputStream get(final InputStream input) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final BufferedImage srcImage = ImageIO.read(input);
            final Dimension dimension = calculateTargetDimension(srcImage.getWidth(), srcImage.getHeight());

            final BufferedImage dstImage = new BufferedImage(dimension.width, dimension.height,
                BufferedImage.TYPE_INT_RGB);
            dstImage.getGraphics().drawImage(
                srcImage, 0, 0, dimension.width, dimension.height, null);
            ImageIO.write(dstImage, "jpg", out);

        } catch (final IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
