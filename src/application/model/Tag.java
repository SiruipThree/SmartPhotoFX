package application.model;

import java.io.Serializable;

/**
 * Represents a tag that can be applied to a photo.
 * Each tag has a unique ID, a name (e.g., "location", "person"), and a flag indicating
 * whether it can have multiple values for the same photo.
 *
 * Implements Serializable to support saving user data to disk.
 * 
 * Examples:
 * - Single-value tag: location = "New York"
 * - Multi-value tag: person = "Alice", person = "Bob"
 * 
 */
public class Tag implements Serializable {

    private long id;
    private String name;
    private boolean multiValue;

    /**
     * Constructs a new Tag.
     */
    public Tag(long id, String name, boolean multiValue) {
        this.id = id;
        this.name = name;
        this.multiValue = multiValue;
    }

    /**
     * Returns the unique ID of the tag.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the name/type of the tag.
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates whether this tag allows multiple values.
     *
     */
    public boolean isMultiValue() {
        return multiValue;
    }
}
