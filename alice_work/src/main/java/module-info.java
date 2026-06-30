module aliceoretorno.alice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports aliceoretorno;
    opens aliceoretorno to javafx.fxml;
    opens aliceoretorno.controller to javafx.fxml;
    opens aliceoretorno.model to javafx.fxml;
    opens aliceoretorno.dao to javafx.fxml;
    opens aliceoretorno.config to javafx.fxml;
}
