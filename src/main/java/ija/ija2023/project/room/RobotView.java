/**
 * @author Anton Havlovskyi
 * Controller pro řizení Model a View robota
 */
package ija.ija2023.project.room;

import ija.ija2023.project.common.Environment;
import ija.ija2023.project.common.Robot;
import ija.ija2023.project.tool.common.Position;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class RobotView {
    public Robot robot;
    public Button geometry;
    public int speed;
    public int angle;
    public boolean computerControlled;

    public RobotView(Environment env, int x, int y) {
        Position pos = new Position((y + 6) * 10, (x + 6) * 10);
        // System.out.println("X create Model: " + pos.getCol() + " Y create: " + pos.getRow());
        // System.out.println("X create View: " + (x + 6) + " Y create: " + (y + 6));
        this.robot = ControlledRobot.create(env, pos);
        this.geometry = new Button();

        SVGPath shape = new SVGPath();
        shape.setContent("M 450 300 A 50 50 0 1 1 400 300 L 400 350 L 450 350 L 450 300");
        this.geometry.setShape(shape);
        this.geometry.setTranslateX(x + 6);
        this.geometry.setTranslateY(y + 6);
        this.geometry.setPrefSize(40.0, 40.0);

        this.speed = 1;
        this.angle = 45;
        this.computerControlled = true;
    }

    public static RobotView create(Environment env, int x, int y) {
        return new RobotView(env, x, y);
    }

    public boolean move() {
        for (int i = 0; i < this.speed; i++) {
            Position oldPosition = this.robot.getPosition();
            double oldX = oldPosition.getCol()/10;
            double oldY = oldPosition.getRow()/10;
            this.geometry.setTranslateX(oldX);
            this.geometry.setTranslateY(oldY);
            if (!this.robot.move()) {
                return i > 0;
            }
            Position newPosition = this.robot.getPosition();
            double newX = this.geometry.getTranslateX() + (newPosition.getCol()/10 - oldX);
            double newY = this.geometry.getTranslateY() + (newPosition.getRow()/10 - oldY);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.05), this.geometry);
            transition.setToX(newX);
            transition.setToY(newY);
            // System.out.println("View:  X: " + newX + " Y: " + newY);
            // System.out.println("Model: X: " + newPosition.getCol() + " Y: " + newPosition.getRow());
            transition.play();
        }
        return true;
    }

    public void turn(int turnAngle) {
        this.geometry.setRotate((double) this.robot.angle());
        double angle = this.robot.angle() + turnAngle;
        this.robot.turn(turnAngle);

        RotateTransition transition = new RotateTransition(Duration.seconds(0.2), this.geometry);
        transition.setToAngle(angle);
        // System.out.println("Angle: " + angle);
        transition.play();
    }
}
