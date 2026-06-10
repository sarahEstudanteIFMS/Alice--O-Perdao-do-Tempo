module aliceoretorno.alice {
    requires javafx.controls;
    requires javafx.fxml;

    opens aliceoretorno.model to javafx.fxml;
    exports aliceoretorno.model;
}
