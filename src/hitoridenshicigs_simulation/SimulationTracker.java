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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author audreyazura
 */
public class SimulationTracker
{
    private BigDecimal m_numberParticle;
    
    private int m_numberFrontExit;
    private int m_numberBackExit;
    private int m_numberNotExited;
    
    private List<BigDecimal> m_meanTrajectory = new ArrayList<>();
    private List<BigDecimal> m_meanVelocity= new ArrayList<>();
    private List<BigDecimal> m_meanAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_fastestFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_fastestFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_fastestFrontAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_slowestFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_slowestFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_slowestFrontAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_meanFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_meanFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_meanFrontAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_fastestBackTrajectory = new ArrayList<>();
    private List<BigDecimal> m_fastestBackVelocity= new ArrayList<>();
    private List<BigDecimal> m_fastestBackAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_slowestBackTrajectory = new ArrayList<>();
    private List<BigDecimal> m_slowestBackVelocity= new ArrayList<>();
    private List<BigDecimal> m_slowestBackAcceleration = new ArrayList<>();
    
    private List<BigDecimal> m_meanBackTrajectory = new ArrayList<>();
    private List<BigDecimal> m_meanBackVelocity= new ArrayList<>();
    private List<BigDecimal> m_meanBackAcceleration = new ArrayList<>();
    
    //No need to log times, it can be deduced from the length of the array and the time step
    
    public SimulationTracker(int p_numberParticle)
    {
        m_numberParticle = new BigDecimal(p_numberParticle);
        m_numberBackExit = 0;
        m_numberFrontExit = 0;
        m_numberNotExited = 0;
    }
    
    private void addMean (ListType p_type, ArrayList<BigDecimal> p_particleTrajectoryArray, ArrayList<BigDecimal> p_particleVelocityArray, ArrayList<BigDecimal> p_particleAccelerationArray)
    {
        List<BigDecimal> trackerTrajectoryArray, trackerVelocityArray, trackerAccelerationArray;
        
        switch(p_type)
        {
            case GENERAL:
                trackerTrajectoryArray = m_meanTrajectory;
                trackerVelocityArray = m_meanVelocity;
                trackerAccelerationArray = m_meanAcceleration;
                break;
            case FRONT:
                trackerTrajectoryArray = m_meanFrontTrajectory;
                trackerVelocityArray = m_meanFrontVelocity;
                trackerAccelerationArray = m_meanFrontAcceleration;
                break;
            case BACK:
                trackerTrajectoryArray = m_meanBackTrajectory;
                trackerVelocityArray = m_meanBackVelocity;
                trackerAccelerationArray = m_meanBackAcceleration;
                break;
            default:
                trackerTrajectoryArray = new ArrayList<BigDecimal>();
                trackerVelocityArray = new ArrayList<BigDecimal>();
                trackerAccelerationArray = new ArrayList<BigDecimal>();
        }
        
        int trackerArraySize = trackerTrajectoryArray.size(); 
        int particleArraySize = p_particleTrajectoryArray.size();
        
        if (trackerArraySize <= particleArraySize)
        {
            for(int i = 0 ; i < particleArraySize ; i += 1)
            {
                if (i < trackerArraySize)
                {
                    trackerTrajectoryArray.set(i, trackerTrajectoryArray.get(i).add(p_particleTrajectoryArray.get(i).divide(m_numberParticle)));
                    trackerVelocityArray.set(i, trackerVelocityArray.get(i).add(p_particleVelocityArray.get(i).divide(m_numberParticle)));
                    trackerAccelerationArray.set(i, trackerAccelerationArray.get(i).add(p_particleAccelerationArray.get(i).divide(m_numberParticle)));
                }
                else
                {
                    trackerTrajectoryArray.add(p_particleTrajectoryArray.get(i).divide(m_numberParticle));
                    trackerVelocityArray.add(p_particleVelocityArray.get(i).divide(m_numberParticle));
                    
                    if (i < particleArraySize -1)
                    {
                        trackerAccelerationArray.add(p_particleAccelerationArray.get(i).divide(m_numberParticle));
                    }
                }
            }
        }
        else
        {
            for(int i = 0 ; i < trackerArraySize ; i += 1)
            {
                if (i < particleArraySize)
                {
                    trackerTrajectoryArray.set(i, trackerTrajectoryArray.get(i).add(p_particleTrajectoryArray.get(i).divide(m_numberParticle)));
                    trackerVelocityArray.set(i, trackerVelocityArray.get(i).add(p_particleVelocityArray.get(i).divide(m_numberParticle)));
                    trackerAccelerationArray.set(i, trackerAccelerationArray.get(i).add(p_particleAccelerationArray.get(i).divide(m_numberParticle)));
                }
                else
                {
                    trackerTrajectoryArray.set(i, trackerTrajectoryArray.get(i));
                    trackerVelocityArray.set(i, trackerVelocityArray.get(i));
                    
                    if (i < trackerArraySize -1)
                    {
                        trackerAccelerationArray.set(i, trackerAccelerationArray.get(i));
                    }
                }
            }
        }
    }
    
