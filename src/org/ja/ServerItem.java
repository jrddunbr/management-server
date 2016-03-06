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

    /** Server Items contain server information strings, keys, and maintainers
     *
     * @param host Server name
     * @param addr Server IP address
     */
    public ServerItem(String host, String addr) {
        up = false;
        lastComm = 0;
        this.hostname = host;
        this.addr = addr;
        keys = new ArrayList<>();
        maintainers = new ArrayList<>();
    }

    /** Fetch Maintainers from server files
     *
     * @return true if success, false if failed
     */
    public boolean fetchMaintainers() {
        maintainers.clear();
        File mainfile = new File("servers/" + hostname + ".txt");
        if (!mainfile.exists()) {
            return false;
        }
        String first, last, email, cell, irc;
        try {
            Scanner reader = new Scanner(mainfile);
            while (reader.hasNextLine()) {
                try {
                    first = reader.nextLine().substring(2);
                    last = reader.nextLine().substring(2);
                    email = reader.nextLine().substring(2);
                    irc = reader.nextLine().substring(2);
                    cell = reader.nextLine().substring(2);
                    maintainers.add(new MaintainerObject(first, last, email, cell, irc));
                } catch (Exception e) {
                    
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Could not open maintainers file for " + hostname + ".");
        }
        return false;
    }

    /** Get maintainers
     *
     * @return List of maintainer objects for server
     */
    public ArrayList<MaintainerObject> getMaintainers() {
        return maintainers;
    }

    /** Get name of Server
     *
     * @return Server Hostname
     */
    public String getName() {
        return hostname;
    }

    /** Get IP of Server
     *
     * @return Server IP address
     */
    public String getAddress() {
        return addr;
    }

    /** Set Key - add or update key to key database
     *
     * @param key name of the key
     * @param value value that the key contains
     */
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

    /** Get Key from key database
     *
     * @param key expected key name
     * @return value of the key, or if the key doesn't exist, ""
     */
    public String getKey(String key) {
        for (Key k : keys) {
            if (k.getKeyName().equalsIgnoreCase(key)) {
                return k.getKeyValue();
            }
        }
        return "";
    }

    /** Has Key - Is there a key by this name in the database
     *
     * @param key Key Name to search for (non case sensitive)
     * @return True if the key exists.
     */
    public boolean hasKey(String key) {
        for (Key k : keys) {
            if (k.getKeyName().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    /** Get Keys - Get the entire database of keys
     *
     * @return key database
     */
    public ArrayList<Key> getKeys() {
        return keys;
    }

    public void lastComm(long now) {
        this.lastComm = now;
    }

    /** Last Comm - This will return the last millis that a communication was made
     * 
     * @return millis that call was made on
     */
    public long lastComm() {
        return this.lastComm;
    }

    /** up? This will determine if the server is up, else clear the keys
     *
     * @return if up, true
     */
    public boolean up() {
        long diff = Math.abs(lastComm - System.currentTimeMillis());
        if (diff < 60000) {
            this.up = true;
        } else {
            this.up = false;
            keys.clear();
        }
        return this.up;
    }

    /** up! Call this if the server is up (or down)
     *
     * @param now state of the server
     */
    public void up(boolean now) {
        this.up = now;
    }

    /** Set the HTML string for the server (set temporary datastructure)
     *
     * @param html
     */
    public void setHTMLString(String html) {
        htmlString = html;
    }

    /** Get the HTML string to prepare for web transmission (from temporary datastructure)
     *
     * @return
     */
    public String getHtML() {
        return htmlString;
    }
}
