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
    private final double m_charge;
    private final double m_masse;
    private double m_position;
    private double m_velocity;
    private List<Double> m_trajectory = new ArrayList<>();
    private List<Double> m_velocities = new ArrayList<>();
    private List<Double> m_accelerations = new ArrayList<>();
    
    CollectionPossibility collectionState = CollectionPossibility.NOTCOLLECTED;
    
    public Particle(double p_charge, double p_masse, double p_position, double p_velocity)
    {
        m_charge = p_charge;
        m_masse = p_masse;
        m_position = p_position;
        m_velocity = p_velocity;
        
        m_trajectory.add(m_position);
        m_velocities.add(m_velocity);
    }
    
    public void applyElectricField(ContinuousFunction p_electricField)
    {
        
    }
    
    public double getCurrentPosition()
    {
        return m_position;
    }
    
    public enum CollectionPossibility
    {
        BACK, FRONT, NOTCOLLECTED
    }
}
