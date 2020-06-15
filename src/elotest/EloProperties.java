/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 * @author ruberg
 */
class EloProperties  extends Properties {
    private File propertiesFile = new File("eloproperties.txt");

    public EloProperties() {
        Reader reader = null;
        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }  
            reader = new FileReader(propertiesFile);
            super.load(reader);      
        } catch (FileNotFoundException ex) {            
            EloTest.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
        } catch (IOException ex) {
            EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());            
        } finally {
            if (reader != null) {
                try {            
                    reader.close();
                } catch (IOException ex) {
                    EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());            
                }                
            }
        }
    }
    
    private void saveProperties() {
        Writer writer = null;
        try {
            writer = new FileWriter(propertiesFile);
            store(writer, "EloProperties");
        } catch (IOException ex) {
            EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        } finally {
            if (writer != null) {
                try {            
                    writer.close();
                } catch (IOException ex) {
                    EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                }                
            }
        }           
    }

    void setSelectedProfile(String name) {
        setProperty("SelectedProfile", name);   
        saveProperties();
    }

    String getSelectedProfile() {
        return getProperty("SelectedProfile");        
    }
    
    String getSelectedEloCli() {
        return getProperty("SelectedEloCli");        
    }    
    
    void setSelectedEloCli(String name) {
        setProperty("SelectedEloCli", name);   
        saveProperties();        
    }
    
    String getSelectedUnittestTools() {
        return getProperty("SelectedUnittestTools"); 
    }

    void setSelectedUnittestTools(String name) {
        setProperty("SelectedUnittestTools", name);   
        saveProperties();        
    }   
    
    String getSelectedEloServices() {
        return getProperty("SelectedEloServices"); 
    }

    void setSelectedEloServices(String name) {
        setProperty("SelectedEloServices", name);   
        saveProperties();        
    }
    
    
    void setPattern(String pattern) {
        setProperty("Pattern", pattern);   
        saveProperties();
    }
    String getPattern() {
        return getProperty("Pattern");        
    }

    void setCaseSensitiv(boolean caseSensitiv) {
        setProperty("CaseSensitiv", Boolean.toString(caseSensitiv));   
        saveProperties();
    }
    boolean getCaseSensitiv() {
        String value = getProperty("CaseSensitiv");
        if (value == null) {
            value = "false";
        }
        value = value.toLowerCase();
        return value.toLowerCase().equals("true");
    }

}

