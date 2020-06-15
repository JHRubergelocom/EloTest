/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import byps.RemoteException;
import de.elo.ix.client.DocVersion;
import de.elo.ix.client.EditInfo;
import de.elo.ix.client.EditInfoC;
import de.elo.ix.client.FindByIndex;
import de.elo.ix.client.FindChildren;
import de.elo.ix.client.FindInfo;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.Sord;
import de.elo.ix.client.SordC;
import de.elo.ix.client.SordZ;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javafx.application.Platform;

/**
 *
 * @author ruberg
 */
class RepoUtils {
    public static Sord[] FindChildren(IXConnection ixConn, String objId, boolean references, boolean recursive1) {
        System.out.println("FindChildren: objId " + objId + " ixConn " + ixConn);
        FindResult findResult = new FindResult();
        List<Sord> sordList = new ArrayList<>();
        try {
            ixConn.ix().checkoutSord(objId, SordC.mbAll, LockC.NO);
            
            FindInfo findInfo = new FindInfo();
            FindChildren findChildren = new FindChildren();
            FindByIndex findByIndex = new FindByIndex();
            Boolean includeReferences = references;
            SordZ sordZ = SordC.mbAll;
            Boolean recursive = recursive1;
            int level = -1;
            findChildren.setParentId(objId);
            findChildren.setMainParent(!includeReferences);
            findChildren.setEndLevel((recursive) ? level : 1);
            findInfo.setFindChildren(findChildren);
            findInfo.setFindByIndex(findByIndex);

            int idx = 0;
            findResult = ixConn.ix().findFirstSords(findInfo, 1000, sordZ);
            while (true) {
                Sord[] sordArray = findResult.getSords();
                sordList.addAll(Arrays.asList(sordArray));                
                if (!findResult.isMoreResults()) {
                    break;
                }
                idx += sordArray.length;
                findResult = ixConn.ix().findNextSords(findResult.getSearchId(), idx, 1000, sordZ);
            }
            
        } catch (RemoteException ex) {
        } finally {
            if (findResult != null)
            {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                }
            }
        }
        Sord[] children = new Sord[sordList.size()];
        children = sordList.toArray(children);
        return children;
    }
    
    static String DownloadDocumentToString (IXConnection ixConn, Sord s) {
        String docText = "";
        try {
            String objId = s.getId() + "";   
            String line;            
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();                    
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                    
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        docText = docText.concat(line);
                    }                       
                } catch (FileNotFoundException ex) {    
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });                                
                } catch (IOException ex) {            
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });                                                                                
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });                                                            
                        }
                    }
                }
                docText = docText.replaceAll(bom, "");
                docText = docText.replaceAll("\b", "");
                docText = docText.replaceAll("\n", "");
            }            
        } catch (RemoteException ex) {
        }
        return docText;
    }
    
    static List<String> DownloadDocumentToList (IXConnection ixConn, Sord s) {
        List<String> docList = new ArrayList<>();
        try {
            String objId = s.getId() + "";   
            String line;            
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();                    
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                    
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        line = line.replaceAll(bom, "");
                        line = line.replaceAll("\b", "");
                        line = line.replaceAll("\n", "");  
                        docList.add(line);
                    }                       
                } catch (FileNotFoundException ex) {    
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });                                
                } catch (IOException ex) {            
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });                                
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });            
                        }
                    }
                }
            }            
        } catch (RemoteException ex) {
        }
        return docList;
    }
    
    static String[] LoadTextDocs(IXConnection ixConn, String parentId) throws RemoteException {
        Sord[] sordRFInfo = FindChildren(ixConn, parentId, true, true);
        List<String> docTexts = new ArrayList<>();        
        for (Sord s : sordRFInfo) {
            String docText = DownloadDocumentToString(ixConn, s);
            docTexts.add(docText);
        }
        String[] docArray = new String[docTexts.size()];
        docArray = docTexts.toArray(docArray);
        return docArray;        
    }
    
    static SortedMap<String, List<String>> LoadTextDocsToSortedMap(IXConnection ixConn, String parentId) throws RemoteException {        
        Sord[] sordRFInfo = FindChildren(ixConn, parentId, true, true);
        SortedMap<String, List<String>> docTexts = new TreeMap<>();
        for (Sord s : sordRFInfo) {
            List<String> docTextList = DownloadDocumentToList(ixConn, s);
            docTexts.put(s.getName(), docTextList);
        }
        return docTexts;        
    }    

    private static SortedMap<Integer, String> DownloadDocumentToLines(IXConnection ixConn, SordDoc sDoc, Pattern p) {
        SortedMap<Integer, String> docLines = new TreeMap<>();
        try {
            String objId = sDoc.getId() + "";   
            String line;            
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();   
                sDoc.setExt(dv.getExt());
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); 
                    int linenr = 1;
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        line = line.replaceAll(bom, "");
                        line = line.replaceAll("\b", "");
                        line = line.replaceAll("\n", "");  
                        if (p.matcher(line).find()){
                            docLines.put(linenr, line);                                                                            
                        } 
                        linenr++;                        
                    }                       
                } catch (FileNotFoundException ex) {    
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });            
                    
                } catch (IOException ex) {            
                    Platform.runLater(() -> {
                        EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });                                
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {                            
                            Platform.runLater(() -> {
                                EloTest.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });                                        
                        }
                    }
                }
            }            
        } catch (RemoteException ex) {
        }
        return docLines;
        
    }

    static SortedMap<SordDoc, SortedMap<Integer, String>> LoadSordDocLines(IXConnection ixConn, List<EloPackage> eloPackages, Pattern p) {   
        Comparator<SordDoc> byName = Comparator.comparing(sd -> sd.getName());
        Comparator<SordDoc> byId = Comparator.comparingInt(sd -> sd.getId());
        Comparator<SordDoc> bySordDoc = byName.thenComparing(byId);        
        SortedMap<SordDoc, SortedMap<Integer, String>> dicSordDocLines = new TreeMap<>(bySordDoc);         
        String parentId;
        
        if (p.toString().length() > 0) {
            if (eloPackages.isEmpty()) {
                parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
                Sord[] sords = FindChildren(ixConn, parentId, true, true);
                for (Sord s : sords) {
                    SordDoc sDoc = new SordDoc(s);
                    SortedMap<Integer, String> docLines = DownloadDocumentToLines(ixConn, sDoc, p);
                    dicSordDocLines.put(sDoc, docLines);
                }
            } else {
                for (EloPackage eloPackage : eloPackages) {
                    SortedMap<SordDoc, SortedMap<Integer, String>> dicEloPackageSordDocLines = new TreeMap<>(bySordDoc); 
                    parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder();
                    Sord[] sords = FindChildren(ixConn, parentId, true, true);
                    for (Sord s : sords) {
                        SordDoc sDoc = new SordDoc(s);
                        SortedMap<Integer, String> docLines = DownloadDocumentToLines(ixConn, sDoc, p);
                        dicEloPackageSordDocLines.put(sDoc, docLines);
                        dicSordDocLines.putAll(dicEloPackageSordDocLines);
                    }                
                }            
            }            
        }
        
        return dicSordDocLines;        
        
    }

}
