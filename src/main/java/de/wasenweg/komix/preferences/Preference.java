package de.wasenweg.komix.preferences;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PREFERENCES")
public class Preference {

    @NotBlank
    @Id
    @Size(max = 100)
    private String key;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String value;

    @Lob
    private String comment;

    public Preference() { }

    public Preference(final String key, final String name, final String value, final String comment) {
        this.key = key;
        this.name = name;
        this.value = value;
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
