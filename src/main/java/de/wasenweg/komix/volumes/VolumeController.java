package de.wasenweg.komix.volumes;

import de.wasenweg.komix.comics.Comic;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/volumes")
@RestController
public class VolumeController {

    private VolumeService volumeService;

    @PutMapping("/markAsRead")
    public void markAsRead(@Valid @RequestBody final Volume volume) {
        this.volumeService.updateRead(volume, true);
    }

    @PutMapping("/markAsUnread")
    public void markAsUnread(@Valid @RequestBody final Volume volume) {
        this.volumeService.updateRead(volume, false);
    }

    @PutMapping("/markAllAsReadUntil")
    public void markAsUnread(@Valid @RequestBody final Comic comic) {
        this.volumeService.updateReadUntil(comic);
    }
}
