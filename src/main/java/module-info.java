module ija.ija2023.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens ija.ija2023.project to javafx.fxml;
    exports ija.ija2023.project;
}
