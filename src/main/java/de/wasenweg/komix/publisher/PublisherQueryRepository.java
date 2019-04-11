package de.wasenweg.komix.publisher;

import java.util.List;

public interface PublisherQueryRepository {

    List<Publisher> findAll(final String userName);
}
