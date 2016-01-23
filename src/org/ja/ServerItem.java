package org.ja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class ServerItem {

    private String hostname, addr, yaml;

    private long lastComm;

    private ArrayList<Key> keys;

    public ServerItem(String host, String addr) {
        this.hostname = host;
        this.addr = addr;
        keys = new ArrayList<>();
        File templatefile = new File("template.yml");
        String template = "";
        try {
            Scanner reader = new Scanner(templatefile);
            String read = "";
            while (reader.hasNextLine()) {
                read += reader.nextLine();
            }
            template = read;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        File file = new File("servers/" + host + ".yml");
        if (!file.exists()) {
            System.out.println("Generating YAML for " + host);
            try {
                file.createNewFile();
                PrintStream out = new PrintStream(file);
                out.println(template);
                out.close();
            } catch (IOException ex) {
                System.out.println("Error creating " + file.getAbsolutePath());
            }
        } else {
            System.out.println("Found YAML for " + host);
        }
        File yamlf = new File("servers/" + host + ".yml");
        try {
            Scanner reader = new Scanner(yamlf);
            String read = "";
            while (reader.hasNextLine()) {
                read += reader.nextLine();
            }
            yaml = read;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        keys = YamlOperator.readKeys(yaml);
        for (Key k : keys) {
            System.out.println("Key: " + k.getKeyName() + " Value: " + k.getKeyValue());
        }
    }

    public String getName() {
        return hostname;
    }

    public String getAddress() {
        return addr;
    }

    public void setKey(String key, String value) {
        for (Key k : keys) {
            if (k.getKeyName().equalsIgnoreCase(key)) {
                k.updateKey(value);
                return;
            }
        }
        Key k = new Key(key, value);
        keys.add(k);
    }

    public String getKey(String key) {
        for (Key k : keys) {
            if (k.getKeyName().equalsIgnoreCase(key)) {
                return k.getKeyValue();
            }
        }
        return "";
    }
}
