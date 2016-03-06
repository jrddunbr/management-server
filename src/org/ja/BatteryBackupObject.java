
package org.ja;

/** Battery Backup Object - Contains all the information about a battery backup.
 *
 * @author jared
 */
public class BatteryBackupObject {
    
    private String ID;
    private double percentFull;
    private String timeLeft;
    private boolean hasPower;
    private long msPowerLoss;
    private final double recommendShutdown = 50.0;
    
    /** Create a battery backup object. We are expecting a serial of the device for ID purposes
     *
     * @param serial serial number of the battery backup
     */
    public BatteryBackupObject(String serial) {
        ID = serial;
        percentFull = 0.0;
        timeLeft = "";
    }
    
    /** Set the time left on the battery backup
     *
     * @param left time left string
     */
    public void setTimeLeft(String left) {
        timeLeft = left;
    }
    
    /** Get the time left on the battery backup
     *
     * @return time left string
     */
    public String getTimeLeft() {
        return timeLeft;
    }
    
    /** Get the battery level (percentage)
     *
     * @return battery level percentage
     */
    public double getBatteryLevel() {
        return percentFull;
    }
    
    /** Set Battery level
     *
     * @param percentage battery level percentage
     */
    public void setBattery(double percentage) {
        if(!(percentage > 100.0 || percentage < 0.0)) {
            percentFull = percentage;
        }
    }
    
    /** Set if the battery backup is connected to line voltage
     *
     * @param hasLine True if there is line voltage
     */
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
    
    /** Should we recommend shutdowns on this particular battery backup?
     *
     * @return True if we recommend shutdowns now, otherwise false
     */
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
    
    /** Get Serial
     *
     * @return Serial number of the battery backup
     */
    public String getSerial() {
        return ID;
    }
    
    /** toString
     *
     * @return ID battery% time-left
     */
    @Override
    public String toString() {
        return ID + " " + percentFull + timeLeft;
    }
}

