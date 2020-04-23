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
    private final double m_frontPosition;
    private final double m_backPosition;
    
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
    
    public Absorber(File p_electricField, CalculationConditions p_conditions) throws DifferentArraySizeException, DataFormatException, IOException
    {
        m_electricField = ContinuousFunction.createElectricField(p_electricField, p_conditions.getAbscissaMultiplier());
        m_zeroAtFront = p_conditions.isZeroAtFront();
        if(m_zeroAtFront)
        {
            m_frontPosition = 0;
            m_backPosition = p_conditions.getSolarCellSize() - p_conditions.getBufferAndWindowSize();
        }
        else
        {
            m_frontPosition = p_conditions.getSolarCellSize() - p_conditions.getBufferAndWindowSize();
            m_backPosition = 0;
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
            if (exited = (p_particle.getCurrentPosition() <= m_frontPosition))
            {
                p_particle.collectionState = Particle.CollectionPossibility.FRONT;
            }
            else if (exited = (p_particle.getCurrentPosition() >= m_backPosition))
            {
                p_particle.collectionState = Particle.CollectionPossibility.BACK;
            }
        }
        else
        {
            if (exited = (p_particle.getCurrentPosition() >= m_frontPosition))
            {
                p_particle.collectionState = Particle.CollectionPossibility.FRONT;
            }
            else if (exited = (p_particle.getCurrentPosition() <= m_backPosition))
            {
                p_particle.collectionState = Particle.CollectionPossibility.BACK;
            }
        }
        
        return exited;
    }
}
