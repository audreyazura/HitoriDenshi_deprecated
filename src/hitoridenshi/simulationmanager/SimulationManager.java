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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class managing the whole simulation
 * @author Alban Lafuente
 */
public class SimulationManager implements Runnable
{
    private final CalculationConditions m_conditions;
    private final ProgressNotifierInterface m_guiApp;
    private final int m_numberOfWorker;
    private final String m_outputFolder;
    
    private double m_progress = 0;
    private int m_totalCalculations = 0;
    
    
    public SimulationManager (String p_outputFolder, CalculationConditions p_conditions, ProgressNotifierInterface p_guiApp)
    {
        m_conditions = p_conditions;
        m_outputFolder = p_outputFolder;
        m_guiApp = p_guiApp;
        
        //calculating the number of worker used to run the simulation
        int nAvailableCore = Runtime.getRuntime().availableProcessors();
        int nIndependantCalculation = p_conditions.getAbsorbers().size();
        m_numberOfWorker = Integer.min(nAvailableCore, nIndependantCalculation);
    }
    
    /**
     * Function for the class of the package to send an update on their progress to the terminal chosen for the execution
     * @param p_workerID the ID of the worker sending the message
     * @param p_workerProgress the progress of the worker
     */
    void sendUpdate (int p_workerID, double p_workerProgress)
    {
        m_progress += 1.0 / m_totalCalculations;
        
        m_guiApp.updateProgress(p_workerID, p_workerProgress, m_progress);
    }
    
    /**
     * Function for the class of the package to send a message to the chosen terminal
     * @param p_message the message to be printed
     */
    void sendMessage (String p_message)
    {
        m_guiApp.sendMessage(p_message);
    }
    
    //put more of this into the constructor (such as the creation of the absorber, and possibly the one of the worker too
    @Override
    public void run()
    {      
        sendMessage("Launching simulation...\n");
        try
        {
            List<Absorber> absorberList = m_conditions.getAbsorbers();
            
            //int division casting to an int truncate it
            int nAbsorbers = absorberList.size();
            int chunkSize = nAbsorbers / m_numberOfWorker;
            int nSupplementaryAbsorber = nAbsorbers % m_numberOfWorker;
            Iterator<Absorber> absorberListIterator = absorberList.iterator();
            Thread[] workerArray = new Thread[m_numberOfWorker];
            
            for (int workerCounter = 0 ; workerCounter < m_numberOfWorker ; workerCounter +=1)
            {
                Set<Absorber> currentChunk = new HashSet<>();
                int currentChunkSize = chunkSize;
                if (nSupplementaryAbsorber > 0)
                {
                    currentChunkSize += 1;
                    nSupplementaryAbsorber -= 1;
                }
                
                //adding the required number of Absorber to the current chunk
                for (int eltsCounter = 0 ; eltsCounter < currentChunkSize ; eltsCounter += 1)
                {
                    currentChunk.add(absorberListIterator.next());
                }
                
                //starting a thread with the current chunk
                SimulationWorker currentWorker = new SimulationWorker(workerCounter, m_outputFolder, (HashSet) currentChunk, m_conditions, new PcgRSFast(workerCounter*100000, workerCounter*10+workerCounter), this);
                m_totalCalculations += currentWorker.getNumberCalculations();
                Thread currentThread = new Thread(currentWorker);
                currentThread.start();
                workerArray[workerCounter] = currentThread;
            }
            
            //waiting for the threads to finish
            for (int threadWalker = 0 ; threadWalker < m_numberOfWorker ; threadWalker += 1)
            {
                workerArray[threadWalker].join();
            }
            
            sendMessage("\nEnd of simulation!");
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getNumberOfWorker()
    {
        return m_numberOfWorker;
    }
}
