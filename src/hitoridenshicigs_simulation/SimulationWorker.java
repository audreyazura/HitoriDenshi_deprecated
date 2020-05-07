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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alban Lafuente
 */
public class SimulationWorker implements Runnable
{
    private final ArrayList<BigDecimal> m_startingPositions;
    private final ArrayList<BigDecimal> m_velocities;
    private final HashMap<String, BigDecimal> m_particleParameters;
    private final int m_id;
    private final int m_maxSteps;
    private final PhysicalConstants.UnitsPrefix m_abscissaUnit;
    private final Set<Absorber> m_absorbers;
    private final String m_outputFolder;
    
    public SimulationWorker (int p_id, String p_outputFolder, HashSet<Absorber> p_chunk, CalculationConditions p_conditions)
    {
        m_startingPositions = p_conditions.getStartingPositionList();
        m_velocities = p_conditions.getVelocityList();
        m_particleParameters = p_conditions.getParticleParameters();
        m_id = p_id;
        m_maxSteps = p_conditions.getMaxSteps();
        m_abscissaUnit = p_conditions.getAbscissaScale();
        m_absorbers = p_chunk;
        m_outputFolder = p_outputFolder;
    }
    
    @Override
    public void run()
    {
        for (Absorber currentAbsorber: m_absorbers)
        {
            String currrentNotchPosition = currentAbsorber.getNotchPositionString();
            String currentBias = currentAbsorber.getBias();
            try
            {
                for (BigDecimal initialPosition: m_startingPositions)
                {
                    SimulationTracker currentTracker = new SimulationTracker(m_velocities.size());

                    System.out.println("SimulationWorker-"+String.valueOf(m_id)+": Calculation starts for E_bias = "+currentBias+", x_notch = "+currrentNotchPosition+"nm and x_init = "+String.valueOf((initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128)).intValue())+"nm.");

                    for (BigDecimal velocity: m_velocities)
                    {
                        Particle currentIndividual = new Particle(m_particleParameters, initialPosition, velocity);

                        int numberOfSteps = 0;
                        while (!currentIndividual.isCollected() && numberOfSteps < m_maxSteps)
                        {
                            currentIndividual.applyExteriorFields(currentAbsorber, CalculationConditions.DT);
                            numberOfSteps += 1;
                        }

                        currentTracker.logParticle(currentIndividual);
                    }

                    currentTracker.saveToFile(m_outputFolder, currentBias, currrentNotchPosition, initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128), m_abscissaUnit);
                    System.out.println("SimulationWorker-"+String.valueOf(m_id)+": Calculation ended for E_bias = "+currentBias+", x_notch = "+currrentNotchPosition+"nm and x_init = "+String.valueOf((initialPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128)).intValue())+"nm.");
                }
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
}
