/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import byps.RemoteException;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;
import javafx.scene.control.Alert;

/**
 *
 * @author ruberg
 */
class Connection {
    static IXConnection getIxConnection(Profile profile, Profiles profiles) throws Exception{
        IXConnection ixConn;
        IXConnFactory connFact;        
        try {
            connFact = new IXConnFactory(profile.getIxUrl(profiles.getGitUser()), "IXConnection", "1.0");            
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("ELO Connection");
            alert.setContentText("Falsche Verbindungsdaten zu ELO \n: " + ex.getMessage());
            alert.showAndWait();                                                
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            throw new Exception("Connection");
        }
        try {
            ixConn = connFact.create(profiles.getUser(), profiles.getPwd(), null, null);
        } catch (RemoteException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("ELO Connection");
            alert.setContentText("Indexserver-Verbindung ungültig \n User: " + profiles.getUser() + "\n IxUrl: " + profile.getIxUrl(profiles.getGitUser()));
            alert.showAndWait();                                                            
            System.out.println("RemoteException message: " + ex.getMessage()); 
            throw new Exception("Connection");
        }
        return ixConn;
    }        
}
