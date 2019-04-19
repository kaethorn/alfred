package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;

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
