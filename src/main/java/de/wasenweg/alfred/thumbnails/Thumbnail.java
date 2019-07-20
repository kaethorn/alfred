package de.wasenweg.alfred.thumbnails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Thumbnail {

  @Id
  private String id;

  @NonNull
  @Indexed
  private ObjectId comicId;

  private byte[] thumbnail;
}
