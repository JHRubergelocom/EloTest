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
class EloCommand {

    private String name;
    private String cmd;
    private String workspace;
    private String version;
    

    EloCommand(JSONObject obj) {
        name = "";
        cmd = "";
        workspace = "";
        version = "";
        
        try {
            name = obj.getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            cmd = obj.getString("cmd");      
        } catch (JSONException ex) {            
        }
        try {
            workspace = obj.getString("workspace");      
        } catch (JSONException ex) {            
        }
        try {
            version = obj.getString("version");      
        } catch (JSONException ex) {            
        }
    }    
    EloCommand() {
        name = "";
        cmd = "";
        workspace = "";
        version = "";
    }
    
    public String getName() {
        return name;
    }
    
    public String getCmd() {
        return cmd;
    }
    
    public String getWorkspace() {
        return workspace;
    }
    
    public String getVersion() {
        return version;
    }
    
}
