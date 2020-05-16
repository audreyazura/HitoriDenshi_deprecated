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

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import nu.studer.java.util.OrderedProperties;

/**
 *
 * @author Alban Lafuente
 */
public class SimulationWindowController
{
    @FXML private HBox workerpbarmainbox;
    @FXML private ProgressBar mainpbar;
    @FXML private TextArea consolewindow;
    @FXML private VBox mainpbarbox;
    @FXML private VBox workerpbarsleft;
    @FXML private VBox workerpbarsright;
    
    private OrderedProperties m_tempConfigProp;
    private MainWindowCall m_mainWindow;
    private ProgressBar[] m_workerPBarArray;
    
    void initialize(OrderedProperties p_tempConfigProperties, MainWindowCall p_mainWindowCall, int p_workerAmount)
    {
        m_tempConfigProp = p_tempConfigProperties;
        m_mainWindow = p_mainWindowCall;
        
        consolewindow.appendText("Launching simulation...\n\n");
        
        mainpbar.prefWidthProperty().bind(mainpbarbox.widthProperty());
        
        int numberRightWorker = p_workerAmount / 2;
        int numberLeftWorker = p_workerAmount - numberRightWorker;
        
        m_workerPBarArray = new ProgressBar[p_workerAmount];
        workerpbarsleft.prefWidthProperty().bind(workerpbarmainbox.widthProperty().subtract(25).divide(2));
        workerpbarsright.prefWidthProperty().bind(workerpbarmainbox.widthProperty().subtract(25).divide(2));
        for(int i = 0 ; i < numberLeftWorker ; i+=1)
        {
            Label currentLabel = new Label("SimulationWorker-"+i+" progress");
            ProgressBar currentPBar = new ProgressBar(0);
            
            VBox currentVBox = new VBox(5, currentLabel, currentPBar);
            HBox.setHgrow(currentVBox, Priority.ALWAYS);
            currentVBox.setFillWidth(true);
            currentPBar.prefWidthProperty().bind(currentVBox.widthProperty());
            workerpbarsleft.getChildren().add(currentVBox);
            m_workerPBarArray[i] = currentPBar;
        }
        for(int i = 0 ; i < numberRightWorker ; i+=1)
        {
            int workerID = i + numberLeftWorker;
            Label currentLabel = new Label("SimulationWorker-"+workerID+" progress");
            ProgressBar currentPBar = new ProgressBar(0);
            
            VBox currentVBox = new VBox(5, currentLabel, currentPBar);
            HBox.setHgrow(currentVBox, Priority.ALWAYS);
            currentVBox.setFillWidth(true);
            currentPBar.prefWidthProperty().bind(currentVBox.widthProperty());
            workerpbarsright.getChildren().add(currentVBox);
            m_workerPBarArray[workerID] = currentPBar;
        }
    }
    
    void updateProgressIndividual (int p_workerID, double p_workerProgress, double p_globalProgress)
    {
        mainpbar.setProgress(p_globalProgress);
        m_workerPBarArray[p_workerID].setProgress(p_workerProgress);
    }
    
    void updateMessage (String p_message)
    {
	String currentText = p_message+"\n";
	consolewindow.appendText(currentText);
    }
    
    @FXML void callparameterswindow (ActionEvent event)
    {
        m_mainWindow.launchParametersWindow(m_tempConfigProp);
    }
    
    @FXML void closeApp (ActionEvent event)
    {
	System.exit(0);
    }
}
