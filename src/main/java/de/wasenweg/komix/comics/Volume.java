package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Volume {

    private String id;

    private String volume;
    private byte[] thumbnail;
}
