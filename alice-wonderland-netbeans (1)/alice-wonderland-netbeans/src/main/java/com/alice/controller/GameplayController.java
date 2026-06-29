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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

/**
 * MECÂNICA PRINCIPAL:
 * Alice se move em um plano 2D coletando xícaras de chá (pontos/moedas).
 * Relógios loucos se movem pelo cenário. Se Alice colidir com um relógio, perde vida.
 * Ao coletar xícaras suficientes, acumula "perdão" para o Tempo.
 * Cada run é salva no banco. Dificuldade aumenta a cada nível.
 */
public class GameplayController implements Initializable {

    // ── FXML ─────────────────────────────────────────────────────────────────────
    @FXML private Canvas canvas;
    @FXML private Label lblVida;
    @FXML private Label lblPontos;
    @FXML private Label lblMoedas;
    @FXML private Label lblNivel;
    @FXML private Label lblStatus;
    @FXML private Pane painelCanvas;

    // ── Constantes ────────────────────────────────────────────────────────────────
    private static final int W = 700;
    private static final int H = 450;
    private static final int ALICE_SIZE = 28;
    private static final int XICARA_SIZE = 20;
    private static final int RELOGIO_SIZE = 26;
    private static final int ALICE_VEL = 4;

    // ── Estado do jogo ───────────────────────────────────────────────────────────
    private double aliceX, aliceY;
    private int vida;
    private boolean fimDeJogo;
    private boolean vitoria;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private List<double[]> xicalas = new ArrayList<>();   // {x, y}
    private List<double[]> relogios = new ArrayList<>();  // {x, y, vx, vy}

    private int pontosRun;
    private int moedasRun;
    private int metaXicalas; // xícaras para vencer a run

    private AnimationTimer timer;
    private Random rand = new Random();

