/**
 * @author Anton Havlovskyi
 * Rozhraní reprezentující prostředí, v které se pohybují roboti.
 */
package ija.ija2023.project.tool.common;

import java.util.List;

public interface ToolEnvironment {
    int cols();
    int rows();
    boolean obstacleAt(Position p);
    List<ToolRobot> robots();
}
