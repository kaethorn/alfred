package de.wasenweg.alfred.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Progress {

  @Id
  private String id;

  @NonNull
  private String userId;

  @NonNull
  private ObjectId comicId;

  @Builder.Default
  private boolean read = false;

  @NonNull
  @Builder.Default
  private Short currentPage = 0;

  private Date lastRead;
}
