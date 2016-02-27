
package org.ja;

/**
 *
 * @author jared
 */
public class BatteryBackupObject {
    
    private String ID, Loc;
    private double percentFull;
    private String timeLeft;
    private boolean hasPower;
    private long msPowerLoss;
    private final double recommendShutdown = 50.0;
    
    public BatteryBackupObject(String serial, String location) {
        ID = serial;
        Loc = location;
        percentFull = 0.0;
        timeLeft = "";
    }
    
    public String getTimeLeft() {
        return timeLeft;
    }
    
    public double getBatteryLevel() {
        return percentFull;
    }
    
    public void setBattery(double percentage) {
        if(!(percentage > 100.0 || percentage < 0.0)) {
            percentFull = percentage;
        }
    }
    
    public void setPower(boolean hasLine) {
        if(!hasLine) {
            if(hasPower) {
                hasPower = false;
                msPowerLoss = System.currentTimeMillis();
            }
        }else{
            if(!hasPower) {
                hasPower = true;
                msPowerLoss = 0;
            }
        }
    }
    
    public boolean recommendShutdown() {
        if(!hasPower) {
            if(msPowerLoss > 3000000 || percentFull < 50)/*300000 is 5 minutes*/ {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
