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
public class Solution {
    private String name;
    private Map<String, EloPackage> eloPackages;
    private Map<String, EloCommand> eloCommands;

    Solution(JSONObject obj) {
        name = "";
        eloPackages = new HashMap<>();
        eloCommands = new HashMap<>();
        try {
            name = obj.getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject[] jarrayEloPackages = JsonUtils.getArray(obj, "eloPackages");
            for(JSONObject objEloPackage: jarrayEloPackages){
                eloPackages.put(objEloPackage.getString("name"), new EloPackage(objEloPackage));                
            }
        } catch (JSONException ex) {  
            eloPackages = new HashMap<>();            
        }
        try {
            JSONObject[] jarrayEloCommands = JsonUtils.getArray(obj, "eloCommands");
            for(JSONObject objEloCommand: jarrayEloCommands){
                eloCommands.put(objEloCommand.getString("name"), new EloCommand(objEloCommand));
            }
        } catch (JSONException ex) {    
            eloCommands = new HashMap<>();
        }        
    }
    
    public String getName() {
        return name;
    }
    
    Map<String, EloPackage> getEloPackages() {
        return eloPackages;
    }

    Map<String, EloCommand> getEloCommands() {
        return eloCommands;
    }
    
    public String getIxUrl(String gitUser) {   
        return  "http://" + getStack(gitUser) + ".dev.elo/ix-Solutions/ix";
    }
    
    public String getStack(String gitUser) {   
        if(name.contains("playground")) {
            return "playground";
        }
        return  gitUser + "-" + name;
    }
    
    public String getWorkingDir(String gitSolutionsDir) {
        if(name.contentEquals("recruiting")) {
            return gitSolutionsDir + "\\hr_" + name + ".git";                        
        } else {
            return gitSolutionsDir + "\\" + name + ".git";            
        }        
    }
    
    EloCommand getEloCommand(String commandName) {
        return getEloCommands().get(commandName);
    }

    
}
