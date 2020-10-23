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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author audreyazura
 */
public class SampleBox implements Sample
{
    private final Label m_title = new Label("Sample #");
    private final Button m_updateButton = new Button("Update data");
    private final HBox m_titleBox = new HBox(m_title, m_updateButton);
    
    private final Label m_fileLabel = new Label("SCAPS Energy Band files (*.eb)");
    private final TextField m_fileField = new TextField("");
    private final Button m_browseButton = new Button("Browse");
    private final HBox m_browseBox = new HBox(m_fileField, m_browseButton);
    private final VBox m_fileBox = new VBox(m_fileLabel, m_browseBox);
    
    private final Label m_gradingLabel = new Label("Grading");
    private final Label m_frontGapLabel = new Label("Front Bandgap (eV)");
    private final TextField m_frontGap = new TextField("");
    private final HBox m_frontBox = new HBox(m_frontGapLabel, m_frontGap);
    private final Label m_notchGapLabel = new Label("Notch Bandgap (eV)");
    private final TextField m_notchGap = new TextField("");
    private final HBox m_notchGapBox = new HBox(m_notchGapLabel, m_notchGap);
    private final Label m_backGapLabel = new Label("Back Bandgap (eV)");
    private final TextField m_backGap = new TextField("");
    private final HBox m_backBox = new HBox(m_backGapLabel, m_backGap);
    private final Label m_notchPositionLabel = new Label("Notch Position (nm)");
    private final TextField m_notchPosition = new TextField("");
    private final HBox m_notchPositionBox = new HBox(m_notchPositionLabel, m_notchPosition);
    private final HBox m_gradingOptionBox = new HBox(m_frontBox, m_notchGapBox, m_backBox, m_notchPositionBox);
    private final VBox m_gradingBox = new VBox(m_gradingLabel, m_gradingOptionBox);
    
    private final VBox m_parametersVBox = new VBox(m_fileBox);
    
    private final VBox m_outerVBox = new VBox(m_titleBox, m_parametersVBox);
    
    private final List<VBox> m_trapBoxes = new ArrayList<>();
    
    private File m_configFile;
    private HashMap<String, String> m_gradingProfile;
    private List<HashMap<String, String>> m_traps;
    
    public SampleBox (File p_file)
    {
        baseInitialization(p_file);
    }
    
    public SampleBox (File p_file, HashMap<String, String> p_grading)
    {
        baseInitialization(p_file);
        initiateGrading(p_grading);
    }
    
    public SampleBox (File p_file, HashMap<String, String> p_grading, List<HashMap<String, String>> p_traps)
    {
        baseInitialization(p_file);
        initiateGrading(p_grading);
        
        m_traps = p_traps;
        for (int i = 0 ; i < m_traps.size() ; i += 1)
        {
            HashMap<String, String> currentTrap = m_traps.get(i);
            
            addTrap(i+1);
            
            HBox trapParameters = (HBox) m_trapBoxes.get(i).getChildren().get(1);
            HBox densityBox = (HBox) trapParameters.getChildren().get(0);
            HBox xsectionBox = (HBox) trapParameters.getChildren().get(1);
            HBox energyBox = (HBox) trapParameters.getChildren().get(2);
            
            ((TextField) densityBox.getChildren().get(1)).setText(currentTrap.get("density"));
            ((TextField) xsectionBox.getChildren().get(1)).setText(currentTrap.get("cross-section"));
            ((TextField) energyBox.getChildren().get(1)).setText(currentTrap.get("energy"));
        }
    }
    
    private void baseInitialization (File p_file)
    {
        m_configFile = p_file;
        m_fileField.setText(p_file.getAbsolutePath());
        
        m_updateButton.setOnAction(() ->
        {
            updateData();
        });
        m_browseButton.setOnAction(() ->
        {
            browse();
        });
    }
    
    private void initiateGrading (HashMap<String, String> p_grading)
    {
        m_gradingProfile = p_grading;
        
        m_frontGap.setText(p_grading.get("front"));
        m_notchGap.setText(p_grading.get("notchgap"));
        m_backGap.setText(p_grading.get("back"));
        m_notchPosition.setText(p_grading.get("notchposition"));
        m_outerVBox.getChildren().add(m_gradingBox);
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
    
    private void updateData ()
    {
        m_gradingProfile.put("front", m_frontGap.getText());
        m_gradingProfile.put("notchgap", m_notchGap.getText());
        m_gradingProfile.put("back", m_backGap.getText());
        m_gradingProfile.put("notchposition", m_notchPosition.getText());
        
        for (int i = 0 ; i < m_traps.size() ; i += 1)
        {
            try
            {
                HBox parametersBox = (HBox) m_trapBoxes.get(i).getChildren().get(2);
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
    
    public void addTrap(int p_trapIndex)
    {
        m_traps.add(new HashMap<String,String>());
        
        Label trapTitle = new Label("Trap #"+p_trapIndex);
        Label trapDensityLabel = new Label("Trap density (cm⁻³)");
        TextField trapDensity = new TextField("");
        HBox densityBox = new HBox(trapDensityLabel, trapDensity);
        Label trapXsectionLabel = new Label("Trap cross-section (cm⁻²)");
        TextField trapXsection = new TextField("");
        HBox xsectionBox = new HBox(trapXsectionLabel, trapXsection);
        Label trapEnergyLabel = new Label("Trap energy (eV)");
        TextField trapEnergy = new TextField("");
        HBox energyBox = new HBox(trapEnergyLabel, trapEnergy);
        HBox trapParameters = new HBox(densityBox, xsectionBox, energyBox);
        VBox trapBox = new VBox(trapTitle, trapParameters);
        
        m_outerVBox.getChildren().add(trapBox);
        m_trapBoxes.add(trapBox);
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
    
    @Override
    public ArrayList<HashMap<String, BigDecimal>> getTraps()
    {
        ArrayList<HashMap<String, BigDecimal>> returnList = new ArrayList<>();
        
        try
        {
            for (HashMap<String, String> trap: m_traps)
            {
                HashMap<String, BigDecimal> currentMap = new HashMap<>();
                
                for (String key: trap.keySet())
                {
                    currentMap.put(key, new BigDecimal(trap.get(key)));
                }
                
                returnList.add(currentMap);
            }
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(SampleBox.class.getName()).log(Level.SEVERE, "Problem with the grading numbers", ex);
        }
        
        return returnList;
    }
}
