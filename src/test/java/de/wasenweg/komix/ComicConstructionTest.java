package de.wasenweg.komix;

import de.wasenweg.komix.comics.Comic;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComicConstructionTest {

    @Test
    public void constructWithNew() throws Exception {
        final Comic comic = new Comic();
        assertThat(comic.getCurrentPage()).isEqualTo((short) 0);
        assertThat(comic.isRead()).isEqualTo(false);
    }

    @Test
    public void constructWithBuilder() throws Exception {
        final Comic comic = Comic.builder()
                .path("")
                .title("")
                .series("")
                .volume("")
                .number("")
                .position("")
                .year((short) 2000)
                .month((short) 5)
                .publisher("")
                .build();
        assertThat(comic.getCurrentPage()).isEqualTo((short) 0);
        assertThat(comic.isRead()).isEqualTo(false);
    }
}
