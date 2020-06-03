/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import com.google.gson.Gson;
import de.elo.ix.client.DocMask;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class JsonUtils {
    public static JSONObject[] getArray (JSONObject jobj, String key) {
        JSONArray jarr = jobj.getJSONArray(key);
        JSONObject jobjs[] = new JSONObject[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jobjs[i] = jarr.getJSONObject(i);
        } 
        return jobjs;
    }
    public static String[] getStringArray (JSONObject jobj, String key) {
        JSONArray jarr = jobj.getJSONArray(key);
        String jstrings[] = new String[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jstrings[i] = jarr.getString(i);
        } 
        return jstrings;
    }

    static String getJsonString(DocMask dm) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(dm);
        return jsonString;        
    }
    
    static DocMask getDocMask(String jsonString) {
        Gson gson = new Gson();
        DocMask dm = gson.fromJson(jsonString, DocMask.class);
        return dm;
    }
    
    static String formatJsonString(String jsonText) {
        try {
            JSONObject obj = new JSONObject (jsonText);
            return obj.toString(2);
        } catch (JSONException ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("IOException");
            alert.setContentText("System.IOException message: " + ex.getMessage());
            alert.showAndWait();            
        }
        return jsonText;
    }
}
