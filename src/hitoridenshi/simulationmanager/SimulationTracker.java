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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alban Lafuente
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
    
    synchronized private void addMean (ListType p_type, ArrayList<BigDecimal> p_particleTrajectoryArray, ArrayList<BigDecimal> p_particleVelocityArray, ArrayList<BigDecimal> p_particleAccelerationArray)
    {
        List<BigDecimal> trackerTrajectoryArray = new ArrayList<BigDecimal>();
        List<BigDecimal> trackerVelocityArray = new ArrayList<BigDecimal>();
        List<BigDecimal> trackerAccelerationArray = new ArrayList<BigDecimal>();
        
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
        }
        
        int trackerArraySize = trackerTrajectoryArray.size(); 
        int particleArraySize = p_particleTrajectoryArray.size();
        int shortestAcceleration = Math.min(trackerAccelerationArray.size(), p_particleAccelerationArray.size());
        
        for(int i = 0 ; i < particleArraySize ; i += 1)
        {
            if (i < trackerArraySize)
            {
                trackerTrajectoryArray.set(i, trackerTrajectoryArray.get(i).add(p_particleTrajectoryArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128)));
                trackerVelocityArray.set(i, trackerVelocityArray.get(i).add(p_particleVelocityArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128)));
                if (i < shortestAcceleration)
                {
                    trackerAccelerationArray.set(i, trackerAccelerationArray.get(i).add(p_particleAccelerationArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128)));
                }
            }
            else
            {
                trackerTrajectoryArray.add(p_particleTrajectoryArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128));
                trackerVelocityArray.add(p_particleVelocityArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128));
                if (i < particleArraySize -1)
                {
                    trackerAccelerationArray.add(p_particleAccelerationArray.get(i).divide(m_numberParticle, MathContext.DECIMAL128));
                }
            }
        }
    }
    
    private void writeFile(ListTypeToWrite p_listType, BufferedWriter p_writer, PhysicalConstants.UnitsPrefix p_prefix) throws IOException
    {
        BigDecimal multiplier = p_prefix.getMultiplier();
        String toBeWritten = new String();
        List<BigDecimal> trajectoryToWrite;
        List<BigDecimal> velocitiesToWrite;
        List<BigDecimal> accelerationsToWrite;
        
        switch(p_listType)
        {
            case GENERALMEAN:
                trajectoryToWrite = new ArrayList(m_meanTrajectory);
                velocitiesToWrite = new ArrayList(m_meanVelocity);
                accelerationsToWrite = new ArrayList(m_meanAcceleration);
                break;
            case FRONTMEAN:
                trajectoryToWrite = new ArrayList(m_meanFrontTrajectory);
                velocitiesToWrite = new ArrayList(m_meanFrontVelocity);
                accelerationsToWrite = new ArrayList(m_meanFrontAcceleration);
                break;
            case FRONTFAST:
                trajectoryToWrite = new ArrayList(m_fastestFrontTrajectory);
                velocitiesToWrite = new ArrayList(m_fastestFrontVelocity);
                accelerationsToWrite = new ArrayList(m_fastestFrontAcceleration);
                break;
            case FRONTSLOW:
                trajectoryToWrite = new ArrayList(m_slowestFrontTrajectory);
                velocitiesToWrite = new ArrayList(m_slowestFrontVelocity);
                accelerationsToWrite = new ArrayList(m_slowestFrontAcceleration);
                break;
            case BACKMEAN:
                trajectoryToWrite = new ArrayList(m_meanBackTrajectory);
                velocitiesToWrite = new ArrayList(m_meanBackVelocity);
                accelerationsToWrite = new ArrayList(m_meanBackAcceleration);
                break;
            case BACKFAST:
                trajectoryToWrite = new ArrayList(m_fastestBackTrajectory);
                velocitiesToWrite = new ArrayList(m_fastestBackVelocity);
                accelerationsToWrite = new ArrayList(m_fastestBackAcceleration);
                break;
            case BACKSLOW:
                trajectoryToWrite = new ArrayList(m_slowestBackTrajectory);
                velocitiesToWrite = new ArrayList(m_slowestBackVelocity);
                accelerationsToWrite = new ArrayList(m_slowestBackAcceleration);
                break;
            default:
                trajectoryToWrite = new ArrayList();
                velocitiesToWrite = new ArrayList();
                accelerationsToWrite = new ArrayList();
                break;
        }
        
        p_writer.write("Time (ns)\tPosition ("+p_prefix.getPrefix()+"m)\tVelocity (m/s)\tAcceleration (m²/s)");
        for (int i = 0 ; i < trajectoryToWrite.size() ; i++)
        {
            p_writer.newLine();
            toBeWritten = (new BigDecimal(i)).multiply(PhysicalConstants.UnitsPrefix.NANO.getMultiplier()).divide(CalculationConditions.DT)+"\t"+trajectoryToWrite.get(i).divide(multiplier, MathContext.DECIMAL32)+"\t"+velocitiesToWrite.get(i).round(MathContext.DECIMAL32);
            toBeWritten += i < accelerationsToWrite.size() ? "\t"+accelerationsToWrite.get(i).round(MathContext.DECIMAL32):"";
            p_writer.write(toBeWritten);
        }
        p_writer.flush();
        p_writer.close();
    }
    
    private enum ListType
    {
        GENERAL, FRONT, BACK;
    }
    
    private enum ListTypeToWrite
    {
        GENERALMEAN, FRONTMEAN, FRONTFAST, FRONTSLOW, BACKMEAN, BACKFAST, BACKSLOW;
    }
    
    synchronized public void logParticle (Particle p_particle)
    {
        ArrayList<BigDecimal> particleTrajectory = p_particle.getTrajectory();
        ArrayList<BigDecimal> particleVelocities = p_particle.getVelocityList();
        ArrayList<BigDecimal> particleAccelerations = p_particle.getAccelerationList();
        
        this.addMean(ListType.GENERAL, particleTrajectory, particleVelocities, particleAccelerations);
        
        switch (p_particle.getCollection())
        {
            case FRONT:
                m_numberFrontExit += 1;
                addMean(ListType.FRONT, particleTrajectory, particleVelocities, particleAccelerations);
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
                break;
            case BACK:
                m_numberBackExit += 1;
                addMean(ListType.BACK, particleTrajectory, particleVelocities, particleAccelerations);
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
                break;
            case NOTCOLLECTED:
                m_numberNotExited += 1; 
                break;
        }
    }
    
    synchronized public void saveToFile (String p_generalOutputFolder, String p_biasVoltage, String p_notchPosition, BigDecimal p_initialPosition, PhysicalConstants.UnitsPrefix p_prefix) throws FileSystemException, IOException
    {
        String initialPositionString = String.valueOf(p_initialPosition.intValue());
        
        File currenOutputFolder = new File (new String(p_generalOutputFolder + "/E" + p_biasVoltage + "V/Notch"+p_notchPosition+"nm/xi"+initialPositionString+"nm"));
        
        if (currenOutputFolder.mkdirs() || currenOutputFolder.isDirectory())
        {
            BufferedWriter exitFileBuffer = new BufferedWriter(new FileWriter(currenOutputFolder + "/Exit.sim"));
            exitFileBuffer.write("Abscissa\tPosition\t#carriers");
            exitFileBuffer.newLine();
            exitFileBuffer.write("0\tNot accounted\t"+m_numberNotExited);
            exitFileBuffer.newLine();
            exitFileBuffer.write("1\tFront\t"+m_numberFrontExit);
            exitFileBuffer.newLine();
            exitFileBuffer.write("2\tBack\t"+m_numberBackExit);
            exitFileBuffer.flush();
            exitFileBuffer.close();
            
            writeFile(ListTypeToWrite.GENERALMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovement.sim")), p_prefix);
            writeFile(ListTypeToWrite.FRONTFAST, new BufferedWriter(new FileWriter(currenOutputFolder + "/FastestMovementToFront.sim")), p_prefix);
            writeFile(ListTypeToWrite.FRONTSLOW, new BufferedWriter(new FileWriter(currenOutputFolder + "/SlowestMovementToFront.sim")), p_prefix);
            writeFile(ListTypeToWrite.FRONTMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovementToFront.sim")), p_prefix);
            writeFile(ListTypeToWrite.BACKFAST, new BufferedWriter(new FileWriter(currenOutputFolder + "/FastestMovementToBack.sim")), p_prefix);
            writeFile(ListTypeToWrite.BACKSLOW, new BufferedWriter(new FileWriter(currenOutputFolder + "/SlowestMovementToBack.sim")), p_prefix);
            writeFile(ListTypeToWrite.BACKMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovementToBack.sim")), p_prefix);
        }
        else
        {
            throw new FileSystemException(currenOutputFolder.getPath(), null, "Impossible to create the output directory.");
        }
    }
}
