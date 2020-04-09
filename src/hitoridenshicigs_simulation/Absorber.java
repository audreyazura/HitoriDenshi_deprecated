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
    
    public Absorber(File p_electricField, boolean p_zeroAtFront, double p_bufferWindowSize, double p_absorberSize) throws DifferentSizeOfAbscissaAndValuesTableException
    {
        m_electricField = new ContinuousFunction(p_electricField);
        m_zeroAtFront = p_zeroAtFront;
        if(m_zeroAtFront)
        {
            m_frontPosition = p_bufferWindowSize;
            m_backPosition = m_frontPosition + p_absorberSize;
        }
        else
        {
            m_frontPosition = p_absorberSize;
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
