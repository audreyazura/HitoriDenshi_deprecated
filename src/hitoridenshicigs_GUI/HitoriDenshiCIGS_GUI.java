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
     * Elements to include in the GUI:
     *  - Field to enter the list of bias voltage
     *  - Field to enter the list of notch position
     *  - Field to enter the list of starting positions
     *  - Field to select the folder containing the electric field files
     *  - Field for the number of particle
     *  - Switch electron/holes
     *  - Switch to select if the position 0 is at the front or back
     *  - Field to enter the size of the buffer and window
     *  - Field to enter the size of the absorber
     *  - Field to enter the number of particle to simulate at each iteration
     */
    
    /**
     * @param args the command line arguments
     */
    public static void startHitoriGUI(String[] args) 
    {
        System.out.println("Starting...");
        
        String folder = "/home/audreyazura/Documents/Work/Simulation/ChargeInEfield/Extract/Notch/Einternal/Light";
        boolean isElectron = true;
        boolean zeroAtFront = true;
        double bufferWindow = 500.0;
        double absorberSize = 2500.0;
        int numberOfParticle = 10000;
        String simulatedBiases = "0.8";
        String notchPositions = "1800";
        String generationPositions = "1500";
        
        CalculationConditions conditions = new CalculationConditions(isElectron, zeroAtFront, bufferWindow, absorberSize, numberOfParticle, simulatedBiases, notchPositions, generationPositions);
        
        HitoriDenshiCIGS_Simulation simu = new HitoriDenshiCIGS_Simulation();
        simu.startSimulation(folder, conditions);
    }
    
}
