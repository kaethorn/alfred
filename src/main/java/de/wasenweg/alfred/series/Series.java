package de.wasenweg.alfred.series;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Series {

  private String id;

  private String name;
  private String publisher;
  private Integer volumesCount;
}
