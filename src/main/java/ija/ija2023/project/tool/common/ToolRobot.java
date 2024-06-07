/**
 * @author Anton Havlovskyi
 * Rozhraní reprezentující robot, který se může pohybovat v prostředí.
 */
package ija.ija2023.project.tool.common;

public interface ToolRobot extends Observable {
    int angle();
    Position getPosition();
    void turn(int n);
}