    // ── Inicialização ─────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Sessao.get().iniciarRun();
        iniciarRun();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                atualizar();
                desenhar();
            }
        };
        timer.start();

        // Garante foco no canvas para receber teclas
        canvas.requestFocus();
        painelCanvas.setOnMouseClicked(e -> canvas.requestFocus());
    }

    private void iniciarRun() {
        aliceX = W / 2.0;
        aliceY = H / 2.0;
        vida = 3;
        fimDeJogo = false;
        vitoria = false;
        pontosRun = 0;
        moedasRun = 0;

        Jogador j = Sessao.get().getJogador();
        int nivel = j.getNivel();
        metaXicalas = 5 + nivel * 2; // mais xícaras a cada nível

        xicalas.clear();
        relogios.clear();

        // Spawna xícaras aleatórias
        for (int i = 0; i < metaXicalas; i++) {
            spawnXicara();
        }

        // Spawna relógios (quantidade cresce com nível)
        int nRelogios = 2 + nivel;
        for (int i = 0; i < nRelogios; i++) {
            spawnRelogio();
        }

        atualizarHUD();
    }

    // ── Loop principal ───────────────────────────────────────────────────────────

    private void atualizar() {
        if (fimDeJogo || vitoria) return;

        // Mover Alice
        double vel = ALICE_VEL + Sessao.get().getJogador().getUpgradeTempo() * 0.5;
        if (moveUp    && aliceY > 0)    aliceY -= vel;
        if (moveDown  && aliceY < H - ALICE_SIZE) aliceY += vel;
        if (moveLeft  && aliceX > 0)    aliceX -= vel;
        if (moveRight && aliceX < W - ALICE_SIZE) aliceX += vel;

        // Mover relógios
        for (double[] r : relogios) {
            r[0] += r[2];
            r[1] += r[3];
            if (r[0] < 0 || r[0] > W - RELOGIO_SIZE) r[2] *= -1;
            if (r[1] < 0 || r[1] > H - RELOGIO_SIZE) r[3] *= -1;
        }

        // Colisão Alice + xícara
        Iterator<double[]> itX = xicalas.iterator();
        while (itX.hasNext()) {
            double[] x = itX.next();
            if (colide(aliceX, aliceY, ALICE_SIZE, x[0], x[1], XICARA_SIZE)) {
                itX.remove();
                pontosRun += 10 + Sessao.get().getJogador().getUpgradeDano() * 2;
                moedasRun += 1 + Sessao.get().getJogador().getUpgradeSorte();
                Sessao.get().adicionarItem("Xícara de Chá", "Colecionável");
                atualizarHUD();
            }
        }

        // Colisão Alice + relógio
        for (double[] r : relogios) {
            if (colide(aliceX, aliceY, ALICE_SIZE, r[0], r[1], RELOGIO_SIZE)) {
                vida--;
                // Afasta Alice do relógio para não perder vida várias vezes
                aliceX = W / 2.0;
                aliceY = H / 2.0;
                atualizarHUD();
                if (vida <= 0) {
                    encerrarRun(false);
                    return;
                }
            }
        }

        // Vitória: todas as xícaras coletadas
        if (xicalas.isEmpty()) {
            encerrarRun(true);
        }
    }

    private void encerrarRun(boolean venceu) {
        fimDeJogo = true;
        vitoria = venceu;

        Jogador j = Sessao.get().getJogador();
        j.setMoedas(j.getMoedas() + moedasRun);
        j.adicionarXp(pontosRun / 10);

        Sessao.get().setPontosRun(pontosRun);
        Sessao.get().setMoedasRun(moedasRun);

        BancoDados.salvarPartida(j.getId(), pontosRun, moedasRun);
        BancoDados.salvarJogador(j);

        // Salva itens da run no banco
        for (var item : Sessao.get().getItensRun()) {
            BancoDados.salvarItemInventario(j.getId(), item.getNome(), item.getTipo());
        }

        lblStatus.setText(venceu
            ? "✨ O Tempo perdoou! Pontos: " + pontosRun
            : "💀 O Tempo não perdoou... Pontos: " + pontosRun);
    }

    // ── Desenho ──────────────────────────────────────────────────────────────────

    private void desenhar() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fundo
        gc.setFill(Color.web("#1a0533"));
        gc.fillRect(0, 0, W, H);

        // Grade decorativa
        gc.setStroke(Color.web("#2d0a4e"));
        gc.setLineWidth(1);
        for (int i = 0; i < W; i += 50) gc.strokeLine(i, 0, i, H);
        for (int j = 0; j < H; j += 50) gc.strokeLine(0, j, W, j);

        // Xícaras de chá
        gc.setFill(Color.web("#f0c040"));
        for (double[] x : xicalas) {
            // Xícara simples com formas básicas
            gc.fillOval(x[0], x[1] + 8, XICARA_SIZE, 12);       // corpo
            gc.fillRect(x[0] + 2, x[1] + 4, XICARA_SIZE - 4, 6); // borda
            gc.strokeOval(x[0], x[1] + 8, XICARA_SIZE, 12);
            gc.setFill(Color.web("#d4a830"));
            gc.fillOval(x[0] + XICARA_SIZE - 4, x[1] + 11, 6, 6); // alça
            gc.setFill(Color.web("#f0c040"));
        }

        // Relógios loucos
        gc.setFill(Color.web("#cc3333"));
        for (double[] r : relogios) {
            gc.fillOval(r[0], r[1], RELOGIO_SIZE, RELOGIO_SIZE);
            gc.setFill(Color.web("#ffffff"));
            // Ponteiros
            double cx = r[0] + RELOGIO_SIZE / 2.0;
            double cy = r[1] + RELOGIO_SIZE / 2.0;
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeLine(cx, cy, cx, cy - 8);
            gc.strokeLine(cx, cy, cx + 5, cy + 4);
            gc.setFill(Color.web("#cc3333"));
        }

        // Alice
        gc.setFill(Color.web("#3399ff"));
        gc.fillOval(aliceX, aliceY, ALICE_SIZE, ALICE_SIZE);  // corpo
        gc.setFill(Color.web("#ffcc99"));
        gc.fillOval(aliceX + 6, aliceY - 12, 16, 16);          // cabeça
        gc.setFill(Color.web("#ffdd00"));
        gc.fillOval(aliceX + 4, aliceY - 18, 20, 10);           // laço/chapéu

        // Overlay fim de jogo
        if (fimDeJogo || vitoria) {
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, W, H);
            gc.setFill(vitoria ? Color.GOLD : Color.TOMATO);
            gc.setFont(javafx.scene.text.Font.font("Serif", 36));
            String msg = vitoria ? "✨ Perdão obtido! ✨" : "💀 Run encerrada";
            gc.fillText(msg, W / 2.0 - 140, H / 2.0);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Serif", 18));
            gc.fillText("Pressione ESPAÇO para continuar", W / 2.0 - 140, H / 2.0 + 40);
        }
    }

    // ── Teclado ──────────────────────────────────────────────────────────────────

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
                    MainApp.trocarTela("upgrades.fxml", "Alice - Upgrades");
                }
            }
            case I -> abrirInventario();
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

    // ── Botões FXML ──────────────────────────────────────────────────────────────

    @FXML
    public void onInventario() {
        abrirInventario();
    }

    @FXML
    public void onMenu() {
        timer.stop();
        MainApp.trocarTela("menu.fxml", "Alice no País das Maravilhas");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void abrirInventario() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/alice/fxml/inventario.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/alice/css/estilo.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Inventário de Alice");
            stage.initOwner(MainApp.primaryStage);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void spawnXicara() {
        xicalas.add(new double[]{
            rand.nextInt(W - XICARA_SIZE),
            rand.nextInt(H - XICARA_SIZE)
        });
    }

    private void spawnRelogio() {
        int nivel = Sessao.get().getJogador().getNivel();
        double vel = 1.0 + nivel * 0.3 + rand.nextDouble();
        double ang = rand.nextDouble() * Math.PI * 2;
        relogios.add(new double[]{
            rand.nextInt(W - RELOGIO_SIZE),
            rand.nextInt(H - RELOGIO_SIZE),
            Math.cos(ang) * vel,
            Math.sin(ang) * vel
        });
    }

    private boolean colide(double x1, double y1, int s1, double x2, double y2, int s2) {
        return x1 < x2 + s2 && x1 + s1 > x2
            && y1 < y2 + s2 && y1 + s1 > y2;
    }

    private void atualizarHUD() {
        Jogador j = Sessao.get().getJogador();
        lblVida.setText("❤ " + vida);
        lblPontos.setText("Pontos: " + pontosRun);
        lblMoedas.setText("☕ " + moedasRun);
        lblNivel.setText(j.getNome() + " | Nível " + j.getNivel());
    }
}
