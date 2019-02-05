package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Series {

    private String id;

    private String series;
    private List<Volume> volumes;
}
