package com.alice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.alice.util.ConnectionFactory;
import com.alice.dao.BancoDados;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        BancoDados.inicializar();
        trocarTela("menu.fxml", "Alice no País das Maravilhas");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void trocarTela(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/alice/fxml/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                MainApp.class.getResource("/com/alice/css/estilo.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
