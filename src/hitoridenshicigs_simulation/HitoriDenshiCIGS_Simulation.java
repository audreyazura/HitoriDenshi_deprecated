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
package hitoridenshicigs_simulation;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshiCIGS_Simulation 
{
    
    public void startSimulation(String p_folderElectricFields, String p_outputFolder, CalculationConditions p_conditions)
    {
        System.out.println("Starting simulation!\nFolder: " + p_folderElectricFields);
        
        for (String bias: p_conditions.getBiasVoltageArray())
        {
            for (String notch: p_conditions.getNotchPositionArray())
            {
                try
                {
                    File electricFieldFile = new File(p_folderElectricFields+"/E"+bias+"V_N"+notch+"nm.eb");
                    final Absorber currentAbsorber = new Absorber(electricFieldFile, p_conditions);
                    
                    for (double initialPosition: p_conditions.getStartingPositionList())
                    {
                        SimulationTracker currentTracker = new SimulationTracker();
                                
                        for (double velocity: p_conditions.getVelocityList())
                        {
                            Particle currentIndividual = new Particle(p_conditions.getParticleParameters(), initialPosition, velocity);
                            
                            int numberOfSteps = 0;
                            while (!currentAbsorber.hasExited(currentIndividual) && numberOfSteps < p_conditions.getMaxSteps())
                            {
                                currentIndividual.applyElectricField(currentAbsorber.getElectricField());
                            }
                            
                            currentTracker.logParticle(currentIndividual);
                        }
                        
                        currentTracker.saveToFile(bias, notch, initialPosition);
                    }
                }
                catch (DifferentArraySizeException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (DataFormatException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    } 
    
}
