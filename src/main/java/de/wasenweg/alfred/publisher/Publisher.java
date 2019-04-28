package de.wasenweg.alfred.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    private String id;

    private String publisher;
    private Short seriesCount;
}
