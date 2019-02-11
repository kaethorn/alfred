package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/volumes")
@RestController
public class VolumeController {

    @Autowired
    VolumeService volumeService;

    @PutMapping("/markAsRead")
    public void markAsRead(@Valid @RequestBody final Volume volume) {
        this.volumeService.updateRead(volume, true);
    }

    @PutMapping("/markAsUnread")
    public void markAsUnread(@Valid @RequestBody final Volume volume) {
        this.volumeService.updateRead(volume, false);
    }
}
