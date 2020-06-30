package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.thumbnails.ThumbnailUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThumbnailUtilTest {

  @Test
  public void calculateTargetDimensionLandscape() {
    assertThat(ThumbnailUtil.calculateTargetDimension(1600, 1000).getWidth()).isEqualTo(400);
    assertThat(ThumbnailUtil.calculateTargetDimension(1600, 1000).getHeight()).isEqualTo(250);
  }

  @Test
  public void calculateTargetDimensionPortrait() {
    assertThat(ThumbnailUtil.calculateTargetDimension(1000, 1600).getWidth()).isEqualTo(375);
    assertThat(ThumbnailUtil.calculateTargetDimension(1000, 1600).getHeight()).isEqualTo(600);
  }

  @Test
  public void calculateTargetDimensionSquare() {
    assertThat(ThumbnailUtil.calculateTargetDimension(1600, 1600).getWidth()).isEqualTo(400);
    assertThat(ThumbnailUtil.calculateTargetDimension(1600, 1600).getHeight()).isEqualTo(400);
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
