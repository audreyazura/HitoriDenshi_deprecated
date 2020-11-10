/*
 * Copyright (C) 2020 Alban Lafuente
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hitoridenshi.guimanager;

import hitoridenshi.simulationmanager.Sample;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author audreyazura
 */
public class SampleBox implements Sample
{
    private File m_configFile;
    private HashMap<String, String> m_gradingProfile = new HashMap<>();
    private List<HashMap<String, String>> m_traps = new ArrayList<>();
    
    private final Label m_title = new Label("");
    private final Button m_updateButton = new Button("Save data");
    private final BorderPane m_titleRegion = new BorderPane(null, null, m_updateButton, null, m_title);
    
    private final Label m_fileLabel = new Label("SCAPS Energy Band files (*.eb)");
    private final TextField m_fileField = new TextField("");
    private final Button m_browseButton = new Button("Browse");
    private final HBox m_browseBox = new HBox(m_fileField, m_browseButton);
    private final VBox m_fileBox = new VBox(m_fileLabel, m_browseBox);
    
    private final Label m_gradingLabel = new Label("Grading");
    private final Label m_notchPositionLabel = new Label("Notch Position (nm)");
    private final TextField m_notchPosition = new TextField("");
    private final HBox m_notchPositionBox = new HBox(m_notchPositionLabel, m_notchPosition);
    private final Label m_frontGapLabel = new Label("Front gap (eV)");
    private final TextField m_frontGap = new TextField("");
    private final HBox m_frontBox = new HBox(m_frontGapLabel, m_frontGap);
    private final Label m_notchGapLabel = new Label("Notch gap (eV)");
    private final TextField m_notchGap = new TextField("");
    private final HBox m_notchGapBox = new HBox(m_notchGapLabel, m_notchGap);
    private final Label m_backGapLabel = new Label("Back gap (eV)");
    private final TextField m_backGap = new TextField("");
    private final HBox m_backBox = new HBox(m_backGapLabel, m_backGap);
    private final HBox m_gradingOptionBox = new HBox(m_notchPositionBox, m_frontBox, m_notchGapBox, m_backBox);
    private final VBox m_gradingBox = new VBox(m_gradingLabel, m_gradingOptionBox);
    
    private final VBox m_parametersVBox = new VBox(m_fileBox);
    
    private final VBox m_outerVBox = new VBox(m_titleRegion, m_parametersVBox);
    
    private List<VBox> m_trapBoxes = new ArrayList<>();
    
    public SampleBox()
    {
        baseInitialization("");
        initiateGrading(new HashMap<>());
        initializeTraps(new ArrayList<>());
    }
    
    public SampleBox (String p_address)
    {
        baseInitialization(p_address);
        initiateGrading(new HashMap<>());
        initializeTraps(new ArrayList<>());
    }
    
    public SampleBox (String p_address, HashMap<String, String> p_grading)
    {
        baseInitialization(p_address);
        initiateGrading(p_grading);
        initializeTraps(new ArrayList<>());
    }
    
    public SampleBox (String p_address, HashMap<String, String> p_grading, List<HashMap<String, String>> p_traps)
    {
        baseInitialization(p_address);
        initiateGrading(p_grading);
        initializeTraps(p_traps);
    }
    
    private void baseInitialization (String p_fileAddress)
    {
        //setting label and text fields content
        m_configFile = new File(p_fileAddress);
        m_fileField.setText(p_fileAddress);
        
        //setting button functions
        m_updateButton.setOnAction((ActionEvent event) ->
        {
            saveData();
        });
        m_browseButton.setOnAction((ActionEvent event) ->
        {
            browse();
        });
        
        //setting styles
        String styleAddress = this.getClass().getResource("WindowsStyle.css").toExternalForm();
        m_outerVBox.getStylesheets().add(styleAddress);
        
        m_outerVBox.getStyleClass().add("propertyvbox");
        
        m_title.getStyleClass().add("titlelabel");
        m_updateButton.getStyleClass().add("leftbutton");
        
        m_parametersVBox.getStyleClass().add("internalvbox");
        
        m_fileBox.getStyleClass().add("internalvbox");
        m_fileLabel.getStyleClass().add("windowtext");
        m_browseBox.getStyleClass().add("internalhbox");
        m_browseBox.setHgrow(m_fileField, Priority.ALWAYS);
        m_browseBox.setPrefWidth(600);
        m_fileField.getStyleClass().add("inputfield");
        m_browseButton.getStyleClass().add("button");
        
        m_gradingBox.getStyleClass().add("internalvbox");
        m_gradingLabel.getStyleClass().add("subtitle");
        m_gradingOptionBox.getStyleClass().add("parametershbox");
        m_notchPositionBox.getStyleClass().add("internalhbox");
        m_notchPositionLabel.getStyleClass().add("windowtext");
        m_notchPosition.getStyleClass().add("inputfield");
        m_notchPosition.setPrefWidth(65);
        m_frontBox.getStyleClass().add("internalhbox");
        m_frontGapLabel.getStyleClass().add("windowtext");
        m_frontGap.getStyleClass().add("inputfield");
        m_frontGap.setPrefWidth(65);
        m_notchGapBox.getStyleClass().add("internalhbox");
        m_notchGapLabel.getStyleClass().add("windowtext");
        m_notchGap.getStyleClass().add("inputfield");
        m_notchGap.setPrefWidth(65);
        m_backBox.getStyleClass().add("internalhbox");
        m_backGapLabel.getStyleClass().add("windowtext");
        m_backGap.getStyleClass().add("inputfield");
        m_backGap.setPrefWidth(65);
    }
    
