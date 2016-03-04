
package org.ja;

/**
 *
 * @author jared
 */
public class MaintainerObject {
    
    private String maintainerFirstName, maintainerLastName, maintainerEmail, maintainerCell;
    
    public MaintainerObject(String first, String last) {
        maintainerFirstName = first;
        maintainerLastName = last;
        maintainerEmail = "";
        maintainerCell = "";
    }
    
    public MaintainerObject(String first, String last, String email) {
        maintainerFirstName = first;
        maintainerLastName = last;
        if(!email.isEmpty()) {
            maintainerEmail = email;
        }else{
            maintainerEmail = "";
        }
        maintainerCell = "";
    }
    
    public MaintainerObject(String first, String last, String email, String cell) {
        maintainerFirstName = first;
        maintainerLastName = last;
        if(!email.isEmpty()) {
            maintainerEmail = email;
        }else{
            maintainerEmail = "";
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
}
