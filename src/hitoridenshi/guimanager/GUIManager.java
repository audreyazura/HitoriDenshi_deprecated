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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import nu.studer.java.util.OrderedProperties;
import hitoridenshi.executionmanager.OutputInterface;

/**
 *
 * @author Alban Lafuente
 */
public class GUIManager extends Application implements OutputInterface
{
    private SimulationWindowController m_simulationWindowController;
    private Stage m_mainStage;
    
    @Override
    public void startOutput(String[] curratedArguments) 
    {
        Font.loadFont(GUIManager.class.getResource("SourceSansPro-Regular.ttf").toExternalForm(), 10);
        launch(curratedArguments);
    }
    
    @Override
    public void start(Stage stage)
    {
        m_mainStage = stage;
        File propertiesFile = new File(getParameters().getRaw().get(0));
        try
        {
            Reader fileReader = new FileReader(propertiesFile);
            OrderedProperties properties = new OrderedProperties();
            properties.load(fileReader);
            launchParametersWindow(properties);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void launchParametersWindow(OrderedProperties p_configurationProperties)
    {
        FXMLLoader parameterWindowLoader = new FXMLLoader(GUIManager.class.getResource("FXMLParametersWindow.fxml"));
        
        try
        {
            Parent windowFxml = parameterWindowLoader.load();
            ParametersWindowController controller = parameterWindowLoader.getController();
            m_mainStage.setScene(new Scene(windowFxml));
	    controller.initialize(this, p_configurationProperties);
            m_mainStage.setTitle("Hitori Denshi");
            
            //quick fix for the case Nsample > 2, where the ScrollPane doesn't activate scroll
            m_mainStage.setMaximized(true);
            m_mainStage.setResizable(false);
            
            m_mainStage.sizeToScene();
	    m_mainStage.show();
            m_mainStage.setMaxHeight(m_mainStage.getHeight());
        }
        catch (IOException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void launchOnGoingSimulationWindow(int p_workerAmount, OrderedProperties p_tempConfigProperties)
    {
        FXMLLoader simulationTrackerWindowLoader = new FXMLLoader(GUIManager.class.getResource("FXMLOnGoingSimulationWindow.fxml"));
        Stage simulationStage = new Stage();
        
        try
        {
            Parent simulationWindowFxml = simulationTrackerWindowLoader.load();
            m_simulationWindowController = simulationTrackerWindowLoader.getController();
            m_simulationWindowController.initialize(p_tempConfigProperties, this, p_workerAmount);
            int longestColumn = p_workerAmount - p_workerAmount / 2;
            simulationStage.setTitle("Hitori Denshi - Simulation on-going");
            simulationStage.setScene(new Scene(simulationWindowFxml, 800, 525+longestColumn*50));
	    simulationStage.show();
        }
        catch (IOException ex)
        {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void resizeStage()
    {
        if (!m_mainStage.isMaximized())
        {
            m_mainStage.sizeToScene();
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
