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
package hitoridenshicigs_GUI;

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
public class HitoriDenshiCIGS_GUI extends Application implements MainWindowCall
{
    private Stage m_mainStage;
    
    /**
     * Elements to include in the GUI:
     *  - Field to enter the list of bias voltage
     *  - Field to enter the list of notch position
     *  - Field to enter the list of starting positions
     *  - Field to select the folder with the files in (tell the name formalism)
     *  - Field for the number of particle
     *  - Switch electron/holes
     *  - Switch to select if the position 0 is at the front or back
     *  - Field to enter the size of the buffer and window
     *  - Field to enter the size of tExtractVideos_GUIhe absorber
     *  - Field to enter the number of particle to simulate at each iteration
     */
    
    /**
     * @param args the command line arguments
     */
    public void startHitoriGUI(String[] args) 
    {
        Font.loadFont(HitoriDenshiCIGS_GUI.class.getResource("SourceSansPro-Regular.ttf").toExternalForm(), 10);
        launch(args);
    }
    
    @Override
    public void start(Stage stage)
    {
        m_mainStage = stage;
        launchParametersWindow();
    }
    
    @Override
    public Stage getMainStage()
    {
        return m_mainStage;
    }
    
    private void launchParametersWindow()
    {
        
        FXMLLoader parameterWindowLoader = new FXMLLoader(HitoriDenshiCIGS_GUI.class.getResource("FXMLParametersWindow.fxml"));
        
        try
        {
            Parent windowFxml = parameterWindowLoader.load();
	    FXMLParametersWindowController controller = parameterWindowLoader.getController();
	    controller.setMainWindow(this);
            m_mainStage.setScene(new Scene(windowFxml, 800, 800));
	    m_mainStage.show();
        }
        catch (IOException ex)
        {
            Logger.getLogger(HitoriDenshiCIGS_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        HitoriDenshiCIGS_Simulation simu = new HitoriDenshiCIGS_Simulation();
//        simu.startSimulation(folder, conditions);
    }
    
}
