package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Volume {

    private String id;

    private String volume;
    private String series;
    private String publisher;
    private Short issueCount;
    private Boolean read;
    private byte[] thumbnail;
}
