package de.wasenweg.alfred.thumbnails;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public final class ThumbnailUtility {

  private static final int BOUND_WIDTH = 200;
  private static final int BOUND_HEIGHT = 300;

  private ThumbnailUtility() {
    throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static Dimension calculateTargetDimension(final int srcWidth, final int srcHeight) {
    final Dimension dimension = new Dimension(srcWidth, srcHeight);

    if (srcWidth > BOUND_WIDTH) {
      dimension.setSize(BOUND_WIDTH, (BOUND_WIDTH * srcHeight) / srcWidth);
    }

    if (dimension.getHeight() > BOUND_HEIGHT) {
      dimension.setSize((BOUND_HEIGHT * srcWidth) / srcHeight, BOUND_HEIGHT);
    }

    return dimension;
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

    } catch (final IOException exception) {
      log.error("Failed to generate thumbnail.", exception);
    }
    return out;
  }
}
