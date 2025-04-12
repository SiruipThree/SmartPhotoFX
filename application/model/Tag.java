package application.model;

import java.io.Serializable;

/**
 * 标签类，保存标签名与标签值的组合
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    
    public Tag(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public String getName(){
        return name;
    }
    
    public String getValue(){
        return value;
    }
    
    @Override
    public String toString(){
        return name + "=" + value;
    }
}
