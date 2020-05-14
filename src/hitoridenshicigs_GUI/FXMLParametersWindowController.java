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
package hitoridenshicigs_GUI;

import hitoridenshicigs_simulation.CalculationConditions;
import hitoridenshicigs_simulation.HitoriDenshiCIGS_Simulation;
import hitoridenshicigs_simulation.PhysicalConstants;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author audreyazura
 */
public class FXMLParametersWindowController
{
    
    @FXML private ChoiceBox unitselec;
    @FXML private Label notchlabel;
    @FXML private Label generationlabel;
    @FXML private Label samplewidthlabel;
    @FXML private Label bufferwindowlabel;
    @FXML private RadioButton electronselection;
    @FXML private RadioButton holeselection;
    @FXML private RadioButton zeroatfront;
    @FXML private TextField biasvoltages;
    @FXML private TextField notches;
    @FXML private TextField generationpositions;
    @FXML private TextField bufferwindowwidth;
    @FXML private TextField samplewidth;
    @FXML private TextField effectivemass;
    @FXML private TextField lifetime;
    @FXML private TextField numbersimulated;
    @FXML private TextField inputFolder;
    @FXML private TextField outputFolder;
    @FXML private TextField frontbangap;
    @FXML private TextField notchbandgap;
    @FXML private TextField backbangap;
    
    private MainWindowCall m_mainApp;
    private PhysicalConstants.UnitsPrefix m_previouslySelectedUnit = PhysicalConstants.UnitsPrefix.BASE;
    
    void setMainWindow (HitoriDenshiCIGS_GUI p_mainGUI)
    {
        m_mainApp = p_mainGUI;
    }
    
    @FXML private void savePreviousSelection ()
    {
        try
        {
            m_previouslySelectedUnit = selectPrefix((String) unitselec.getValue());
        }
        catch (NullPointerException ex)
        {
            m_previouslySelectedUnit = PhysicalConstants.UnitsPrefix.BASE;
        }
    }
    
    @FXML private void applyNewUnitSelection (ActionEvent event)
    {
        PhysicalConstants.UnitsPrefix currentPrefix = PhysicalConstants.UnitsPrefix.BASE;
        try
        {
            String selectedUnit = (String) unitselec.getValue();
            
            currentPrefix = selectPrefix(selectedUnit);
            if (m_previouslySelectedUnit == PhysicalConstants.UnitsPrefix.BASE)
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
    
    @FXML private void changeSelectedParticle(ActionEvent event)
    {
        if (electronselection.isSelected())
        {
            effectivemass.setText("0.089");
            lifetime.setText("100");
            numbersimulated.setText("10000");
        }
        else if (holeselection.isSelected())
        {
            effectivemass.setText("0.693");
            lifetime.setText("50");
            numbersimulated.setText("5000");
        }
    }
    
    @FXML private void browsingInput (ActionEvent event)
    {
        DirectoryChooser browser = new DirectoryChooser();
	
	browser.setTitle("Chose the folder containing the input files");
	
	try
        {
            String fieldText = inputFolder.getText();
            browser.setInitialDirectory(new File((new File(fieldText)).getParent()));
        }
        catch (NullPointerException ex)
        {
            browser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        try
	{
            String adress = browser.showDialog(m_mainApp.getMainStage()).getAbsolutePath();
            inputFolder.setText(adress);
	}
	catch (NullPointerException ex)
	{
	    System.err.println("Input folder address empty!!!!!!!");
	}
    }
    
    @FXML private void browsingOutput (ActionEvent event)
    {
        DirectoryChooser browser = new DirectoryChooser();
	
	browser.setTitle("Chose the output folder");
	
	try
        {
            String fieldText = outputFolder.getText();
            browser.setInitialDirectory(new File((new File(fieldText)).getParent()));
        }
        catch (NullPointerException ex)
        {
            browser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        try
	{
            String adress = browser.showDialog(m_mainApp.getMainStage()).getAbsolutePath();
            outputFolder.setText(adress);
	}
	catch (NullPointerException ex)
	{
	    System.err.println("Input folder address empty!!!!!!!");
	}
    }
    
    @FXML private void startSimulation (ActionEvent event)
    {
        String biasVoltagesList = biasvoltages.getText();
        String notchesList = notches.getText();
        String initialPositionsList = generationpositions.getText();
        String inputFolderAddress = inputFolder.getText();
        String outputFolderAddress = outputFolder.getText();
        
        boolean isElectron = electronselection.isSelected();
        boolean zeroFront = zeroatfront.isSelected();
        
        try
        {
            BigDecimal totalSampleWidth = new BigDecimal(samplewidth.getText());
            BigDecimal bufferWindowSize = new BigDecimal(bufferwindowwidth.getText());
            BigDecimal effectiveMassDouble = new BigDecimal(effectivemass.getText());
            BigDecimal lifetimeNumber = new BigDecimal(lifetime.getText());
            BigDecimal frontBangapNumber = new BigDecimal(frontbangap.getText());
            BigDecimal notchBandgapNumber = new BigDecimal(notchbandgap.getText());
            BigDecimal backBangapNumber = new BigDecimal(backbangap.getText());
            int numberSimulatedParticle = Integer.parseInt(numbersimulated.getText());
            
            PhysicalConstants.UnitsPrefix passedUnit = selectPrefix((String) unitselec.getValue());
        
            CalculationConditions conditions = new CalculationConditions(isElectron, zeroFront, passedUnit, numberSimulatedParticle, effectiveMassDouble, lifetimeNumber, bufferWindowSize, totalSampleWidth, frontBangapNumber, notchBandgapNumber, backBangapNumber, biasVoltagesList, notchesList, initialPositionsList);
            Thread simulationThread = new Thread(new HitoriDenshiCIGS_Simulation(inputFolderAddress, outputFolderAddress, conditions));
            simulationThread.start();
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Verify you have writtem a number in the sample size field, the buffer+window size field and the number of simulated particle field.");
//            ex.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            System.err.println("Verify that each field is properly filled.");
//            ex.printStackTrace();
        }
    }
    
    private PhysicalConstants.UnitsPrefix selectPrefix (String p_unit)
    {
        PhysicalConstants.UnitsPrefix unitSelected = PhysicalConstants.UnitsPrefix.BASE;
        
        switch (p_unit)
        {
            case "nm":
                unitSelected = PhysicalConstants.UnitsPrefix.NANO;
                break;
            case "μm":
                unitSelected = PhysicalConstants.UnitsPrefix.MICRO;
                break;
        }
        
        return unitSelected;
    }
    
    private String changeEnteredNumberUnit (String p_numberEntered, BigDecimal p_previousMultiplier, BigDecimal p_newMultiplier)
    {
        String correctedNumber = new String(p_numberEntered);
        
        if (correctedNumber.equals(""))
        {
            correctedNumber = "0";
        }
        
        return (new BigDecimal(correctedNumber)).multiply(p_previousMultiplier).divide(p_newMultiplier).stripTrailingZeros().toPlainString();
    }
}
