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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Alban Lafuente
 */
public class PhysicalConstants
{
    //USE BIGDECIMAL INSTEAD OF DOUBLE, OR SEARCH MORE PRECISE METHOD
    
    //Boltzman constant in J/K
    static final public BigDecimal KB = new BigDecimal("1.380649e-23");
    //electron mass in kg
    static final public BigDecimal ME = new BigDecimal("9.10938188e-31");
    //elementary charge in C
    static final public BigDecimal Q = new BigDecimal("1.60217733e-19");
    //definition of the electronVolt in J
    static final public BigDecimal EV = new BigDecimal("1.602176634e-19");
    
    //contains the possible units multiplier
    static public enum UnitsPrefix
    {
        NANO ("1e-9"),
        MICRO ("1e-6"),
        BASE ("1.0");
        
        private final BigDecimal m_multiplier;

        UnitsPrefix(String p_multiplier)
        {
            m_multiplier = new BigDecimal(p_multiplier);
        }
        
        public BigDecimal getMultiplier()
        {
            return m_multiplier;
        }
    }
}
