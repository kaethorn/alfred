package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

  @Autowired
  private ComicQueryRepositoryImpl comicQueryRepository;

  @GetMapping()
  public ResponseEntity<List<Comic>> get() {
    final List<Comic> comics = this.comicQueryRepository.findAllWithErrors();
    return new ResponseEntity<List<Comic>>(comics, HttpStatus.OK);
  }
}
