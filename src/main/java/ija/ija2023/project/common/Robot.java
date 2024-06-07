/**
 * @author Anton Havlovskyi
 * Rozhraní reprezentující robot, který se může pohybovat v prostředí.
 */
package ija.ija2023.project.common;

import ija.ija2023.project.tool.common.ToolRobot;

public interface Robot extends ToolRobot {
    boolean canMove();
    boolean move();
    void turn();
}
