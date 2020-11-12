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

import com.github.audreyazura.commonutils.PhysicsTools;
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
 * Track an ongoing simulation, remembering the fastest and slowest particle speed and trajectory, as well as the mean trajectory and speed of the particle generated 
 * @author Alban Lafuente
 */
public class SimulationTracker
{
    private BigDecimal m_numberParticle;
    
    private int m_numberFrontExit;
    private int m_numberBackExit;
    private int m_numberNotExited;
    private int m_captured;
    
    //save for the overall mean trajectory, velocity and acceleration
    private List<BigDecimal> m_meanTrajectory = new ArrayList<>();
    private List<BigDecimal> m_meanVelocity= new ArrayList<>();
    private List<BigDecimal> m_meanAcceleration = new ArrayList<>();
    
    //save the trajectory, speed and acceleration history of the particle that reached the front the fastest
    private List<BigDecimal> m_fastestFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_fastestFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_fastestFrontAcceleration = new ArrayList<>();
    
    //save the trajectory, speed and acceleration history of the particle that reached the front the slowest
    private List<BigDecimal> m_slowestFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_slowestFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_slowestFrontAcceleration = new ArrayList<>();
    
    //save the mean trajectory, speed and acceleration history of the particle that reached the front
    private List<BigDecimal> m_meanFrontTrajectory = new ArrayList<>();
    private List<BigDecimal> m_meanFrontVelocity= new ArrayList<>();
    private List<BigDecimal> m_meanFrontAcceleration = new ArrayList<>();
    
    //save the trajectory, speed and acceleration history of the particle that reached the back the fastest
    private List<BigDecimal> m_fastestBackTrajectory = new ArrayList<>();
    private List<BigDecimal> m_fastestBackVelocity= new ArrayList<>();
    private List<BigDecimal> m_fastestBackAcceleration = new ArrayList<>();
    
    //save the trajectory, speed and acceleration history of the particle that reached the back the slowest
    private List<BigDecimal> m_slowestBackTrajectory = new ArrayList<>();
    private List<BigDecimal> m_slowestBackVelocity= new ArrayList<>();
    private List<BigDecimal> m_slowestBackAcceleration = new ArrayList<>();
    
