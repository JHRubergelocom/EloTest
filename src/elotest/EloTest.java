/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elotest;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author ruberg
 */
public class EloTest extends Application {
    static final String LABEL_STYLE = "-fx-font-weight: bold; -fx-font-style: regular; -fx-font-size: 18px";
    static final String LISTVIEW_STYLE = "-fx-font-style: regular; -fx-font-size: 14px";
    
    private final Profiles profiles = new Profiles("Profiles.json");
    private final EloProperties eloProperties = new EloProperties();
    
    private final Label lblProfile = new Label(); 
    private final ListView<String> listvProfile = new ListView<>(); 
    
    private final Label lblEloCli = new Label(); 
    private final ListView<String> listvEloCli = new ListView<>(); 
    
    private final Label lblUnittestTools = new Label(); 
    private final ListView<String> listvUnittestTools = new ListView<>(); 
    
    private final Label lblEloServices = new Label(); 
    private final ListView<String> listvEloServices = new ListView<>(); 
    
    
    
    private void fillListView(Label label, String lblText, ListView<String> listview, List <String> entries) {
        label.setText(lblText);
        label.setStyle(LABEL_STYLE);
        
        listview.setItems(FXCollections.observableArrayList(entries));
        listview.setStyle(LISTVIEW_STYLE);
    }
    
    private void fillListViewProfile() {
        final ArrayList<String> entries = new ArrayList<>();
        
        profiles.getProfiles().forEach((n, p) -> {
            entries.add(p.getName());
        }); 
        
        fillListView(lblProfile, "Profile", listvProfile, entries);        
    }
    
    private void initListViewProfile() {
        fillListViewProfile();        
        String selectedProfileName = eloProperties.getSelectedProfile();
        if (selectedProfileName != null) {
            listvProfile.getSelectionModel().select(selectedProfileName);            
        } else {
            listvProfile.getSelectionModel().select(0);                    
        }
        
        listvProfile.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvProfile.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Achtung!");
                alert.setHeaderText("Doppelclick");
                alert.setContentText(currentItemSelected);
                alert.showAndWait();
            }
        });      
        
        listvProfile.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedProfile(newValue);
            fillListViewEloCli();
        });        
    }
  
    private void fillListViewEloCli() {
        String pname = listvProfile.getSelectionModel().getSelectedItem();
                
        final ArrayList<String> entries = new ArrayList<>();        
        Profile p = profiles.getProfiles().get(pname);
        
        p.getEloCommands().forEach((n, c) -> {
            entries.add(c.getName());
        }); 
                
        fillListView(lblEloCli, "ELO Cli", listvEloCli, entries);        
    }
    
    
    private void initListViewEloCli() {
        fillListViewEloCli();        
        String selectedEloCliName = eloProperties.getSelectedEloCli();
        if (selectedEloCliName != null) {
            listvEloCli.getSelectionModel().select(selectedEloCliName);            
        } else {
            listvEloCli.getSelectionModel().select(0);                    
        }
        
        listvEloCli.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                setDisableControls(true);
                String currentItemSelected = listvEloCli.getSelectionModel().getSelectedItem();
                
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Achtung!");
                alert.setHeaderText("Doppelclick");
                alert.setContentText(currentItemSelected);
                alert.showAndWait();
                // TODO EloCommand ausführen
                
                setDisableControls(false);
                
                
                
            }
            
            
            // TODO
        });      
        
        listvEloCli.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedEloCli(newValue);
        });   
        
        
    }
    
    private void setDisableControls(boolean value) {
        lblProfile.setDisable(value);
        listvProfile.setDisable(value);        
        lblEloCli.setDisable(value);
        listvEloCli.setDisable(value);
        lblUnittestTools.setDisable(value);
        listvUnittestTools.setDisable(value);
        lblEloServices.setDisable(value);  
        listvEloServices.setDisable(value);
    }

    
    @Override
    public void start(Stage primaryStage) {     
        
        GridPane root = new GridPane();
        
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints(150);
            root.getColumnConstraints().add(column);
        }

        root.setPadding(new Insets(10, 10, 10, 10));
        root.setHgap(7);
        root.setVgap(7);
        
        initListViewProfile();
        
        initListViewEloCli();
        
/*                
        ArrayList<String> entries = new ArrayList<>();
        entries.add("elocli1");
        entries.add("elocli2");
        entries.add("elocli3");
        fillListView(lblEloCli, "ELO Cli", listvEloCli, entries);
*/        
        
        ArrayList<String> entries = new ArrayList<>();
        entries.add("show");
        entries.add("adjust");
        entries.add("create");
        entries.add("ranger");
        entries.add("gitpullall");
        fillListView(lblUnittestTools, "Unittest Tools", listvUnittestTools, entries);
        
        entries = new ArrayList<>();
        entries.add("Application Server");
        entries.add("Admin Console");
        entries.add("App Manager");
        entries.add("Webclient");
        fillListView(lblEloServices, "Elo Services", listvEloServices, entries);
        
        root.add(lblProfile, 0, 0);
        root.add(listvProfile, 0, 1);

        root.add(lblEloCli, 1, 0);
        root.add(listvEloCli, 1, 1);
        
        root.add(lblUnittestTools, 2, 0);
        root.add(listvUnittestTools, 2, 1);
        
        root.add(lblEloServices, 0, 2);
        root.add(listvEloServices, 0, 3);

        Scene scene = new Scene(root, 600, 400);
        
        primaryStage.setTitle("ELO Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
