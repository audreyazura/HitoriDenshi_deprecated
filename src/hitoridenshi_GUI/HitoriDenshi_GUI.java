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
package hitoridenshi_GUI;

import hitoridenshi_simulation.GUICallBack;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Alban Lafuente
 */
public class HitoriDenshi_GUI extends Application implements MainWindowCall, GUICallBack
{
    private Stage m_mainStage;
    private SimulationWindowController m_simulationWindowController;
    
    /**
     * @param args the command line arguments
     */
    public void startHitoriGUI(String[] args) 
    {
        Font.loadFont(HitoriDenshi_GUI.class.getResource("SourceSansPro-Regular.ttf").toExternalForm(), 10);
        launch(args);
    }
    
    @Override
    public void start(Stage stage)
    {
        m_mainStage = stage;
        launchParametersWindow(new File("ConfigurationFiles/default.json"));
    }
    
    @Override
    public Stage getMainStage()
    {
        return m_mainStage;
    }
    
    @Override
    public void launchParametersWindow(File p_configurationFile)
    {
        
        FXMLLoader parameterWindowLoader = new FXMLLoader(HitoriDenshi_GUI.class.getResource("FXMLParametersWindow.fxml"));
        
        try
        {
            Parent windowFxml = parameterWindowLoader.load();
	    FXMLParametersWindowController controller = parameterWindowLoader.getController();
	    controller.initialize(this, p_configurationFile);
            m_mainStage.setScene(new Scene(windowFxml, 800, 800));
	    m_mainStage.show();
        }
        catch (IOException ex)
        {
            Logger.getLogger(HitoriDenshi_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void launchOnGoingSimulationWindow(int p_workerAmount, File p_tempConfigFile)
    {
        FXMLLoader simulationTrackerWindowLoader = new FXMLLoader(HitoriDenshi_GUI.class.getResource("FXMLOnGoingSimulationWindow.fxml"));
        
        try
        {
            Parent simulationWindowFxml = simulationTrackerWindowLoader.load();
            SimulationWindowController controller = simulationTrackerWindowLoader.getController();
            m_simulationWindowController = controller;
            controller.initialize(p_tempConfigFile, this, p_workerAmount);
            int longestColumn = p_workerAmount - p_workerAmount / 2;
            m_mainStage.setScene(new Scene(simulationWindowFxml, 800, 525+longestColumn*50));
	    m_mainStage.show();
        }
        catch (IOException ex)
        {
            Logger.getLogger(HitoriDenshi_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void updateProgress (int p_workerID, double p_workerProgress, double p_globalProgress)
    {
        m_simulationWindowController.updateProgressIndividual(p_workerID, p_workerProgress, p_globalProgress);
    }
    
    @Override
    public void sendMessage (String p_message)
    {
        if (p_message != null)
        {
            m_simulationWindowController.updateMessage(p_message);
        }
    }
}
