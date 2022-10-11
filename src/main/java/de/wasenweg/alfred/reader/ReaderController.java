package de.wasenweg.alfred.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReaderController {

  private final ReaderService readerService;

  @Transactional
  @GetMapping("/read/{id}")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> read(
      @PathVariable("id") final String id) {
    return this.readerService.read(id, 0);
  }

  @Transactional
  @GetMapping("/read/{id}/{page}")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> readPage(
      @PathVariable("id") final String id,
      @PathVariable("page") final Integer page) {
    return this.readerService.read(id, page);
  }

  @Transactional
  @GetMapping("/download/{id}")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> download(@PathVariable("id") final String id) {
    return this.readerService.download(id);
  }
}
