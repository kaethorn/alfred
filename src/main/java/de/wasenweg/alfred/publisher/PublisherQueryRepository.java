package de.wasenweg.alfred.publisher;

import java.util.List;

public interface PublisherQueryRepository {

    List<Publisher> findAll(final String userId);
}
