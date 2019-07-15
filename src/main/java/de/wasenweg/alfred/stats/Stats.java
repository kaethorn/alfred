package de.wasenweg.alfred.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {

  private long issues;
  private int publishers;
  private int series;
  private int volumes;

  private long users;
}
