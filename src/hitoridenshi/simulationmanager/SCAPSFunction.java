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

import com.github.audreyazura.commonutils.ContinuousFunction;
import com.github.audreyazura.commonutils.PhysicsTools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.TreeSet;
import java.util.zip.DataFormatException;

/**
 * Represents a continuous function as an ensemble of value associated with an abscissa
 * To make up for the fact there is a finite number of abscissa, value in-between are approximated by doing a linear interpolation between the two closest points
 * @author Alban Lafuente
 */
class SCAPSFunction extends ContinuousFunction
{
    /**
     * Create a continuous function representing the electric field given in a *.eb file from SCAPS
     * @param p_inputFile the *.eb file containing the electric field and abscissa values given by SCAPS
     * @param p_unitMultiplier the multiplier to convert the abscissa unit into metres
     * @return a continuous function representing the electric field
     * @throws DataFormatException
     * @throws IOException
     * @throws ArrayIndexOutOfBoundsException 
     */
    static public SCAPSFunction createElectricFieldFromSCAPS(File p_inputFile, BigDecimal p_unitMultiplier) throws DataFormatException, IOException, ArrayIndexOutOfBoundsException
    {
       return new SCAPSFunction(p_inputFile, p_unitMultiplier, PhysicsTools.UnitsPrefix.UNITY.getMultiplier().divide(PhysicsTools.UnitsPrefix.CENTI.getMultiplier(), MathContext.DECIMAL128), "eb", "\t", 23, new int[] {1,12});
    }
    
    public SCAPSFunction()
    {
        super();
    }
    
    public SCAPSFunction (SCAPSFunction p_passedFunction)
    {
        super(p_passedFunction);
    }
    
    public SCAPSFunction (ContinuousFunction p_passedFunction)
    {
        super(p_passedFunction);
    }
    
    /**
     * Create a continuous function representing an effective electric field creating by the double grading of the absorber
     * @param p_abscissa a TreeSet of the abscissa
     * @param p_notchPosition the position of the notch
     * @param p_effectiveField0toNotch the value of the electric field field between x = 0 and x = x_notch
     * @param p_effectiveFieldNotchtoEnd the value of the electric field between x = x_notch and the end of the electric field
     * @param p_end the end position of the absorber
     */
    public SCAPSFunction(TreeSet<BigDecimal> p_abscissa, BigDecimal p_notchPosition, BigDecimal p_effectiveField0toNotch, BigDecimal p_effectiveFieldNotchtoEnd, BigDecimal p_end)
    {
        for (BigDecimal position: p_abscissa)
        {
            if (position.compareTo(p_end) <= 0)
            {
                if (position.compareTo(p_notchPosition) < 0)
                {
                    m_values.put(formatBigDecimal(position), formatBigDecimal(p_effectiveField0toNotch));
                }
                else if (position.compareTo(p_notchPosition) > 0)
                {
                    m_values.put(formatBigDecimal(position), formatBigDecimal(p_effectiveFieldNotchtoEnd));
                }
            }
            else
            {
                m_values.put(formatBigDecimal(position), BigDecimal.ZERO);
            }
        }
    }
    
    private SCAPSFunction (File p_inputFile, BigDecimal p_abscissaUnitMultiplier, BigDecimal p_valuesUnitMultiplier, String p_expectedExtension, String p_separator, int p_ncolumn, int[] p_columnToExtract) throws FileNotFoundException, DataFormatException, ArrayIndexOutOfBoundsException, IOException
    {
        super(p_inputFile, p_abscissaUnitMultiplier, p_valuesUnitMultiplier, p_expectedExtension, p_separator, p_ncolumn, p_columnToExtract);
    }
    
    @Override
    protected BigDecimal formatBigDecimal (BigDecimal p_toBeFormatted)
    {
        return CalculationConditions.formatBigDecimal(p_toBeFormatted);
    }
}
