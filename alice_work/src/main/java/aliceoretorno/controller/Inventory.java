package aliceoretorno.controller;

import aliceoretorno.model.Partida;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Inventory {

    @FXML private Label lblQtdItens;
    @FXML private Button btnConsumir;
    
    private Gameplay gpleController;

    public void setGameplayController(Gameplay ctrl) {
        this.gpleController = ctrl;
        atualizarTela();
    }

    private void atualizarTela() {
        int qtd = Partida.runAtual.getTempoExtraItens();
        lblQtdItens.setText("Tempo Extra (+15s): " + qtd + " unidades");
        btnConsumir.setDisable(qtd <= 0);
    }

    @FXML
    void handleUsarItem(ActionEvent event) {
        Partida run = Partida.runAtual;
        if (run.getTempoExtraItens() > 0) {
            run.setTempoExtraItens(run.getTempoExtraItens() - 1);
            if (gpleController != null) {
                gpleController.adicionarTempoItem(15);
            }
            atualizarTela();
        }
    }

    @FXML
    void handleFechar(ActionEvent event) {
        Stage stage = (Stage) lblQtdItens.getScene().getWindow();
        stage.close();
    }
}