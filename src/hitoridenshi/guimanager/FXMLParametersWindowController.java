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

import hitoridenshi.simulationmanager.CalculationConditions;
import hitoridenshi.simulationmanager.SimulationManager;
import commonutils.PhysicsTools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import nu.studer.java.util.OrderedProperties;
import hitoridenshi.simulationmanager.ProgressNotifierInterface;

/**
 * 
 * @author Alban Lafuente
 */
public class FXMLParametersWindowController
{
    @FXML private CheckBox includegrading;
    @FXML private CheckBox includeqds;
    @FXML private CheckBox includetraps;
    @FXML private ChoiceBox unitselec;
    @FXML private ChoiceBox materialselec;
    @FXML private HBox gradingbox;
    @FXML private HBox trapbox;
    @FXML private HBox qdbox;
    @FXML private Label notchlabel;
    @FXML private Label generationlabel;
    @FXML private Label samplewidthlabel;
    @FXML private Label bufferwindowlabel;
    @FXML private RadioButton originatfront;
    @FXML private RadioButton originatback;
    @FXML private RadioButton electronselection;
    @FXML private RadioButton holeselection;
    @FXML private TextField biasvoltages;
    @FXML private TextField notches;
    @FXML private TextField generationpositions;
    @FXML private TextField samplewidth;
    @FXML private TextField bufferwindowwidth;
    @FXML private TextField frontbangap;
    @FXML private TextField notchbandgap;
    @FXML private TextField backbangap;
    @FXML private TextField effectivemass;
    @FXML private TextField lifetime;
    @FXML private TextField numbersimulated;
    @FXML private TextField inputFolder;
    @FXML private TextField outputFolder;
    @FXML private TextField trapcapture;
    @FXML private TextField trapdensity;
    @FXML private TextField qdcapture;
    @FXML private TextField qddepth;
    @FXML private TextField qddensity;
    
    private MainWindowCall m_mainApp;
    private PhysicsTools.UnitsPrefix m_previouslySelectedUnit = PhysicsTools.UnitsPrefix.UNITY;
    
    /**
     * Save the previously selected unit in the unitselec field to apply changes later
     */
    @FXML private void savePreviousSelection ()
    {
        try
        {
            m_previouslySelectedUnit = PhysicsTools.UnitsPrefix.selectPrefix((String) unitselec.getValue());
        }
        catch (NullPointerException|StringIndexOutOfBoundsException ex)
        {
            m_previouslySelectedUnit = PhysicsTools.UnitsPrefix.UNITY;
        }
    }
    
    /**
     * convert the distance and position entered in the interface to the newly selected unit
     */
    @FXML private void applyNewUnitSelection ()
    {
        PhysicsTools.UnitsPrefix currentPrefix = PhysicsTools.UnitsPrefix.UNITY;
        
        try
        {
            String selectedUnit = (String) unitselec.getValue();
            
            if (!selectedUnit.isEmpty())
            {
                currentPrefix = PhysicsTools.UnitsPrefix.selectPrefix(selectedUnit);
            }
            if (m_previouslySelectedUnit == PhysicsTools.UnitsPrefix.UNITY)
            {
                m_previouslySelectedUnit = currentPrefix;
            }
            
            notchlabel.setText("Notch positions in the absorber ("+selectedUnit+", separated by ';'):");
            generationlabel.setText("Generation positions ("+selectedUnit+", separated by ';'):");
            samplewidthlabel.setText("Sample width ("+selectedUnit+"):");
            bufferwindowlabel.setText("Buffer+window width ("+selectedUnit+"):");
            
            BigDecimal previousMultiplier = m_previouslySelectedUnit.getMultiplier();
            BigDecimal currentMultiplier = currentPrefix.getMultiplier();

            bufferwindowwidth.setText(changeEnteredNumberUnit(bufferwindowwidth.getText(), previousMultiplier, currentMultiplier));
            samplewidth.setText(changeEnteredNumberUnit(samplewidth.getText(), previousMultiplier, currentMultiplier));

            List<String> newNotchPositions = new ArrayList<>();
            Arrays.asList(notches.getText().strip().split("\\h*;\\h*")).forEach(new Consumer<String>()
            {
                @Override
                public void accept(String position)
                {
                    newNotchPositions.add(changeEnteredNumberUnit(position, previousMultiplier, currentMultiplier));
                }
            });
            notches.setText(String.join(" ; ", newNotchPositions));

            List<String> newGenerationPositions = new ArrayList<>();
            Arrays.asList(generationpositions.getText().strip().split("\\h*;\\h*")).forEach(new Consumer<String>()
            {
                @Override
                public void accept(String position)
                {
                    newGenerationPositions.add(changeEnteredNumberUnit(position, previousMultiplier, currentMultiplier));
                }
            });
            generationpositions.setText(String.join(" ; ", newGenerationPositions));
        }
        catch (NullPointerException ex)
        {
            System.err.println("Select a proper unit!");
        }
    }
            
