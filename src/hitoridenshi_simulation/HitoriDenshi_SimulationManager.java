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
package hitoridenshi_simulation;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystemException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshi_SimulationManager implements Runnable
{
    private final CalculationConditions m_conditions;
    private final GUICallBack m_guiApp;
    private final int m_numberOfWorker;
    private final List<BigDecimal> m_notchPositions;
    private final String m_inputFolder;
    private final String m_outputFolder;
    private final String[] m_biasVoltages;
    
    private double m_progress = 0;
    private int m_totalCalculations = 0;
    
    
    public HitoriDenshi_SimulationManager (String p_folderElectricFields, String p_outputFolder, CalculationConditions p_conditions, GUICallBack p_guiApp)
    {
        m_conditions = p_conditions;
        m_biasVoltages = m_conditions.getBiasVoltageArray();
        m_notchPositions = m_conditions.getNotchPositionArray();
        m_inputFolder = p_folderElectricFields;
        m_outputFolder = p_outputFolder;
        m_guiApp = p_guiApp;
        
        //calculating the number of worker used to run the simulation
        int nAvailableCore = Runtime.getRuntime().availableProcessors();
        int nIndependantCalculation = m_biasVoltages.length * m_notchPositions.size();
        m_numberOfWorker = (nAvailableCore < nIndependantCalculation) ? nAvailableCore : nIndependantCalculation;
    }
    
    void sendUpdate (int p_workerID, double p_workerProgress)
    {
        m_progress += 1.0 / m_totalCalculations;
        
        m_guiApp.updateProgress(p_workerID, p_workerProgress, m_progress);
    }
    
    void sendMessage (String p_message)
    {
        m_guiApp.sendMessage(p_message);
    }
    
    @Override
    public void run()
    {
        System.out.println("Starting simulation!\nFolder: " + m_inputFolder);
        System.out.println(m_notchPositions);
        System.out.println(m_conditions.getStartingPositionList());
        
        
        try
        {
            //preparing the absorbers on which the simulation will be run
            Set<Absorber> absorberList = new HashSet<>();
            //all the values in p_conditions are in SI units
            for (String bias: m_biasVoltages)
            {
                for (BigDecimal notch: m_notchPositions)
                {
                    absorberList.add(new Absorber(m_inputFolder, bias, notch, m_conditions));
                }
            }
            
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
                SimulationWorker currentWorker = new SimulationWorker(workerCounter, m_outputFolder, (HashSet) currentChunk, m_conditions, this);
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
        catch (DataFormatException ex)
        {
            Logger.getLogger(HitoriDenshi_SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (FileSystemException ex)
        {
            System.err.println("Erreur with the file "+ex.getFile()+": "+ex.getReason());
        }
        catch (IOException ex)
        {
            Logger.getLogger(HitoriDenshi_SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(HitoriDenshi_SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getNumberOfWorker()
    {
        return m_numberOfWorker;
    }
}

//0 ; 0.2 ; 0.4 ; 0.6 ; 0.8 ; 1 ; 1.2 ; 1.3 ; 1.4 ; 1.5 ; 1.6 ; 1.7 ; 1.75 ; 1.8 ; 1.85 ; 1.9 ; 1.95 ; 2
//1 ; 1.25 ; 1.5 ; 1.55 ; 1.6 ; 1.65 ; 1.7 ; 1.75 ; 1.8 ; 1.85