    //save the mean trajectory, speed and acceleration history of the particle that reached the back
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
        m_captured = 0;
    }
    
    /**
     * Add the current particle to the given category of mean trajectory, speed and acceleration
     * @param p_type the type of list to which the mean have to be added.
     * @param p_absorber the absorber in which the absorber was.
     * @param p_particleTrajectoryArray the array containing the position history of the particle
     * @param p_particleVelocityArray the array containing the speed history of the particle
     * @param p_particleAccelerationArray the array containing the acceleration history of the particle
     */
    synchronized private void addMean(MeanType p_type, Absorber p_absorber, ArrayList<BigDecimal> p_particleTrajectoryArray, ArrayList<BigDecimal> p_particleVelocityArray, ArrayList<BigDecimal> p_particleAccelerationArray)
    {
        List<BigDecimal> trackerTrajectoryArray = new ArrayList<BigDecimal>();
        List<BigDecimal> trackerVelocityArray = new ArrayList<BigDecimal>();
        List<BigDecimal> trackerAccelerationArray = new ArrayList<BigDecimal>();
        BigDecimal toAddIfLongerList = BigDecimal.ZERO;
        
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
                toAddIfLongerList = CalculationConditions.formatBigDecimal(new BigDecimal(m_numberFrontExit*p_absorber.getFrontPosition().doubleValue()));
                break;
            case BACK:
                trackerTrajectoryArray = m_meanBackTrajectory;
                trackerVelocityArray = m_meanBackVelocity;
                trackerAccelerationArray = m_meanBackAcceleration;
                toAddIfLongerList = CalculationConditions.formatBigDecimal(new BigDecimal(m_numberBackExit*p_absorber.getBackPosition().doubleValue()));
                break;
        }
        
        int trackerArraySize = trackerTrajectoryArray.size(); 
        int particleArraySize = p_particleTrajectoryArray.size();
        int shortestAcceleration = Math.min(trackerAccelerationArray.size(), p_particleAccelerationArray.size());
        int index;
        
        for(index = 0 ; index < particleArraySize ; index += 1)
        {
            if (index < trackerArraySize)
            {
                trackerTrajectoryArray.set(index, trackerTrajectoryArray.get(index).add(p_particleTrajectoryArray.get(index).divide(m_numberParticle, MathContext.DECIMAL128)));
                trackerVelocityArray.set(index, trackerVelocityArray.get(index).add(p_particleVelocityArray.get(index).divide(m_numberParticle, MathContext.DECIMAL128)));
                if (index < shortestAcceleration)
                {
                    trackerAccelerationArray.set(index, trackerAccelerationArray.get(index).add(p_particleAccelerationArray.get(index).divide(m_numberParticle, MathContext.DECIMAL128)));
                }
            }
            else
            {
                if (p_type.equals(MeanType.GENERAL) && m_numberBackExit + m_numberFrontExit + m_numberNotExited > 1)
                {
                    toAddIfLongerList = trackerTrajectoryArray.get(index-1);
                }
                trackerTrajectoryArray.add((p_particleTrajectoryArray.get(index).add(toAddIfLongerList)).divide(m_numberParticle, MathContext.DECIMAL128));
                trackerVelocityArray.add(p_particleVelocityArray.get(index).divide(m_numberParticle, MathContext.DECIMAL128));
                if (index < particleArraySize -1)
                {
                    trackerAccelerationArray.add(p_particleAccelerationArray.get(index).divide(m_numberParticle, MathContext.DECIMAL128));
                }
            }
        }
        
        //if the tracker mean arrays are longer than the particle arrays, we add the end position of the particle to the mean until reaching the end of the array
        while (index < trackerArraySize)
        {
            trackerTrajectoryArray.set(index, trackerTrajectoryArray.get(index).add(p_particleTrajectoryArray.get(particleArraySize-1).divide(m_numberParticle, MathContext.DECIMAL128)));
            trackerVelocityArray.set(index, trackerVelocityArray.get(index).add(p_particleVelocityArray.get(particleArraySize-1).divide(m_numberParticle, MathContext.DECIMAL128)));
            if (index < shortestAcceleration)
            {
                trackerAccelerationArray.set(index, trackerAccelerationArray.get(index).add(p_particleAccelerationArray.get(particleArraySize-1).divide(m_numberParticle, MathContext.DECIMAL128)));
            }
            
            index += 1;
        }
    }
    
    /**
     * Write the list designated by the given list type to the given BufferedWriter
     * @param p_listType the type of the list to write
     * @param p_writer the BufferedWriter in which to write the lists
     * @param p_prefix the SI prefix of the distance unit
     * @throws IOException 
     */
    private void writeFile(ListType p_listType, BufferedWriter p_writer, PhysicsTools.UnitsPrefix p_prefix) throws IOException
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
            toBeWritten = (new BigDecimal(i)).multiply(CalculationConditions.DT).divide(PhysicsTools.UnitsPrefix.NANO.getMultiplier())+"\t"+trajectoryToWrite.get(i).divide(multiplier, MathContext.DECIMAL32)+"\t"+velocitiesToWrite.get(i).round(MathContext.DECIMAL32);
            toBeWritten += i < accelerationsToWrite.size() ? "\t"+accelerationsToWrite.get(i).round(MathContext.DECIMAL32):"";
            p_writer.write(toBeWritten);
        }
        p_writer.flush();
        p_writer.close();
    }
    
    /**
     * The different type of mean lists, used to identify where to write each particle
        GENERAL: the list containing the mean for all the particle
        FRONT: the list for the particle collected at the front
        BACK: the list for the particle collected at the back
     */
    private enum MeanType
    {
        GENERAL, FRONT, BACK;
    }
    
    /**
     * Used to identify the type of list to write.
     * GENERALMEAN: the mean lists of all the particles
     * FRONTMEAN: the mean lists of the particles having exited at the front
     * FRONTFAST: the lists of the fastest particle having exited to the front
     * FRONTSLOW: the lists of the slowest particle having exited to the front
     * BACKMEAN: the mean lists of the particles having exited at the back
     * BACKFAST: the lists of the fastest particle having exited to the back
     * BACKSLOW: the lists of the slowest particle having exited to the back
     */
    private enum ListType
    {
        GENERALMEAN, FRONTMEAN, FRONTFAST, FRONTSLOW, BACKMEAN, BACKFAST, BACKSLOW;
    }
    
    /**
     * Register a particle to the tracker
     * @param p_particle the particle to be registered
     * @param p_absorber the absorber that particle was in
     */
    synchronized public void logParticle(Particle p_particle, Absorber p_absorber)
    {
        ArrayList<BigDecimal> particleTrajectory = p_particle.getTrajectory();
        ArrayList<BigDecimal> particleVelocities = p_particle.getVelocityList();
        ArrayList<BigDecimal> particleAccelerations = p_particle.getAccelerationList();
        
        if (!p_particle.isCaptured())
        {
            addMean(MeanType.GENERAL, p_absorber, particleTrajectory, particleVelocities, particleAccelerations);
        }
        
        switch (p_particle.getCollection())
        {
            case FRONTCOLLECTED:
                m_numberFrontExit += 1;
                addMean(MeanType.FRONT, p_absorber, particleTrajectory, particleVelocities, particleAccelerations);
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
            case BACKCOLLECTED:
                m_numberBackExit += 1;
                addMean(MeanType.BACK, p_absorber, particleTrajectory, particleVelocities, particleAccelerations);
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
                if (p_particle.isCaptured())
                {
                    m_captured += 1;
                }
                break;
        }
    }
    
    /**
     * Save the registered data to a file
     * @param p_generalOutputFolder the address of the folder in which to write the files
     * @param p_biasVoltage the applied bias voltage, used to name the file
     * @param p_notchPosition the position of the notch in the absorber, used to name the file
     * @param p_initialPosition the initial position of the electrons, used to name the file
     * @param p_prefix the SI prefix of the abscissa unit
     * @throws FileSystemException
     * @throws IOException 
     */
    synchronized public void saveToFile(String p_outputFolder, BigDecimal p_initialPosition, PhysicsTools.UnitsPrefix p_prefix) throws FileSystemException, IOException
    {
        String initialPositionString = String.valueOf(p_initialPosition.intValue());
        
        File currenOutputFolder = new File (p_outputFolder + "/xi"+initialPositionString+"nm");
        
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
            
            writeFile(ListType.GENERALMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovement.sim")), p_prefix);
            writeFile(ListType.FRONTFAST, new BufferedWriter(new FileWriter(currenOutputFolder + "/FastestMovementToFront.sim")), p_prefix);
            writeFile(ListType.FRONTSLOW, new BufferedWriter(new FileWriter(currenOutputFolder + "/SlowestMovementToFront.sim")), p_prefix);
            writeFile(ListType.FRONTMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovementToFront.sim")), p_prefix);
            writeFile(ListType.BACKFAST, new BufferedWriter(new FileWriter(currenOutputFolder + "/FastestMovementToBack.sim")), p_prefix);
            writeFile(ListType.BACKSLOW, new BufferedWriter(new FileWriter(currenOutputFolder + "/SlowestMovementToBack.sim")), p_prefix);
            writeFile(ListType.BACKMEAN, new BufferedWriter(new FileWriter(currenOutputFolder + "/MeanMovementToBack.sim")), p_prefix);
        }
        else
        {
            throw new FileSystemException(currenOutputFolder.getPath(), null, "Impossible to create the output directory.");
        }
    }
}
