package de.wasenweg.alfred.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.lang.String.format;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Setting {

  @Id
  private String id;

  @NonNull
  private String key;
  @NonNull
  private String name;
  @NonNull
  private String value;
  @NonNull
  private String comment;

  @Override
  public String toString() {
    return format(
        "Setting[id=%s, key='%s', value='%s']",
        this.id, this.key, this.value);
  }
}
