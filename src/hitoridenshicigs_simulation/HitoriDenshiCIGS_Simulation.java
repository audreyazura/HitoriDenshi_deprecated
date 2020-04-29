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
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.FileSystemException;
import java.util.List;
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
        System.out.println(p_conditions.getNotchPositionArray());
        System.out.println(p_conditions.getStartingPositionList());
        
        //all the values in p_conditions are in SI units
        for (String bias: p_conditions.getBiasVoltageArray())
        {
            for (BigDecimal notch: p_conditions.getNotchPositionArray())
            {
                try
                {
                    String notchNanometer = String.valueOf((notch.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier())).intValue());
                    File electricFieldFile = new File(p_folderElectricFields+"/E"+bias+"V_N"+notchNanometer+"nm.eb");
                    final Absorber currentAbsorber = new Absorber(electricFieldFile, notch, p_conditions);
                    
                    for (BigDecimal initialPosition: p_conditions.getStartingPositionList())
                    {
                        List<BigDecimal> velocities = p_conditions.getVelocityList();
                        SimulationTracker currentTracker = new SimulationTracker(velocities.size());
                        
                        System.out.println("Calculation starts for x_notch = "+notchNanometer+"nm and x_init = "+String.valueOf((initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128)).intValue())+"nm.");
                                
                        for (BigDecimal velocity: velocities)
                        {
                            Particle currentIndividual = new Particle(p_conditions.getParticleParameters(), initialPosition, velocity);
                            
                            int numberOfSteps = 0;
                            while (!currentIndividual.isCollected() && numberOfSteps < p_conditions.getMaxSteps())
                            {
                                currentIndividual.applyExteriorFields(currentAbsorber, CalculationConditions.DT);
                            }
                            
                            currentTracker.logParticle(currentIndividual);
                        }
                        
                        currentTracker.saveToFile(p_outputFolder, bias, notchNanometer, initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128), p_conditions.getAbscissaScale());
                        System.out.println("Calculation ended for x_notch = "+notchNanometer+"nm and x_init = "+String.valueOf((initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128)).intValue())+"nm.");
                    }
                }
                catch (DataFormatException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (FileSystemException ex)
                {
                    System.err.println("Erreur with the file "+ex.getFile()+": "+ex.getReason());
                }
                catch (IOException ex)
                {
                    Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("End of simulation!");
    } 
    
}

//0 ; 0.2 ; 0.4 ; 0.6 ; 0.8 ; 1 ; 1.2 ; 1.3 ; 1.4 ; 1.5 ; 1.6 ; 1.7 ; 1.75 ; 1.8 ; 1.85 ; 1.9 ; 1.95 ; 2
//1 ; 1.25 ; 1.5 ; 1.55 ; 1.6 ; 1.65 ; 1.7 ; 1.75 ; 1.8 ; 1.85
