package de.wasenweg.alfred.progress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping(value = "/api/progress", produces = { "application/hal+json" })
@RestController
public class ProgressController {

  @Autowired
  private ProgressRepository progressRepository;

  @DeleteMapping("")
  public void deleteProgress() {
    this.progressRepository.deleteAll();
  }

  @DeleteMapping("/me")
  public void deleteProgressForCurrentUser(final Principal principal) {
    this.progressRepository.deleteByUserId(principal.getName());
  }
}
