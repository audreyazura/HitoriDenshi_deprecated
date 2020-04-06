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
package hitoridenshicigs_GUI;

import hitoridenshicigs_simulation.CalculationConditions;
import hitoridenshicigs_simulation.HitoriDenshiCIGS_Simulation;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshiCIGS_GUI 
{
    /**
     * Elements to inclus in the GUI:
     *  - Field to enter the list of bias voltage
     *  - Field to enter the list of notch position
     *  - Field to enter the list of starting positions
     *  - Field to select the folder containing the electric field files
     *  - Field for the number of particle
     *  - Switch electron/holes
     *  - Switch to select if the position 0 is at the front or back
     */
    
    /**
     * @param args the command line arguments
     */
    public static void startHitoriGUI(String[] args) 
    {
        System.out.println("Starting...");
        
        String folder = "/home/audreyazura/Documents/Work/Simulation/ChargeInEfield/Extract/Notch/Einternal/Light";
        
        CalculationConditions conditions = new CalculationConditions(true, "0.8", "1800", "1500");
        
        HitoriDenshiCIGS_Simulation simu = new HitoriDenshiCIGS_Simulation();
        simu.startSimulation(folder, conditions);
    }
    
}
