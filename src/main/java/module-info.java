module me.light.chess {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.light.chess to javafx.fxml;
    exports me.light.chess;
}