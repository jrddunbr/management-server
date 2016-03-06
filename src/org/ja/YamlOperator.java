package org.ja;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class YamlOperator {

    /**
     *
     * @param yaml
     * @return
     */
    public static ArrayList<Key> readKeys(String yaml) {
        ArrayList<Key> keys = new ArrayList<>();
        int keysStart = yaml.indexOf("keys:") + 6;
        if(yaml.length() < keysStart) {
            return keys;
        }
        Scanner reader = new Scanner(yaml.substring(keysStart, yaml.length()));
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            if (line.charAt(0) != ' ') {
                break;
            }
            String[] parts = line.split(":");
            String key = parts[0].trim();
            String value = "";
            if (parts.length > 1) {
                value = parts[1].trim();
            }
            Key k = new Key(key, value);
            keys.add(k);
            
        }
        return keys;
    }

    /**
     *
     * @param keys
     * @return
     */
    public static String makeKeys(ArrayList<Key> keys) {
        String ret = "keys:\n";
        for (Key k : keys) {
            ret += "    " + k.getKeyName() + ": " + k.getKeyValue() + "\n";
        }
        return ret;
    }

    /**
     *
     * @param yaml
     * @return
     */
    public static String removeKeys(String yaml) {
        int keysStart = yaml.indexOf("keys:");
        Scanner reader = new Scanner(yaml.substring(keysStart + 6, yaml.length()));
        String end = "";
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            try {
                if (line.charAt(0) != '\t' || line.charAt(0) != ' ') {
                    end = line;
                }
            } catch (Exception e) {
                break;
            }
        }
        int keysEnd = yaml.indexOf(end);
        String out = yaml.substring(0, keysStart) + yaml.substring(keysEnd, yaml.length());
        return out;
    }
}
