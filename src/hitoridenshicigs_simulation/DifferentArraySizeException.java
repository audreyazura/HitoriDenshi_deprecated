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

/**
 *
 * @author audreyazura
 */
public class DifferentArraySizeException extends Exception
{
    public DifferentArraySizeException()
    {
        
    }
    
    public DifferentArraySizeException(String p_str)
    {
        super(p_str);
    }
    
    public DifferentArraySizeException(String p_str, int p_absissaSize, int p_valueSize)
    {
        System.err.println(p_str);
        System.err.println("Abscissa table size: "+Integer.toString(p_absissaSize));
        System.err.println("Value table size: "+Integer.toString(p_valueSize));
    }
}
