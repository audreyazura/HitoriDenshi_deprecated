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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 *
 * @author Alban Lafuente
 */
class ContinuousFunction
{
    //do not truncate values here: the field is also defined outside the absorber. Only the absorber knows if a particle exited itself. A ContinuousFunction can only say if a given position is in its range.
    private final Set<BigDecimal> m_abscissa;
    private final Map<BigDecimal, BigDecimal> m_values;
    
    static public ContinuousFunction createElectricFieldFromSCAPS(File p_fileValues, BigDecimal p_unitMultiplier) throws DataFormatException, IOException, ArrayIndexOutOfBoundsException
    {
       return new ContinuousFunction(p_fileValues, p_unitMultiplier, PhysicalConstants.UnitsPrefix.CENTI.getMultiplier(), "eb", 23, new int[] {1,12});
    }
    
    @Override
    public boolean equals(Object o)
    {
        boolean result = this.getClass().equals(o.getClass());
        
        if (result)
        {
            result = m_values.equals(((ContinuousFunction) o).getValues());
        }
        
        return result;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(m_values);
    }
    
    @Override
    public String toString()
    {
        String result = new String("");
        
        for (BigDecimal abscissa: m_abscissa)
        {
            result = result.concat(abscissa+"\t=> "+m_values.get(abscissa)+"\n");
        }
        
        return result;
    }
    
    public ContinuousFunction (ContinuousFunction p_passedFunction)
    {
        m_values = p_passedFunction.getValues();
        m_abscissa = new TreeSet(m_values.keySet());
        
    }
    
    public ContinuousFunction (HashMap<BigDecimal, BigDecimal> p_values)
    {
        m_abscissa = new TreeSet(p_values.keySet());
        m_values = p_values;
    }
    
    public ContinuousFunction(TreeSet<BigDecimal> p_abscissa, BigDecimal p_notchPosition, BigDecimal p_effectiveField0toNotch, BigDecimal p_effectiveFieldNotchtoEnd, BigDecimal p_end)
    {
        m_values = new HashMap<>();
        
        for (BigDecimal position: p_abscissa)
        {
            if (position.compareTo(p_end) <= 0)
            {
                if (position.compareTo(p_notchPosition) < 0)
                {
                    m_values.put(CalculationConditions.formatBigDecimal(position), CalculationConditions.formatBigDecimal(p_effectiveField0toNotch));
                }
                else if (position.compareTo(p_notchPosition) > 0)
                {
                    m_values.put(CalculationConditions.formatBigDecimal(position), CalculationConditions.formatBigDecimal(p_effectiveFieldNotchtoEnd));
                }
            }
            else
            {
                m_values.put(CalculationConditions.formatBigDecimal(position), BigDecimal.ZERO);
            }
        }
        
        m_abscissa = new TreeSet(m_values.keySet());
    }
    
    private ContinuousFunction (File p_fileValues, BigDecimal p_abscissaUnitMultiplier, BigDecimal p_valuesUnitMultiplier, String p_expectedExtension, int p_ncolumn, int[] p_columnToExtract) throws FileNotFoundException, DataFormatException, ArrayIndexOutOfBoundsException, IOException
    {
        m_values = new HashMap<>();
        
        String[] nameSplit = p_fileValues.getPath().split("\\.");
        
        if (!nameSplit[nameSplit.length-1].equals(p_expectedExtension))
        {
            throw new DataFormatException();
        }
        
        BufferedReader fieldFile = new BufferedReader(new FileReader(p_fileValues));
        Pattern numberRegex = Pattern.compile("^\\-?\\d+(\\.\\d+(e(\\+|\\-)\\d+)?)?");
	
	String line;
	while (((line = fieldFile.readLine()) != null))
	{	    
	    String[] lineSplit = line.strip().split("\t");
	    
	    if(lineSplit.length == p_ncolumn && numberRegex.matcher(lineSplit[0]).matches())
	    {
		//we put the abscissa in meter in order to do all calculations in SI
                BigDecimal currentAbscissa = CalculationConditions.formatBigDecimal((new BigDecimal(lineSplit[p_columnToExtract[0]].strip())).multiply(p_abscissaUnitMultiplier));
                Set<BigDecimal> abscissaSet = new TreeSet(m_values.keySet());
                
                if (!abscissaSet.contains(currentAbscissa))
                {
                    m_values.put(currentAbscissa, CalculationConditions.formatBigDecimal((new BigDecimal(lineSplit[p_columnToExtract[1]].strip())).divide(p_valuesUnitMultiplier, MathContext.DECIMAL128)));
                }
	    }
        }
        
        m_abscissa = new TreeSet(m_values.keySet());
    }
    
    public void print ()
    {
        for (BigDecimal abscissa: m_abscissa)
        {
            System.out.println(abscissa+"\t"+m_values.get(abscissa));
        }
    }
    
    public TreeSet<BigDecimal> getAbscissa()
    {
        return new TreeSet(m_abscissa);
    }
    
    public HashMap<BigDecimal, BigDecimal> getValues()
    {
        return new HashMap(m_values);
    }
    
    public ContinuousFunction add (ContinuousFunction p_passedFunction)
    {
        Map<BigDecimal, BigDecimal> addedValues = new HashMap<>();
        
        if (m_abscissa.equals(p_passedFunction.getAbscissa()))
        {
            for (BigDecimal position: m_abscissa)
            {
                addedValues.put(position, m_values.get(position).add(p_passedFunction.getValues().get(position)));
            }
        }
        else
        {
            for (BigDecimal position: m_abscissa)
            {
                try
                {
                    addedValues.put(position, m_values.get(position).add(p_passedFunction.getValueAtPosition(position)));
                }
                catch (NoSuchElementException ex)
                {
                    addedValues.put(position, m_values.get(position));
                }
            }
        }
        
        return new ContinuousFunction((HashMap) addedValues);
    }
            
    public BigDecimal getValueAtPosition(BigDecimal position)
    {
        BigDecimal value;
        
        if (isInRange(position))
        {
            if (m_abscissa.contains(position))
            {
                value = m_values.get(position);
            }
            else
            {
                BigDecimal previousPosition = ((TreeSet<BigDecimal>) m_abscissa).lower(position);
                BigDecimal nextPosition = ((TreeSet<BigDecimal>) m_abscissa).higher(position);
                
                BigDecimal interpolationSlope = (m_values.get(nextPosition).subtract(m_values.get(previousPosition))).divide(nextPosition.subtract(previousPosition), MathContext.DECIMAL128);
                BigDecimal interpolationOffset = m_values.get(previousPosition).subtract(interpolationSlope.multiply(previousPosition));
                
                value = interpolationSlope.multiply(position).add(interpolationOffset);
            }
        }
        else
        {
            throw new NoSuchElementException("No field value for position:" + String.valueOf(position));
        }

        return value;
    }
    
    private boolean isInRange (BigDecimal position)
    {
        return position.compareTo(((TreeSet<BigDecimal>) m_abscissa).first()) >= 0 && position.compareTo(((TreeSet<BigDecimal>) m_abscissa).last()) <= 0;
    }
}
