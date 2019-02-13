package de.wasenweg.komix.volumes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Series {

    private String id;

    private String series;
    private List<Volume> volumes;
}
