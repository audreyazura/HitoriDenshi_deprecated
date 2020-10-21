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
package hitoridenshi.consolemanager;

import com.github.audreyazura.commonutils.PhysicsTools;
import hitoridenshi.executionmanager.OutputInterface;
import hitoridenshi.guimanager.GUIManager;
import hitoridenshi.simulationmanager.CalculationConditions;
import hitoridenshi.simulationmanager.SimulationManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.studer.java.util.OrderedProperties;

/**
 * Manage the information passed to the software user when executing in a terminal
 * @author audreyazura
 */
public class ConsoleManager implements OutputInterface
{
    /**
     * Creates a CalculationCondition object from passed properties
     * @param p_properties the properly formatted properties
     * @return the created calculation conditions
     * @throws StringIndexOutOfBoundsException
     * @throws NumberFormatException 
     */    
    private CalculationConditions getCalculationConditions(OrderedProperties p_properties) throws StringIndexOutOfBoundsException, NumberFormatException 
    {
        boolean isElectron = (p_properties.getProperty("simulated_particle").equals("electron"));
        boolean zeroAtFront = (p_properties.getProperty("zero_position").equals("front"));
        
        String biasVoltagesList = p_properties.getProperty("bias_voltages");
        String notchesList = p_properties.getProperty("notch_positions");
        String initialPositionsList = p_properties.getProperty("generation_positions");
        String fileString = p_properties.getProperty("input_files");
        
        PhysicsTools.UnitsPrefix unitPrefix = PhysicsTools.UnitsPrefix.selectPrefix(p_properties.getProperty("abscissa_unit"));
            
        BigDecimal totalSampleWidth = new BigDecimal(p_properties.getProperty("sample_width"));
        BigDecimal bufferWindowSize = new BigDecimal(p_properties.getProperty("bufferwindow width"));
        BigDecimal effectiveMassDouble = new BigDecimal(p_properties.getProperty("effective_mass"));
        BigDecimal lifetimeNumber = new BigDecimal(p_properties.getProperty("lifetime"));
        
        int numberSimulatedParticle = Integer.parseInt(p_properties.getProperty("number_of_simulated_particles"));

        CalculationConditions conditions = new CalculationConditions(isElectron, zeroAtFront, unitPrefix, numberSimulatedParticle, effectiveMassDouble, lifetimeNumber, bufferWindowSize, totalSampleWidth, biasVoltagesList, notchesList, initialPositionsList, fileString);
        
        if (p_properties.getProperty("has_grading").equals("true"))
        {
            BigDecimal frontBangapNumber = new BigDecimal(p_properties.getProperty("front_bandgap"));
            BigDecimal minimumBandgapNumber = new BigDecimal(p_properties.getProperty("minimum_bandgap"));
            BigDecimal backBangapNumber = new BigDecimal(p_properties.getProperty("back_bandgap"));
            conditions.addGrading(frontBangapNumber, minimumBandgapNumber, backBangapNumber);
        }
        
        return conditions;
    }
    
    @Override
    public void startOutput(String[] args)
    {
        File propertiesFile = new File(args[0]);
        try
        {
            Reader fileReader = new FileReader(propertiesFile);
            OrderedProperties properties = new OrderedProperties();
            properties.load(fileReader);
            
            CalculationConditions conditions = getCalculationConditions(properties);
            String inputFolderAddress = properties.getProperty("input_folder");
            String outputFolderAddress = properties.getProperty("output_folder");
            
            SimulationManager simulationLauncher = new SimulationManager(inputFolderAddress, outputFolderAddress, conditions, this);
            Thread simulationThread = new Thread(simulationLauncher);
            simulationThread.start();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (StringIndexOutOfBoundsException ex)
        {
            System.err.println("The abscissa unit field is empty");
            System.exit(0);
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Verify you have written a number in the sample size field, the buffer+window size field and the number of simulated particle field.");
            System.exit(0);
        }
    }
    
    @Override
    public void updateProgress (int p_workerID, double p_workerProgress, double p_globalProgress)
    {
        //would spam the console, so do nothing in this case
    }
    
    @Override
    public void sendMessage (String p_message)
    {
        System.out.println(p_message);
    }
}
