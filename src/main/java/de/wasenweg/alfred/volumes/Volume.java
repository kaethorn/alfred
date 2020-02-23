package de.wasenweg.alfred.volumes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Volume {

  private String id;

  private String name;
  private String series;
  private String publisher;
  private Integer issueCount;
  private Integer readCount;
  private boolean read;
  private String firstComicId;

  @Override
  public String toString() {
    return format(
        "%s (Vol. %s) by %s",
        this.series, this.name, this.publisher);
  }
}
