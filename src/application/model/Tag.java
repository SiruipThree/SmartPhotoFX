package application.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private long id;
    private String name;
    private boolean multiValue;
    
    public Tag(long id, String name, boolean multiValue) {
        this.id = id;
        this.name = name;
        this.multiValue = multiValue;
    }

    public long getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public boolean isMultiValue(){
        return multiValue;
    }
}
