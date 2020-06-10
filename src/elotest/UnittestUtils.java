/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import byps.RemoteException;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author ruberg
 */
class UnittestUtils {
    
    private static boolean Match(String uName, EloPackage eloPackage, String[] jsTexts) {
        for (String jsText : jsTexts) {
            String[] jsLines = jsText.split("\n");
            for (String line : jsLines) {
                if (line.contains(eloPackage.getName())) {
                    if (line.contains(uName)) {
                        return true;
                    }
                }
            }
        }
        return false;                
    }
    
    private static SortedMap<String, Boolean> GetRFs(IXConnection ixConn, String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/IndexServer Scripting Base";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/IndexServer Scripting Base/_ALL/business_solutions";
        }
        Sord[] sordRFInfo = RepoUtils.FindChildren(ixConn, parentId, true, true);
        SortedMap<String, Boolean> dicRFs = new TreeMap<>();
        
        for(Sord s : sordRFInfo) {  
           String jsText = RepoUtils.DownloadDocumentToString (ixConn, s);  
           jsText = jsText.replaceAll("\b", "");
           jsText = jsText.replaceAll("\n", " ");
           String[] jsLines = jsText.split(" ");
            for (String line : jsLines) {
                if (line.contains("RF_")) {
                    if (eloPackage.getName().equals("") || (!eloPackage.getName().equals("") && line.contains(eloPackage.getName()))) {
                        String rfName = line;
                        String[] rfNames = rfName.split("\\(");
                        rfName = rfNames[0];
                        if (!line.endsWith(",")
                                && rfName.startsWith("RF_") && !line.contains("RF_ServiceBaseName") && !line.endsWith(".")
                                && !line.contains("RF_FunctionName") && !line.contains("RF_MyFunction")
                                && !line.contains("RF_custom_functions_MyFunction") && !line.contains("RF_custom_services_MyFunction")
                                && !line.contains("RF_sol_function_FeedComment}.") && !line.contains("RF_sol_my_actions_MyAction")
                                && !line.contains("RF_sol_service_ScriptVersionReportCreate")) {
                            if (!dicRFs.containsKey(rfName)) {
                                boolean match = Match(rfName, eloPackage, jsTexts);
                                dicRFs.put(rfName, match);
                            }
                        }                        
                    }
                }                    
            }
           
        }                
        return dicRFs;
    }
    
    private static SortedMap<String, Boolean> GetRules(IXConnection ixConn, String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/ELOas Base/Direct";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/ELOas Base/Direct";
        }
        Sord[] sordRuleInfo = RepoUtils.FindChildren(ixConn, parentId, true, true);
        SortedMap<String, Boolean> dicRules = new TreeMap<>();
        for(Sord s : sordRuleInfo) {            
            try {
                String xmlText = RepoUtils.DownloadDocumentToString (ixConn, s);             
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                InputSource source = new InputSource(new StringReader(xmlText));            
                String rulesetname = xpath.evaluate("ruleset/base/name", source);
                if (!dicRules.containsKey(rulesetname)) {
                    boolean match = Match(rulesetname, eloPackage, jsTexts);
                    dicRules.put(rulesetname, match);
                }
            } catch (XPathExpressionException ex) {
                System.err.println("XPathExpressionException: " +  ex.getMessage()); 
            }
        }
        return dicRules;        
    }

    private static SortedMap<String, Boolean> GetActionDefs(IXConnection ixConn, String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/Action definitions";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Action definitions";
        }

        Sord[] sordActionDefInfo = RepoUtils.FindChildren(ixConn, parentId, true, true);
        SortedMap <String, Boolean> dicActionDefs = new TreeMap<>();
        
        for(Sord s : sordActionDefInfo) {
            String actionDef = s.getName();
            String[] rf = actionDef.split("\\.");
            actionDef = rf[rf.length-1];
            actionDef = "actions." + actionDef;
            if (!dicActionDefs.containsKey(actionDef)) {
                boolean match = Match(actionDef, eloPackage, jsTexts);
                if(eloPackage.getName().equals("privacy") || eloPackage.getName().equals("pubsec")) {
                    match = true;
                }                
                dicActionDefs.put(actionDef, match);
            }
        }
        return dicActionDefs;        
    }

    private static SortedMap<String, SortedMap<String, Boolean>> GetTestedLibs(SortedMap<String, List<String>> jsTexts) {
        SortedMap<String, SortedMap<String, Boolean>> dicTestedLibs = new TreeMap<>();
        SortedMap<String, Boolean> dicMethods;
        String className = "";
        String method = "";
        for (Map.Entry<String, List<String>> entryJsText : jsTexts.entrySet()) {
            if (entryJsText.getKey().contains("[action]")) {
                continue;
            }
            if (entryJsText.getKey().contains("[function]")) {
                continue;
            }
            if (entryJsText.getKey().contains("[service]")) {
                continue;
            }            
            
            for (String line : entryJsText.getValue()) {
                if (line.contains("className")){
                    String[] words = line.split(":");
                    words = words[1].split("\"");
                    className = words[1];
                    className = className.trim();                    
                }
                
                if (line.contains("params: { method:")) {
                    continue;
                }
                
                if (line.contains("method")){
                    String[] words = line.split(":");
                    words = words[1].split("\"");
                    method = words[1];
                    method = method.trim();                                        
                }
                // TODO
                if (className.equals("sol.common.HttpUtils")) {
                    if (method.contains("checkClientTrusted: function (chain, authType)")) {
                        continue;
                    }
                    if (method.contains("checkServerTrusted: function (chain, authType)")) {
                        continue;
                    }
                    if (method.contains("getAcceptedIssuers: function ()")) {
                        continue;
                    }
                    if (method.contains("verify: function (hostname, session)")) {
                        continue;
                    }                    
                }                
                // TODO
                
                if (!dicTestedLibs.containsKey(className)) {
                    dicTestedLibs.put(className, new TreeMap<>());                    
                }
                dicMethods = dicTestedLibs.get(className);
                if (!dicMethods.containsKey(method)) {
                    dicMethods.put(method, false);                    
                }
            }
        }
        return dicTestedLibs;        
    }

    private static SortedMap<String, SortedMap<String, List<String>>> GetLibs(IXConnection ixConn, EloPackage eloPackage, String libDir) {
        if (eloPackage.getFolder().equals("")) {
            return new TreeMap<>();        
        }        
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/" + libDir;
        Sord[] sordRFInfo = RepoUtils.FindChildren(ixConn, parentId, true, false);
        SortedMap<String, SortedMap<String, List<String>>> dicLibs = new TreeMap<>();        
        for(Sord s : sordRFInfo) {  
           String libName = ""; 
           List<String> jsLines = RepoUtils.DownloadDocumentToList (ixConn, s);  
            for (String line : jsLines) {
                if (line.contains("sol.define(")) {
                    libName = line;                    
                    if (libName.split("\"").length > 1) {
                        libName = libName.split("\"")[1].trim();                    
                        if (!dicLibs.containsKey(libName)) {
                            dicLibs.put(libName, new TreeMap<>());
                        }                        
                    }
                }
                if (dicLibs.containsKey(libName)) {
                    if (line.contains("function") && line.contains(":") && line.contains("(") && line.contains(")")&& !line.contains("*")){
                        String fName = line;
                        if (fName.split(":").length > 0) {
                            fName = fName.split(":")[0].trim();
                            List<String> params = new ArrayList<>();                            
                            String pNames = line;
                            pNames = pNames.trim();
                            pNames = pNames.split("\\(")[1];
                            pNames = pNames.split("\\)")[0];
                            String [] ps = pNames.split(",");
                            for (String p: ps) {
                                params.add(p.trim());
                            } 
                            dicLibs.get(libName).put(fName, params);                            
                        }                        
                    }                       
                }                
            }           
        }                
        return dicLibs;        
    }
    
    private static SortedMap<String, SortedMap<String, Boolean>> GetLibsMatch(IXConnection ixConn, SortedMap<String, List<String>> jsTexts, EloPackage eloPackage, String libDir) {
        SortedMap<String, SortedMap<String, Boolean>> dicTestedLibs = GetTestedLibs(jsTexts);
        SortedMap<String, Boolean> dicMethods;
        SortedMap<String, SortedMap<String, Boolean>> dicLibsMatch = new TreeMap<>();
        SortedMap<String, Boolean> dicMethodsMatch;
        SortedMap<String, SortedMap<String, List<String>>> dicLibs = GetLibs(ixConn, eloPackage, libDir);    
        
        for (Map.Entry<String, SortedMap<String, List<String>>> entryClass : dicLibs.entrySet()) {
            dicMethodsMatch = new TreeMap<>();
            String className = entryClass.getKey();
            // TODO
            if (className.equals("sol.common.ActionBase")) {
                className = "sol.unittest.ActionBase";
            }
            if (className.equals("sol.common.Map")) {
                className = "sol.unittest.Map";
            }
            if (className.equals("sol.common.ix.ActionBase")) {
                className = "sol.unittest.ix.ActionBase";
            }
            if (className.equals("sol.common.ix.DataCollectorBase")) {
                className = "sol.unittest.ix.DataCollectorBase";
            }
            if (className.equals("sol.common.ix.DynKwlSearchIterator")) {
                className = "sol.unittest.ix.DynKwlSearchIterator";
            }
            if (className.equals("sol.common.ix.FunctionBase")) {
                className = "sol.unittest.ix.FunctionBase";
            }
            if (className.equals("sol.common.ix.ServiceBase")) {
                className = "sol.unittest.ix.ServiceBase";
            }
            if (className.equals("sol.common.as.ActionBase")) {
                className = "sol.unittest.as.ActionBase";
            }
            if (className.equals("sol.common.as.FunctionBase")) {
                className = "sol.unittest.as.FunctionBase";
            }
            if (className.equals("sol.common.as.OfficeDocument")) {
                className = "sol.common.as.WordDocument";
            }
            
            if (className.equals("sol.pubsec.ix.DynamicRoutine")) {
                continue;
            }   

            
            // TODO
            for (Map.Entry<String, List<String>> entryMethod : entryClass.getValue().entrySet()) {
                String method = entryMethod.getKey();

                if (method.contains("me.$className")) {
                    continue;
                } 
                
                if (method.trim().length() == 0) {
                    continue;
                }
                
                if (method.contains("!name")) {
                    continue;
                }                

                if (method.contains("Handlebars")) {
                    continue;
                }                

                if (method.contains("me.handleException")) {
                    continue;
                }                

                if (method.contains("me.logger")) {
                    continue;
                }   
                
                if (method.contains("sol.common.IxUtils")) {
                    continue;
                }   

                if (className.equals("sol.common.HttpUtils")) {
                    if (method.contains("checkClientTrusted")) {
                        continue;
                    }
                    if (method.contains("checkServerTrusted")) {
                        continue;
                    }
                    if (method.contains("getAcceptedIssuers")) {
                        continue;
                    }
                    if (method.contains("verify")) {
                        continue;
                    }                    
                }
                
                if (className.equals("sol.common.WfUtils")) {
                    if (method.equals("getTasks")) {
                        continue;
                    }
                }
                
                if (className.equals("sol.connector_xml.Importer")) {
                    if (method.equals("resolveEntity")) {
                        continue;
                    }
                }
                
                if (className.equals("sol.common.as.DocumentGenerator")) {
                    if (method.equals("collect")) {
                        continue;
                    }
                }
                
                if (className.equals("sol.invoice.as.InvoiceXmlImporter")) {
                    if (method.equals("accept")) {
                        continue;
                    }
                }
                
                if (className.equals("sol.contact.as.actions.CreateContactReport")) {
                    if (method.equals("compareFct")) {
                        continue;
                    }
                }  
                
                if (className.equals("sol.pubsec.as.actions.CreateFileReport")) {
                    if (method.equals("compareFct")) {
                        continue;
                    }
                }  
                
                if (className.equals("sol.pubsec.as.actions.CreateFilingplanReport")) {
                    if (method.equals("compareFct")) {
                        continue;
                    }
                }  
                
                if (className.equals("sol.pubsec.as.functions.CreateFileDeletionReport")) {
                    if (method.equals("compareFct")) {
                        continue;
                    }
                }  
                
                if (className.equals("sol.visitor.as.actions.CreateVisitorList")) {
                    if (method.equals("compareFct")) {
                        continue;
                    }
                }  
                
                // TODO
                boolean match = false;
                if(dicTestedLibs.containsKey(className)){
                    dicMethods = dicTestedLibs.get(className);
                    if(dicMethods.containsKey(method)) {
                        match = true;
                    }
                }    
                // TODO
                dicMethodsMatch.put(method, match);                    
            }
            dicLibsMatch.put(entryClass.getKey(), dicMethodsMatch);
        }        
        return dicLibsMatch;        
    }
    
    private static String CreateReportMatchUnittest(SortedMap<String, Boolean> dicRFs, SortedMap<String, Boolean> dicASDirectRules, SortedMap<String, Boolean> dicActionDefs, SortedMap<String, SortedMap<String, Boolean>> dicLibAlls, SortedMap<String, SortedMap<String, Boolean>> dicLibRhinos, SortedMap<String, SortedMap<String, Boolean>> dicLibIndexServerScriptingBases, SortedMap<String, SortedMap<String, Boolean>> dicLibELOasBases) {
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead("Register Functions matching Unittest");
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        List<String> cols = new ArrayList<>();
        cols.add("RF");
        cols.add("Unittest");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicRFs.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);
        }
        String htmlTable = Http.CreateHtmlTable("Register Functions matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("AS Direct Rule");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicASDirectRules.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("AS Direct Rules matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("Action Definition");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicActionDefs.entrySet()) {
            List<String> row = new ArrayList();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("Action Definitions matching Unittest", cols, rows);
        htmlBody += htmlTable;
        
        cols = new ArrayList<>();
        cols.add("Class");
        cols.add("Method");
        cols.add("Unittest");
        rows = new ArrayList<>();        
        for (Map.Entry<String, SortedMap<String, Boolean>> entryClass : dicLibAlls.entrySet()) {
            for (Map.Entry<String, Boolean> entryMethod : entryClass.getValue().entrySet()) {
                List<String> row = new ArrayList();            
                row.add(entryClass.getKey());
                row.add(entryMethod.getKey());
                row.add(entryMethod.getValue().toString());
                rows.add(row);            
            }
        }        
        htmlTable = Http.CreateHtmlTable("All lib matching Unittest", cols, rows);
        htmlBody += htmlTable;
        
        rows = new ArrayList<>();        
        for (Map.Entry<String, SortedMap<String, Boolean>> entryClass : dicLibRhinos.entrySet()) {
            for (Map.Entry<String, Boolean> entryMethod : entryClass.getValue().entrySet()) {
                List<String> row = new ArrayList();            
                row.add(entryClass.getKey());
                row.add(entryMethod.getKey());
                row.add(entryMethod.getValue().toString());
                rows.add(row);            
            }
        }        
        htmlTable = Http.CreateHtmlTable("All Rhino lib matching Unittest", cols, rows);
        htmlBody += htmlTable;
        
        rows = new ArrayList<>();        
        for (Map.Entry<String, SortedMap<String, Boolean>> entryClass : dicLibIndexServerScriptingBases.entrySet()) {
            for (Map.Entry<String, Boolean> entryMethod : entryClass.getValue().entrySet()) {
                List<String> row = new ArrayList();            
                row.add(entryClass.getKey());
                row.add(entryMethod.getKey());
                row.add(entryMethod.getValue().toString());
                rows.add(row);            
            }
        }        
        htmlTable = Http.CreateHtmlTable("IndexServer Scripting Base lib matching Unittest", cols, rows);
        htmlBody += htmlTable;
        
        rows = new ArrayList<>();        
        for (Map.Entry<String, SortedMap<String, Boolean>> entryClass : dicLibELOasBases.entrySet()) {
            for (Map.Entry<String, Boolean> entryMethod : entryClass.getValue().entrySet()) {
                List<String> row = new ArrayList();            
                row.add(entryClass.getKey());
                row.add(entryMethod.getKey());
                row.add(entryMethod.getValue().toString());
                rows.add(row);            
            }
        }        
        htmlTable = Http.CreateHtmlTable("ELOas Base/OptionalJsLibs lib matching Unittest", cols, rows);
        htmlBody += htmlTable;        

        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        return htmlDoc;        
    }

    static void ShowReportMatchUnittest(IXConnection ixConn, Profile profile) {
        List<EloPackage> eloPackages = profile.getEloPackages();
        
        try {
            String[] jsTexts = RepoUtils.LoadTextDocs(ixConn, "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests");   
            SortedMap<String, List<String>> jsTextsSortedMap = RepoUtils.LoadTextDocsToSortedMap(ixConn, "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests");   
            
            SortedMap<String, Boolean> dicRFs = new TreeMap<>();
            SortedMap<String, Boolean> dicASDirectRules = new TreeMap<>();
            SortedMap<String, Boolean> dicActionDefs = new TreeMap<>();
            SortedMap<String, SortedMap<String, Boolean>> dicLibAlls = new TreeMap<>();
            SortedMap<String, SortedMap<String, Boolean>> dicLibRhinos = new TreeMap<>();
            SortedMap<String, SortedMap<String, Boolean>> dicLibIndexServerScriptingBases = new TreeMap<>();
            SortedMap<String, SortedMap<String, Boolean>> dicLibELOasBases = new TreeMap<>();            
            
            if (eloPackages.isEmpty()) {                
                dicRFs = GetRFs(ixConn, jsTexts, new EloPackage()); 
                dicASDirectRules = GetRules(ixConn, jsTexts, new EloPackage());
                dicActionDefs = GetActionDefs(ixConn, jsTexts, new EloPackage()); 
                dicLibAlls = GetLibsMatch(ixConn, jsTextsSortedMap, new EloPackage(), "All"); 
                dicLibRhinos = GetLibsMatch(ixConn, jsTextsSortedMap, new EloPackage(), "All Rhino"); 
                dicLibIndexServerScriptingBases = GetLibsMatch(ixConn, jsTextsSortedMap, new EloPackage(), "IndexServer Scripting Base"); 
                dicLibELOasBases = GetLibsMatch(ixConn, jsTextsSortedMap, new EloPackage(), "ELOas Base/OptionalJsLibs"); 
            } else {
                for (EloPackage eloPackage : eloPackages) {
                    SortedMap<String, Boolean> dicRF = GetRFs(ixConn, jsTexts, eloPackage);        
                    SortedMap<String, Boolean> dicASDirectRule = GetRules(ixConn, jsTexts, eloPackage);
                    SortedMap<String, Boolean> dicActionDef = GetActionDefs(ixConn, jsTexts, eloPackage);
                    SortedMap<String, SortedMap<String, Boolean>> dicLibAll = GetLibsMatch(ixConn, jsTextsSortedMap, eloPackage, "All");
                    SortedMap<String, SortedMap<String, Boolean>> dicLibRhino = GetLibsMatch(ixConn, jsTextsSortedMap, eloPackage, "All Rhino");
                    SortedMap<String, SortedMap<String, Boolean>> dicLibIndexServerScriptingBase = GetLibsMatch(ixConn, jsTextsSortedMap, eloPackage, "IndexServer Scripting Base");
                    SortedMap<String, SortedMap<String, Boolean>> dicLibELOasBase = GetLibsMatch(ixConn, jsTextsSortedMap, eloPackage, "ELOas Base/OptionalJsLibs");                                
                    dicRFs.putAll(dicRF);
                    dicASDirectRules.putAll(dicASDirectRule);
                    dicActionDefs.putAll(dicActionDef);
                    dicLibAlls.putAll(dicLibAll);
                    dicLibRhinos.putAll(dicLibRhino);
                    dicLibIndexServerScriptingBases.putAll(dicLibIndexServerScriptingBase);
                    dicLibELOasBases.putAll(dicLibELOasBase);                    
                }                
            }
            String htmlDoc = CreateReportMatchUnittest(dicRFs, dicASDirectRules, dicActionDefs, dicLibAlls, dicLibRhinos, dicLibIndexServerScriptingBases, dicLibELOasBases);
            Http.ShowReport(htmlDoc);
        } catch (RemoteException ex) {
        }        
    }
}
