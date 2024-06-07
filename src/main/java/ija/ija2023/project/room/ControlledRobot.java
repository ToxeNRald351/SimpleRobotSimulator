/**
 * @author Anton Havlovskyi
 * Třída reprezentující řízeného robota.
 */
package ija.ija2023.project.room;

import ija.ija2023.project.tool.common.AbstractObservableRobot;
import ija.ija2023.project.tool.common.Position;
import ija.ija2023.project.common.Environment;
import ija.ija2023.project.common.Robot;

public class ControlledRobot extends AbstractObservableRobot implements Robot {
    public Environment env;
    public Position pos;
    public double speed;
    public Position targetPos;
    public Position availablePos;
    public int angleValue;

    public ControlledRobot(Environment env, Position pos) {
        this.env = env;
        this.pos = pos;
        this.speed = 0.2;
        this.targetPos = calcTargetPos(pos, 0, this.speed);
        this.availablePos = pos;
        this.angleValue = 0;

        this.notifyObservers();
    }

    public static ControlledRobot create(Environment env, Position pos) {
        if (env == null || pos == null) {
            return null;
        }
        
        ControlledRobot newRobot = new ControlledRobot(env, pos);
        if (env.addRobot(newRobot)) {
            return newRobot;
        }
        return null;
    }

    private Position calcTargetPos(Position startPos, int angle, double distance) {
        double radians = Math.toRadians((double) (angle - 90));
        
        // Calculate the next row and column based on the starting position and direction
        // Round to precision of 0.1
        int targetRow = startPos.getRow() + (int) (Math.round(Math.sin(radians) * 100) * distance);
        int targetCol = startPos.getCol() + (int) (Math.round(Math.cos(radians) * 100) * distance);
    
        return new Position(targetRow, targetCol);
    }

    @Override
    public int angle() {
        return this.angleValue;
    }

    @Override
    public boolean canMove() {
        Position pointA = calcTargetPos(this.pos, this.angleValue, 3);

        int radius = 240;
        for(int y = -radius; y <= radius; y++) {
            for(int x = -radius; x <= radius; x++) {
                if((x * x + y * y) <= (radius * radius + radius)) {
                    Position p = new Position(pointA.getRow() + y, pointA.getCol() + x);
                    if (!this.env.containsPosition(p)) {
                        return false;
                    }
                    if (this.env.obstacleAt(p) || this.env.robotAt(p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Position getPosition() {
        return this.pos;
    }

    @Override
    public boolean move() {
        if (this.canMove()) {
            this.pos = new Position(this.targetPos.getRow(), this.targetPos.getCol());
            this.targetPos = calcTargetPos(this.pos, this.angleValue, this.speed);
            this.notifyObservers();
            return true;
        }
        return false;
    }

    @Override
    public void turn() {
        this.angleValue += 45;

        if (this.angleValue > 359) {
            this.angleValue = 0;
        }
        this.targetPos = calcTargetPos(this.pos, this.angleValue, this.speed);
        this.notifyObservers();
    }

    @Override
    public void turn(int angle) {
        this.angleValue += angle;

        this.angleValue = (this.angleValue + 360) % 360;

        this.targetPos = calcTargetPos(this.pos, this.angleValue, this.speed);
        this.notifyObservers();
    }
}