    private enum ListType
    {
        GENERAL, FRONT, BACK;
    }
    
    public void logParticle (Particle p_particle)
    {
        ArrayList<BigDecimal> particleTrajectory = p_particle.getTrajectory();
        ArrayList<BigDecimal> particleVelocities = p_particle.getVelocityList();
        ArrayList<BigDecimal> particleAccelerations = p_particle.getAccelerationList();
        
        this.addMean(ListType.GENERAL, particleTrajectory, particleVelocities, particleAccelerations);
        
        switch (p_particle.getCollection())
        {
            case FRONT:
                m_numberFrontExit += 1;
                this.addMean(ListType.FRONT, particleTrajectory, particleVelocities, particleAccelerations);
                //if it is the first particle to reach the front -> we log everything directly
                if (m_numberFrontExit == 1)
                {
                    m_fastestFrontTrajectory = particleTrajectory;
                    m_fastestFrontVelocity = particleVelocities;
                    m_fastestFrontAcceleration = particleAccelerations;
                    
                    m_slowestFrontTrajectory = particleTrajectory;
                    m_slowestFrontVelocity = particleVelocities;
                    m_slowestFrontAcceleration = particleAccelerations;
                }
                else
                {
                    if (particleTrajectory.size() < m_fastestFrontTrajectory.size())
                    {
                        m_fastestFrontTrajectory = particleTrajectory;
                        m_fastestFrontVelocity = particleVelocities;
                        m_fastestFrontAcceleration = particleAccelerations;
                    }
                    if (particleTrajectory.size() > m_slowestFrontTrajectory.size())
                    {
                        m_slowestFrontTrajectory = particleTrajectory;
                        m_slowestFrontVelocity = particleVelocities;
                        m_slowestFrontAcceleration = particleAccelerations;
                    }
                }
            case BACK:
                m_numberBackExit += 1;
                this.addMean(ListType.BACK, particleTrajectory, particleVelocities, particleAccelerations);
                //if it is the first particle to reach the front -> we log everything directly
                if (m_numberBackExit == 1)
                {
                    m_fastestBackTrajectory = particleTrajectory;
                    m_fastestBackVelocity = particleVelocities;
                    m_fastestBackAcceleration = particleAccelerations;
                    
                    m_slowestBackTrajectory = particleTrajectory;
                    m_slowestBackVelocity = particleVelocities;
                    m_slowestBackAcceleration = particleAccelerations;
                }
                else
                {
                    if (particleTrajectory.size() < m_fastestBackTrajectory.size())
                    {
                        m_fastestBackTrajectory = particleTrajectory;
                        m_fastestBackVelocity = particleVelocities;
                        m_fastestBackAcceleration = particleAccelerations;
                    }
                    if (particleTrajectory.size() > m_slowestBackTrajectory.size())
                    {
                        m_slowestBackTrajectory = particleTrajectory;
                        m_slowestBackVelocity = particleVelocities;
                        m_slowestBackAcceleration = particleAccelerations;
                    }
                }
            case NOTCOLLECTED:
                m_numberNotExited += 1;            
        }
    }
    
    public void saveToFile (String p_biasVoltage, String p_notchPosition, BigDecimal p_initialPosition)
    {
        String initialPosition = String.valueOf(p_initialPosition.intValue());
        
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
