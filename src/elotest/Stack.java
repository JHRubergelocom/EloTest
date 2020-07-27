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
public class Stack {
    private String solution;
    private String stack;
    private Map<String, EloPackage> eloPackages;
    private Map<String, EloCommand> eloCommands;

    Stack(JSONObject obj) {
        solution = "";
        stack = "playground";
        eloPackages = new HashMap<>();
        eloCommands = new HashMap<>();
        try {
            solution = obj.getString("solution");            
        } catch (JSONException ex) {            
        }
        try {
            stack = obj.getString("stack");            
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
    
    public String getSolution() {
        return solution;
    }
    
    public String getStack() {
        return stack;
    }
    
    Map<String, EloPackage> getEloPackages() {
        return eloPackages;
    }

    Map<String, EloCommand> getEloCommands() {
        return eloCommands;
    }
    
    public String getIxUrl() {   
        return  "http://" + getStack() + ".dev.elo/ix-Solutions/ix";
    }
    
    public String getWorkingDir(String gitSolutionsDir) {
        if(solution.contentEquals("recruiting")) {
            return gitSolutionsDir + "\\hr_" + solution + ".git";                        
        } else {
            return gitSolutionsDir + "\\" + solution + ".git";            
        }        
    }
    
    EloCommand getEloCommand(String commandName) {
        return getEloCommands().get(commandName);
    }

    
}
