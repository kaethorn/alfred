package de.wasenweg.alfred.progress;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ObjectOperators.MergeObjects.merge;

public final class ProgressUtility {

  private static final String PROGRESS = "progress";

  private ProgressUtility() {
    throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  // Merge progress flags for the current user
  public static List<AggregationOperation> mergeProgress(final String userId) {
    return Stream.of(
        lookup(PROGRESS, "_id", "comicId", PROGRESS),
        replaceRoot().withValueOf(
            merge(Aggregation.ROOT)
            .mergeWithValuesOf(
                arrayOf(
                    filter(PROGRESS).as("item").by(ComparisonOperators.Eq.valueOf("item.userId").equalToValue(userId))
                    ).elementAt(0))
            // Restore comic _id previously overwritten with progress _id.
            .mergeWith(new BasicDBObject("_id", Aggregation.ROOT + "._id"))),
        project().andExclude(PROGRESS, "comicId", "userId")
    ).collect(Collectors.toList());
  }

  // Syntactic sugar
  public static Aggregation aggregateWithProgress(final String userId, final AggregationOperation... operations) {
    return Aggregation.newAggregation(
        Stream.concat(
            mergeProgress(userId).stream(),
            Stream.of(operations)).collect(Collectors.toList()));
  }
}
