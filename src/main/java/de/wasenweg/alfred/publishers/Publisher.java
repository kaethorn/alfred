package de.wasenweg.alfred.publishers;

import de.wasenweg.alfred.series.Series;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

  private String id;

  private String name;
  private List<Series> series;
  private Integer seriesCount;
}
