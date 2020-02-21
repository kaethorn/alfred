package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.thumbnails.ThumbnailUtility;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThumbnailUtilityTest {

  @Test
  public void calculateTargetDimensionLandscape() throws Exception {
    assertThat(ThumbnailUtility.calculateTargetDimension(800, 500).getWidth()).isEqualTo(200);
    assertThat(ThumbnailUtility.calculateTargetDimension(800, 500).getHeight()).isEqualTo(125);
  }

  @Test
  public void calculateTargetDimensionPortrait() throws Exception {
    assertThat(ThumbnailUtility.calculateTargetDimension(500, 800).getWidth()).isEqualTo(187);
    assertThat(ThumbnailUtility.calculateTargetDimension(500, 800).getHeight()).isEqualTo(300);
  }

  @Test
  public void calculateTargetDimensionSquare() throws Exception {
    assertThat(ThumbnailUtility.calculateTargetDimension(800, 800).getWidth()).isEqualTo(200);
    assertThat(ThumbnailUtility.calculateTargetDimension(800, 800).getHeight()).isEqualTo(200);
  }

  @Test
  public void calculateTargetDimensionSmall() throws Exception {
    assertThat(ThumbnailUtility.calculateTargetDimension(100, 150).getWidth()).isEqualTo(100);
    assertThat(ThumbnailUtility.calculateTargetDimension(100, 150).getHeight()).isEqualTo(150);
  }
}
