package de.wasenweg.comix;

import de.wasenweg.komix.comics.Comic;

import java.util.GregorianCalendar;

public class ComicFixtures {

    public static final Comic COMIC_A1 = Comic.builder()
            .path("/a1.cbz").title("Title A1").series("Series A")
            .volume("1999").number("1").position("0001.0")
            .year((short) 1999).month((short) 2).publisher("Pub A")
            .read(true)
            .lastRead(new GregorianCalendar(2019, 1, 20).getTime())
            .build();

    public static final Comic COMIC_A2 = Comic.builder()
            .path("/a2.cbz").title("Title A2").series("Series A")
            .volume("1999").number("2").position("0002.0")
            .year((short) 1999).month((short) 3).publisher("Pub A")
            .build();

    public static final Comic COMIC_A3 = Comic.builder()
            .path("/a3.cbz").title("Title A3").series("Series A")
            .volume("1999").number("3").position("0003.0")
            .year((short) 1999).month((short) 4).publisher("Pub A")
            .build();

    public static final Comic COMIC_B1 = Comic.builder()
            .path("/b1.cbz").title("Title B1").series("Series A")
            .volume("2005").number("1").position("0001.0")
            .year((short) 2005).month((short) 5).publisher("Pub A")
            .build();

    public static final Comic COMIC_B2 = Comic.builder()
            .path("/b2.cbz").title("Title B2").series("Series A")
            .volume("2005").number("2").position("0002.0")
            .year((short) 2005).month((short) 6).publisher("Pub A")
            .build();

    public static final Comic COMIC_B3 = Comic.builder()
            .path("/b3.cbz").title("Title B3").series("Series A")
            .volume("2005").number("3").position("0003.0")
            .year((short) 2005).month((short) 7).publisher("Pub A")
            .build();

    public static final Comic COMIC_C1 = Comic.builder()
            .path("/c1.cbz").title("Title C1").series("Series A")
            .volume("2011").number("1").position("0001.0")
            .year((short) 2011).month((short) 1).publisher("Pub A")
            .read(true)
            .lastRead(new GregorianCalendar(2018, 10, 8).getTime())
            .build();

    public static final Comic COMIC_C2 = Comic.builder()
            .path("/c2.cbz").title("Title C2").series("Series A")
            .volume("2011").number("2").position("0002.0")
            .year((short) 2011).month((short) 2).publisher("Pub A")
            .read(true)
            .lastRead(new GregorianCalendar(2018, 10, 13).getTime())
            .build();

    public static final Comic COMIC_C3 = Comic.builder()
            .path("/c3.cbz").title("Title C3").series("Series A")
            .volume("2011").number("3").position("0003.0")
            .year((short) 2011).month((short) 3).publisher("Pub A")
            .build();
}
