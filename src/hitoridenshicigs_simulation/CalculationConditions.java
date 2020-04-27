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

import java.math.BigDecimal;
import java.math.MathContext;
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
    //Temperature in K
    static final BigDecimal T = new BigDecimal("300");
    //calculation step, chosen as one each femtosecond
    static final BigDecimal DT = new BigDecimal("1e-12");

    private final boolean m_isZeroAtFront;
    private final int m_maxSteps;
    
    //All the following numbers have to be stocked with SI units
    private final BigDecimal m_bufferWindowSize;
    private final BigDecimal m_sampleSize;
    private final PhysicalConstants.UnitsPrefix m_abscissaUnit;
    private final String[] m_biasVoltages;
    private final String[] m_notchPositions;
    private final List<BigDecimal> m_startingPositons = new ArrayList<>();
    private final List<BigDecimal> m_velocityList = new ArrayList<>();
    private final Map<String, BigDecimal> m_particleParameters = new HashMap<>();
    private final Map<String, BigDecimal> m_bandgaps = new HashMap<>();
    
    public CalculationConditions (boolean p_isElectron, boolean p_isZeroAtFront, PhysicalConstants.UnitsPrefix p_prefix, int p_numberSimulatedParticules, BigDecimal p_effectiveMass, BigDecimal p_lifeTime, BigDecimal p_bufferWindowSize, BigDecimal p_sampleSize, BigDecimal p_frontBandgap, BigDecimal p_notchBandgap, BigDecimal p_backBandgap, String p_biasVoltages, String p_notchPositions, String p_startingPositions)
    {
        //to convert the abscissa from the unit given by SCAPS (micrometer or nanometer) into meter
        m_abscissaUnit = p_prefix;
        
        m_isZeroAtFront = p_isZeroAtFront;
        m_bufferWindowSize = p_bufferWindowSize.multiply(m_abscissaUnit.getMultiplier());
        m_sampleSize = p_sampleSize.multiply(m_abscissaUnit.getMultiplier());
        
        m_bandgaps.put("front", p_frontBandgap.multiply(PhysicalConstants.EV));
        m_bandgaps.put("notch", p_notchBandgap.multiply(PhysicalConstants.EV));
        m_bandgaps.put("back", p_backBandgap.multiply(PhysicalConstants.EV));
        
        BigDecimal particleEffectiveMass = p_effectiveMass.multiply(PhysicalConstants.ME);
        m_particleParameters.put("mass", particleEffectiveMass);
        //lifetime is given in nanosecond, and we have to convert it into step, with a step every DT
        m_maxSteps = (p_lifeTime.multiply(new BigDecimal("1e-9")).divide(DT, MathContext.DECIMAL128)).intValue();
        
        m_biasVoltages = p_biasVoltages.strip().split("\\h*;\\h*");
        m_notchPositions = p_notchPositions.strip().split("\\h*;\\h*");
        
        //splitting the starting position string as above, and converting each element to double to add them to m_startingPositions
        Arrays.asList(p_startingPositions.strip().split("\\h*;\\h*")).forEach(new Consumer<String>()
        {
            @Override
            public void accept(String position)
            {
                //the starting positions are entered in nm, they have to be converted back to m
                m_startingPositons.add((new BigDecimal(position)).multiply(m_abscissaUnit.getMultiplier()));
            }
        });
        
        if(p_isElectron)
        {
            m_particleParameters.put("charge", PhysicalConstants.Q.negate());
        }
        else
        {
            m_particleParameters.put("charge", PhysicalConstants.Q);
        }
        
        /**
         * filling velocityList with as many velocities as they are particles from a Boltzman distribution
         * we initialize the random generator with a seed in order to always get the same random list of speed, so the simulation can be stopped and started again later
        */
        BigDecimal vth = (PhysicalConstants.KB.multiply(T).divide(particleEffectiveMass, MathContext.DECIMAL128)).sqrt(MathContext.DECIMAL128);
        Random randomGenerator = new Random(0);
        for (int i = 0; i < p_numberSimulatedParticules; i+=1)
        {
            m_velocityList.add((new BigDecimal(randomGenerator.nextGaussian())).multiply(vth));
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
    
    //no need to return copy of BigDecimal because it is immutable
    public BigDecimal getBufferAndWindowSize()
    {
        return m_bufferWindowSize;
    }
    
    public BigDecimal getSolarCellSize()
    {
        return m_sampleSize;
    }
    
    public BigDecimal getAbscissaMultiplier()
    {
        return m_abscissaUnit.getMultiplier();
    }
    
    public HashMap<String, BigDecimal> getParticleParameters()
    {
        return new HashMap(m_particleParameters);
    }
    
    public HashMap<String, BigDecimal> getBandgaps()
    {
        return new HashMap(m_bandgaps);
    }
    
    public String[] getBiasVoltageArray()
    {
        return m_biasVoltages.clone();
    }
    
    public String[] getNotchPositionArray()
    {
        return m_notchPositions.clone();
    }
    
    public ArrayList<BigDecimal> getStartingPositionList()
    {
        return new ArrayList(m_startingPositons);
    }
    
    public ArrayList<BigDecimal> getVelocityList()
    {
        return new ArrayList(m_velocityList);
    }
    
}
