package de.wasenweg.alfred.progress;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/progress", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class ProgressController {

  private final ProgressService progressService;

  @DeleteMapping("")
  public void deleteProgress() {
    this.progressService.deleteProgress();
  }

  @DeleteMapping("/me")
  public void deleteProgressForCurrentUser(final Principal principal) {
    this.progressService.deleteProgressForCurrentUser(principal.getName());
  }
}