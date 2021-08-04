package de.wasenweg.alfred.volumes;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/volumes")
@RequiredArgsConstructor
public class VolumesController {

  private final ProgressService progressService;
  private final VolumesService volumesService;

  @GetMapping
  public CollectionModel<Volume> findAllVolumesByPublisherAndSeries(
      final Principal principal,
      @RequestParam("publisher") final String publisher,
      @RequestParam("series") final String series) {
    return CollectionModel.of(
        this.volumesService.findAllVolumesByPublisherAndSeries(principal.getName(), publisher, series),
        linkTo(methodOn(VolumesController.class)
            .findAllVolumesByPublisherAndSeries(principal, publisher, series)).withSelfRel()
    );
  }

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
