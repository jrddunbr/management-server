
package org.ja;

/**
 *
 * @author jared
 */
public class MaintainerObject {
    
    private final String maintainerFirstName, maintainerLastName, maintainerEmail, maintainerCell, maintainerIRC;

    /**
     *
     * @param first
     * @param last
     * @param email
     * @param cell
     * @param irc
     */
    public MaintainerObject(String first, String last, String email, String cell, String irc) {
        maintainerFirstName = first;
        maintainerLastName = last;
        if(!email.isEmpty()) {
            maintainerEmail = email;
        }else{
            maintainerEmail = "";
        }
        if(!irc.isEmpty()) {
            maintainerIRC = irc;
        }else{
            maintainerIRC = "";
        }
        if(!cell.isEmpty()) {
            maintainerCell = cell;
        }else{
            maintainerCell = "";
        }
    }
    
    /**
     *
     * @return
     */
    public String getFirst() {
        return maintainerFirstName;
    }
    
    /**
     *
     * @return
     */
    public String getLast() {
        return maintainerLastName;
    }
    
    /**
     *
     * @return
     */
    public String getEmail() {
        return maintainerEmail;
    }
    
    /**
     *
     * @return
     */
    public String getCell() {
        return maintainerCell;
    }
    
    /**
     *
     * @return
     */
    public String getIRC() {
        return maintainerIRC;
    }
}
