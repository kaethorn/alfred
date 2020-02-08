package de.wasenweg.alfred.reader;

import lombok.Data;

import java.io.InputStream;

import static java.lang.String.format;

@Data
public class ComicPage {
  private InputStream stream;
  private String name;
  private long size;
  private String type;

  @Override
  public String toString() {
    return format(
        "ComicPage[name='%s', size='%s', type='%s']",
        this.name, this.size, this.type);
  }
}
