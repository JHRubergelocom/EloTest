/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import de.elo.ix.client.IXConnection;
import java.io.File;
import javafx.application.Platform;

/**
 *
 * @author ruberg
 */
class EloExport {
    static private final boolean REFERENCES = false;     

    static void StartExport(IXConnection ixConn, Solution solution, Solutions solutions) {
        try {
            String name = solution.getName();
            String exportPath = "C:\\Temp\\ExportElo\\" + name;
            String arcPath = solutions.getArcPath();
            File exportDir = new File(exportPath);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }  
            RepoUtils.FindChildren(ixConn, arcPath, exportDir, REFERENCES);
            WfUtils.FindWorkflows(ixConn, exportDir);
            MaskUtils.FindDocMasks(ixConn, exportDir);
            System.out.println("ticket=" + ixConn.getLoginResult().getClientInfo().getTicket());
            
        } catch (Exception ex) {
            Platform.runLater(() -> {
                EloTest.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
            });            
        }
    }    
}