    /**
     * launch a FileChooser to select a configuration file (properties file) 
     * load software configuration from the selected properties file
     */
    @FXML private void loadconfig ()
    {
        FileChooser browser = new FileChooser();
        browser.setInitialDirectory(new File("ConfigurationFiles"));
        browser.setTitle("Chose the file to load the configuration from");
        browser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Configuration files (*.conf)", "*.conf"), new FileChooser.ExtensionFilter("Properties files (*.properties)", "*.properties"), new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        
        File selectedFile = browser.showOpenDialog(m_mainApp.getMainStage());
        
        if (selectedFile != null)
        {
            try
            {
                Reader inputReader = new FileReader(selectedFile);
                OrderedProperties selectedProperties = new OrderedProperties();
                selectedProperties.load(inputReader);
                loadProperties(selectedProperties);
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(FXMLParametersWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex)
            {
                Logger.getLogger(FXMLParametersWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    /**
     * launch a FileChooser to select the file in which the configuration will be saved
     * save the configuration to said file
     */
    @FXML private void saveconfig ()
    {
        FileChooser browser = new FileChooser();
	browser.setTitle("Chose the file to write the configuration in");
        browser.setInitialDirectory(new File("ConfigurationFiles"));
        browser.setInitialFileName("MyConfig.conf");
        
        File toWriteFile = browser.showSaveDialog(m_mainApp.getMainStage());
        
        if (toWriteFile != null)
        {
            writeConfigToFile(toWriteFile);
        }
    }
    
    /**
     * save the current configuration as the default configuration, in the file default.conf
     */
    @FXML private void makedefault()
    {
        writeConfigToFile(new File("ConfigurationFiles/default.conf"));
    }
    
    @FXML private void showgrading()
    {
        if(includegrading.isSelected())
        {
            gradingbox.setVisible(true);
            gradingbox.setManaged(true);
            m_mainApp.getMainStage().sizeToScene();
        }
        else
        {
            gradingbox.setVisible(false);
            gradingbox.setManaged(false);
            m_mainApp.getMainStage().sizeToScene();
        }
    }
    
    @FXML private void showtraps()
    {
        if(includetraps.isSelected())
        {
            trapbox.setVisible(true);
            trapbox.setManaged(true);
            m_mainApp.getMainStage().sizeToScene();
        }
        else
        {
            trapbox.setVisible(false);
            trapbox.setManaged(false);
            m_mainApp.getMainStage().sizeToScene();
        }
    }
    
    @FXML private void showqds()
    {
        if(includeqds.isSelected())
        {
            qdbox.setVisible(true);
            qdbox.setManaged(true);
            m_mainApp.getMainStage().sizeToScene();
        }
        else
        {
            qdbox.setVisible(false);
            qdbox.setManaged(false);
            m_mainApp.getMainStage().sizeToScene();
        }
    }
    
    /**
     * automatically changes the particle dependent parameters after the particle selection
     * to be refactored once different material are introduce. Materials will be listed in PhysicalConstant.
     */
    @FXML private void changeSelectedParticle()
    {
        PhysicsTools.Materials material = PhysicsTools.Materials.getMaterialFromString((String) materialselec.getValue());
        
        if (electronselection.isSelected())
        {
            effectivemass.setText(material.getElectronEffectiveMass().stripTrailingZeros().toPlainString());
            lifetime.setText("100");
            numbersimulated.setText("10000");
        }
        else if (holeselection.isSelected())
        {
            effectivemass.setText(material.getHoleEffectiveMass().stripTrailingZeros().toPlainString());
            lifetime.setText("50");
            numbersimulated.setText("5000");
        }
    }
    
    /**
     * launch a DirectoryChooser to select the input directory and write its address in the field inputFolder
     */
    @FXML private void browsingInput ()
    {
        DirectoryChooser browser = new DirectoryChooser();
	browser.setTitle("Chose the folder containing the input files");
        
        String fieldText = inputFolder.getText();
        try
        {
            browser.setInitialDirectory(new File((new File(fieldText)).getParent()));
        }
        catch (NullPointerException ex)
        {
            browser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        File selectedFolder = browser.showDialog(m_mainApp.getMainStage());
        if (selectedFolder != null)
        {
            inputFolder.setText(selectedFolder.getAbsolutePath());
        }
        else
        {
            inputFolder.setText(fieldText);
        }
    }
    
    /**
     * launch a DirectoryChooser to select the output directory and write its address in the field inputFolder
     */
    @FXML private void browsingOutput ()
    {
        DirectoryChooser browser = new DirectoryChooser();
	browser.setTitle("Chose the output folder");
        
        String fieldText = outputFolder.getText();
        try
        {
            browser.setInitialDirectory(new File((new File(fieldText)).getParent()));
        }
        catch (NullPointerException ex)
        {
            browser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        File selectedFolder = browser.showDialog(m_mainApp.getMainStage());
        if (selectedFolder != null)
        {
            outputFolder.setText(selectedFolder.getAbsolutePath());
        }
        else
        {
            outputFolder.setText(fieldText);
        }
    }
    
    /**
     * save the current configuration to an OrderedProperties for future use (when reloading the paramater windows)
     * create a CalculationCondition object from the parameters of the window and start a simulation with it
     * call the window tracking the ongoing simulation
     */
    @FXML private void startSimulation ()
    {
        //temporarily saving current configuration in Properties
        OrderedProperties tempProp = writeConfigToProperties();
        
        //configuring the simulation and launching it
        try
        {
            CalculationConditions conditions = new CalculationConditions(electronselection.isSelected(), originatfront.isSelected(), PhysicsTools.UnitsPrefix.selectPrefix((String) unitselec.getValue()), Integer.parseInt(numbersimulated.getText()), new BigDecimal(effectivemass.getText()), new BigDecimal(lifetime.getText()), new BigDecimal(bufferwindowwidth.getText()), new BigDecimal(samplewidth.getText()), biasvoltages.getText(), notches.getText(), generationpositions.getText());
            
            if (includegrading.isSelected())
            {
                conditions.addGrading(new BigDecimal(frontbangap.getText()), new BigDecimal(notchbandgap.getText()), new BigDecimal(backbangap.getText()));
            }
            
            if (includetraps.isSelected())
            {
                conditions.addTraps(new BigDecimal(trapdensity.getText()), new BigDecimal(trapcapture.getText()));
            }
            
            if (includeqds.isSelected())
            {
                conditions.addQDs(new BigDecimal(qddepth.getText()), new BigDecimal(qddensity.getText()), new BigDecimal(qdcapture.getText()));
            }
            
            SimulationManager simulationLauncher = new SimulationManager(inputFolder.getText(), outputFolder.getText(), conditions, (ProgressNotifierInterface) m_mainApp);
            m_mainApp.launchOnGoingSimulationWindow(simulationLauncher.getNumberOfWorker(), tempProp);
            Thread simulationThread = new Thread(simulationLauncher);
            simulationThread.start();
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Verify you have written numbers in the given fields.");
//            ex.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            System.err.println("Verify that each field is properly filled.");
//            ex.printStackTrace();
        }
    }
    
    /**
     * take a number as a string, as well as the multiplier of its previous unit and of its new unit in order to convert it to the new unit
     * @param p_numberEntered the number as string
     * @param p_previousMultiplier the multiplier of the previous unit
     * @param p_newMultiplier the multiplier of the new unit
     * @return the converted number as string
     */
    private String changeEnteredNumberUnit (String p_numberEntered, BigDecimal p_previousMultiplier, BigDecimal p_newMultiplier)
    {
        String correctedNumber = new String(p_numberEntered);
        
        if (!correctedNumber.equals(""))
        {
            correctedNumber = (new BigDecimal(correctedNumber)).multiply(p_previousMultiplier).divide(p_newMultiplier).stripTrailingZeros().toPlainString();
        }
        
        return correctedNumber;
    }
    
    /**
     * write the current configuration to the file passed
     * @param p_file the file to write the configuration to
     */
    private void writeConfigToFile (File p_file)
    {
        if (p_file.isFile())
        {
            p_file.delete();
        }
        if (p_file.isDirectory())
        {
            //throw error
        }
        
        try
        {
            Writer fileWriter = new FileWriter(p_file);
            writeConfigToProperties().store(fileWriter, null);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(FXMLParametersWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FXMLParametersWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * create an OrderedProperties from the current configuration
     * @return an OrderedProperties containing the current configuration
     */
    private OrderedProperties writeConfigToProperties ()
    {
        OrderedProperties extractedProperties = new OrderedProperties();
                
        extractedProperties.setProperty("abscissa_unit", ((String) unitselec.getValue()));
        extractedProperties.setProperty("material",  ((String) materialselec.getValue()));
        extractedProperties.setProperty("bias_voltages",  biasvoltages.getText());
        extractedProperties.setProperty("notch_positions",  notches.getText());
        extractedProperties.setProperty("generation_positions",  generationpositions.getText());
        extractedProperties.setProperty("origin_position",  (originatfront.isSelected() ? "front" : "back"));
        extractedProperties.setProperty("sample_width",  samplewidth.getText());
        extractedProperties.setProperty("bufferwindow_width",  bufferwindowwidth.getText());
        extractedProperties.setProperty("has_grading", includegrading.isSelected() ? "true":"false");
        extractedProperties.setProperty("front_bandgap",  frontbangap.getText());
        extractedProperties.setProperty("minimum_bandgap",  notchbandgap.getText());
        extractedProperties.setProperty("back_bandgap",  backbangap.getText());
        extractedProperties.setProperty("has_traps", includetraps.isSelected() ? "true":"false");
        extractedProperties.setProperty("trap_density", trapdensity.getText());
        extractedProperties.setProperty("trap_cross_section",  trapcapture.getText());
        extractedProperties.setProperty("has_QDs", includeqds.isSelected() ? "true":"false");
        extractedProperties.setProperty("QD_depth", qddepth.getText());
        extractedProperties.setProperty("QD_density", qddensity.getText());
        extractedProperties.setProperty("QD_cross_section", qdcapture.getText());
        extractedProperties.setProperty("simulated_particle",  (electronselection.isSelected() ? "electron" : "hole"));
        extractedProperties.setProperty("effective_mass",  effectivemass.getText());
        extractedProperties.setProperty("lifetime",  lifetime.getText());
        extractedProperties.setProperty("number_of_simulated_particles",  numbersimulated.getText());
        extractedProperties.setProperty("input_folder",  inputFolder.getText());
        extractedProperties.setProperty("output_folder",  outputFolder.getText());
        
        return extractedProperties;
    }
    
    /**
     * fill the configuration fields from the passed OrderedProperties
     * to be refactored
     * @param p_properties an OrderedProperties containing the configuration fields
     */
    private void loadProperties (OrderedProperties p_properties)
    {
        unitselec.setValue(p_properties.getProperty("abscissa_unit"));
        materialselec.setValue(p_properties.getProperty("material"));

        biasvoltages.setText(p_properties.getProperty("bias_voltages"));
        notches.setText(p_properties.getProperty("notch_positions"));
        generationpositions.setText(p_properties.getProperty("generation_positions"));
        samplewidth.setText(p_properties.getProperty("sample_width"));
        bufferwindowwidth.setText(p_properties.getProperty("bufferwindow_width"));
        frontbangap.setText(p_properties.getProperty("front_bandgap"));
        notchbandgap.setText(p_properties.getProperty("minimum_bandgap"));
        backbangap.setText(p_properties.getProperty("back_bandgap"));
        trapdensity.setText(p_properties.getProperty("trap_density"));
        trapcapture.setText(p_properties.getProperty("trap_cross_section"));
        qddepth.setText(p_properties.getProperty("QD_depth"));
        qddensity.setText(p_properties.getProperty("QD_density"));
        qdcapture.setText(p_properties.getProperty("QD_cross_section"));
        effectivemass.setText(p_properties.getProperty("effective_mass"));
        lifetime.setText(p_properties.getProperty("lifetime"));
        numbersimulated.setText(p_properties.getProperty("number_of_simulated_particles"));
        inputFolder.setText(p_properties.getProperty("input_folder"));
        outputFolder.setText(p_properties.getProperty("output_folder"));

        switch (p_properties.getProperty("origin_position"))
        {
            case "front":
                originatfront.setSelected(true);
                break;
            case "back":
                originatback.setSelected(true);
                break;
            case "":
                break;
            default:
                throw new RuntimeException("Invalid origin position: "+p_properties.getProperty("zero_position")+". Should be front, back or nothing.");
        }

        switch (p_properties.getProperty("simulated_particle"))
        {
            case "electron":
                electronselection.setSelected(true);
                break;
            case "hole":
                holeselection.setSelected(true);
                break;
            case "":
                break;
            default:
                throw new RuntimeException("Invalid particle: "+p_properties.getProperty("simulated_particle")+". Should be electron, hole or nothing.");
        }
        
        switch (p_properties.getProperty("has_grading"))
        {
            case "true":
                if (!includegrading.isSelected())
                {
                    includegrading.fire();
                }
                break;
            case "false":
                if (includegrading.isSelected())
                {
                    includegrading.fire();
                }
                break;
            default:
                throw new RuntimeException("Invalid grading property: "+p_properties.getProperty("has_grading")+". Should be either true or false.");
        }
        
        switch (p_properties.getProperty("has_traps"))
        {
            case "true":
                if (!includetraps.isSelected())
                {
                    includetraps.fire();
                }
                break;
            case "false":
                if (includetraps.isSelected())
                {
                    includetraps.fire();
                }
                break;
            default:
                throw new RuntimeException("Invalid traps property: "+p_properties.getProperty("has_traps")+". Should be either true or false.");
        }
        
        switch (p_properties.getProperty("has_QDs"))
        {
            case "true":
                if (!includeqds.isSelected())
                {
                    includeqds.fire();
                }
                break;
            case "false":
                if (includeqds.isSelected())
                {
                    includeqds.fire();
                }
                break;
            default:
                throw new RuntimeException("Invalid QDs property: "+p_properties.getProperty("has_qds")+". Should be either true or false.");
        }
    }
    
    /**
     * Initialize the controller of the configuration window
     * @param p_mainApp the main application for callback
     * @param p_configProperties an OrderedProperties with the contents of the different fields
     */
    public void initialize (MainWindowCall p_mainApp, OrderedProperties p_configProperties)
    {
        m_mainApp = p_mainApp;
        gradingbox.setVisible(false);
        gradingbox.setManaged(false);
        trapbox.setVisible(false);
        trapbox.setManaged(false);
        qdbox.setVisible(false);
        qdbox.setManaged(false);
        loadProperties(p_configProperties);
    }
}
