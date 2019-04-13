package de.wasenweg.komix.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/publishers", produces = { "application/hal+json" })
public class PublisherController {

    @Autowired
    private PublisherQueryRepositoryImpl repository;

    @GetMapping
    public Resources<Publisher> findAll(final Principal principal) {
        final List<Publisher> publishers = this.repository.findAll(principal.getName());
        final Link link = linkTo(PublisherController.class).withSelfRel();
        return new Resources<Publisher>(publishers, link);
    }
}
