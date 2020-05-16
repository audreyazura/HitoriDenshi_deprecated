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
package hitoridenshi.executionmanager;

import hitoridenshi.guimanager.GUIManager;
import net.opentsdb.tools.ArgP;

/**
 *
 * @author Alban Lafuente
 */
public class ExecutionManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        final ArgP argParser = new ArgP();
        argParser.addOption("--console-mode", "To execute in console, without loading the GUI.");
        argParser.addOption("--file", "To give a properties file to load. As priority over -f.");
        argParser.addOption("--help", "This help.");
        argParser.addOption("-c", "eq. to --console-mode");
        argParser.addOption("-f", "eq. to --file");
        
        //for test, to be replaced by just the args coming as parameters
        String[] arguments = {"-f=ConfigurationFiles/default.conf"};
        try
	{
	    args = argParser.parse(arguments);
	}
	catch (IllegalArgumentException e)
	{
	    System.err.println(e.getMessage());
	    System.err.print(argParser.usage());
	    System.exit(1);
	}
        
        if (argParser.has("--help"))
        {
            //just print help message, not continuing execution
            System.out.println(argParser.usage());
        }
        else
        {
            String[] curratedArgument = new String[0];
            if (argParser.has("--file"))
            {
                curratedArgument[0] = argParser.get("--file");
            }
            else if (argParser.has("-f"))
            {
                curratedArgument[0] = argParser.get("-file");
            }
            else
            {
                curratedArgument[0] = "ConfigurationFiles/default.conf";
            }

            OutputInterface appToLaunch;
            if (argParser.has("--console-mode") || argParser.has("-c"))
            {
                //appToLaunch start the console manager
            }
            else
            {
                appToLaunch = new GUIManager();
            }
            appToLaunch.startGUI(arguments);
        }
    }
    
}
