package de.wasenweg.komix.volumes;

import java.util.List;

public interface VolumeRepository {

    List<Publisher> findVolumesBySeriesAndPublishers();

    List<Series> findVolumesBySeries();
}