    private void initiateGrading (HashMap<String, String> p_grading)
    {
        m_gradingProfile = new HashMap(p_grading);
        
        m_frontGap.setText(p_grading.containsKey("front") ? p_grading.get("front") : "");
        m_notchGap.setText(p_grading.containsKey("notchgap") ? p_grading.get("notchgap") : "");
        m_backGap.setText(p_grading.containsKey("back") ? p_grading.get("back") : "");
        m_notchPosition.setText(p_grading.containsKey("notchposition") ? p_grading.get("notchposition") : "");
        m_outerVBox.getChildren().add(m_gradingBox);
        m_gradingBox.setManaged(false);
        m_gradingBox.setVisible(false);
    }
    
    private void initializeTraps(List<HashMap<String, String>> p_traps)
    {
        for (int i = 0 ; i < p_traps.size() ; i += 1)
        {
            addTrap(i);
            
            HashMap<String, String> currentTrap = p_traps.get(i);
            m_traps.set(i, new HashMap(currentTrap));
            
            HBox trapParameters = (HBox) m_trapBoxes.get(i).getChildren().get(1);
            HBox densityBox = (HBox) trapParameters.getChildren().get(0);
            HBox xsectionBox = (HBox) trapParameters.getChildren().get(1);
            HBox energyBox = (HBox) trapParameters.getChildren().get(2);
            
            ((TextField) densityBox.getChildren().get(1)).setText(currentTrap.containsKey("density") ? currentTrap.get("density") : "");
            ((TextField) xsectionBox.getChildren().get(1)).setText(currentTrap. containsKey("cross-section") ? currentTrap.get("cross-section") : "");
            ((TextField) energyBox.getChildren().get(1)).setText(currentTrap.containsKey("energy") ? currentTrap.get("energy") : "");
            
            hideOrRemoveTrap(i);
        }
    }
    
