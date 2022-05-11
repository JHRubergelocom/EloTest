/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class EloPackage {

    private String name;
    private Map<String, String> folders;
    
    EloPackage(JSONObject obj) throws JSONException {
        name = "";
        folders = new HashMap<>();
        
        try {
            name = obj.getString("name");            
        } catch (JSONException ex) {            
        }
        String[] jarrayfolders = JsonUtils.getStringArray(obj, "folders");
        for(String folder: jarrayfolders){
            folders.put(folder, folder);
        }
    }    
    EloPackage() {
        name = "";
        folders = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public Map<String, String> getFolders() {
        return folders;
    }
        
}
