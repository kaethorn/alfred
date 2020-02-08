package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {

  @Autowired
  private ComicQueryRepositoryImpl comicQueryRepository;

  public List<Comic> get() {
    return this.comicQueryRepository.findAllWithErrors();
  }

  public List<Comic> getValid() {
    return this.comicQueryRepository.findAllWithoutErrors();
  }
}