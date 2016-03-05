
package org.ja;

/**
 *
 * @author jared
 */
public class MaintainerObject {
    
    private final String maintainerFirstName, maintainerLastName, maintainerEmail, maintainerCell, maintainerIRC;
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
    
    public String getFirst() {
        return maintainerFirstName;
    }
    
    public String getLast() {
        return maintainerLastName;
    }
    
    public String getEmail() {
        return maintainerEmail;
    }
    
    public String getCell() {
        return maintainerCell;
    }
    
    public String getIRC() {
        return maintainerIRC;
    }
}
