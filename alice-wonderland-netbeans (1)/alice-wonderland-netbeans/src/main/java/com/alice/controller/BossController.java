package com.alice.controller;

import com.alice.MainApp;
import com.alice.dao.BancoDados;
import com.alice.model.Jogador;
import com.alice.model.Sessao;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * MECÂNICA DO BOSS:
 * Alice precisa esquivar dos ponteiros giratórios do "Tempo" enquanto
 * acerta os pontos fracos (números do relógio) que aparecem periodicamente.
 * Vencer = pedido de perdão aceito = final do jogo.
 */
public class BossController implements Initializable {

    @FXML private Canvas canvas;
    @FXML private Label lblVidaBoss;
    @FXML private Label lblVidaAlice;
    @FXML private Label lblStatus;

    private static final int W = 700;
    private static final int H = 450;
    private static final int CENTRO_X = W / 2;
    private static final int CENTRO_Y = H / 2;
    private static final int RAIO_RELOGIO = 120;

    private double anguloPonteiro;
    private double velPonteiro = 0.02;

    private double aliceX, aliceY;
    private boolean moveUp, moveDown, moveLeft, moveRight;

    private int vidaBoss;
    private int vidaAlice;
    private boolean fimDeJogo;
    private boolean vitoria;

    private List<double[]> alvos = new ArrayList<>(); // pontos fracos {x, y, ativo(1/0)}
    private Random rand = new Random();
    private long ultimoSpawnAlvo;

    private AnimationTimer timer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aliceX = CENTRO_X;
        aliceY = CENTRO_Y + RAIO_RELOGIO + 60;
        vidaAlice = 3;
        vidaBoss = 10;
        fimDeJogo = false;
        vitoria = false;

