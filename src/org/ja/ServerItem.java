package org.ja;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class ServerItem {

    private final String hostname, addr;
    private String yaml;
    private long lastComm;
    private final ArrayList<Key> keys;
    private final ArrayList<MaintainerObject> maintainers;
    private boolean up;
    private String htmlString = "";

    public ServerItem(String host, String addr) {
        up = false;
        lastComm = 0;
        this.hostname = host;
        this.addr = addr;
        keys = new ArrayList<>();
        maintainers = new ArrayList<>();
    }
    
    public boolean fetchMaintainers() {
        maintainers.clear();
        File mainfile = new File("servers/" + hostname + ".txt");
        if(!mainfile.exists()) {
            return false;
        }
        String first, last, email, cell;
        try {
            Scanner reader = new Scanner(mainfile);
            if(reader.hasNextLine()) {
                first = reader.nextLine();
            }else{
                return false;
            }
            if(reader.hasNextLine()) {
                last = reader.nextLine();
            }else{
                return false;
            }
            if(reader.hasNextLine()) {
                email = reader.nextLine();
            }else{
                maintainers.add(new MaintainerObject(first, last));
                return true;
            }
            if(reader.hasNextLine()) {
                cell = reader.nextLine();
            }else{
                maintainers.add(new MaintainerObject(first, last, email));
                return true;
            }
            maintainers.add(new MaintainerObject(first, last, email, cell));
            return true;
        }catch (Exception e) {
            System.out.println("Could not open maintainers file for " + hostname + ".");
        }
        return false;
    }
    
    public ArrayList<MaintainerObject> getMaintainers() {
        return maintainers;
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
    
    public boolean hasKey(String key) {
        for(Key k : keys) {
            if(k.getKeyName().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Key> getKeys() {
        return keys;
    }
    
    public void lastComm(long now) {
        this.lastComm = now;
    }
    
    public long lastComm() {
        return this.lastComm;
    }
    
    public boolean up() {
        long diff = Math.abs(lastComm - System.currentTimeMillis());
        if(diff < 60000) {
            this.up = true;
        }else{
            this.up = false;
            keys.clear();
        }
        return this.up;
    }
    
    public void up(boolean now) {
        this.up = now;
    }
    
    public void setHTMLString(String html) {
        htmlString = html;
    }
    
    public String getHtML() {
        return htmlString;
    }
}
