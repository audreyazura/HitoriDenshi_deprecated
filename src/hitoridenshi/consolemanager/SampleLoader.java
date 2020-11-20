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
package hitoridenshi.consolemanager;

import hitoridenshi.simulationmanager.Sample;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author audreyazura
 */
public class SampleLoader implements Sample
{
    private final boolean m_isFrontGradingInCB;
    private final boolean m_isBackGradingInCB;
    private final File m_sampleFile;
    private final Map<String, BigDecimal> m_grading;
    private final List<HashMap<String, BigDecimal>> m_trapList = new ArrayList<>();
    
    public SampleLoader (String p_fileAddress, HashMap<String, String> p_gradingString, List<HashMap<String, String>> p_trapsString, boolean p_frontGradingCB, boolean p_backGradingCB)
    {
        m_isFrontGradingInCB = p_frontGradingCB;
        m_isBackGradingInCB = p_backGradingCB;
        
        m_sampleFile = new File(p_fileAddress);
        
        m_grading = convertMap(p_gradingString, "Grading");
        
        for (int i = 0 ; i < p_trapsString.size() ; i += 1)
        {
            HashMap<String, String> trap = p_trapsString.get(i);
            
            m_trapList.add(convertMap(trap, "Trap" + i));
        }
    }
    
    private HashMap<String, BigDecimal> convertMap (Map<String, String> p_mapToCopy, String p_mapTag)
    {
        HashMap<String, BigDecimal> convertedMap = new HashMap<>();
        
        for (String key: p_mapToCopy.keySet())
        {
            String value = p_mapToCopy.get(key);
            
            try
            {
                convertedMap.put(key, value.equals("") ? BigDecimal.ZERO : new BigDecimal(value));
            }
            catch (NumberFormatException ex)
            {
                Logger.getLogger(SampleLoader.class.getName()).log(Level.SEVERE, "Problem with key " + key + " in Map " + p_mapTag, ex);
            }
        }
        
        return convertedMap;
    }
    
    @Override
    public File getConfigFile()
    {
        return m_sampleFile;
    }
    
    @Override
    public HashMap<String, BigDecimal> getGradingValue()
    {
        return new HashMap(m_grading);
    }
    
    @Override
    public List<HashMap<String, BigDecimal>> getTraps()
    {
        return new ArrayList(m_trapList);
    }
    
    @Override
    public boolean isFrontGradingInCB()
    {
        return m_isFrontGradingInCB;
    }
    
    @Override
    public boolean isBackGradingInCB()
    {
        return m_isBackGradingInCB;
    }
}
