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
package hitoridenshi.simulationmanager;

import com.github.kilianB.pcg.fast.PcgRSFast;
import com.github.audreyazura.commonutils.PhysicsTools;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A worker calculating on a single processor core to execute the simulation
 * @author Alban Lafuente
 */
public class SimulationWorker implements Runnable
{
    private final ArrayList<BigDecimal> m_startingPositions;
    private final ArrayList<BigDecimal> m_velocities;
    private final HashMap<String, BigDecimal> m_particleParameters;
    private final SimulationManager m_manager;
    private final int m_id;
    private final int m_maxSteps;
    private final PhysicsTools.UnitsPrefix m_abscissaUnit;
    private final Set<Absorber> m_absorbers;
    private final String m_outputFolder;
    private final PcgRSFast m_RNG;
    
    private int m_numberCalculations;
    
    public SimulationWorker (int p_id, String p_outputFolder, HashSet<Absorber> p_chunk, CalculationConditions p_conditions, PcgRSFast p_generator, SimulationManager p_manager)
    {
        m_startingPositions = p_conditions.getStartingPositionList();
        m_velocities = p_conditions.getVelocityList();
        m_particleParameters = p_conditions.getParticleParameters();
        m_manager = p_manager;
        m_id = p_id;
        m_maxSteps = p_conditions.getMaxSteps();
        m_abscissaUnit = p_conditions.getAbscissaScale();
        m_absorbers = p_chunk;
        m_outputFolder = p_outputFolder;
        m_RNG = p_generator;
        
        m_numberCalculations = m_absorbers.size()*m_startingPositions.size()*m_velocities.size();
        for (Absorber absorber: m_absorbers)
        {
            if (!m_startingPositions.contains(absorber.getNotchPosition()))
            {
                m_numberCalculations += m_velocities.size();
            }
        }
    }
    
    @Override
    public void run()
    {
        double workerProgress = 0;
        for (Absorber currentAbsorber: m_absorbers)
        {
            BigDecimal notchPosition = currentAbsorber.getNotchPosition();
            try
            {
                List<BigDecimal> initialPositionWithNotch = new ArrayList(m_startingPositions);
                if (!initialPositionWithNotch.contains(notchPosition))
                {
                    initialPositionWithNotch.add(notchPosition);
                    
                }
                
                for (BigDecimal initialPosition: initialPositionWithNotch)
                {
                    SimulationTracker currentTracker = new SimulationTracker(m_velocities.size());

                    for (BigDecimal velocity: m_velocities)
                    {
                        Particle currentIndividual = new Particle(m_particleParameters, initialPosition, velocity, currentAbsorber);

                        int numberOfSteps = 0;
                        while (!currentIndividual.isCollected() && numberOfSteps < m_maxSteps)
                        {
                            currentIndividual.stepInTime(m_RNG);
                            numberOfSteps += 1;
                        }

                        currentTracker.logParticle(currentIndividual, currentAbsorber);
                        workerProgress += 1.0 / m_numberCalculations;
                        m_manager.sendUpdate(m_id, workerProgress);
                    }

                    currentTracker.saveToFile(m_outputFolder + "/" + currentAbsorber.getFileName(), initialPosition.divide(PhysicsTools.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128), m_abscissaUnit);
                    m_manager.sendMessage("SimulationWorker-"+String.valueOf(m_id)+": Calculation ended for " + currentAbsorber.getFileName() + " and x_init = "+String.valueOf((initialPosition.divide(PhysicsTools.UnitsPrefix.NANO.getMultiplier(), MathContext.DECIMAL128)).intValue())+"nm.");
                }
            }
            catch (FileSystemException ex)
            {
                System.err.println("Erreur with the file "+ex.getFile()+": "+ex.getReason());
            }
            catch (IOException ex)
            {
                Logger.getLogger(SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public double getRandomDouble()
    {
        return m_RNG.nextDouble();
    }
    
    public int getNumberCalculations()
    {
        return m_numberCalculations;
    }
}
