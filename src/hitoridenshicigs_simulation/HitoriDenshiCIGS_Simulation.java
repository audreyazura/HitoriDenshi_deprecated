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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystemException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshiCIGS_Simulation implements Runnable
{
    private final CalculationConditions m_conditions;
    private final String m_inputFolder;
    private final String m_outputFolder;
    
    public HitoriDenshiCIGS_Simulation (String p_folderElectricFields, String p_outputFolder, CalculationConditions p_conditions)
    {
        m_conditions = p_conditions;
        m_inputFolder = p_folderElectricFields;
        m_outputFolder = p_outputFolder;
    }
    
    public void run()
    {
        System.out.println("Starting simulation!\nFolder: " + m_inputFolder);
        System.out.println(m_conditions.getNotchPositionArray());
        System.out.println(m_conditions.getStartingPositionList());
        
        
        try
        {
            //preparing the absorbers on which the simulation will be run
            Set<Absorber> absorberList = new HashSet<>();
            //all the values in p_conditions are in SI units
            for (String bias: m_conditions.getBiasVoltageArray())
            {
                for (BigDecimal notch: m_conditions.getNotchPositionArray())
                {
                    absorberList.add(new Absorber(m_inputFolder, bias, notch, m_conditions));
                }
            }
            
            //preparing the different chunk to be threaded
            int nAvailableCore = Runtime.getRuntime().availableProcessors();
            int nAbsorbers = absorberList.size();
            int nWorker = (nAvailableCore < nAbsorbers) ? nAvailableCore : nAbsorbers;
            //int division casting to an int truncate it
            int chunkSize = nAbsorbers / nWorker;
            int nSupplementaryAbsorber = nAbsorbers % nWorker;
            Iterator<Absorber> absorberListIterator = absorberList.iterator();
            Thread[] workerArray = new Thread[nWorker];
            
            for (int workerCounter = 0 ; workerCounter < nWorker ; workerCounter +=1)
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
                Thread currentWorker = new Thread(new SimulationWorker(workerCounter+1, m_outputFolder, (HashSet) currentChunk, m_conditions));
                currentWorker.start();
                workerArray[workerCounter] = currentWorker;
            }
            
            //waiting for the threads to finish
            for (int threadWalker = 0 ; threadWalker < nWorker ; threadWalker += 1)
            {
                workerArray[threadWalker].join();
            }
            
            
        }
        catch (DataFormatException ex)
        {
            Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (FileSystemException ex)
        {
            System.err.println("Erreur with the file "+ex.getFile()+": "+ex.getReason());
        }
        catch (IOException ex)
        {
            Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(HitoriDenshiCIGS_Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("End of simulation!");
    } 
    
}

//0 ; 0.2 ; 0.4 ; 0.6 ; 0.8 ; 1 ; 1.2 ; 1.3 ; 1.4 ; 1.5 ; 1.6 ; 1.7 ; 1.75 ; 1.8 ; 1.85 ; 1.9 ; 1.95 ; 2
//1 ; 1.25 ; 1.5 ; 1.55 ; 1.6 ; 1.65 ; 1.7 ; 1.75 ; 1.8 ; 1.85
