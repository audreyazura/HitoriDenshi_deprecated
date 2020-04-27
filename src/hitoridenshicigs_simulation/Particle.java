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

import hitoridenshicigs_simulation.ParticleTracker.CollectionState;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 *
 * @author Alban Lafuente
 */
public class Particle 
{
    //USE BIGDECIMAL INSTEAD OF DOUBLE, OR SEARCH MORE PRECISE METHOD
    
    private final BigDecimal m_charge;
    private final BigDecimal m_masse;
    private BigDecimal m_position;
    private BigDecimal m_velocity;
    private BigDecimal m_acceleration = new BigDecimal("0");
    private ParticleTracker m_tracker;
    
    public Particle(BigDecimal p_charge, BigDecimal p_masse, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = p_charge;
        m_masse = p_masse;
        m_position = p_position;
        m_velocity = p_velocity;
        m_tracker = new ParticleTracker(m_position, m_velocity, m_acceleration);
    }
    
    public Particle(HashMap<String, BigDecimal> p_parameters, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = p_parameters.get("charge");
        m_masse = p_parameters.get("mass");
        m_position = p_position;
        m_velocity = p_velocity;
        m_tracker = new ParticleTracker(m_position, m_velocity, m_acceleration);
    }
    
    public Particle(HashMap<String, BigDecimal> p_parameters, BigDecimal p_position, BigDecimal p_velocity, BigDecimal p_electricFieldAtPosition)
    {
        m_charge = p_parameters.get("charge");
        m_masse = p_parameters.get("mass");
        m_position = p_position;
        m_velocity = p_velocity;
        m_acceleration = getInitialAcceleration(p_electricFieldAtPosition);
        m_tracker = new ParticleTracker(m_position, m_velocity, m_acceleration);
    }
    
    private BigDecimal getInitialAcceleration(BigDecimal p_exteriorElectricFieldValue)
    {
        return m_charge.multiply(p_exteriorElectricFieldValue).divide(m_masse);
    }
    
    public void update(BigDecimal p_newPosition, BigDecimal p_newVelocity, BigDecimal p_newAcceleration, CollectionState p_collection)
    {
        m_position = p_newPosition;
        m_velocity = p_newVelocity;
        m_acceleration = p_newAcceleration;
        
        m_tracker.update(m_position, m_velocity, m_acceleration, p_collection);
    }
    
    public BigDecimal getCurrentPosition()
    {
        return m_position;
    }
    
    public BigDecimal getCurrentVelocity()
    {
        return m_velocity;
    }
    
    public BigDecimal getCurrentAcceleration()
    {
        return m_acceleration;
    }
    
    public ParticleTracker getTracker()
    {
        return new ParticleTracker(m_tracker);
    }
}
