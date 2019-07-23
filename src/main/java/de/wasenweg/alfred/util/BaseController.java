package de.wasenweg.alfred.util;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public abstract class BaseController<T> {

  private String getItemId(final T item) {
    try {
      final Method getId = item.getClass().getDeclaredMethod("getId");
      return (String) getId.invoke(item);
    } catch (final Exception exception) {
      return "";
    }
  }

  protected Resources<Resource<T>> wrap(final List<T> items) {
    return new Resources<Resource<T>>(
        items.stream()
          .map(item -> {
            return this.wrap(item);
          }).collect(Collectors.toList()),
        linkTo(this.getClass()).withSelfRel());
  }

  protected Resource<T> wrap(final Optional<T> item) {
    if (item.isPresent()) {
      return this.wrap(item.get());
    } else {
      throw new ResourceNotFoundException();
    }
  }

  protected Resource<T> wrap(final T item) {
    final Link link = linkTo(this.getClass()).slash(this.getItemId(item)).withSelfRel();
    return new Resource<T>(item, link);
  }

  protected Resource<T> wrapRoot(final T item) {
    final Link link = linkTo(this.getClass()).withSelfRel();
    return new Resource<T>(item, link);
  }
}
