/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import de.elo.ix.client.Sord;

/**
 *
 * @author ruberg
 */
class SordDoc {
    private final Sord sord;
    private String ext;
    
    public SordDoc(Sord sord) {
        this.sord = sord;   
        this.ext = "";
    }

    Integer getId() {
        return sord.getId();
    }

    String getGuid() {
        return sord.getGuid();
    }
    
    String getName() {
        return sord.getName();
    }

    String getExt() {
        return ext;
    }

    void setExt(String ext) {
        this.ext = ext;        
    }
}
