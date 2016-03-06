
package org.ja;

/**
 *
 * @author jared
 */
public class Key {
    private String key, value;
    
    /**
     *
     * @param key
     * @param value
     */
    public Key(String key, String value) {
        this.value = value;
        this.key = key;
    }
    
    /**
     *
     * @param value
     */
    public void updateKey(String value) {
        this.value = value;
    }
    
    /**
     *
     * @return
     */
    public String getKeyValue() {
        return value;
    }
    
    /**
     *
     * @return
     */
    public String getKeyName() {
        return key;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return key;
    }
}
