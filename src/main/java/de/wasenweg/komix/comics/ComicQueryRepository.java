package de.wasenweg.komix.comics;

import java.util.List;

public interface ComicQueryRepository {

    List<Comic> findAllLastReadByVolume();
}
