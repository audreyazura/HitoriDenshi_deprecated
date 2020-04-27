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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alban Lafuente
 */
public class Particle 
{
    private final BigDecimal m_charge;
    private final BigDecimal m_masse;
    private BigDecimal m_position;
    private BigDecimal m_velocity;
    private List<BigDecimal> m_trajectory = new ArrayList<>();
    private List<BigDecimal> m_velocities = new ArrayList<>();
    private List<BigDecimal> m_accelerations = new ArrayList<>();
    
    CollectionState m_collectionState = CollectionState.NOTCOLLECTED;
    
    public Particle(BigDecimal p_charge, BigDecimal p_masse, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = p_charge;
        m_masse = p_masse;
        m_position = p_position;
        m_velocity = p_velocity;
        
        m_trajectory.add(m_position);
        m_velocities.add(m_velocity);
    }
    
    public Particle(HashMap<String, BigDecimal> p_parameters, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = p_parameters.get("charge");
        m_masse = p_parameters.get("mass");
        m_position = p_position;
        m_velocity = p_velocity;
        
        m_trajectory.add(m_position);
        m_velocities.add(m_velocity);
    }
    
    public void applyExteriorFields(Absorber p_absorber)
    {
        ContinuousFunction internalElectricField = p_absorber.getElectricField();
        //operating
        
        m_collectionState = p_absorber.giveCollection(m_position);
    }
    
    public boolean isCollected()
    {
        return m_collectionState != CollectionState.NOTCOLLECTED;
    }
    
    public BigDecimal getCurrentPosition()
    {
        return m_position;
    }
    
    public enum CollectionState
    {
        BACK, FRONT, NOTCOLLECTED
    }
}
