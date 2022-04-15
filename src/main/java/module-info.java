module roshambo.roshambo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens roshambo.roshambo to javafx.fxml;
    exports roshambo.roshambo;
}