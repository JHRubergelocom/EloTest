/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import de.elo.ix.client.IXConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Optional;
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
    private EloTest eloTest;
    private String unittestTool;
    private IXConnection ixConn;
    
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
    
    private void setEloTest(EloTest eloTest) {
        this.eloTest = eloTest;
    }
    
    private void setUnittestTool(String unittestTool) {
        this.unittestTool = unittestTool;        
    }

    private void setIxConn(IXConnection ixConn) {
        this.ixConn = ixConn;        
    }
    
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

    private void executeUnittestTools() {
        switch(unittestTool) {
            case "show":                
                EloApp.ShowUnittests(ixConn, profile, profiles);
                break;
            default:
                break;
        }            
    }
    
    private void executeGitPullAll(String workingDir) {
        try {
            SubDirectories(workingDir);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("IOException");
            alert.setContentText("System.IOException message: " + ex.getMessage());
            alert.showAndWait();                                                
        }
    }
    
    private static String GitPull (String htmlBody, String gitDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "pull");  
        pb.directory(new File (gitDir));
        Process powerShellProcess = pb.start();
        // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;
        
        
        System.out.println("Standard Output:");
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            while ((line = stdout.readLine()) != null) {
                htmlBody += "<h4>"+ line + "</h4>";
                System.out.println(line);
            }
        }
        System.out.println("Standard Error:");
        try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()))) {
            while ((line = stderr.readLine()) != null) {
                System.err.println(line);
            }
        }
        System.out.println("Done");        
        return htmlBody;
    }
    
    private static void SubDirectories(String directory) throws IOException {
        Optional<ArrayList<File>> optFiles = getPaths(new File(directory), new ArrayList<>());
        ArrayList<File> files;
        if (optFiles.isPresent()) {
            files = optFiles.get();
        } else {
            return;
        }
        
        String gitCommand;
        gitCommand = "directory " + directory + ": " + "GitPullAll";
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead(gitCommand);
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        htmlBody += "<h1>"+ gitCommand + "</h1>";

        for (File file: files) {
            htmlBody += "<h4>"+ file.getCanonicalPath() + "</h4>";            
            System.out.println(file.getCanonicalPath()); 
            htmlBody = GitPull(htmlBody, file.getCanonicalPath());                    
        }

        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        Http.ShowReport(htmlDoc);

    }

    private static Optional<ArrayList<File>> getPaths(File file, ArrayList<File> list) {
        if (file == null || list == null || !file.isDirectory())
            return Optional.empty();
        File[] fileArr = file.listFiles(f ->(f.getName().endsWith(".git")) && !(f.getName().contentEquals(".git")));
        for (File f : fileArr) {
            if (f.isDirectory()) {
                getPaths(f, list);
                list.add(f);                
            }
        }
        return Optional.of(list);
    }
    
    public void runEloCommand(EloCommand eloCommand, Profile profile, Profiles profiles, EloTest eloTest) {
        setTypeCommand(ELOCLI);
        setEloCommand(eloCommand);
        setProfile(profile);
        setProfiles(profiles);
        setEloTest(eloTest);
        if (isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            reset();
            start();
        }           
    }
    
    public void runUnittestTools(String unittestTool, Profile profile, Profiles profiles, EloTest eloTest) {
        IXConnection ixConn;
        try {
            ixConn = Connection.getIxConnection(profile, profiles);            
            setTypeCommand(UNITTESTTOOLS);
            setUnittestTool(unittestTool);
            setProfile(profile);
            setProfiles(profiles);        
            setEloTest(eloTest);
            setIxConn(ixConn);            
            if (isRunning()) {
                System.out.println("Already running. Nothing to do.");
            } else {
                reset();
                start();
            }           
        }catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Achtung!");
            alert.setHeaderText("Exception");
            alert.setContentText("System.Exception message: " + ex.getMessage());
            alert.showAndWait();                                                                        
        } 
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                eloTest.setDisableControls(true);
                switch(typeCommand) {
                    case ELOCLI:
                        if (eloCommand.getName().equals("eloPrepare")) {
                            executeGitPullAll(profiles.getDevDir());
                            executeGitPullAll(profiles.getGitSolutionsDir());
                        }                        
                        executeEloCli();                        
                        break;
                    case UNITTESTTOOLS:
                        executeUnittestTools();
                        break;
                    default:
                        break;
                }
                eloTest.setDisableControls(false);
                return true;
            }
        };
    }

}
