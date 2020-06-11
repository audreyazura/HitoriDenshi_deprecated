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
package hitoridenshi.simulationmanager;

/**
 * An interface given for implementation of class that manages the notification of the simulation manager to the user
 * @author audreyazura
 */
public interface ProgressNotifierInterface
{
    /**
     * A function to notify the progress of a worker in the simulation
     * @param p_workerID the ID of the worker who sent the update
     * @param p_workerProgress the progress in regard to the worker total task
     * @param p_globalProgress the progress in regard to the total simulation
     */
    public void updateProgress (int p_workerID, double p_workerProgress, double p_globalProgress);
    
    /**
     * A function to print a message on the dedicated terminal
     * @param p_message the message to be printed
     */
    public void sendMessage (String p_message);
}
