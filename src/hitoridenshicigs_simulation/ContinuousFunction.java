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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 *
 * @author Alban Lafuente
 */
class ContinuousFunction
{
    //do not truncate values here: the field is also defined outside the absorber. Only the absorber knows if a particle exited itself. A ContinuousFunction can only say if a given position is in its range.
    private final TreeSet<Double> m_abscissa;
    private final HashMap<Double, Double> m_values;
    
    public ContinuousFunction (File p_fileValues) throws DifferentSizeOfAbscissaAndValuesTableException
    {
        List<Double> extractedAbscissa = new ArrayList<>();
        List<Double> extractedValues = new ArrayList<>();
        
        extractedAbscissa.add(0.0);
        extractedValues.add(0.0);
        
        if (extractedAbscissa.size() == extractedValues.size())
        {            
            m_abscissa = new TreeSet<>();
            m_values = new HashMap<>();
            
            for (int i = 0 ; i < extractedAbscissa.size() ; i+=1)
            {
                m_abscissa.add(extractedAbscissa.get(i));
                m_values.put(extractedAbscissa.get(i), extractedValues.get(i));
            }
        }
        else
        {
            throw new DifferentSizeOfAbscissaAndValuesTableException("The absissa and value table are of different sizes.", extractedAbscissa.size(), extractedValues.size());
        }
    }
    
    public ContinuousFunction (ContinuousFunction p_passedFunction)
    {
        m_abscissa = p_passedFunction.getAbscissa();
        m_values = p_passedFunction.getValues();
        
    }
    
    public TreeSet getAbscissa()
    {
        return (TreeSet) m_abscissa.clone();
    }
    
    public HashMap getValues()
    {
        return (HashMap) m_values.clone();
    }
            
    public double getValueAtPosition(double position)
    {
        double value;
        
        if (isInRange(position))
        {
            if (m_abscissa.contains(position))
            {
                value = m_values.get(position);
            }
            else
            {
                double previousPosition = m_abscissa.lower(position);
                double nextPosition = m_abscissa.higher(position);
                
                double interpolationSlope = (m_values.get(nextPosition) - m_values.get(previousPosition)) / (nextPosition - previousPosition);
                double interpolationOffset = m_values.get(previousPosition) - interpolationSlope*previousPosition;
                
                value = interpolationSlope*position + interpolationOffset;
            }
        }
        else
        {
            throw new NoSuchElementException("No field value for position:" + String.valueOf(position));
        }

        return value;
    }
    
    private boolean isInRange(double position)
    {
        return position >= m_abscissa.first() && position <= m_abscissa.last();
    }
}
