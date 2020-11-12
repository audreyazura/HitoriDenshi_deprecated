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

import com.github.kilianB.pcg.fast.PcgRSFast;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represent a particle with its current position and velocity, as well as its movement history
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
    private CapturedState m_captured = CapturedState.FREE;
    
    private final Absorber m_attachedAbsorber;
    
    public Particle(BigDecimal p_charge, BigDecimal p_masse, BigDecimal p_position, BigDecimal p_velocity, Absorber p_absorber)
    {
        m_attachedAbsorber = new Absorber(p_absorber);
        
        m_charge = CalculationConditions.formatBigDecimal(p_charge);
        m_masse = CalculationConditions.formatBigDecimal(p_masse);
        m_position = CalculationConditions.formatBigDecimal(p_position);
        m_velocity = CalculationConditions.formatBigDecimal(p_velocity);
        
        m_trajectory.add(m_position);
        m_velocityList.add(m_velocity);
    }
    
    /**
     * Create a particle from a Hashmap containing its charge and mass, as given by CalculationCondiction
     * @param p_parameters the hashmap containing the particle mass and charge
     * @param p_position
     * @param p_velocity 
     */
    public Particle(HashMap<String, BigDecimal> p_parameters, BigDecimal p_position, BigDecimal p_velocity, Absorber p_absorber)
    {
        m_charge = CalculationConditions.formatBigDecimal(p_parameters.get("charge"));
        m_masse = CalculationConditions.formatBigDecimal(p_parameters.get("mass"));
        m_position = CalculationConditions.formatBigDecimal(p_position);
        m_velocity = CalculationConditions.formatBigDecimal(p_velocity);
        
        m_trajectory.add(m_position);
        m_velocityList.add(m_velocity);
        
        m_attachedAbsorber = p_absorber;
    }
    
    public void stepInTime(PcgRSFast p_RNG)
    {
        if (m_captured != CapturedState.CAPTURED)
        {
            applyExteriorFields(p_RNG);
        }
        else
        {
            tryToEscape(p_RNG);
        }
    }
    
    /**
     * Move the particle by calculating the effect of an external electric field
     * @param p_absorber the absorber the particle is in
     * @param p_RNG a random number generator
     */
    private void applyExteriorFields(PcgRSFast p_RNG)
    {
        BigDecimal electricFieldValueAtPosition = CalculationConditions.formatBigDecimal(m_attachedAbsorber.getElectricField().getValueAtPosition(m_position));

        //calculating acceleration
        BigDecimal currentAcceleration = CalculationConditions.formatBigDecimal(m_charge.multiply(electricFieldValueAtPosition).divide(m_masse, MathContext.DECIMAL128));
        m_accelerationList.add(currentAcceleration);

        //calculating new velocity and a mean velocity that will be used to update the position
        BigDecimal newVelocity = CalculationConditions.formatBigDecimal(m_velocity.add(currentAcceleration.multiply(CalculationConditions.DT)));
        BigDecimal meanVelocity = CalculationConditions.formatBigDecimal((m_velocity.add(newVelocity)).divide(new BigDecimal("2", MathContext.DECIMAL128)));
        m_velocity = newVelocity;
        m_velocityList.add(m_velocity);

        //calculating new position
        m_position = CalculationConditions.formatBigDecimal(m_position.add(meanVelocity.multiply(CalculationConditions.DT)));
        m_trajectory.add(m_position);

        m_collectionState = m_attachedAbsorber.giveCollection(m_position);
        m_captured = m_attachedAbsorber.giveCapture(newVelocity, p_RNG);
    }
    
    private void tryToEscape (PcgRSFast p_RNG)
    {
        
    }
    
    public boolean isCollected()
    {
        return m_collectionState != CollectionState.NOTCOLLECTED;
    }
    
    public boolean isCaptured()
    {
        return m_captured != CapturedState.FREE;
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
    
    /**
     * An enumeration of the different possible collection state of the particle
        FRONTCOLLECTED: collected at the front
        BACKCOLLECTED: collected at the back
        NOTCOLLECTED: not yet collected
     */
    public enum CollectionState
    {
        BACKCOLLECTED, FRONTCOLLECTED, NOTCOLLECTED
    }
    
    public enum CapturedState
    {
        CAPTURED, FREE
    }
}
