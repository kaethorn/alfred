package de.wasenweg.alfred.fixtures;

import de.wasenweg.alfred.comics.Comic;

public final class ComicFixtures {

  private static final String SERIES_A = "Series A";
  private static final String PUBLISHER_A = "Publisher A";

  public static final Comic COMIC_V1_1 = Comic.builder()
      .path("/a1.cbz").fileName("a1.cbz").title("Title A1").series(SERIES_A)
      .volume("1999").number("1").position("0001.0")
      .year(1999).month(2).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V1_2 = Comic.builder()
      .path("/a2.cbz").fileName("a2.cbz").title("Title A2").series(SERIES_A)
      .volume("1999").number("2").position("0002.0")
      .year(1999).month(3).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V1_3 = Comic.builder()
      .path("/a3.cbz").fileName("a3.cbz").title("Title A3").series(SERIES_A)
      .volume("1999").number("3").position("0003.0")
      .year(1999).month(4).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V2_1 = Comic.builder()
      .path("/b1.cbz").fileName("b1.cbz").title("Title B1").series(SERIES_A)
      .volume("2005").number("1").position("0001.0")
      .year(2005).month(5).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V2_2 = Comic.builder()
      .path("/b2.cbz").fileName("b2.cbz").title("Title B2").series(SERIES_A)
      .volume("2005").number("2").position("0002.0")
      .year(2005).month(6).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V2_3 = Comic.builder()
      .path("/b3.cbz").fileName("b3.cbz").title("Title B3").series(SERIES_A)
      .volume("2005").number("3").position("0003.0")
      .year(2005).month(7).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V3_1 = Comic.builder()
      .path("/c1.cbz").fileName("c1.cbz").title("Title C1").series(SERIES_A)
      .volume("2011").number("1").position("0001.0")
      .year(2011).month(1).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V3_2 = Comic.builder()
      .path("/c2.cbz").fileName("c2.cbz").title("Title C2").series(SERIES_A)
      .volume("2011").number("2").position("0002.0")
      .year(2011).month(2).publisher(PUBLISHER_A)
      .build();

  public static final Comic COMIC_V3_3 = Comic.builder()
      .path("/c3.cbz").fileName("c3.cbz").title("Title C3").series(SERIES_A)
      .volume("2011").number("3").position("0003.0")
      .year(2011).month(3).publisher(PUBLISHER_A)
      .build();

  private ComicFixtures() {
    throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
