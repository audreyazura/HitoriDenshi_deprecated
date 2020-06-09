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

import hitoridenshi.executionmanager.OutputInterface;
import hitoridenshi.guimanager.GUIManager;
import hitoridenshi.simulationmanager.CalculationConditions;
import commonutils.PhysicalConstants;
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
 *
 * @author audreyazura
 */
public class ConsoleManager implements OutputInterface
{
    private CalculationConditions getCalculationConditions(OrderedProperties p_properties) throws StringIndexOutOfBoundsException, NumberFormatException 
    {
        boolean isElectron = (p_properties.getProperty("simulated_particle") == "electron");
        boolean zeroAtFront = (p_properties.getProperty("zero_position") == "front");
        
        String biasVoltagesList = p_properties.getProperty("bias_voltages");
        String notchesList = p_properties.getProperty("notch_positions");
        String initialPositionsList = p_properties.getProperty("generation_positions");
        
        PhysicalConstants.UnitsPrefix unitPrefix = PhysicalConstants.UnitsPrefix.selectPrefix(p_properties.getProperty("abscissa_unit"));
            
        BigDecimal totalSampleWidth = new BigDecimal(p_properties.getProperty("sample_width"));
        BigDecimal bufferWindowSize = new BigDecimal(p_properties.getProperty("bufferwindow width"));
        BigDecimal effectiveMassDouble = new BigDecimal(p_properties.getProperty("effective_mass"));
        BigDecimal lifetimeNumber = new BigDecimal(p_properties.getProperty("lifetime"));
        BigDecimal frontBangapNumber = new BigDecimal(p_properties.getProperty("front_bandgap"));
        BigDecimal minimumBandgapNumber = new BigDecimal(p_properties.getProperty("minimum_bandgap"));
        BigDecimal backBangapNumber = new BigDecimal(p_properties.getProperty("back_bandgap"));
        int numberSimulatedParticle = Integer.parseInt(p_properties.getProperty("number_of_simulated_particles"));

        return new CalculationConditions(isElectron, zeroAtFront, unitPrefix, numberSimulatedParticle, effectiveMassDouble, lifetimeNumber, bufferWindowSize, totalSampleWidth, frontBangapNumber, minimumBandgapNumber, backBangapNumber, biasVoltagesList, notchesList, initialPositionsList);
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
            System.err.println("Verify you have writtem a number in the sample size field, the buffer+window size field and the number of simulated particle field.");
//            ex.printStackTrace();
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
