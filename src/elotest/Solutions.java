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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Solutions {
    private SortedMap<String, Solution> solutions;
    private String gitSolutionsDir;
    private String gitDevDir;
    private String gitUser;
    private String arcPath;
    private String user;
    private String pwd;    
    
    Solutions(String jsonFile) {
        solutions = new TreeMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        gitUser = "";
        arcPath = "";
        user = "";
        pwd = "";

        JSONObject jobjSolutions;
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
        jobjSolutions = new JSONObject(jsonString);        
        JSONObject[] jarraySolutions = JsonUtils.getArray(jobjSolutions, "solutions");
        for(JSONObject objEloSolution: jarraySolutions){
            solutions.put(objEloSolution.getString("name"), new Solution(objEloSolution));
        }
        
        try {
            gitSolutionsDir = jobjSolutions.getString("gitSolutionsDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitDevDir = jobjSolutions.getString("gitDevDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitUser = jobjSolutions.getString("gitUser");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobjSolutions.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            user = jobjSolutions.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobjSolutions.getString("pwd");              
        } catch (JSONException ex) {            
        }
        
    }

    Solutions() {
        solutions = new TreeMap<>();
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

    public String getArcPath() {
        return arcPath;
    }

    public Map<String, Solution> getSolutions() {
      return solutions;  
    } 
    
}
