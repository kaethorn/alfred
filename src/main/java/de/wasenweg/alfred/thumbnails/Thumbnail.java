package de.wasenweg.alfred.thumbnails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Thumbnail {

  public enum ThumbnailType {
    FRONT_COVER,
    BACK_COVER
  }

  @Id
  private String id;

  @NonNull
  @Indexed
  private ObjectId comicId;

  @NonNull
  @Indexed
  private ThumbnailType type;

  private String path;

  private byte[] image;
}
