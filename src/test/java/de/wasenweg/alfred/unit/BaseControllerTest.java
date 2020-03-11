package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.util.BaseController;
import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseControllerTest {

  @Test
  public void wrapEntity() throws Exception {
    final class TestController extends BaseController<Comic> {
      public EntityModel<Comic> get() {
        return this.wrap(ComicFixtures.COMIC_V1_1.toBuilder()
            .id("comic-id-1-1")
            .build());
      }
    }

    final TestController controller = new TestController();

    assertThat(controller.get().getLinks().hasSingleLink()).isTrue();
    assertThat(controller.get().getLinks().toList().get(0).getHref()).isEqualTo("/comic-id-1-1");
  }

  @Test
  public void wrapCollection() throws Exception {
    final class TestController extends BaseController<Comic> {
      public CollectionModel<EntityModel<Comic>> get() {
        return this.wrap(Arrays.asList(
            ComicFixtures.COMIC_V1_1.toBuilder()
                .id("comic-id-1-1")
                .build(),
            ComicFixtures.COMIC_V1_2.toBuilder()
                .id("comic-id-1-2")
                .build()));
      }
    }

    final TestController controller = new TestController();

    assertThat(controller.get().getLinks().hasSingleLink()).isTrue();
    assertThat(controller.get().getLinks().toList().get(0).getHref()).isEqualTo("/");
    assertThat(controller.get()
        .getContent().stream()
        .map(entity -> entity.getLinks().toList().get(0).getHref()))
          .containsOnly("/comic-id-1-1", "/comic-id-1-2");
  }

  @Test
  public void wrapWithMissingEntity() throws Exception {
    final class TestController extends BaseController<Object> {
      public EntityModel<Object> get() {
        return this.wrap(Optional.ofNullable(null));
      }
    }

    final TestController controller = new TestController();

    assertThrows(ResourceNotFoundException.class, () -> controller.get());
  }

  @Test
  public void wrapWithNonEntity() throws Exception {
    final class NonEntity {
    }

    final class TestController extends BaseController<NonEntity> {
      public EntityModel<NonEntity> get() {
        return this.wrap(new NonEntity());
      }
    }

    final TestController controller = new TestController();

    assertThat(controller.get().getLinks().hasSingleLink()).isTrue();
    assertThat(controller.get().getLinks().toList().get(0).getHref()).isEqualTo("/");
  }
}
