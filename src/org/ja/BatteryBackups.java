
package org.ja;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class BatteryBackups {
    
    /** Init UPS
     * 
     * Discovers all UPS's available and initializes their values.
     * 
     * @return List of Battery Backups
     */
    public static ArrayList<BatteryBackupObject> initUPS() {
        String upower = getUPSData();
        ArrayList<BatteryBackupObject> ups = new ArrayList<>();
        String[] split = upower.split("\n\n");
        for(int i = 0; i < (split.length - 2); i++) {
            String upsString = split[i];
            int nat = upsString.indexOf("native-path:");
            int natend = upsString.indexOf("\n", nat);
            String path = upsString.substring(nat + 22, natend);
            String serial = readSerial(path);
            BatteryBackupObject b = new BatteryBackupObject(serial);
            int per = upsString.indexOf("percentage:");
            int perend = upsString.indexOf("\n", per);
            int percentage = 0;
            String percent = upsString.substring(per + 12, perend-1).trim();
            try {
                percentage = Integer.parseInt(percent);
            }catch (Exception e) {
            }
            int time = upsString.indexOf("time to empty:");
            int timeend = upsString.indexOf("\n", time);
            String timeLeft = upsString.substring(time + 14, timeend).trim();
            b.setBattery(percentage);
            b.setTimeLeft(timeLeft);
            ups.add(b);
        }
        return ups;
    }
    
    /** Update UPS - updates the percentage and time remaining from upower by serial number
     * 
     * @param batteries List of Battery Backups
     */
    public static void updateUPS(ArrayList<BatteryBackupObject> batteries) {
        String upower = getUPSData();
        ArrayList<BatteryBackupObject> ups = new ArrayList<>();
        String[] split = upower.split("\n\n");
        for(int i = 0; i < (split.length - 2); i++) {
            String upsString = split[i];
            int nat = upsString.indexOf("native-path:");
            int natend = upsString.indexOf("\n", nat);
            String path = upsString.substring(nat + 22, natend);
            String serial = readSerial(path);
            int per = upsString.indexOf("percentage:");
            int perend = upsString.indexOf("\n", per);
            double percentage = 0;
            String percent = upsString.substring(per + 12, perend-1).trim();
            try {
                percentage = Double.parseDouble(percent);
            }catch (Exception e) {}
            int time = upsString.indexOf("time to empty:");
            int timeend = upsString.indexOf("\n", time);
            String timeLeft = upsString.substring(time + 14, timeend).trim();
            BatteryBackupObject b = new BatteryBackupObject("");
            for(BatteryBackupObject a : batteries) {
                if(a.getSerial().equals(serial)) {
                    b = a;
                }
            }
            b.setBattery(percentage);
            b.setTimeLeft(timeLeft);
        }
    }
    
    private static String getUPSData() {
        String data = "";
        try {
            Process exec = Runtime.getRuntime().exec("upower --dump");
            exec.waitFor();
            Scanner reader = new Scanner(exec.getInputStream());
            while (reader.hasNextLine()) {
                data += reader.nextLine() + "\n";
            }
        } catch (IOException | InterruptedException ex) {
        }
        return data;
    }
    
    private static String readSerial(String devicePath) {
        File file = new File(devicePath);
        File serialPath = new File(file.getParentFile().getParentFile().getParentFile().toString() + "/serial");
        try {
            Scanner reader = new Scanner(serialPath);
            String serial = reader.nextLine();
            return serial.trim();
        }catch(Exception e) {
            return "";
        }
    }
}
