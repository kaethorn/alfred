package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.thumbnails.ThumbnailUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThumbnailUtilTest {

  @Test
  public void calculateTargetDimensionLandscape() {
    assertThat(ThumbnailUtil.calculateTargetDimension(800, 500).getWidth()).isEqualTo(200);
    assertThat(ThumbnailUtil.calculateTargetDimension(800, 500).getHeight()).isEqualTo(125);
  }

  @Test
  public void calculateTargetDimensionPortrait() {
    assertThat(ThumbnailUtil.calculateTargetDimension(500, 800).getWidth()).isEqualTo(187);
    assertThat(ThumbnailUtil.calculateTargetDimension(500, 800).getHeight()).isEqualTo(300);
  }

  @Test
  public void calculateTargetDimensionSquare() {
    assertThat(ThumbnailUtil.calculateTargetDimension(800, 800).getWidth()).isEqualTo(200);
    assertThat(ThumbnailUtil.calculateTargetDimension(800, 800).getHeight()).isEqualTo(200);
  }

  @Test
  public void calculateTargetDimensionSmall() {
    assertThat(ThumbnailUtil.calculateTargetDimension(100, 150).getWidth()).isEqualTo(100);
    assertThat(ThumbnailUtil.calculateTargetDimension(100, 150).getHeight()).isEqualTo(150);
  }

  @Test
  public void getWithError() {
    assertThat(ThumbnailUtil.get(null).size()).isEqualTo(0);
  }
}
