package de.wasenweg.comix;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Comic {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String path;

    public Comic(long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getPath() {
    	return path;
    }
}