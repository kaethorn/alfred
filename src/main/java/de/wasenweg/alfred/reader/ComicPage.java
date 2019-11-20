package de.wasenweg.alfred.reader;

import lombok.Data;

import java.io.InputStream;

@Data
public class ComicPage {
  private InputStream stream;
  private String name;
  private long size;
  private String type;
}
