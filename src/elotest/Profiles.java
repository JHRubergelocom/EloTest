/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Profiles {
    private Map<String, Profile> profiles;
    private String gitSolutionsDir;
    private String gitDevDir;
    private String gitUser;
    private String arcPath;
    private String user;
    private String pwd;    
    
    Profiles(String jsonFile) {
        profiles = new HashMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        gitUser = "";
        arcPath = "";
        user = "";
        pwd = "";

        JSONObject jobjProfiles;
        String jsonString = "";
        BufferedReader in = null;
        File file = new File(jsonFile); 
        String line;
        
        try { 
            in = new BufferedReader(new FileReader(jsonFile));
            while ((line = in.readLine()) != null) {
                // System.out.println("Gelesene Zeile: " + line);
                jsonString = jsonString.concat(line);
            }            
        } catch (FileNotFoundException ex) {    
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("FileNotFoundException");
            alert.setContentText("System.FileNotFoundException message: " + ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) {            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("IOException");
            alert.setContentText("System.IOException message: " + ex.getMessage());
            alert.showAndWait();            
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Achtung!");
                    alert.setHeaderText("IOException");
                    alert.setContentText("System.IOException message: " + ex.getMessage());
                    alert.showAndWait();            
                }
            }
        }
        jobjProfiles = new JSONObject(jsonString);        
        JSONObject[] jarrayProfiles = JsonUtils.getArray(jobjProfiles, "profiles");
        for(JSONObject objEloProfile: jarrayProfiles){
            profiles.put(objEloProfile.getString("name"), new Profile(objEloProfile));
        }
        
        try {
            gitSolutionsDir = jobjProfiles.getString("gitSolutionsDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitDevDir = jobjProfiles.getString("gitDevDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitUser = jobjProfiles.getString("gitUser");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobjProfiles.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            user = jobjProfiles.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobjProfiles.getString("pwd");              
        } catch (JSONException ex) {            
        }
        
    }

    Profiles() {
        profiles = new HashMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        gitUser = "";
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

    public String getGitUser() {
        return gitUser;
    }

    public Map<String, Profile> getProfiles() {
      return profiles;  
    } 
    
}
