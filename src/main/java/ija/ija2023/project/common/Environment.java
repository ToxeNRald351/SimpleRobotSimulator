/**
 * @author Anton Havlovskyi
 * Rozhraní reprezentující prostředí, v které se pohybují roboti.
 */

package ija.ija2023.project.common;

import ija.ija2023.project.tool.common.Position;
import ija.ija2023.project.tool.common.ToolEnvironment;

public interface Environment extends ToolEnvironment {
    boolean addRobot(Robot robot);
    void removeRobot(Robot robot);
    boolean containsPosition(Position nextMove);
    boolean createObstacleAt(int row, int col);
    boolean removeObstacleAt(int row, int col);
    boolean obstacleAt(int row, int col);
    boolean robotAt(Position nextMove);
}
