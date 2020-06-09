/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import byps.RemoteException;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;

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
            EloTest.showAlert("Achtung!", "ELO Connection", "Falsche Verbindungsdaten zu ELO \n: " + ex.getMessage());            
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            throw new Exception("Connection");
        }
        try {
            ixConn = connFact.create(profiles.getUser(), profiles.getPwd(), null, null);
        } catch (RemoteException ex) {
            EloTest.showAlert("Achtung!", "ELO Connection", "Indexserver-Verbindung ungültig \n User: " + profiles.getUser() + "\n IxUrl: " + profile.getIxUrl(profiles.getGitUser()));                        
            System.out.println("RemoteException message: " + ex.getMessage()); 
            throw new Exception("Connection");
        }
        return ixConn;
    }        
}
