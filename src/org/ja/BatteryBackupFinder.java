
package org.ja;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class BatteryBackupFinder {
    
    private static String ex;
    
    public static void main(String[] args) {
        try {
        Scanner reader = new Scanner(new File("lsusb-example.txt"));
        while(reader.hasNextLine()) {
            ex += reader.nextLine() + "\n";
        }
        }catch (Exception e) {
            System.out.println("Having a hard time finding your file lsusb-example.txt");
        }
        searchForUPS(ex);
    }
    
    public static ArrayList<BatteryBackupObject> searchForUPS(String lsusb) {
        ArrayList<BatteryBackupObject> ups = new ArrayList<>();
        String[] splitList = lsusb.split("Bus ");
        for(String s:splitList) {
            if(s.contains("American Power Conversion")) {
                int serInd = s.indexOf("iSerial");
                int endl = s.indexOf('\n', serInd);
                String SerialLine = s.substring(serInd, endl);
                SerialLine = SerialLine.trim();
                int space = SerialLine.lastIndexOf(' ');
                String serial = SerialLine.substring(space);
                serial = serial.trim();
                System.out.println(serial);
            }
        }
        return ups;
    }
}
