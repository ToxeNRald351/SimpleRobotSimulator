/**
 * @author Anton Havlovskyi
 * Proces řízení robotů počítačem
 */
package ija.ija2023.project;

import java.util.List;

import ija.ija2023.project.room.RobotView;

public class robotBrain implements Runnable {
    public List<RobotView> allRobots;
    public boolean runFlag;
    private final Object lock = new Object();
    private boolean isRunning = false;

    public robotBrain(List<RobotView> allRobots) {
        this.allRobots = allRobots;
        this.runFlag = true;
    }

    @Override
    public void run() {
        {
            synchronized (lock) {
                if (isRunning) {
                    // Another thread is already running, so exit
                    return;
                }
                // Set the flag to indicate that this thread is running
                isRunning = true;
            }
    
            try {
                // Your thread logic goes here
                while (runFlag) {
                    for (int i = 0; i < this.allRobots.size(); i++) {
                        RobotView robot = this.allRobots.get(i);
                        if (robot.computerControlled) {
                            if (!robot.move()) {
                                try {
                                    // Pause the thread for 1 second (1000 milliseconds)
                                    Thread.sleep(25);
                                } catch (InterruptedException e) {
                                    // Handle InterruptedException if necessary
                                    this.runFlag = false;
                                }
                                robot.turn(robot.angle);
                            }
                        }
                    }
                    try {
                        // Pause the thread for 1 second (1000 milliseconds)
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // Handle InterruptedException if necessary
                        this.runFlag = false;
                    }
                }
            } finally {
                // Reset the flag when the thread finishes
                synchronized (lock) {
                    isRunning = false;
                }
            }
        }
    }

    public boolean isRunning() {
        // Check if the thread is still running
        return isRunning;
    }
}
