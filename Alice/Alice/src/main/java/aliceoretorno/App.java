
package aliceoretorno;

/**
 *
 * @author sarah
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Certifique-se de que a linha abaixo crie a variável 'Parent root'
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        
        // Agora o 'root' existe e o erro some:
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Alice - O Perdão do Tempo");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}