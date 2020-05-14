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
package hitoridenshi_simulation;

import java.math.BigDecimal;
import java.math.MathContext;
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
    private List<BigDecimal> m_velocityList = new ArrayList<>();
    private List<BigDecimal> m_accelerationList = new ArrayList<>();
    
    private CollectionState m_collectionState = CollectionState.NOTCOLLECTED;
    
    public Particle(BigDecimal p_charge, BigDecimal p_masse, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = CalculationConditions.formatBigDecimal(p_charge);
        m_masse = CalculationConditions.formatBigDecimal(p_masse);
        m_position = CalculationConditions.formatBigDecimal(p_position);
        m_velocity = CalculationConditions.formatBigDecimal(p_velocity);
        
        m_trajectory.add(m_position);
        m_velocityList.add(m_velocity);
    }
    
    public Particle(HashMap<String, BigDecimal> p_parameters, BigDecimal p_position, BigDecimal p_velocity)
    {
        m_charge = CalculationConditions.formatBigDecimal(p_parameters.get("charge"));
        m_masse = CalculationConditions.formatBigDecimal(p_parameters.get("mass"));
        m_position = CalculationConditions.formatBigDecimal(p_position);
        m_velocity = CalculationConditions.formatBigDecimal(p_velocity);
        
        m_trajectory.add(m_position);
        m_velocityList.add(m_velocity);
    }
    
    public void applyExteriorFields(Absorber p_absorber, BigDecimal p_timeStep)
    {
        BigDecimal electricFieldValueAtPosition = CalculationConditions.formatBigDecimal(p_absorber.getElectricField().getValueAtPosition(m_position));
        
        //calculating acceleration
        BigDecimal currentAcceleration = CalculationConditions.formatBigDecimal(m_charge.multiply(electricFieldValueAtPosition).divide(m_masse, MathContext.DECIMAL128));
        m_accelerationList.add(currentAcceleration);
        
        //calculating new velocity and a mean velocity that will be used to update the position
        BigDecimal newVelocity = CalculationConditions.formatBigDecimal(m_velocity.add(currentAcceleration.multiply(p_timeStep)));
        BigDecimal meanVelocity = CalculationConditions.formatBigDecimal((m_velocity.add(newVelocity)).divide(new BigDecimal("2", MathContext.DECIMAL128)));
        m_velocity = newVelocity;
        m_velocityList.add(m_velocity);
        
        //calculating new position
        m_position = CalculationConditions.formatBigDecimal(m_position.add(meanVelocity.multiply(p_timeStep)));
        m_trajectory.add(m_position);
        
        m_collectionState = p_absorber.giveCollection(m_position);
    }
    
    public boolean isCollected()
    {
        return m_collectionState != CollectionState.NOTCOLLECTED;
    }
    
    public BigDecimal getCurrentPosition()
    {
        return CalculationConditions.formatBigDecimal(m_position);
    }
    
    public ArrayList<BigDecimal> getTrajectory()
    {
        return new ArrayList(m_trajectory);
    }
    
    public ArrayList<BigDecimal> getVelocityList()
    {
        return new ArrayList(m_velocityList);
    }
    
    public ArrayList<BigDecimal> getAccelerationList()
    {
        return new ArrayList(m_accelerationList);
    }
    
    public CollectionState getCollection ()
    {
        return m_collectionState;
    }
    
    public enum CollectionState
    {
        BACK, FRONT, NOTCOLLECTED
    }
}
