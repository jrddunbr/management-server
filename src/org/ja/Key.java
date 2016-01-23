
package org.ja;

/**
 *
 * @author jared
 */
public class Key {
    private String key, value;
    
    public Key(String key, String value) {
        this.value = value;
        this.key = key;
    }
    
    public void updateKey(String value) {
        this.value = value;
    }
    
    public String getKeyValue() {
        return value;
    }
    
    public String getKeyName() {
        return key;
    }
    
    @Override
    public String toString() {
        return key;
    }
}
