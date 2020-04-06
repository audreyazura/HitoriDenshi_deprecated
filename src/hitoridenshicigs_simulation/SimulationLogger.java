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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author audreyazura
 */
public class SimulationLogger
{
    private int m_numberFrontExit;
    private int m_numberBackExit;
    
    private List<Double> m_meanTrajectory = new ArrayList<>();
    private List<Double> m_meanVelocity= new ArrayList<>();
    private List<Double> m_meanAcceleration = new ArrayList<>();
    
    private List<Double> m_fastestFrontTrajectory = new ArrayList<>();
    private List<Double> m_fastestFrontVelocity= new ArrayList<>();
    private List<Double> m_fastestFrontAcceleration = new ArrayList<>();
    
    private List<Double> m_slowestFrontTrajectory = new ArrayList<>();
    private List<Double> m_slowestFrontVelocity= new ArrayList<>();
    private List<Double> m_slowestFrontAcceleration = new ArrayList<>();
    
    private List<Double> m_meanFrontTrajectory = new ArrayList<>();
    private List<Double> m_meanFrontVelocity= new ArrayList<>();
    private List<Double> m_meanFrontAcceleration = new ArrayList<>();
    
    private List<Double> m_fastestBackTrajectory = new ArrayList<>();
    private List<Double> m_fastestBackVelocity= new ArrayList<>();
    private List<Double> m_fastestBackAcceleration = new ArrayList<>();
    
    private List<Double> m_slowestBackTrajectory = new ArrayList<>();
    private List<Double> m_slowestBackVelocity= new ArrayList<>();
    private List<Double> m_slowestBackAcceleration = new ArrayList<>();
    
    private List<Double> m_meanBackTrajectory = new ArrayList<>();
    private List<Double> m_meanBackVelocity= new ArrayList<>();
    private List<Double> m_meanBackAcceleration = new ArrayList<>();
    
    //No need to log times, it can be deduced from the length of the array and the time step
    
    public SimulationLogger()
    {
        m_numberFrontExit = 0;
        m_numberBackExit = 0;
    }
    
    public void saveToFile (double p_notchPosition, double p_initialPosition, double p_biasVoltage)
    {
        File accelerationFile = new File("");
        File exitFile = new File("");
        File meanFile = new File("");
        File frontFastFile = new File("");
        File frontSlowFile = new File("");
        File frontMeanFile = new File("");
        File backFastFile = new File("");
        File backSlowFile = new File("");
        File backMeanFile = new File("");
    }
}
