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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshiCIGS_Simulation 
{
    
    public void startSimulation(String p_folderElectricField, CalculationConditions p_conditions)
    {
        System.out.println("Starting simulation!\nFolder: " + p_folderElectricField);
        
        double charge, mass;
        if (p_conditions.m_isElectron)
        {
            charge = -1.60217733e-19;
            mass = 0.089*9.10938188e-31;
        }
        else
        {
            charge = 1.60217733e-19;
            mass = 0.693*9.10938188e-31;
        }
        
        for (double bias: p_conditions.m_biasVoltages)
        {
            for (double notch: p_conditions.m_notchPositions)
            {
                for (double initialPosition: p_conditions.m_startingPositons)
                {
                    for (double velocity: p_conditions.m_velocityList)
                    {
                        Particle currentIndividual = new Particle(charge, mass, initialPosition, velocity);
                        
                        File inputFiles = new File("");
                    }
                }
            }
        }
    } 
    
}
