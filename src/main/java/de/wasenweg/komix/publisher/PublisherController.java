package de.wasenweg.komix.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/publishers")
@RestController
public class PublisherController {

    @Autowired
    private PublisherQueryRepositoryImpl repository;

    @GetMapping("/search/findAll")
    public List<Publisher> findAll(final Principal principal) {
        return this.repository.findAll(principal.getName());
    }
}
