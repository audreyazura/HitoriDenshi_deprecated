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
package hitoridenshi.guimanager;

import javafx.stage.Stage;
import nu.studer.java.util.OrderedProperties;

/**
 * an interface for the GUI manager
 * @author Alban Lafuente
 */
public interface MainWindowCall
{
    void resizeStage();
    
    /**
     * put the configuration window of the simulation on the stage
     * @param p_configurationProperties an OrderedProperties to fill the configuration window
     */
    void launchParametersWindow(OrderedProperties p_configurationProperties);
    
    /**
     * launch the window to track the simulation
     * @param p_workerAmount the number of core used by the simulation
     * @param p_tempConfigProperties a properties with the parameters of the simulation just launched
     */
    void launchOnGoingSimulationWindow(int p_workerAmount, OrderedProperties p_tempConfigProperties);
}
