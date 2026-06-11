package com.alice.controller;

import com.alice.MainApp;
import javafx.fxml.FXML;

public class CreditosController {

    @FXML
    public void onVoltar() {
        MainApp.trocarTela("menu.fxml", "Alice no País das Maravilhas");
    }
}
