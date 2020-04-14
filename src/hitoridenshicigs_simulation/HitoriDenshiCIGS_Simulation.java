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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshiCIGS_Simulation 
{
    
    public void startSimulation(String p_folderElectricFields, String p_outputFolder, CalculationConditions p_conditions)
    {
        System.out.println("Starting simulation!\nFolder: " + p_folderElectricFields);
        
        double charge, mass;
        int maxSteps;
        if (p_conditions.m_isElectron)
        {
            charge = -1.60217733e-19;
            mass = 0.089*9.10938188e-31;
            maxSteps = 100000;
        }
        else
        {
            charge = 1.60217733e-19;
            mass = 0.693*9.10938188e-31;
            maxSteps = 50000;
        }
        
        for (String bias: p_conditions.m_biasVoltages)
        {
            for (String notch: p_conditions.m_notchPositions)
            {
                try
                {
                    File electricFieldFile = new File(p_folderElectricFields+"/E"+bias+"_N"+notch+"nm.gen");
                    final Absorber currentAbsorber = new Absorber(electricFieldFile, p_conditions.m_isZeroAtFront, p_conditions.m_bufferWindowSize, p_conditions.m_sampleSize);
                    
                    for (double initialPosition: p_conditions.m_startingPositons)
                    {
                        SimulationTracker currentTracker = new SimulationTracker();
                                
                        for (double velocity: p_conditions.m_velocityList)
                        {
                            Particle currentIndividual = new Particle(charge, mass, initialPosition, velocity);
                            
                            int numberOfSteps = 0;
                            while (!currentAbsorber.hasExited(currentIndividual) && numberOfSteps < maxSteps)
                            {
                                currentIndividual.applyElectricField(currentAbsorber.getElectricField());
                            }
                            
                            currentTracker.logParticle(currentIndividual);
                        }
                        
                        currentTracker.saveToFile(notch, initialPosition, bias);
                    }
                }
                catch (DifferentArraySizeException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    } 
    
}
