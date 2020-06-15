/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import byps.RemoteException;
import de.elo.ix.client.FileData;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.FindWorkflowInfo;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.WFDiagram;
import de.elo.ix.client.WFDiagramC;
import de.elo.ix.client.WFDiagramZ;
import de.elo.ix.client.WFTypeC;
import de.elo.ix.client.WorkflowExportOptions;
import de.elo.ix.client.WorkflowExportOptionsC;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 *
 * @author ruberg
 */
class WfUtils {

    private static WFDiagram[] FindWorkflows(IXConnection ixConn, FindWorkflowInfo findWorkflowInfo) {
        int max = 100;
        int idx = 0;
        FindResult findResult = new FindResult(); 
        List<WFDiagram> wfList = new ArrayList<>();
        WFDiagramZ checkoutOptions = WFDiagramC.mbLean;
        try {
            findResult = ixConn.ix().findFirstWorkflows(findWorkflowInfo, max, checkoutOptions);
            while (true) {
                WFDiagram[] wfArray = findResult.getWorkflows();   
                wfList.addAll(Arrays.asList(wfArray));
                if (!findResult.isMoreResults()) {
                  break;
                }
                idx += wfArray.length;
                findResult = ixConn.ix().findNextWorkflows(findResult.getSearchId(), idx, max);
            }
          } catch (RemoteException ex) {
          } finally {
            if (findResult != null) {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                }
            }
        }        
        WFDiagram[] workflows = new WFDiagram[wfList.size()];
        workflows = wfList.toArray(workflows);
        return workflows;        
    }
    
    private static WFDiagram[] GetTemplates(IXConnection ixConn) {
        FindWorkflowInfo info = new FindWorkflowInfo();
        info.setType(WFTypeC.TEMPLATE);
        return FindWorkflows(ixConn, info);        
    }
    
    private static int GetWorkflowTemplateId(IXConnection ixConn, String workflowTemplateName) throws RemoteException {
        WFDiagram wfDiag = ixConn.ix().checkoutWorkflowTemplate(workflowTemplateName, "", new WFDiagramZ(WFDiagramC.mbId), LockC.NO);
        return wfDiag.getId();                
    }
    
    private static String GetWorkflowAsJsonText(IXConnection ixConn, int flowId) throws RemoteException, UnsupportedEncodingException {
        WorkflowExportOptions workflowExportOptions = new WorkflowExportOptions();
        workflowExportOptions.setFlowId(Integer.toString(flowId));

        workflowExportOptions.setFormat(WorkflowExportOptionsC.FORMAT_JSON);
        FileData fileData = ixConn.ix().exportWorkflow(workflowExportOptions);
        String jsonData = new String(fileData.getData(), "UTF-8");
        return jsonData;
    }
    
    private static String ExportWorkflow(IXConnection ixConn, int workflowId) throws RemoteException, UnsupportedEncodingException {
        return GetWorkflowAsJsonText(ixConn, workflowId);
    }
    
    private static String ExportWorkflowTemplate(IXConnection ixConn, WFDiagram wf) throws RemoteException, UnsupportedEncodingException {
        int workflowTemplateId = GetWorkflowTemplateId(ixConn, wf.getName());  
        return ExportWorkflow(ixConn, workflowTemplateId);        
    }    
    
    static SortedMap<WFDiagram, SortedMap<Integer, String>> LoadWorkflowLines(IXConnection ixConn, Pattern pattern) throws RemoteException, UnsupportedEncodingException {
        Comparator<WFDiagram> byName = Comparator.comparing(wf -> wf.getName());
        Comparator<WFDiagram> byId = Comparator.comparingInt(wf -> wf.getId());
        Comparator<WFDiagram> byWFDiagram = byName.thenComparing(byId);                        
        SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines = new TreeMap<>(byWFDiagram);
        WFDiagram[] wfTemplates = GetTemplates(ixConn);
        for (WFDiagram wf : wfTemplates) {
            SortedMap<Integer, String> wfLines = new TreeMap<>();
            String wfJsonText = ExportWorkflowTemplate(ixConn, wf);
            wfJsonText = JsonUtils.formatJsonString(wfJsonText);            
            String[] lines = wfJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);
                if (pattern.toString().length() > 0) {
                    if (pattern.matcher(line).find()){
                        wfLines.put(linenr, line);                                                                            
                    }                                                                
                }
                linenr++;
            }
            dicWorkflowLines.put(wf, wfLines);
        }
        return dicWorkflowLines;        
    }    

}
