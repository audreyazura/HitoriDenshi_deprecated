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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 * Represents the necessary parameters of the calculation, correctly converted and formatted
 * @author Alban Lafuente
 */
public class CalculationConditions
{    
    //Temperature in K
    static final BigDecimal T = CalculationConditions.formatBigDecimal(new BigDecimal("300"));
    //calculation step, chosen as one each femtosecond
    static final BigDecimal DT = CalculationConditions.formatBigDecimal(PhysicsTools.UnitsPrefix.FEMTO.getMultiplier());

    private final boolean m_isZeroAtFront;
    private final int m_maxSteps;
    
    private final List<Absorber> m_absorbers = new ArrayList<>();
    
    //All the following numbers have to be stocked with SI units
    private final PhysicsTools.UnitsPrefix m_abscissaUnit;
    private final String[] m_biasVoltages;
    private final List<BigDecimal> m_startingPositons;
    private final List<BigDecimal> m_velocityList = new ArrayList<>();
    private final Map<String, BigDecimal> m_particleParameters = new HashMap<>();
    
    public CalculationConditions (List<Sample> sampleList, boolean p_isElectron, boolean p_isZeroAtFront, PhysicsTools.UnitsPrefix p_prefix, int p_numberSimulatedParticules, BigDecimal p_effectiveMass, BigDecimal p_lifeTime, BigDecimal p_bufferWindowSize, BigDecimal p_absorberSize, String p_biasVoltages, String p_startingPositions)
    {
        //to convert the abscissa from the unit given by SCAPS (micrometer or nanometer) into meter
        m_abscissaUnit = p_prefix;
        
        m_isZeroAtFront = p_isZeroAtFront;
        
        BigDecimal particleEffectiveMass = formatBigDecimal(p_effectiveMass.multiply(PhysicsTools.ME));
        m_particleParameters.put("mass", particleEffectiveMass);
        //lifetime is given in nanosecond, and we have to convert it into step, with a step every DT
        m_maxSteps = (p_lifeTime.multiply(new BigDecimal("1e-9")).divide(DT, MathContext.DECIMAL128)).intValue();
        
        m_biasVoltages = p_biasVoltages.strip().split("\\h*;\\h*");
        
        m_startingPositons = getBigDecimalArrayFromString(p_startingPositions, formatBigDecimal(m_abscissaUnit.getMultiplier()));
        
        if(p_isElectron)
        {
            m_particleParameters.put("charge", formatBigDecimal(PhysicsTools.Q.negate()));
        }
        else
        {
            m_particleParameters.put("charge", formatBigDecimal(PhysicsTools.Q));
        }
        
        for (Sample sample: sampleList)
        {
            try
            {
                m_absorbers.add(new Absorber(sample, m_isZeroAtFront, p_isElectron, p_absorberSize, p_bufferWindowSize, m_abscissaUnit.getMultiplier()));
            }
            catch(DataFormatException | IOException ex)
            {
                Logger.getLogger(CalculationConditions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * filling velocityList with as many velocities as they are particles from a Boltzman distribution
         * we initialize the random generator with a seed in order to always get the same random list of speed, so the simulation can be stopped and started again later
        */
        BigDecimal vth = formatBigDecimal((PhysicsTools.KB.multiply(T).divide(particleEffectiveMass, MathContext.DECIMAL128)).sqrt(MathContext.DECIMAL128));
        PcgRSFast randomGenerator = new PcgRSFast(42,1);
        for (int i = 0; i < p_numberSimulatedParticules; i+=1)
        {
            m_velocityList.add(formatBigDecimal((new BigDecimal(randomGenerator.nextGaussian())).multiply(vth)));
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
                returnList.add(formatBigDecimal((new BigDecimal(position)).multiply(p_multiplier)));
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
    
    public synchronized PhysicsTools.UnitsPrefix getAbscissaScale()
    {
        return m_abscissaUnit;
    }
    
    public BigDecimal getAbscissaMultiplier()
    {
        return m_abscissaUnit.getMultiplier();
    }
    
    public synchronized HashMap<String, BigDecimal> getParticleParameters()
    {
        return new HashMap(m_particleParameters);
    }
    
    public String[] getBiasVoltageArray()
    {
        return m_biasVoltages.clone();
    }
    
    public synchronized ArrayList<BigDecimal> getStartingPositionList()
    {
        return new ArrayList(m_startingPositons);
    }
    
    public synchronized ArrayList<BigDecimal> getVelocityList()
    {
        return new ArrayList(m_velocityList);
    }
    
    public ArrayList<Absorber> getAbsorbers()
    {
        return new ArrayList(m_absorbers);
    }
    
}
