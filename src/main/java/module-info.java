module com.teris.tetris {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tetris to javafx.fxml;
    exports com.tetris;
}