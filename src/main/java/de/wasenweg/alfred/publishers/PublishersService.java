package de.wasenweg.alfred.publishers;

import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublishersService {

  private final ComicQueryRepositoryImpl repository;

  public List<Publisher> findAllPublishers(final String userId) {
    return this.repository.findAllPublishers(userId);
  }
}