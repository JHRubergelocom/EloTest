/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class EloPackage {

    private String name;
    private String folder;
    
    EloPackage(JSONObject obj) {
        name = "";
        folder = "";
        
        try {
            name = obj.getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            folder = obj.getString("folder");      
        } catch (JSONException ex) {            
        }
    }    
    EloPackage() {
        name = "";
        folder = "";
    }
    
    public String getName() {
        return name;
    }
    
    public String getFolder() {
        return folder;
    }
    
    
}
