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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alban Lafuente
 */
public class Particle 
{
    private final int m_charge;
    private final int m_masse;
    private int m_position;
    List<Integer> m_trajectory = new ArrayList<>();
    
    public Particle(int p_charge, int p_masse, int p_position)
    {
        m_charge = p_charge;
        m_masse = p_masse;
        m_position = p_position;
    }
    
    public void applyElectricField(Field p_electricField)
    {
        
    }
}
