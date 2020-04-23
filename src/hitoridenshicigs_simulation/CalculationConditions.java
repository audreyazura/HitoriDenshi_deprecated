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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 *
 * @author audreyazura
 */
public class CalculationConditions
{
    //Boltzman constant in J/K
    private final double KB = 1.380649e-23;
    //Temperature in K
    private final double T = 300;
    //electron mass in kg
    private final double ME = 9.10938188e-31;
    //elementary charge in C
    private final double Q = 1.60217733e-19;
    //calculation step, chosen as one each femtosecond
    private final double dt = 1e-12;
    
    private final boolean m_isZeroAtFront;
    private final int m_maxSteps;
    private final double m_bufferWindowSize;
    private final double m_sampleSize;
    private final double m_abscissaMultiplier;
    private final String[] m_biasVoltages;
    private final String[] m_notchPositions;
    private final List<Double> m_startingPositons = new ArrayList<>();
    private final List<Double> m_velocityList = new ArrayList<>();
    private final Map<String, Double> m_particleParameters = new HashMap<>();
    
    public CalculationConditions (boolean p_isElectron, boolean p_isZeroAtFront, boolean p_isMicrometer, int p_numberSimulatedParticules, double p_effectiveMass, double p_lifeTime, double p_bufferWindowSize, double p_sampleSize, String p_biasVoltages, String p_notchPositions, String p_startingPositions)
    {
        m_isZeroAtFront = p_isZeroAtFront;
        m_bufferWindowSize = p_bufferWindowSize;
        m_sampleSize = p_sampleSize;
        
        double particleEffectiveMass = p_effectiveMass*ME;
        m_particleParameters.put("mass", particleEffectiveMass);
        //lifetime is given in nanosecond, and we have to convert it into step, with a step every dt
        m_maxSteps = (int) StrictMath.round(StrictMath.floor(p_lifeTime*1e-9/dt));
        //to convert the abscissa from the unit given by SCAPS (micrometer or nanometer) into meter
        m_abscissaMultiplier = p_isMicrometer ? 1e-6 : 1e-9;
        
        m_biasVoltages = p_biasVoltages.strip().split("\\h*;\\h*");
        m_notchPositions = p_notchPositions.strip().split("\\h*;\\h*");
        
        //splitting the starting position string as above, and converting each element to double to add them to m_startingPositions
        Arrays.asList(p_startingPositions.strip().split("\\h*;\\h*")).forEach(new Consumer<String>()
        {
            @Override
            public void accept(String position)
            {
                m_startingPositons.add(Double.valueOf(position));
            }
        });
        
        if(p_isElectron)
        {
            m_particleParameters.put("charge", -Q);
        }
        else
        {
            m_particleParameters.put("charge", -Q);
        }
        
        /**
         * filling velocityList with as many velocities as they are particles from a Boltzman distribution
         * we initialize the random generator with a seed in order to always get the same random list of speed, so the simulation can be stopped and started again later
        */
        double vth = StrictMath.sqrt(KB*T/particleEffectiveMass);
        Random randomGenerator = new Random(0);
        for (int i = 0; i < p_numberSimulatedParticules; i+=1)
        {
            m_velocityList.add(randomGenerator.nextGaussian()*vth);
        }
    }
    
    public boolean isZeroAtFront()
    {
        return m_isZeroAtFront;
    }
    
    public int getMaxSteps()
    {
        return m_maxSteps;
    }
    
    public double getBufferAndWindowSize()
    {
        return m_bufferWindowSize;
    }
    
    public double getSolarCellSize()
    {
        return m_sampleSize;
    }
    
    public double getAbscissaMultiplier()
    {
        return m_abscissaMultiplier;
    }
    
    public HashMap<String, Double> getParticleParameters()
    {
        return new HashMap(m_particleParameters);
    }
    
    public String[] getBiasVoltageArray()
    {
        return m_biasVoltages.clone();
    }
    
    public String[] getNotchPositionArray()
    {
        return m_notchPositions.clone();
    }
    
    public ArrayList<Double> getStartingPositionList()
    {
        return new ArrayList(m_startingPositons);
    }
    
    public ArrayList<Double> getVelocityList()
    {
        return new ArrayList(m_velocityList);
    }
    
}
