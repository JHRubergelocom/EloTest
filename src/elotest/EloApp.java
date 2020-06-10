/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class EloApp {
    
    private static Map<String, String> GetUnittestApp(IXConnection ixConn) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        Sord[] sordELOappsClientInfo = RepoUtils.FindChildren(ixConn, parentId, false, true);
        String configApp = "";
        String configId = "";
        String jsonString;
        
        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = RepoUtils.DownloadDocumentToString(ixConn, s);
            jsonString = jsonString.replaceAll("namespace", "namespace1");
            JSONObject config = new JSONObject(jsonString);    
            JSONObject web = config.getJSONObject("web");                 
            String webId = web.getString("id");
            if (webId != null)
            {
                if (webId.contains("UnitTests"))
                {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = config.getString("id");
                }
            }
        }
        dicApp.put("configApp", configApp);
        dicApp.put("configId", configId);
            
        return dicApp;
    }    

    static void ShowUnittests(IXConnection ixConn, Profile profile, Profiles profiles) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();            
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetUnittestApp(ixConn);
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);                                  
    }    

}
