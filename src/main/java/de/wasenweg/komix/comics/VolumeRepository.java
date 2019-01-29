package de.wasenweg.komix.comics;

import java.util.List;

public interface VolumeRepository {

    List<Publisher> findVolumesBySeriesAndPublishers();

    List<Series> findVolumesBySeries();
}
