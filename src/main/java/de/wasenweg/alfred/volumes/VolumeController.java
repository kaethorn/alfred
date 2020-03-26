package de.wasenweg.alfred.volumes;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/api/volumes")
@RequiredArgsConstructor
public class VolumeController {

  private final ProgressService progressService;

  @PutMapping("/markAsRead")
  public void markAsRead(@Valid @RequestBody final Volume volume, final Principal principal) {
    this.progressService.updateVolume(principal.getName(), volume, true);
  }

  @PutMapping("/markAsUnread")
  public void markAsUnread(@Valid @RequestBody final Volume volume, final Principal principal) {
    this.progressService.updateVolume(principal.getName(), volume, false);
  }

  @PutMapping("/markAllAsReadUntil")
  public void markAllAsReadUntil(@Valid @RequestBody final Comic comic, final Principal principal) {
    this.progressService.updateVolumeUntil(principal.getName(), comic);
  }
}
