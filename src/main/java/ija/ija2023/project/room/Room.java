/**
 * @author Anton Havlovskyi
 * Třída reprezentující konrétní prostředí (místnost).
 */
package ija.ija2023.project.room;

import ija.ija2023.project.tool.common.Position;
import ija.ija2023.project.tool.common.ToolRobot;

import java.util.ArrayList;
import java.util.List;

import ija.ija2023.project.common.Environment;
import ija.ija2023.project.common.Obstacle;
import ija.ija2023.project.common.Robot;

public class Room implements Environment {
    public int rows;
    public int cols;
    public List<ToolRobot> robots;
    public List<Obstacle> obstacles;

    public Room(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.robots = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }

    public static Room create(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("A value cannot be less or equal to zero!");
        }

        return new Room(rows, cols);
    }

    @Override
    public boolean addRobot(Robot robot) {
        Position robotPos = robot.getPosition();
        if (!containsPosition(robotPos) || this.robotAt(robotPos) || this.obstacleAt(robotPos)) {
            return false;
        }

        this.robots.add(robot);
        return true;
    }

    @Override
    public void removeRobot(Robot robot) {
        this.robots.remove(robot);
    }

    @Override
    public boolean containsPosition(Position pos) {
        if (pos.getCol() > this.cols || pos.getRow() > this.rows || pos.getCol() < 0 || pos.getRow() < 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean createObstacleAt(int row, int col) {
        Position obstaclePos = new Position(row, col);
        if (!containsPosition(obstaclePos) || this.robotAt(obstaclePos) || this.obstacleAt(obstaclePos)) {
            return false;
        }

        this.obstacles.add(new Obstacle(this, obstaclePos));
        return true;
    }

    @Override
    public boolean removeObstacleAt(int row, int col) {
        Position obstaclePos = new Position(row, col);
        for (Obstacle obstacle : this.obstacles) {
            if (obstaclePos.equals(obstacle.getPosition())) {
                this.obstacles.remove(obstacle);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean obstacleAt(int row, int col) {
        Position pos = new Position(row, col);
        return this.obstacleAt(pos);
    }

    @Override
    public boolean obstacleAt(Position p) {
        for (Obstacle obstacle : this.obstacles) {
            if (p.equals(obstacle.getPosition())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean robotAt(Position p) {
        for (ToolRobot robot : this.robots) {
            if (p.equals(robot.getPosition())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int rows() {
        return this.rows;
    }

    @Override
    public int cols() {
        return this.cols;
    }

    @Override
    public List<ToolRobot> robots() {
        return new ArrayList<>(this.robots);
    }
}
