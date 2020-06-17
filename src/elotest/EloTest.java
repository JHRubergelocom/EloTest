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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
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
    private final EloService eloService = new EloService();
    
    private final Label lblProfile = new Label(); 
    private final ComboBox<String> cmbProfile = new ComboBox<>(); 
    
    private final Label lblEloCli = new Label(); 
    private final ListView<String> listvEloCli = new ListView<>(); 
    
    private final Label lblUnittestTools = new Label(); 
    private final ListView<String> listvUnittestTools = new ListView<>(); 
    
    private final Label lblEloServices = new Label(); 
    private final ListView<String> listvEloServices = new ListView<>(); 
    
    private final TextField txtPattern = new TextField();
    private final CheckBox chkCaseSensitiv = new CheckBox("Case sensitiv");

    
    private void fillListView(Label label, String lblText, ListView<String> listview, List <String> entries) {
        label.setText(lblText);
        label.setStyle(LABEL_STYLE);
        
        listview.setItems(FXCollections.observableArrayList(entries));
        listview.setStyle(LISTVIEW_STYLE);
    }
    
   private void fillComboBox(Label label, String lblText, ComboBox<String> combobox, List<String> entries) {
        label.setText(lblText);
        label.setStyle(LABEL_STYLE);
        
        combobox.setItems(FXCollections.observableArrayList(entries));
        combobox.setStyle(LISTVIEW_STYLE);       
   }
    
    private void fillComboBoxProfile() {
        final ArrayList<String> entries = new ArrayList<>();
        
        profiles.getProfiles().forEach((n, p) -> {
            entries.add(p.getName());
        }); 
        
        fillComboBox(lblProfile, "Profile", cmbProfile, entries);        
    }
    
    private void initComboBoxProfile() {
        fillComboBoxProfile();        
        String selectedProfileName = eloProperties.getSelectedProfile();
        if (selectedProfileName != null) {
            cmbProfile.getSelectionModel().select(selectedProfileName);            
        } else {
            cmbProfile.getSelectionModel().select(0);                    
        }
        
        cmbProfile.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedProfile(newValue);
            fillListViewEloCli();
        });        
    }
  
    private void fillListViewEloCli() {
        String pname = cmbProfile.getSelectionModel().getSelectedItem();
                
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
                String currentItemSelected = listvEloCli.getSelectionModel().getSelectedItem();
                String pname = cmbProfile.getSelectionModel().getSelectedItem();
                Profile profile = profiles.getProfiles().get(pname);
                EloCommand eloCommand = profile.getEloCommands().get(currentItemSelected);
                eloService.runEloCommand(eloCommand, profile, profiles, this);                
            }
        });      
        
        listvEloCli.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedEloCli(newValue);
        });   
                
    }
    
    private void fillListViewUnittestTools() {        
        final ArrayList<String> entries = new ArrayList<>();
        entries.add("show");
        entries.add("matching");
        entries.add("create");
        entries.add("ranger");
        entries.add("gitpullall");
        entries.add("search");
        entries.add("export");
        fillListView(lblUnittestTools, "Unittest Tools", listvUnittestTools, entries);
    }

    private void initListViewUnittestTools() {
        fillListViewUnittestTools();        
        String selectedUnittestToolsName = eloProperties.getSelectedUnittestTools();
        if (selectedUnittestToolsName != null) {
            listvUnittestTools.getSelectionModel().select(selectedUnittestToolsName);            
        } else {
            listvUnittestTools.getSelectionModel().select(0);                    
        }
        
        listvUnittestTools.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvUnittestTools.getSelectionModel().getSelectedItem();
                String pname = cmbProfile.getSelectionModel().getSelectedItem();
                Profile profile = profiles.getProfiles().get(pname);
                eloService.runUnittestTools(currentItemSelected, profile, profiles, this);
            }
        });      
        
        listvUnittestTools.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedUnittestTools(newValue);
        });   
        
    }
    
    private void fillListViewEloServices() {
        final ArrayList<String> entries = new ArrayList<>();
        entries.add("Application Server");
        entries.add("Admin Console");
        entries.add("App Manager");
        entries.add("Webclient");
        fillListView(lblEloServices, "Elo Services", listvEloServices, entries);
    }

    private void initListViewEloServices() {
        fillListViewEloServices();        
        String selectedEloServicesName = eloProperties.getSelectedEloServices();
        if (selectedEloServicesName != null) {
            listvEloServices.getSelectionModel().select(selectedEloServicesName);            
        } else {
            listvEloServices.getSelectionModel().select(0);                    
        }
        
        listvEloServices.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvEloServices.getSelectionModel().getSelectedItem();
                String pname = cmbProfile.getSelectionModel().getSelectedItem();
                Profile profile = profiles.getProfiles().get(pname);
                eloService.runEloServices(currentItemSelected, profile, profiles, this);
            }
        });      
        
        listvEloServices.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedEloServices(newValue);
        });           
        
    }

    private void initSearchPattern() {
        txtPattern.setText(eloProperties.getPattern());
        txtPattern.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println(" Text Changed to  " + newValue + "\n");
            eloProperties.setPattern(newValue);
        });
        
        chkCaseSensitiv.setSelected(eloProperties.getCaseSensitiv());  
        chkCaseSensitiv.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            System.out.println(" CheckBox Changed to  " + newValue + "\n");
            eloProperties.setCaseSensitiv(newValue);
        });
    }
    
    public void setDisableControls(boolean value) {
        lblProfile.setDisable(value);
        cmbProfile.setDisable(value);        
        lblEloCli.setDisable(value);
        listvEloCli.setDisable(value);
        lblUnittestTools.setDisable(value);
        listvUnittestTools.setDisable(value);
        lblEloServices.setDisable(value);  
        listvEloServices.setDisable(value);
        txtPattern.setDisable(value);        
        chkCaseSensitiv.setDisable(value); 
    }
    
    public static void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();                     
    }
    
    public TextField getTxtPattern() {
        return txtPattern;
    }
    
    public CheckBox getChkCaseSensitiv() {
        return chkCaseSensitiv;
    }
    
    @Override
    public void start(Stage primaryStage) {     
        
        GridPane root = new GridPane();
        
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints(130);
            root.getColumnConstraints().add(column);
        }
        
        root.getRowConstraints().add(new RowConstraints(30));
        root.getRowConstraints().add(new RowConstraints(320));

        root.setPadding(new Insets(10, 10, 10, 10));
        root.setHgap(7);
        root.setVgap(7);
        
        initComboBoxProfile();
        
        initListViewEloCli();
        
        initListViewUnittestTools();

        initListViewEloServices();
        
        initSearchPattern();
        
        root.add(lblProfile, 0, 0);
        root.add(cmbProfile, 1, 0);
        GridPane.setHalignment(cmbProfile, HPos.RIGHT);

        TabPane tabPane = new TabPane();
        VBox vbox = new VBox(lblEloCli, listvEloCli);
        Tab tab = new Tab("ELO Cli", vbox);
        tabPane.getTabs().add(tab);
        
        vbox = new VBox(lblUnittestTools, listvUnittestTools);
        tab = new Tab("Unittest Tools", vbox);
        tabPane.getTabs().add(tab);

        vbox = new VBox(lblEloServices, listvEloServices);
        tab = new Tab("ELO Services", vbox);
        tabPane.getTabs().add(tab);
        
        root.add(tabPane, 0, 1, 2, 1);

        root.add(txtPattern, 0, 2);
        root.add(chkCaseSensitiv, 1, 2);        
        GridPane.setHalignment(chkCaseSensitiv, HPos.RIGHT);

        Scene scene = new Scene(root, 290, 410);
        
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
