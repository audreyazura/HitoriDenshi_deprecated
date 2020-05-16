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

import java.math.BigDecimal;

/**
 *
 * @author Alban Lafuente
 */
public class PhysicalConstants
{
    //USE BIGDECIMAL INSTEAD OF DOUBLE, OR SEARCH MORE PRECISE METHOD
    
    //Boltzman constant in J/K
    static final public BigDecimal KB = CalculationConditions.formatBigDecimal(new BigDecimal("1.380649e-23"));
    //electron mass in kg
    static final public BigDecimal ME = CalculationConditions.formatBigDecimal(new BigDecimal("9.10938188e-31"));
    //elementary charge in C
    static final public BigDecimal Q = CalculationConditions.formatBigDecimal(new BigDecimal("1.60217733e-19"));
    //definition of the electronVolt in J
    static final public BigDecimal EV = CalculationConditions.formatBigDecimal(new BigDecimal("1.602176634e-19"));
    
    //contains the possible units multiplier
    static public enum UnitsPrefix
    {
        NANO ("1e-9", "n"),  //[SI unit]/[NANO unit]
        MICRO ("1e-6", "μ"), //[SI unit]/[MICRO unit]
        CENTI ("1e-2", "c"), //[SI unit]/[CENTI unit]
        UNITY ("1.0", "");   //[SI unit]/[SI unit]
        
        private final BigDecimal m_multiplier;
        private final String m_textPrefix;

        UnitsPrefix(String p_multiplier, String p_prefix)
        {
            m_multiplier = CalculationConditions.formatBigDecimal(new BigDecimal(p_multiplier));
            m_textPrefix = new String(p_prefix);
        }
        
        public BigDecimal getMultiplier()
        {
            return m_multiplier;
        }
        
        public String getPrefix()
        {
            return m_textPrefix;
        }
        
        static public PhysicalConstants.UnitsPrefix selectPrefix (String p_unit)
        {
            PhysicalConstants.UnitsPrefix prefixSelected;

            switch (p_unit.charAt(0))
            {
                case 'n':
                    prefixSelected = PhysicalConstants.UnitsPrefix.NANO;
                    break;
                case 'μ':
                    prefixSelected = PhysicalConstants.UnitsPrefix.MICRO;
                    break;
                case 'c':
                    prefixSelected = PhysicalConstants.UnitsPrefix.CENTI;
                    break;
                default:
                    prefixSelected = PhysicalConstants.UnitsPrefix.UNITY;
                    break;
            }

            return prefixSelected;
        }
    }
}
