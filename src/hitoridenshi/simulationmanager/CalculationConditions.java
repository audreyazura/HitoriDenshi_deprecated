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

import commonutils.PCGGenerator;
import commonutils.PhysicalConstants;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents the necessary parameters of the calculation, correctly converted and formatted
 * @author Alban Lafuente
 */
public class CalculationConditions
{    
    //Temperature in K
    static final BigDecimal T = CalculationConditions.formatBigDecimal(new BigDecimal("300"));
    //calculation step, chosen as one each femtosecond
    static final BigDecimal DT = CalculationConditions.formatBigDecimal(PhysicalConstants.UnitsPrefix.FEMTO.getMultiplier());

    private final boolean m_isZeroAtFront;
    private final int m_maxSteps;
    
    //All the following numbers have to be stocked with SI units
    private final BigDecimal m_bufferWindowSize;
    private final BigDecimal m_sampleSize;
    private final PhysicalConstants.UnitsPrefix m_abscissaUnit;
    private final String[] m_biasVoltages;
    private final List<BigDecimal> m_notchPositions;
    private final List<BigDecimal> m_startingPositons;
    private final List<BigDecimal> m_velocityList = new ArrayList<>();
    private final Map<String, BigDecimal> m_particleParameters = new HashMap<>();
    private final Map<String, BigDecimal> m_bandgaps = new HashMap<>();
    
    public CalculationConditions (boolean p_isElectron, boolean p_isZeroAtFront, PhysicalConstants.UnitsPrefix p_prefix, int p_numberSimulatedParticules, BigDecimal p_effectiveMass, BigDecimal p_lifeTime, BigDecimal p_bufferWindowSize, BigDecimal p_sampleSize, BigDecimal p_frontBandgap, BigDecimal p_notchBandgap, BigDecimal p_backBandgap, String p_biasVoltages, String p_notchPositions, String p_startingPositions)
    {
        //to convert the abscissa from the unit given by SCAPS (micrometer or nanometer) into meter
        m_abscissaUnit = p_prefix;
        
        m_isZeroAtFront = p_isZeroAtFront;
        m_bufferWindowSize = CalculationConditions.formatBigDecimal(p_bufferWindowSize.multiply(m_abscissaUnit.getMultiplier()));
        m_sampleSize = CalculationConditions.formatBigDecimal(p_sampleSize.multiply(m_abscissaUnit.getMultiplier()));
        
        m_bandgaps.put("front", CalculationConditions.formatBigDecimal(p_frontBandgap.multiply(PhysicalConstants.EV)));
        m_bandgaps.put("notch", CalculationConditions.formatBigDecimal(p_notchBandgap.multiply(PhysicalConstants.EV)));
        m_bandgaps.put("back", CalculationConditions.formatBigDecimal(p_backBandgap.multiply(PhysicalConstants.EV)));
        
        BigDecimal particleEffectiveMass = CalculationConditions.formatBigDecimal(p_effectiveMass.multiply(PhysicalConstants.ME));
        m_particleParameters.put("mass", particleEffectiveMass);
        //lifetime is given in nanosecond, and we have to convert it into step, with a step every DT
        m_maxSteps = (p_lifeTime.multiply(new BigDecimal("1e-9")).divide(DT, MathContext.DECIMAL128)).intValue();
        
        m_biasVoltages = p_biasVoltages.strip().split("\\h*;\\h*");
        
        m_notchPositions = getBigDecimalArrayFromString(p_notchPositions, CalculationConditions.formatBigDecimal(m_abscissaUnit.getMultiplier()));
        m_startingPositons = getBigDecimalArrayFromString(p_startingPositions, CalculationConditions.formatBigDecimal(m_abscissaUnit.getMultiplier()));
        
        if(p_isElectron)
        {
            m_particleParameters.put("charge", CalculationConditions.formatBigDecimal(PhysicalConstants.Q.negate()));
        }
        else
        {
            m_particleParameters.put("charge", CalculationConditions.formatBigDecimal(PhysicalConstants.Q));
        }
        
        /**
         * filling velocityList with as many velocities as they are particles from a Boltzman distribution
         * we initialize the random generator with a seed in order to always get the same random list of speed, so the simulation can be stopped and started again later
        */
        BigDecimal vth = CalculationConditions.formatBigDecimal((PhysicalConstants.KB.multiply(T).divide(particleEffectiveMass, MathContext.DECIMAL128)).sqrt(MathContext.DECIMAL128));
        PCGGenerator randomGenerator = new PCGGenerator(42);
        for (int i = 0; i < p_numberSimulatedParticules; i+=1)
        {
            m_velocityList.add(CalculationConditions.formatBigDecimal((new BigDecimal(randomGenerator.nextGaussian())).multiply(vth)));
        }
    }
    
    /**
     * format a BigDecimal so it can be properly used for the simulation.
     * @param p_toBeFormatted
     * @return the formatted BigDecimal
     */
    static public BigDecimal formatBigDecimal(BigDecimal p_toBeFormatted)
    {
        return p_toBeFormatted.stripTrailingZeros();
    }
    
    /**
     * splits the passed string and converts each element to BigDecimal to before returning the list created this way
     * @param p_values the string containing the values to be converted
     * @param p_multiplier the multiplier to convert the values in the string to SI unit
     * @return a ArrayList of BigDecimal with the converted value in SI, correctly formatted for the calculation
     */
    private ArrayList<BigDecimal> getBigDecimalArrayFromString(String p_values, BigDecimal p_multiplier)
    {
        List<BigDecimal> returnList = new ArrayList<>();
        
        Arrays.asList(p_values.strip().split("\\h*;\\h*")).forEach(new Consumer<String>()
        {
            @Override
            public void accept(String position)
            {
                //the starting positions are entered in nm, they have to be converted back to m
                returnList.add(CalculationConditions.formatBigDecimal((new BigDecimal(position)).multiply(p_multiplier)));
            }
        });
        
        return (ArrayList<BigDecimal>) returnList;
    }
    
    public boolean isElectron()
    {
        return m_particleParameters.get("charge").compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isZeroAtFront()
    {
        return m_isZeroAtFront;
    }
    
    public synchronized int getMaxSteps()
    {
        return m_maxSteps;
    }
    
    public synchronized PhysicalConstants.UnitsPrefix getAbscissaScale()
    {
        return m_abscissaUnit;
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
    
    public synchronized HashMap<String, BigDecimal> getParticleParameters()
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
    
    public ArrayList<BigDecimal> getNotchPositionArray()
    {
        return new ArrayList(m_notchPositions);
    }
    
    public synchronized ArrayList<BigDecimal> getStartingPositionList()
    {
        return new ArrayList(m_startingPositons);
    }
    
    public synchronized ArrayList<BigDecimal> getVelocityList()
    {
        return new ArrayList(m_velocityList);
    }
    
}
