package de.wasenweg.komix.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    private String id;

    private String publisher;
    private List<Series> series;
}
