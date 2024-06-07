/**
 * @author Anton Havlovskyi
 * Ovladač pole robotů a překažek
 */
package ija.ija2023.project;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ija.ija2023.project.common.Environment;
import ija.ija2023.project.room.RobotView;
import ija.ija2023.project.room.Room;
import ija.ija2023.project.tool.common.Position;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class fieldController {
    @FXML
    private GridPane chessBoard;

    @FXML
    private TextField widthTextField;

    @FXML
    private TextField heightTextField;

    @FXML
    private Rectangle robotButtonRec;

    @FXML
    private Rectangle obstacleButtonRec;

    private int boardWidth;
    private int boardHeight;

    private RobotView selectedRobot;
    private List<RobotView> allRobots;
    private List<Position> allObstacles;
    private Environment room;

    private boolean placeRobot;
    private boolean placeObstacle;

    private robotBrain brain;
    private Thread thread;

    public void initialize() {
        // Initialize the chess board with default size
        this.boardWidth = 10;
        this.boardHeight = 10;
        createBoard(this.boardWidth, this.boardHeight); // Default size
        this.placeRobot = false;
        this.placeObstacle = false;

        this.allRobots = new ArrayList<>();
        this.allObstacles = new ArrayList<>();
        this.brain = new robotBrain(this.allRobots);
    }

    @FXML
    private void saveToFile(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        // Set initial directory (optional)
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        // Add extension filters
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Roomba Simulator Save Files", "*.save"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        // Dump status into string
        String saveString = "Field Dimensions (Width, Height)\n" + 
                            this.boardWidth + 
                            "\n" + 
                            this.boardHeight + 
                            "\n" +
                            "Robot Info (PosX, PosY, speed, turnAngle, lookAngle)\n";

        for (RobotView robot : this.allRobots) {
            Position p = robot.robot.getPosition();
            saveString +=   "=\n" + 
                            (p.getCol()/10 - 6) + 
                            "\n" + 
                            (p.getRow()/10 - 6) + 
                            "\n" + 
                            robot.speed + 
                            "\n" + 
                            robot.angle + 
                            "\n" + 
                            robot.robot.angle() + 
                            "\n";
        }

        saveString += "-\nObstacle Info (PosX, PosY)\n";
        for (Position p : this.allObstacles) {
            saveString +=   "=\n" + 
                            p.getCol() + 
                            "\n" + 
                            p.getRow() + 
                            "\n";
        }
        saveString += "-\n";

        if (file != null) {
            saveTextToFile(saveString, file);
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadFromFile(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load File");
        
        // Set initial directory (optional)
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        // Add file filters (optional)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Roomba Simulator Save Files", "*.save"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))) {
                String line;
                int state = 0;
                while ((line = br.readLine()) != null) {
                    switch (state) {
                        case 0:
                            // Skip info line
                            // System.out.println(state);
                            state += 1;
                            break;
                        
                        case 1:
                            // Read width
                            // System.out.println(state);
                            int width;
                            try {
                                width = Integer.parseInt(line);
                            } catch (java.lang.NumberFormatException e) {
                                width = 10;
                            }
                            this.boardWidth = width;
                            state += 1;
                            break;
                    
                        case 2:
                            // Read height
                            // System.out.println(state);
                            int height;
                            try {
                                height = Integer.parseInt(line);
                            } catch (java.lang.NumberFormatException e) {
                                height = 10;
                            }
                            this.boardHeight = height;
                            state += 1;
                            break;
                        
                        case 3:
                            // Skip info line
                            // Build scene
                            // System.out.println(state);
                            this.createBoard(this.boardWidth, this.boardHeight);
                            this.placeRobot = false;    // 87c5ff // 0084ff
                            this.placeObstacle = false;

                            this.allRobots = new ArrayList<>();
                            this.brain = new robotBrain(this.allRobots);
                            state += 1;
                            break;
                        
                        case 4:
                            // Read robot info
                            // System.out.println(state);
                            int infoLine = 0;
                            boolean f = false;
                            int robotX = 0;
                            int robotY = 0;
                            int robotSpeed = 1;
                            int robotTurnAngle = 45;
                            int robotLookAngle = 0;
                            RobotView robot = null;
                            while (!f && (line = br.readLine()) != null) {
                                switch (infoLine) {
                                    case 0:
                                        // PosX
                                        try {
                                            robotX = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            Random random = new Random();
                                            robotX = random.nextInt((this.boardWidth * 52 - 25) - 25 + 1) + 25;
                                        }
                                        robotX = robotX > this.boardWidth * 52 ? this.boardWidth * 52 - 25 : robotX;
                                        // System.out.println("RobotX: " + robotX);
                                        infoLine+= 1;
                                        break;
                                    case 1:
                                        // PosY
                                        try {
                                            robotY = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            Random random = new Random();
                                            robotY = random.nextInt((this.boardHeight * 52 - 25) - 25 + 1) + 25;
                                        }
                                        robotY = robotY > this.boardWidth * 52 ? this.boardWidth * 52 - 25 : robotY;
                                        // System.out.println("RobotY: " + robotY);
                                        robot = this.spawnRobot(robotX, robotY);
                                        infoLine+= 1;
                                        break;
                                    case 2:
                                        // speed
                                        try {
                                            robotSpeed = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            robotSpeed = 1;
                                        }
                                        robot.speed = robotSpeed > 10 ? 10 : robotSpeed;
                                        infoLine+= 1;
                                        break;
                                    case 3:
                                        // turnAngle
                                        try {
                                            robotTurnAngle = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            robotTurnAngle = 45;
                                        }
                                        robot.angle = (robotTurnAngle + 360) % 360;
                                        infoLine+= 1;
                                        break;
                                    case 4:
                                        // lookAngle
                                        try {
                                            robotLookAngle = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            robotLookAngle = 0;
                                        }
                                        robotLookAngle = (robotLookAngle + 360) % 360;
                                        robot.turn(robotLookAngle);
                                        infoLine+= 1;
                                        break;
                                    case 5:
                                        if (line.startsWith("-")) {
                                            f = true;
                                        }
                                        infoLine = 0;
                                        break;
                                
                                    default:
                                        break;
                                }
                            }
                            state += 1;
                            break;
                        
                        case 5:
                            // Skip info line
                            // System.out.println(state);
                            this.allObstacles = new ArrayList<>();
                            state += 1;
                            break;
                        
                        case 6:
                            // Read obstacle info
                            // System.out.println(state);
                            int infoL = 0;
                            boolean t = false;
                            int obstX = 0;
                            int obstY = 0;
                            while (!t && (line = br.readLine()) != null) {
                                switch (infoL) {
                                    case 0:
                                        // PosX
                                        try {
                                            obstX = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            Random random = new Random();
                                            obstX = random.nextInt((this.boardWidth * 52 - 25) - 25 + 1) + 25;
                                        }
                                        obstX = obstX > this.boardWidth * 52 ? this.boardWidth * 52 - 25 : obstX;
                                        infoL+= 1;
                                        break;
                                    case 1:
                                        // PosY
                                        try {
                                            obstY = Integer.parseInt(line);
                                        } catch (java.lang.NumberFormatException e) {
                                            Random random = new Random();
                                            obstY = random.nextInt((this.boardHeight * 52 - 25) - 25 + 1) + 25;
                                        }
                                        obstY = obstY > this.boardWidth * 52 ? this.boardWidth * 52 - 25 : obstY;
                                        this.spawnObstacle(obstX, obstY);
                                        infoL+= 1;
                                        break;
                                    case 2:
                                        if (line.startsWith("-")) {
                                            t = true;
                                        }
                                        infoL = 0;
                                        break;
                                
                                    default:
                                        break;
                                }
                            }
                            state += 1;
                            break;

                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void setPlaceRobot() {
        this.placeRobot = true;
        this.robotButtonRec.setFill(Color.web("#0084ff"));
        this.placeObstacle = false;
        this.obstacleButtonRec.setFill(Color.web("#87c5ff"));
    }

    @FXML
    private void setPlaceObstacle() {
        this.placeRobot = false;
        this.robotButtonRec.setFill(Color.web("#87c5ff"));
        this.placeObstacle = true;
        this.obstacleButtonRec.setFill(Color.web("#0084ff"));
    }

    @FXML
    private void runSimulation() {
        if (this.brain.isRunning()) {
            return;
        }
        
        this.brain.runFlag = true;
        this.thread = new Thread(brain);
        this.thread.start();
    }

    @FXML
    private void stopSimulation() {
        this.thread.interrupt();
    }

    @FXML
    private void handleResize() {
        int width;
        try {
            width = Integer.parseInt(widthTextField.getText());
        } catch (java.lang.NumberFormatException e) {
            width = this.boardWidth;
        }
        int height;
        try {
            height = Integer.parseInt(heightTextField.getText());
        } catch (java.lang.NumberFormatException e) {
            height = this.boardHeight;
        }
        this.boardWidth = width > 20 ? 20 : width;
        this.boardHeight = height > 20 ? 20 : height;
        createBoard(this.boardWidth, this.boardHeight);
    }

    public void createBoard(int width, int height) {
        // Clear previous chess board
        // View
        chessBoard.getChildren().clear(); 
        AnchorPane ap = (AnchorPane) chessBoard.getParent();
        ap.getChildren().clear();

        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));
        ap.getChildren().add(chessBoard);
        // Model
        Environment room = Room.create(width * 52 * 10, height * 52 * 10);
        this.room = room;
        this.allRobots = new ArrayList<>();
        this.allObstacles = new ArrayList<>();
        if (this.thread != null && this.brain.isRunning() && this.thread.isAlive()) {
            this.thread.interrupt();
        }

        this.brain = new robotBrain(this.allRobots);

        // Create squares dynamically based on width and height
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Rectangle square = new Rectangle(50, 50); // Square size: 50x50
                square.setFill(javafx.scene.paint.Color.WHITESMOKE);
                square.setStyle("-fx-stroke: rgb(125, 125, 125); -fx-stroke-width: 2px;");
                // Add event handler for mouse click
                square.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (this.placeRobot) {
                            spawnRobot((int) square.getBoundsInParent().getMinX(), (int) square.getBoundsInParent().getMinY());
                        }
                        else if (this.placeObstacle) {
                            spawnObstacle((int) (square.getBoundsInParent().getMinX() + 6), (int) (square.getBoundsInParent().getMinY() + 6));
                        }
                    }
                    else {
                        this.placeRobot = false;
                        this.robotButtonRec.setFill(Color.web("#87c5ff"));
                        this.placeObstacle = false;
                        this.obstacleButtonRec.setFill(Color.web("#87c5ff"));

                        // Reset selected robot
                        if (selectedRobot != null) {
                            selectedRobot.geometry.setStyle("-fx-background-color: blue;"); //setFill(Color.BLUE);
                            selectedRobot.computerControlled = true;
                            selectedRobot = null;
                        }
                    }
                });
                chessBoard.add(square, col, row);
            }
        }

        chessBoard.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
    }

    private void spawnObstacle(int squareX, int squareY) {
        this.allObstacles.add(new Position(squareY, squareX));

        int modelX = squareX * 10;
        int modelY = squareY * 10;
    
        final int width = 150;
        room.createObstacleAt(modelY, modelX);
        room.createObstacleAt(modelY + width, modelX);
        room.createObstacleAt(modelY, modelX + width);
        room.createObstacleAt(modelY - width, modelX);
        room.createObstacleAt(modelY, modelX - width);
        room.createObstacleAt(modelY + width, modelX + width);
        room.createObstacleAt(modelY - width, modelX + width);
        room.createObstacleAt(modelY + width, modelX - width);
        room.createObstacleAt(modelY - width, modelX - width);
        Button geometry = new Button();

        SVGPath shape = new SVGPath();
        shape.setContent("M 350 300 L 350 250 L 400 200 L 450 250 L 450 300 L 350 300 ");
        geometry.setShape(shape);
        geometry.setStyle("-fx-background-color: rgb(255, 0, 0);");
        geometry.setTranslateX(squareX);
        geometry.setTranslateY(squareY);
        geometry.setPrefSize(40.0, 40.0);

        // Add mouse handler
        geometry.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                int mX = (int) (geometry.getTranslateX() * 10);
                int mY = (int) (geometry.getTranslateY() * 10);
                // Remove from the list
                this.allObstacles.remove(new Position(mY, mX));
                // Remove from Model
                room.removeObstacleAt(mY, mX);
                room.removeObstacleAt(mY + width, mX);
                room.removeObstacleAt(mY, mX + width);
                room.removeObstacleAt(mY - width, mX);
                room.removeObstacleAt(mY, mX - width);
                room.removeObstacleAt(mY + width, mX + width);
                room.removeObstacleAt(mY - width, mX + width);
                room.removeObstacleAt(mY + width, mX - width);
                room.removeObstacleAt(mY - width, mX - width);
                // Remove from View
                ((AnchorPane) geometry.getParent()).getChildren().remove(geometry);
            }
        });

        // Add to the chess board
        ((AnchorPane) chessBoard.getParent()).getChildren().add(geometry);
    }

    private RobotView spawnRobot(int squareX, int squareY) {
        // // System.out.println(squareX);
        // // System.out.println(squareY);
        RobotView robot = RobotView.create(this.room, squareX, squareY);
        robot.geometry.setStyle("-fx-background-color: blue;");

        ContextMenu robotContextMenu = attachContextMenu(robot);
    
        // Add event handler for circle click
        robot.geometry.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                robotContextMenu.show(robot.geometry, event.getScreenX(), event.getScreenY());
            }
            else {
                // Remove existing selected circle
                if (selectedRobot != null) {
                    selectedRobot.geometry.setStyle("-fx-background-color: blue;"); //setFill(Color.BLUE);
                    selectedRobot.computerControlled = true;
                }
                selectedRobot = robot;
                selectedRobot.geometry.setStyle("-fx-background-color: green;");
                selectedRobot.computerControlled = false;
                // Add event handler for keyboard arrow keys
                chessBoard.requestFocus(); // Set focus to chessBoard
            }
        });
    
        // Add event handler for keyboard arrow keys to chessBoard
        chessBoard.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                    selectedRobot.move();
                    break;
                case LEFT:
                    selectedRobot.turn(-3);
                    break;
                case RIGHT:
                    selectedRobot.turn(3);
                    break;
                default:
                    break;
            }
        });
    
        // Add robot to the list
        this.allRobots.add(robot);
        // Add robot to the chess board
        ((AnchorPane) chessBoard.getParent()).getChildren().add(robot.geometry);
        
        return robot;
    }

    private ContextMenu attachContextMenu(RobotView robot) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        contextMenu.getItems().addAll(editItem, deleteItem);

        editItem.setOnAction(event -> {
            // Create a layout for the context menu
            VBox editPane = new VBox();
            HBox buttonBox = new HBox();

            // Create text fields
            TextField speedField = new TextField();
            speedField.setPromptText("Speed");
            TextField angleField = new TextField();
            angleField.setPromptText("Angle");

            // Create text above fields
            Text speedText = new Text("Current speed:\t" + robot.speed);
            Text angleText = new Text("Current turn angle:\t" + robot.angle);

            // Create buttons
            Button okButton = new Button("OK");
            Button cancelButton = new Button("Cancel");

            // Add functionality to the buttons
            okButton.setOnAction(okEvent -> {
                // Handle OK button action
                // You can access speedField.getText() and angleField.getText() here
                int speed;
                try {
                    speed = Integer.parseInt(speedField.getText());
                } catch (java.lang.NumberFormatException e) {
                    speed = robot.speed;
                }
                int angle;
                try {
                    angle = Integer.parseInt(angleField.getText());
                } catch (java.lang.NumberFormatException e) {
                    angle = robot.angle;
                }

                robot.speed = speed > 10 ? 10 : speed;
                robot.angle = (angle + 360) % 360;

                ((AnchorPane) editPane.getParent()).getChildren().remove(editPane);
            });

            cancelButton.setOnAction(cancelEvent -> {
                ((AnchorPane) editPane.getParent()).getChildren().remove(editPane);
            });
            
            buttonBox.getChildren().addAll(okButton, cancelButton);
            editPane.getChildren().addAll(speedText, speedField, angleText, angleField, buttonBox);
            contextMenu.hide(); // Hide the existing context menu
            contextMenu.setUserData(editPane); // Store the layout in the context menu's user data

            // Show the custom context menu
            editPane.setTranslateX(robot.geometry.getTranslateX() + 20);
            editPane.setTranslateY(robot.geometry.getTranslateY() + 20);
            editPane.setStyle("-fx-background-color: lightblue;");
            ((AnchorPane) robot.geometry.getParent()).getChildren().add(editPane);
        });

        deleteItem.setOnAction(event -> {
            // Remove the robot from the Model
            this.room.removeRobot(robot.robot);
            // Remove the robot from the scene
            ((AnchorPane) robot.geometry.getParent()).getChildren().remove(robot.geometry);
            // Remove the robot from the list
            this.allRobots.remove(robot);
        });

        return contextMenu;
    }
}
