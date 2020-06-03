/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

/**
 *
 * @author ruberg
 */
class EloService extends Service<Boolean>{
    private static final String ELOCLI = "eloCli";
    private static final String UNITTESTTOOLS = "unitTestTools";
    private static final String ELOSERVICES = "eloServices";
    
    private String typeCommand;
    private EloCommand eloCommand;
    private Profile profile;
    private Profiles profiles;
    
    private void setTypeCommand(String typeCommand) {
        this.typeCommand = typeCommand;
    }

    private void setEloCommand(EloCommand eloCommand) {
        this.eloCommand = eloCommand;
    }

    private void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    private void setProfiles(Profiles profiles) {
        this.profiles = profiles;
    }
    
    
    // TODO ExecuteEloCommand
    private void executeEloCli() {
        try {
            String psCommand = eloCommand.getCmd() + " -stack " + profile.getStack(profiles.getGitUser()) + " -workspace " + eloCommand.getWorkspace();
            if (eloCommand.getVersion().length() > 0) {
                psCommand = psCommand + " -version " + eloCommand.getVersion();                
            }
            
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", psCommand);                
            pb.directory(new File (profile.getWorkingDir(profiles.getGitSolutionsDir())));
            Process p; 
            p = pb.start();  
            
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            String htmlDoc = "<html>\n";
            String htmlHead = Http.CreateHtmlHead(psCommand);
            String htmlStyle = Http.CreateHtmlStyle();
            String htmlBody = "<body>\n";

            htmlBody += "<h1>"+ psCommand + "</h1>";

            while ((line = br.readLine()) != null) {
                htmlBody += "<h4>"+ line + "</h4>";
                System.out.println(line);
                if (line.contains("already exists")) {
                    break;
                }
            }

            if (line != null) {
                if (line.contains("already exists")) {
                    try (OutputStream os = p.getOutputStream()) {
                        OutputStreamWriter osr = new OutputStreamWriter(os);
                        BufferedWriter bw = new BufferedWriter(osr);
                        
                        Scanner sc = new Scanner("n");
                        String input = sc.nextLine();
                        input += "\n";
                        bw.write(input);
                        bw.flush();
                    }
                    br.close();
                }                
            }
            
            htmlBody += "</body>\n";
            htmlDoc += htmlHead;
            htmlDoc += htmlStyle;
            htmlDoc += htmlBody;
            htmlDoc += "</html>\n";

            Http.ShowReport(htmlDoc);

            System.out.println("Programmende"); 

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("IOException");
            alert.setContentText("System.IOException message: " + ex.getMessage());
            alert.showAndWait();                                    
        } 
        
    }
    
    public void runEloCommand(EloCommand eloCommand, Profile profile, Profiles profiles) {
        setTypeCommand(ELOCLI);
        setEloCommand(eloCommand);
        setProfile(profile);
        setProfiles(profiles);
        if (isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            reset();
            start();
        }           
    }
    

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                switch(typeCommand) {
                    case ELOCLI:
                    // TODO ExecuteEloCommand
                        executeEloCli();                        
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
    }

}