        // Dificuldade aumenta a velocidade do ponteiro com base no nível
        int nivel = Sessao.get().getJogador().getNivel();
        velPonteiro = 0.015 + nivel * 0.004;

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);

        atualizarHUD();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                atualizar(now);
                desenhar();
            }
        };
        timer.start();

        canvas.requestFocus();
    }

    private void atualizar(long now) {
        if (fimDeJogo || vitoria) return;

        anguloPonteiro += velPonteiro;

        double vel = 4 + Sessao.get().getJogador().getUpgradeTempo() * 0.5;
        if (moveUp    && aliceY > 0)      aliceY -= vel;
        if (moveDown  && aliceY < H - 28) aliceY += vel;
        if (moveLeft  && aliceX > 0)      aliceX -= vel;
        if (moveRight && aliceX < W - 28) aliceX += vel;

        // Posição da ponta do ponteiro
        double pontaX = CENTRO_X + Math.cos(anguloPonteiro) * RAIO_RELOGIO;
        double pontaY = CENTRO_Y + Math.sin(anguloPonteiro) * RAIO_RELOGIO;

        // Colisão Alice x ponteiro (linha simplificada por proximidade da ponta)
        if (dist(aliceX + 14, aliceY + 14, pontaX, pontaY) < 30) {
            vidaAlice--;
            // empurra Alice para longe
            aliceY = CENTRO_Y + RAIO_RELOGIO + 60;
            atualizarHUD();
            if (vidaAlice <= 0) {
                fimDeJogo = true;
                vitoria = false;
                lblStatus.setText("💀 O Tempo permanece parado para sempre...");
                return;
            }
        }

        // Spawn de alvos (pontos fracos / números do relógio)
        if (now - ultimoSpawnAlvo > 1_500_000_000L && alvos.size() < 3) {
            double ang = rand.nextDouble() * Math.PI * 2;
            double ax = CENTRO_X + Math.cos(ang) * (RAIO_RELOGIO - 30);
            double ay = CENTRO_Y + Math.sin(ang) * (RAIO_RELOGIO - 30);
            alvos.add(new double[]{ax, ay});
            ultimoSpawnAlvo = now;
        }

        // Colisão Alice x alvo
        var it = alvos.iterator();
        while (it.hasNext()) {
            double[] a = it.next();
            if (dist(aliceX + 14, aliceY + 14, a[0], a[1]) < 24) {
                it.remove();
                vidaBoss--;
                atualizarHUD();
                if (vidaBoss <= 0) {
                    fimDeJogo = true;
                    vitoria = true;
                    finalizarJogo();
                    return;
                }
            }
        }
    }

    private void finalizarJogo() {
        Jogador j = Sessao.get().getJogador();
        j.setMoedas(j.getMoedas() + 50);
        j.adicionarXp(100);
        BancoDados.salvarJogador(j);
        BancoDados.salvarPartida(j.getId(), 1000, 50);
        BancoDados.salvarItemInventario(j.getId(), "Perdão do Tempo", "Conquista");
        lblStatus.setText("✨ O Tempo voltou a correr! Alice salvou o País das Maravilhas! ✨");
    }

    private void desenhar() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.web("#0d0221"));
        gc.fillRect(0, 0, W, H);

        // Relógio gigante (corpo do Tempo)
        gc.setStroke(Color.web("#ffd700"));
        gc.setLineWidth(4);
        gc.strokeOval(CENTRO_X - RAIO_RELOGIO, CENTRO_Y - RAIO_RELOGIO, RAIO_RELOGIO * 2, RAIO_RELOGIO * 2);
        gc.setFill(Color.web("#1a0533"));
        gc.fillOval(CENTRO_X - RAIO_RELOGIO, CENTRO_Y - RAIO_RELOGIO, RAIO_RELOGIO * 2, RAIO_RELOGIO * 2);

        // Números (12, 3, 6, 9)
        gc.setFill(Color.web("#ffd700"));
        gc.setFont(Font.font("Serif", 18));
        gc.fillText("12", CENTRO_X - 8, CENTRO_Y - RAIO_RELOGIO + 20);
        gc.fillText("6",  CENTRO_X - 4, CENTRO_Y + RAIO_RELOGIO - 8);
        gc.fillText("3",  CENTRO_X + RAIO_RELOGIO - 18, CENTRO_Y + 6);
        gc.fillText("9",  CENTRO_X - RAIO_RELOGIO + 6, CENTRO_Y + 6);

        // Ponteiro
        double pontaX = CENTRO_X + Math.cos(anguloPonteiro) * RAIO_RELOGIO;
        double pontaY = CENTRO_Y + Math.sin(anguloPonteiro) * RAIO_RELOGIO;
        gc.setStroke(Color.web("#cc3333"));
        gc.setLineWidth(5);
        gc.strokeLine(CENTRO_X, CENTRO_Y, pontaX, pontaY);
        gc.setFill(Color.web("#cc3333"));
        gc.fillOval(pontaX - 8, pontaY - 8, 16, 16);

        // Centro do relógio
        gc.setFill(Color.web("#ffd700"));
        gc.fillOval(CENTRO_X - 6, CENTRO_Y - 6, 12, 12);

        // Alvos (pontos fracos dourados)
        gc.setFill(Color.web("#66ff99"));
        for (double[] a : alvos) {
            gc.fillOval(a[0] - 10, a[1] - 10, 20, 20);
        }

        // Alice
        gc.setFill(Color.web("#3399ff"));
        gc.fillOval(aliceX, aliceY, 28, 28);
        gc.setFill(Color.web("#ffcc99"));
        gc.fillOval(aliceX + 6, aliceY - 12, 16, 16);
        gc.setFill(Color.web("#ffdd00"));
        gc.fillOval(aliceX + 4, aliceY - 18, 20, 10);

        // Overlay fim de jogo
        if (fimDeJogo || vitoria) {
            gc.setFill(Color.color(0, 0, 0, 0.65));
            gc.fillRect(0, 0, W, H);
            gc.setFill(vitoria ? Color.GOLD : Color.TOMATO);
            gc.setFont(Font.font("Serif", 32));
            String msg = vitoria ? "✨ O Tempo perdoou Alice! ✨" : "💀 Alice foi derrotada";
            gc.fillText(msg, W / 2.0 - 170, H / 2.0);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Serif", 16));
            gc.fillText("Pressione ESPAÇO para voltar ao menu", W / 2.0 - 150, H / 2.0 + 40);
        }
    }

    private double dist(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case W, UP    -> moveUp    = true;
            case S, DOWN  -> moveDown  = true;
            case A, LEFT  -> moveLeft  = true;
            case D, RIGHT -> moveRight = true;
            case SPACE -> {
                if (fimDeJogo || vitoria) {
                    timer.stop();
                    MainApp.trocarTela("menu.fxml", "Alice no País das Maravilhas");
                }
            }
            default -> {}
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case W, UP    -> moveUp    = false;
            case S, DOWN  -> moveDown  = false;
            case A, LEFT  -> moveLeft  = false;
            case D, RIGHT -> moveRight = false;
            default -> {}
        }
    }

    private void atualizarHUD() {
        lblVidaBoss.setText("Tempo: " + vidaBoss + " ⏰");
        lblVidaAlice.setText("Alice: ❤ " + vidaAlice);
    }
}
