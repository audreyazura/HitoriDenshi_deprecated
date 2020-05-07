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

import hitoridenshicigs_simulation.Particle.CollectionState;
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
    private final boolean m_zeroAtFront;
    private final ContinuousFunction m_electricField;
    private final String m_bias;
    private final String m_notchPositionNanometerString;
    
//    public Absorber(File p_electricField, double p_unitMultiplier, boolean p_zeroAtFront, double p_bufferWindowSize, double p_sampleSize) throws DifferentArraySizeException, DataFormatException, IOException
//    {
//        m_electricField = ContinuousFunction.createElectricField(p_electricField, p_unitMultiplier);
//        m_zeroAtFront = p_zeroAtFront;
//        if(m_zeroAtFront)
//        {
//            m_frontPosition = 0;
//            m_backPosition = p_sampleSize-p_bufferWindowSize;
//        }
//        else
//        {
//            m_frontPosition = p_sampleSize-p_bufferWindowSize;
//            m_backPosition = 0;
//        }
//    }
    
    public Absorber(String p_folderElectricFields, String p_bias, BigDecimal p_notchPosition, CalculationConditions p_conditions) throws DataFormatException, IOException
    {
        m_bias = p_bias;
        m_notchPositionNanometerString = String.valueOf(p_notchPosition.divide(PhysicalConstants.UnitsPrefix.NANO.getMultiplier()).intValue());
        
        Map<String, BigDecimal> bandgaps = new HashMap(p_conditions.getBandgaps());
        BigDecimal frontEffectiveField;
        BigDecimal backEffectiveField;
        
        ContinuousFunction internalElectricField = ContinuousFunction.createElectricField(new File(p_folderElectricFields+"/E"+p_bias+"V_N"+m_notchPositionNanometerString+"nm.eb"), p_conditions.getAbscissaMultiplier());
        
        m_zeroAtFront = p_conditions.isZeroAtFront();
        //À refactoriser !!!!!!
        if(m_zeroAtFront)
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
            m_backPosition = CalculationConditions.formatBigDecimal(p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize()));
            
            if (p_notchPosition.compareTo(m_frontPosition) == 0)
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
                backEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("back").subtract(bandgaps.get("notch")).divide(m_backPosition.subtract(p_notchPosition), MathContext.DECIMAL128));
            }
            else if (p_notchPosition.compareTo(m_backPosition) == 0)
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("notch").subtract(bandgaps.get("front")).divide(p_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128));
                backEffectiveField = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
            }
            else
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("notch").subtract(bandgaps.get("front")).divide(p_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128));
                backEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("back").subtract(bandgaps.get("notch")).divide(m_backPosition.subtract(p_notchPosition), MathContext.DECIMAL128));
            }
        }
        else
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize()));
            m_backPosition = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
            
            if (p_notchPosition.compareTo(m_backPosition) == 0)
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("front").subtract(bandgaps.get("notch")).divide(m_frontPosition.subtract(p_notchPosition), MathContext.DECIMAL128));
                backEffectiveField = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
            }
            else if (p_notchPosition.compareTo(m_frontPosition) == 0)
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(new BigDecimal("0"));
                backEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("notch").subtract(bandgaps.get("back")).divide(p_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128));
            }
            else
            {
                frontEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("front").subtract(bandgaps.get("notch")).divide(m_frontPosition.subtract(p_notchPosition), MathContext.DECIMAL128));
                backEffectiveField = CalculationConditions.formatBigDecimal(bandgaps.get("notch").subtract(bandgaps.get("back")).divide(p_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128));
            }
        }
        
        ContinuousFunction notchEffectiveElectricField = new ContinuousFunction(internalElectricField.getAbscissa(), p_notchPosition, frontEffectiveField, backEffectiveField);
        m_electricField = internalElectricField.add(notchEffectiveElectricField);
    }
    
    public ContinuousFunction getElectricField()
    {
        return new ContinuousFunction(m_electricField);
    }
    
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
    
    public String getNotchPositionString()
    {
        return m_notchPositionNanometerString;
    }
    
    public String getBias()
    {
        return m_bias;
    }
}
