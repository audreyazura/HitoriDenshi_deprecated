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

import com.sun.jdi.InternalException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author Alban Lafuente
 */
class Field
{
    private final List<Double> m_absisse = new ArrayList<>();
    private final List<Double> m_fieldValues = new ArrayList<>();
    
    public Field(File p_fileField)
    {
        
    }
    
    public boolean hasExited(double position)
    {
        return position <= m_absisse.get(0) && position >= m_absisse.get(m_absisse.size()-1);
    }
    
    public double getFieldValue(double position)
    {
        int positionIndex;
        double fieldValue;
        
        if (!hasExited(position))
        {
            if ((positionIndex = m_absisse.indexOf(position)) == -1)
            {
                int lowPositionIndex = 0;                
                while (m_absisse.get(lowPositionIndex) < position)
                {
                    lowPositionIndex += 1;
                }
                
                double interpolationSlope = (m_fieldValues.get(lowPositionIndex+1) - m_fieldValues.get(lowPositionIndex)) / (m_absisse.get(lowPositionIndex+1) - m_absisse.get(lowPositionIndex));
                double interpolationOffset = m_fieldValues.get(lowPositionIndex) - interpolationSlope*m_absisse.get(lowPositionIndex);
                
                fieldValue = interpolationSlope*position + interpolationOffset;
            }
            else
            {
                fieldValue = m_fieldValues.get(positionIndex);
            }
        }
        else
        {
            throw new NoSuchElementException("No field value for position:" + String.valueOf(position));
        }

        return fieldValue;
    }
    
    public boolean hasExitedAtFront(double position, boolean zeroAtFront)
    {
        boolean result;
        
        if (hasExited(position))
            if(zeroAtFront)
            {
                result = Math.abs(m_absisse.get(0) - position) < Math.abs(position - m_absisse.get(m_absisse.size()-1));
            }
            else
            {
                result = Math.abs(m_absisse.get(0) - position) > Math.abs(position - m_absisse.get(m_absisse.size()-1));
            }
        else
        {
            throw new InternalException("The particle has not yet exited.");
        }
        
        return result;
    }
}
