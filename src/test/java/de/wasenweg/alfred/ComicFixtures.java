package de.wasenweg.alfred;

import de.wasenweg.alfred.comics.Comic;

public class ComicFixtures {

    public static final Comic COMIC_V1_1 = Comic.builder()
            .path("/a1.cbz").title("Title A1").series("Series A")
            .volume("1999").number("1").position("0001.0")
            .year((short) 1999).month((short) 2).publisher("Pub A")
            .build();

    public static final Comic COMIC_V1_2 = Comic.builder()
            .path("/a2.cbz").title("Title A2").series("Series A")
            .volume("1999").number("2").position("0002.0")
            .year((short) 1999).month((short) 3).publisher("Pub A")
            .build();

    public static final Comic COMIC_V1_3 = Comic.builder()
            .path("/a3.cbz").title("Title A3").series("Series A")
            .volume("1999").number("3").position("0003.0")
            .year((short) 1999).month((short) 4).publisher("Pub A")
            .build();

    public static final Comic COMIC_V2_1 = Comic.builder()
            .path("/b1.cbz").title("Title B1").series("Series A")
            .volume("2005").number("1").position("0001.0")
            .year((short) 2005).month((short) 5).publisher("Pub A")
            .build();

    public static final Comic COMIC_V2_2 = Comic.builder()
            .path("/b2.cbz").title("Title B2").series("Series A")
            .volume("2005").number("2").position("0002.0")
            .year((short) 2005).month((short) 6).publisher("Pub A")
            .build();

    public static final Comic COMIC_V2_3 = Comic.builder()
            .path("/b3.cbz").title("Title B3").series("Series A")
            .volume("2005").number("3").position("0003.0")
            .year((short) 2005).month((short) 7).publisher("Pub A")
            .build();

    public static final Comic COMIC_V3_1 = Comic.builder()
            .path("/c1.cbz").title("Title C1").series("Series A")
            .volume("2011").number("1").position("0001.0")
            .year((short) 2011).month((short) 1).publisher("Pub A")
            .build();

    public static final Comic COMIC_V3_2 = Comic.builder()
            .path("/c2.cbz").title("Title C2").series("Series A")
            .volume("2011").number("2").position("0002.0")
            .year((short) 2011).month((short) 2).publisher("Pub A")
            .build();

    public static final Comic COMIC_V3_3 = Comic.builder()
            .path("/c3.cbz").title("Title C3").series("Series A")
            .volume("2011").number("3").position("0003.0")
            .year((short) 2011).month((short) 3).publisher("Pub A")
            .build();
}
