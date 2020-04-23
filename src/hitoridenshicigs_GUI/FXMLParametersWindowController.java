/*
 * Copyright (C) 2020 audreyazura
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
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author audreyazura
 */
public class FXMLParametersWindowController
{
    @FXML private TextField biasvoltages;
    @FXML private TextField notches;
    @FXML private TextField generationpositions;
    @FXML private TextField samplewidth;
    @FXML private TextField effectivemass;
    @FXML private TextField lifetime;
    @FXML private TextField bufferwindowwidth;
    @FXML private TextField numbersimulated;
    @FXML private TextField inputFolder;
    @FXML private TextField outputFolder;
    @FXML private RadioButton electronselection;
    @FXML private RadioButton zeroatfront;
    @FXML private RadioButton unit;
    private MainWindowCall m_mainApp;
    
    void setMainWindow (HitoriDenshiCIGS_GUI p_mainGUI)
    {
        m_mainApp = p_mainGUI;
    }
    
    @FXML private void BrowsingInput (ActionEvent event)
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
	    System.err.println("Input folder addfress empty!!!!!!!");
	}
    }
    
    @FXML private void BrowsingOutput (ActionEvent event)
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
	    System.err.println("Input folder addfress empty!!!!!!!");
	}
    }
    
    @FXML private void StartSimulation (ActionEvent event)
    {
        String biasVoltagesList = biasvoltages.getText();
        String notchesList = notches.getText();
        String initialPositionsList = generationpositions.getText();
        String inputFolderAddress = inputFolder.getText();
        String outputFolderAddress = outputFolder.getText();
        
        boolean isElectron = electronselection.isSelected();
        boolean zeroFront = zeroatfront.isSelected();
        boolean isMicrometer = unit.isSelected();
        
        try
        {
            double totalSampleWidth = Double.parseDouble(samplewidth.getText());
            double bufferWindowSize = Double.parseDouble(bufferwindowwidth.getText());
            double effectiveMassDouble = Double.parseDouble(effectivemass.getText());
            double lifetimeDouble = Double.parseDouble(lifetime.getText());
            int numberSimulatedParticle = Integer.parseInt(numbersimulated.getText());
        
            CalculationConditions conditions = new CalculationConditions(isElectron, zeroFront, isMicrometer, numberSimulatedParticle, effectiveMassDouble, lifetimeDouble, bufferWindowSize, totalSampleWidth, biasVoltagesList, notchesList, initialPositionsList);
            HitoriDenshiCIGS_Simulation simu = new HitoriDenshiCIGS_Simulation();
            simu.startSimulation(inputFolderAddress, outputFolderAddress, conditions);
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Verify you have writtem a number in the sample size field, the buffer+window size field and the number of simulated particle field.");
        }
    }
}
