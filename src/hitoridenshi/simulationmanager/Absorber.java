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

import commonutils.PhysicsTools;
import hitoridenshi.simulationmanager.Particle.CollectionState;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * To store the data about the solar cell, especially the abscissa (one for the eb file, one for the gen file), and tell if a particle has been or not, as well as the collection side. Also store backAbscissa and frontAbscissa.
 * 
 * @author Alban Lafuente
 */
public class Absorber
{
    private final BigDecimal m_backPosition;
    private final BigDecimal m_frontPosition;
    private final BigDecimal m_notchPosition;
    private final boolean m_zeroAtFront;
    private final ContinuousFunction m_electricField;
    private final String m_bias;
    
    /**
     * Constructor for an absorber without special feature (such as a notch)
     * @param p_electricField the *.eb file given from SCAPS
     * @param p_bias the bias voltage applied on the absorber
     * @param p_condition the condition of calculation
     * @throws DataFormatException
     * @throws IOException 
     */
    public Absorber(File p_electricField, String p_bias, CalculationConditions p_condition) throws DataFormatException, IOException
    {
        m_electricField = ContinuousFunction.createElectricFieldFromSCAPS(p_electricField, p_condition.getAbscissaMultiplier());
        m_bias = p_bias;
        m_notchPosition = null;
        m_zeroAtFront = p_condition.isZeroAtFront();
        if(m_zeroAtFront)
        {
            m_frontPosition = BigDecimal.ZERO;
            m_backPosition = p_condition.getSolarCellSize().subtract(p_condition.getBufferAndWindowSize());
        }
        else
        {
            m_frontPosition = p_condition.getSolarCellSize().subtract(p_condition.getBufferAndWindowSize());
            m_backPosition = BigDecimal.ZERO;
        }
    }
    
    /**
     * Constructor for an absorber with a notch
     * Calculate the notch-created effective electric field and add it to the internal electric field given by SCAPS
     * @param p_fileElectricFields the *.eb file given from SCAPS
     * @param p_bias the bias voltage applied on the absorber
     * @param p_notchPosition the position of the notch in the absorber
     * @param p_conditions the condition of calculation
     * @throws DataFormatException
     * @throws IOException 
     */
    public Absorber(String p_fileElectricFields, String p_bias, BigDecimal p_notchPosition, CalculationConditions p_conditions) throws DataFormatException, IOException
    {
        m_bias = p_bias;
        m_notchPosition = p_notchPosition;
        
        
        Map<String, BigDecimal> bandgaps = new HashMap(p_conditions.getBandgaps());
        BigDecimal absorberEnd;
        BigDecimal field0toNotch;
        BigDecimal fieldNotchtoEnd;
        
        m_zeroAtFront = p_conditions.isZeroAtFront();
        if(m_zeroAtFront)
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(BigDecimal.ZERO);
            m_backPosition = CalculationConditions.formatBigDecimal(p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize()));
            absorberEnd = m_backPosition;
        }
        else
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize()));
            m_backPosition = CalculationConditions.formatBigDecimal(BigDecimal.ZERO);
            absorberEnd = m_frontPosition;
        }
        
        if (p_conditions.isElectron())
        {
            ContinuousFunction internalElectricField = ContinuousFunction.createElectricFieldFromSCAPS(new File(p_fileElectricFields), p_conditions.getAbscissaMultiplier());
            //À refactoriser ?
            if(m_zeroAtFront)
            {
                if (m_notchPosition.compareTo(m_frontPosition) == 0)
                {
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((bandgaps.get("notch").subtract(bandgaps.get("back")).divide(m_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    field0toNotch = fieldNotchtoEnd;
                }
                else if (m_notchPosition.compareTo(m_backPosition) == 0)
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((bandgaps.get("front").subtract(bandgaps.get("notch")).divide(m_frontPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = field0toNotch;
                }
                else
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((bandgaps.get("front").subtract(bandgaps.get("notch")).divide(m_frontPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((bandgaps.get("notch").subtract(bandgaps.get("back")).divide(m_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                }
            }
            else
            {
                if (m_notchPosition.compareTo(m_frontPosition) == 0)
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((bandgaps.get("back").subtract(bandgaps.get("notch")).divide(m_backPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = field0toNotch;
                }
                else if (m_notchPosition.compareTo(m_backPosition) == 0)
                {   
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((bandgaps.get("notch").subtract(bandgaps.get("front")).divide(m_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    field0toNotch = fieldNotchtoEnd;
                }
                else
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((bandgaps.get("back").subtract(bandgaps.get("notch")).divide(m_backPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((bandgaps.get("notch").subtract(bandgaps.get("front")).divide(m_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                }
            }

            ContinuousFunction notchEffectiveElectricField = new ContinuousFunction(internalElectricField.getAbscissa(), m_notchPosition, field0toNotch, fieldNotchtoEnd, absorberEnd);
            m_electricField = internalElectricField.add(notchEffectiveElectricField);
        }
        else
        {
            m_electricField = ContinuousFunction.createElectricFieldFromSCAPS(new File(p_fileElectricFields), p_conditions.getAbscissaMultiplier());
        }
    }
    
    public ContinuousFunction getElectricField()
    {
        return new ContinuousFunction(m_electricField);
    }
    
    /**
     * Tell if a particle has been collected or not, and if it has been collected at the back or front 
     * @param p_position the position of the particle
     * @return the collection state of the particle
     */
    public CollectionState giveCollection(BigDecimal p_position)
    {
        CollectionState collection = CollectionState.NOTCOLLECTED;
        
        if(m_zeroAtFront)
        {
            if (p_position.compareTo(m_frontPosition) <= 0)
            {
                collection = CollectionState.FRONT;
            }
            else if (p_position.compareTo(m_backPosition) >= 0)
            {
                collection = CollectionState.BACK;
            }
        }
        else
        {
            if (p_position.compareTo(m_frontPosition) >= 0)
            {
                collection = CollectionState.FRONT;
            }
            else if (p_position.compareTo(m_backPosition) <= 0)
            {
                collection = CollectionState.BACK;
            }
        }
        
        return collection;
    }
    
    public BigDecimal getNotchPosition()
    {
        return m_notchPosition;
    }
    
    public String getBias()
    {
        return m_bias;
    }
    
    public BigDecimal getFrontPosition()
    {
        return m_frontPosition;
    }
    
    public BigDecimal getBackPosition()
    {
        return m_backPosition;
    }
}
