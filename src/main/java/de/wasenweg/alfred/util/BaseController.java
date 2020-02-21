package de.wasenweg.alfred.util;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.jmx.access.InvalidInvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BaseController<T> {

  private String getItemId(final T item) {
    try {
      final Method getId = item.getClass().getDeclaredMethod("getId");
      return (String) getId.invoke(item);
    } catch (final NoSuchMethodException | SecurityException | IllegalAccessException
        | InvalidInvocationException | IllegalArgumentException | InvocationTargetException exception) {
      return "";
    }
  }

  protected CollectionModel<EntityModel<T>> wrap(final List<T> items) {
    return new CollectionModel<EntityModel<T>>(
        items.stream()
          .map(item -> {
            return this.wrap(item);
          }).collect(Collectors.toList()),
        linkTo(this.getClass()).withSelfRel());
  }

  protected EntityModel<T> wrap(final Optional<T> item) {
    if (item.isPresent()) {
      return this.wrap(item.get());
    } else {
      throw new ResourceNotFoundException();
    }
  }

  protected EntityModel<T> wrap(final T item) {
    final Link link = linkTo(this.getClass()).slash(this.getItemId(item)).withSelfRel();
    return new EntityModel<T>(item, link);
  }

  protected EntityModel<T> wrapRoot(final T item) {
    final Link link = linkTo(this.getClass()).withSelfRel();
    return new EntityModel<T>(item, link);
  }
}
