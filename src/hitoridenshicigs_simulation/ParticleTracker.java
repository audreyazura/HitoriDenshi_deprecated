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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author audreyazura
 */
public class ParticleTracker
{
    private List<BigDecimal> m_trajectory = new ArrayList<>();
    private List<BigDecimal> m_velocities = new ArrayList<>();
    private List<BigDecimal> m_acclerations = new ArrayList<>();
    private CollectionState m_collectionState = CollectionState.NOTCOLLECTED;
    
    public ParticleTracker()
    {
        
    }
    
    public ParticleTracker(BigDecimal p_position, BigDecimal p_velocity, BigDecimal p_acceleration)
    {
        m_trajectory.add(p_position);
        m_velocities.add(p_velocity);
        m_acclerations.add(p_acceleration);
    }
    
    public ParticleTracker(ParticleTracker p_tracker)
    {
        m_trajectory = p_tracker.getTrajectory();
        m_velocities = p_tracker.getVelocityList();
        m_acclerations = p_tracker.getAccelerationList();
    }
    
    public void update (BigDecimal p_position, BigDecimal p_velocity, BigDecimal p_acceleration, CollectionState p_collection)
    {
        m_trajectory.add(p_position);
        m_velocities.add(p_velocity);
        m_acclerations.add(p_acceleration);
        m_collectionState = p_collection;
    }
    
    public boolean isCollected()
    {
        return m_collectionState != CollectionState.NOTCOLLECTED;
    }
    
    public CollectionState getCollectionState()
    {
        return m_collectionState;
    }
    
    public ArrayList<BigDecimal> getTrajectory()
    {
        return new ArrayList(m_trajectory);
    }
    
    public ArrayList<BigDecimal> getVelocityList()
    {
        return new ArrayList(m_velocities);
    }
    
    public ArrayList<BigDecimal> getAccelerationList()
    {
        return new ArrayList(m_acclerations);
    }
    
    public enum CollectionState
    {
        BACK, FRONT, NOTCOLLECTED
    }
}