    private void browse ()
    {
        FileChooser browser = new FileChooser();
        
        browser.setTitle("Chose the folder containing the input file");
        browser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SCAPS-1D eb files (*.eb)", "*.eb"), new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
	
	try
        {
            browser.setInitialDirectory(new File((new File(m_fileField.getText())).getParent()));
        }
        catch (NullPointerException ex)
        {
            browser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        File selectedFile = browser.showOpenDialog(new Stage());
        
        if (selectedFile != null)
        {
            m_configFile = selectedFile;
            m_fileField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private boolean isEmptyTrap(int p_position)
    {
        HBox parametersBox = (HBox) m_trapBoxes.get(p_position).getChildren().get(1);
        HBox densityBox = (HBox) parametersBox.getChildren().get(0);
        HBox xsectionBox = (HBox) parametersBox.getChildren().get(1);
        HBox energyBox = (HBox) parametersBox.getChildren().get(0);

        String density = ((TextField) densityBox.getChildren().get(1)).getText();
        String xsection = ((TextField) xsectionBox.getChildren().get(1)).getText();
        String energy = ((TextField) energyBox.getChildren().get(1)).getText();

        return (density == null || density.equals("")) && (xsection == null || xsection.equals("")) && (energy == null || energy.equals(""));
    }
    
    protected void saveData ()
    {
        m_gradingProfile.put("front", m_frontGap.getText());
        m_gradingProfile.put("notchgap", m_notchGap.getText());
        m_gradingProfile.put("back", m_backGap.getText());
        m_gradingProfile.put("notchposition", m_notchPosition.getText());
        
        for (int i = 0 ; i < m_traps.size() ; i += 1)
        {
            try
            {
                HBox parametersBox = (HBox) m_trapBoxes.get(i).getChildren().get(1);
                HBox densityBox = (HBox) parametersBox.getChildren().get(0);
                HBox xsectionBox = (HBox) parametersBox.getChildren().get(1);
                HBox energyBox = (HBox) parametersBox.getChildren().get(0);
                
                m_traps.get(i).put("density", ((TextField) densityBox.getChildren().get(1)).getText());
                m_traps.get(i).put("cross-section", ((TextField) xsectionBox.getChildren().get(1)).getText());
                m_traps.get(i).put("energy", ((TextField) energyBox.getChildren().get(1)).getText());
            }
            catch (IndexOutOfBoundsException ex)
            {
                Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Trap list and Trap Boxes list of different size", ex);
            }
        }
    }
    
    public final void addTrap(int p_trapIndex)
    {
        m_traps.add(p_trapIndex, new HashMap<>());
        
        //creating trap fields and applying the right styles
        Label trapTitle = new Label("Trap " + (p_trapIndex+1));
        trapTitle.getStyleClass().add("subtitle");
        
        Label trapDensityLabel = new Label("Trap density (cm⁻³)");
        trapDensityLabel.getStyleClass().add("windowtext");
        
        TextField trapDensity = new TextField("");
        trapDensity.getStyleClass().add("inputfield");
        trapDensity.setPrefWidth(65);
        
        HBox densityBox = new HBox(trapDensityLabel, trapDensity);
        densityBox.getStyleClass().add("internalhbox");
        
        Label trapXsectionLabel = new Label("Trap cross-section (cm⁻²)");
        trapXsectionLabel.getStyleClass().add("windowtext");
        
        TextField trapXsection = new TextField("");
        trapXsection.getStyleClass().add("inputfield");
        trapXsection.setPrefWidth(65);
        
        HBox xsectionBox = new HBox(trapXsectionLabel, trapXsection);
        xsectionBox.getStyleClass().add("internalhbox");
        
        Label trapEnergyLabel = new Label("Trap energy (eV)");
        trapEnergyLabel.getStyleClass().add("windowtext");
        
        TextField trapEnergy = new TextField("");
        trapEnergy.getStyleClass().add("inputfield");
        trapEnergy.setPrefWidth(65);
        
        HBox energyBox = new HBox(trapEnergyLabel, trapEnergy);
        energyBox.getStyleClass().add("internalhbox");
        
        HBox trapParameters = new HBox(densityBox, xsectionBox, energyBox);
        trapParameters.getStyleClass().add("parametershbox");
        
        VBox trapBox = new VBox(trapTitle, trapParameters);
        trapBox.getStyleClass().add("internalvbox");
        
        m_outerVBox.getChildren().add(trapBox);
        m_trapBoxes.add(trapBox);
    }
    
    public void attach(Pane p_attachPoint)
    {
        p_attachPoint.getChildren().add(m_outerVBox);
    }
    
    public void changeNotchPositionUnit(String p_newUnit, BigDecimal p_previousMultiplier, BigDecimal p_newMultiplier)
    {
        m_notchPositionLabel.setText("Notch Position (" + p_newUnit + ")");
        
        String notchPositionString = m_notchPosition.getText();
        
        if (!notchPositionString.equals(""))
        {
            notchPositionString = (new BigDecimal(notchPositionString)).multiply(p_previousMultiplier).divide(p_newMultiplier).stripTrailingZeros().toPlainString();
        }
        
        m_notchPosition.setText(notchPositionString);
    }
    
    public SampleBox copy()
    {
        saveData();
        
        return new SampleBox(m_fileField.getText(), new HashMap(m_gradingProfile), new ArrayList(m_traps));
    }
    
    @Override
    public File getConfigFile()
    {
        return m_configFile;
    }
    
    @Override
    public HashMap<String, BigDecimal> getGrading()
    {
        HashMap<String, BigDecimal> returnMap = new HashMap<>();
        
        try
        {
            for (String key: m_gradingProfile.keySet())
            {
                returnMap.put(key, new BigDecimal(m_gradingProfile.get(key)));
            }
        }
        catch(NumberFormatException ex)
        {
            Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Problem with the grading numbers", ex);
        }
        
        return returnMap;
    }
    
    public HashMap<String, String> getStringGrading()
    {
        return new HashMap<>(m_gradingProfile);
    }
    
    public ArrayList<HashMap<String, String>> getStringTraps()
    {
        return new ArrayList(m_traps);
    }
    
    @Override
    public ArrayList<HashMap<String, BigDecimal>> getTraps()
    {
        ArrayList<HashMap<String, BigDecimal>> returnList = new ArrayList<>();
        
        try
        {
            for (int i = 0 ; i < m_traps.size() ; i += 1)
            {
                if (m_trapBoxes.get(i).isVisible())
                {
                    HashMap<String, String> trap = m_traps.get(i);
                    
                    String density = trap.get("density");
                    String xsection = trap.get("cross-section");
                    String energy = trap.get("energy");

                    if (!(density.equals("") || xsection.equals("") || energy.equals("")))
                    {
                        HashMap<String, BigDecimal> currentMap = new HashMap<>();

                        currentMap.put("density", new BigDecimal(density));
                        currentMap.put("cross-section", new BigDecimal(xsection));
                        currentMap.put("energy", new BigDecimal(energy));

                        returnList.add(currentMap);
                    }
                }
            }
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Problem with the traps values", ex);
        }
        catch (NullPointerException ex)
        {
            Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Problem with the traps values", ex);
        }
        catch (IndexOutOfBoundsException ex)
        {
            Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Traps list and trap boxes list of different size", ex);
        }
        
        return returnList;
    }
    
    public int numberOfTraps()
    {
        return m_trapBoxes.size();
    }
    
    public void hide()
    {
        m_outerVBox.setVisible(false);
        m_outerVBox.setManaged(false);
    }
    
    public void hideGrading()
    {
        m_gradingBox.setManaged(false);
        m_gradingBox.setVisible(false);
    }
    
    public void hideOrRemoveTrap(int p_position)
    {
        VBox currentTrap = m_trapBoxes.get(p_position);
        
        currentTrap.setVisible(false);
        currentTrap.setManaged(false);
        
        if (isEmptyTrap(p_position))
        {
            m_trapBoxes.remove(p_position);
            m_traps.remove(p_position);
        }
    }
    
    public boolean isEmpty()
    {
        boolean returnBoolean = (m_fileField.getText().equals(""));
        
        if (m_gradingBox.isVisible())
        {
            String notchPos = m_notchPosition.getText();
            String frontGap = m_frontGap.getText();
            String notchGap = m_notchGap.getText();
            String backGap = m_backGap.getText();
            
            returnBoolean &= (notchPos == null || notchPos.equals("")) && (frontGap == null || frontGap.equals("")) && (notchGap == null || notchGap.equals("")) && (backGap == null || backGap.equals(""));
        }
        
        for (int i = 0 ; i < m_trapBoxes.size() ; i += 1)
        {
            returnBoolean &= isEmptyTrap(i);
        }
        
        return returnBoolean;
    }
    
    public void set (String p_address, HashMap<String, String> p_grading, List<HashMap<String, String>> p_traps)
    {
        m_configFile = new File(p_address);
        m_fileField.setText(p_address);
        
        m_gradingProfile = new HashMap(p_grading);
        m_frontGap.setText(p_grading.containsKey("front") ? p_grading.get("front") : "");
        m_notchGap.setText(p_grading.containsKey("notchgap") ? p_grading.get("notchgap") : "");
        m_backGap.setText(p_grading.containsKey("back") ? p_grading.get("back") : "");
        m_notchPosition.setText(p_grading.containsKey("notchposition") ? p_grading.get("notchposition") : "");
        
        for (int i = 0 ; i < p_traps.size() ; i += 1)
        {
            HashMap<String, String> currentTrap = p_traps.get(i);
            m_traps.set(i, new HashMap(currentTrap));
            
            HBox trapParameters = (HBox) m_trapBoxes.get(i).getChildren().get(1);
            HBox densityBox = (HBox) trapParameters.getChildren().get(0);
            HBox xsectionBox = (HBox) trapParameters.getChildren().get(1);
            HBox energyBox = (HBox) trapParameters.getChildren().get(2);
            
            ((TextField) densityBox.getChildren().get(1)).setText(currentTrap.containsKey("density") ? currentTrap.get("density") : "");
            ((TextField) xsectionBox.getChildren().get(1)).setText(currentTrap. containsKey("cross-section") ? currentTrap.get("cross-section") : "");
            ((TextField) energyBox.getChildren().get(1)).setText(currentTrap.containsKey("energy") ? currentTrap.get("energy") : "");
        }
    }
    
    public void show(int sampleNumber)
    {
        m_title.setText("Sample " + sampleNumber);
        
        m_outerVBox.setVisible(true);
        m_outerVBox.setManaged(true);
    }
    
    public void showGrading()
    {
        m_gradingBox.setManaged(true);
        m_gradingBox.setVisible(true);
    }
    
    public void showTrap(int p_position)
    {
        m_trapBoxes.get(p_position).setVisible(true);
        m_trapBoxes.get(p_position).setManaged(true);
    }
}
