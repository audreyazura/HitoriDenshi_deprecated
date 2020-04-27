/*
 * Copyright (C) 2020 audreyazura
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
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * To store the data about the solar cell, especially the abscissa (one for the eb file, one for the gen file), and tell if a particle has been or not, as well as the collection side. Also store backAbscissa and frontAbscissa.
 * 
 * @author audreyazura
 */
public class Absorber
{
    private final ContinuousFunction m_electricField;
    private final boolean m_zeroAtFront;
    private final BigDecimal m_frontPosition;
    private final BigDecimal m_backPosition;
    
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
    
    public Absorber(File p_electricField, BigDecimal p_currentNotch, CalculationConditions p_conditions) throws DataFormatException, IOException
    {
        Map<String, BigDecimal> bandgaps = new HashMap(p_conditions.getBandgaps());
        BigDecimal frontEffectiveField;
        BigDecimal backEffectiveField;
        ContinuousFunction internalElectricField = ContinuousFunction.createElectricField(p_electricField, p_conditions.getAbscissaMultiplier());
        
        m_zeroAtFront = p_conditions.isZeroAtFront();
        if(m_zeroAtFront)
        {
            m_frontPosition = new BigDecimal("0");
            m_backPosition = p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize());
            
            frontEffectiveField = bandgaps.get("notch").subtract(bandgaps.get("front")).divide(p_currentNotch.subtract(m_frontPosition), MathContext.DECIMAL128);
            backEffectiveField = bandgaps.get("back").subtract(bandgaps.get("notch")).divide(m_backPosition.subtract(p_currentNotch), MathContext.DECIMAL128);
            
            ContinuousFunction notchEffectiveElectricField = new ContinuousFunction(internalElectricField.getAbscissa(), p_currentNotch, frontEffectiveField, backEffectiveField);
            m_electricField = internalElectricField.add(notchEffectiveElectricField);
        }
        else
        {
            m_frontPosition = p_conditions.getSolarCellSize().subtract(p_conditions.getBufferAndWindowSize());
            m_backPosition = new BigDecimal("0");
            
            frontEffectiveField = bandgaps.get("front").subtract(bandgaps.get("notch")).divide(m_frontPosition.subtract(p_currentNotch), MathContext.DECIMAL128);
            backEffectiveField = bandgaps.get("notch").subtract(bandgaps.get("back")).divide(p_currentNotch.subtract(m_backPosition), MathContext.DECIMAL128);
            
            ContinuousFunction notchEffectiveElectricField = new ContinuousFunction(internalElectricField.getAbscissa(), p_currentNotch, backEffectiveField, frontEffectiveField);
            m_electricField = internalElectricField.add(notchEffectiveElectricField);
        }
    }
    
    public ContinuousFunction getElectricField()
    {
        return new ContinuousFunction(m_electricField);
    }
    
    public boolean hasExited(Particle p_particle)
    {
        boolean exited = false;
        
        if(m_zeroAtFront)
        {
            if (exited = (p_particle.getCurrentPosition().compareTo(m_frontPosition) <= 0))
            {
                p_particle.collectionState = Particle.CollectionState.FRONT;
            }
            else if (exited = (p_particle.getCurrentPosition().compareTo(m_backPosition) >= 0))
            {
                p_particle.collectionState = Particle.CollectionState.BACK;
            }
        }
        else
        {
            if (exited = (p_particle.getCurrentPosition().compareTo(m_frontPosition) >= 0))
            {
                p_particle.collectionState = Particle.CollectionState.FRONT;
            }
            else if (exited = (p_particle.getCurrentPosition().compareTo(m_backPosition) <= 0))
            {
                p_particle.collectionState = Particle.CollectionState.BACK;
            }
        }
        
        return exited;
    }
}
