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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author audreyazura
 */
public class CalculationConditions
{
    final boolean m_isElectron;
    final List<Double> m_biasVoltages = new ArrayList<>();
    final List<Double> m_notchPositions = new ArrayList<>();
    final List<Double> m_startingPositons = new ArrayList<>();
    final List<Double> m_velocityList = new ArrayList<>();
    
    public CalculationConditions (boolean p_isElectron, String p_biasVoltages, String p_notchPositions, String p_startingPositions)
    {
        m_isElectron = p_isElectron;
        //transform strings to ArrayLists
        //generate velocities using Maxwell-Botzman
    }
    
}
