package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Volume {

    private String id;

    private String series;
    private List<String> volumes;
}
