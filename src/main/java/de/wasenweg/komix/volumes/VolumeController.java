package de.wasenweg.komix.volumes;

import de.wasenweg.komix.comics.Comic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

@RequestMapping("/api/volumes")
@RestController
public class VolumeController {

    @Autowired
    private VolumeService service;

    @PutMapping("/markAsRead")
    public void markAsRead(@Valid @RequestBody final Volume volume, final Principal principal) {
        this.service.updateRead(principal.getName(), volume, true);
    }

    @PutMapping("/markAsUnread")
    public void markAsUnread(@Valid @RequestBody final Volume volume, final Principal principal) {
        this.service.updateRead(principal.getName(), volume, false);
    }

    @PutMapping("/markAllAsReadUntil")
    public void markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
        this.service.updateReadUntil(principal.getName(), comic);
    }
}
