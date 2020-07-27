/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Stacks {
    private SortedMap<String, Stack> stacks;
    private String gitSolutionsDir;
    private String gitDevDir;
    private String arcPath;
    private String user;
    private String pwd;    
    
    Stacks(String jsonFile) {
        stacks = new TreeMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        arcPath = "";
        user = "";
        pwd = "";

        JSONObject jobjStacks;
        String jsonString = "";
        BufferedReader in = null;
        String line;
        
        try { 
            in = new BufferedReader(new FileReader(jsonFile));
            while ((line = in.readLine()) != null) {
                // System.out.println("Gelesene Zeile: " + line);
                jsonString = jsonString.concat(line);
            }            
        } catch (FileNotFoundException ex) {    
            EloTest.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
        } catch (IOException ex) {            
            EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                }
            }
        }
        jobjStacks = new JSONObject(jsonString);        
        JSONObject[] jarrayStacks = JsonUtils.getArray(jobjStacks, "stacks");
        for(JSONObject objEloStack: jarrayStacks){
            stacks.put(objEloStack.getString("stack"), new Stack(objEloStack));
        }
        
        try {
            gitSolutionsDir = jobjStacks.getString("gitSolutionsDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitDevDir = jobjStacks.getString("gitDevDir");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobjStacks.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            user = jobjStacks.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobjStacks.getString("pwd");              
        } catch (JSONException ex) {            
        }
        
    }

    Stacks() {
        stacks = new TreeMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        arcPath = "";
        user = "";
        pwd = "";
    }

    public String getGitSolutionsDir() {
        return gitSolutionsDir;
    }
    
    public String getDevDir() {
        return gitDevDir;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPwd() {
        return pwd;
    }

    public String getArcPath() {
        return arcPath;
    }

    public Map<String, Stack> getStacks() {
      return stacks;  
    } 
    
}
