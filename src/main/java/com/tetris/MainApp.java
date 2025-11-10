package com.tetris;

import com.tetris.controller.GameEngine;
import com.tetris.view.GamePanel;
import com.tetris.view.InfoPanel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination; // <-- ADICIONE ESTA
import javafx.scene.input.KeyCodeCombination; // <-- ADICIONE ESTA

import javafx.stage.Stage;

import java.util.Objects;

/**
 * Classe principal que inicia a aplicação JavaFX.
 * (Versão com layout HBox responsivo para alinhar o jogo à esquerda)
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // --- MUDANÇA DE LAYOUT: USANDO HBOX ---
        HBox root = new HBox();
        root.setId("root-pane"); // Aplica o fundo holográfico (do styles.css)
        root.setAlignment(Pos.CENTER); // Centraliza verticalmente na tela

        // 1. Instanciar o motor do jogo
        GameEngine gameEngine = new GameEngine();

        // 2. Criar os painéis
        GamePanel gamePanel = new GamePanel(gameEngine); // (A nova versão responsiva)
        InfoPanel infoPanel = new InfoPanel(gameEngine, gamePanel); // (O seu HUD fixo)

        // --- NOVO (Wrapper Responsivo) ---
        // 3. Criar um "wrapper" para o GamePanel (o Canvas)
        //    Este StackPane vai crescer e diminuir com a janela
        StackPane gameWrapper = new StackPane(gamePanel);

        // 4. Ligar (bind) o tamanho do Canvas (GamePanel) ao tamanho do Wrapper
        //    Isso torna o CANVAS responsivo
        gamePanel.widthProperty().bind(gameWrapper.widthProperty());
        gamePanel.heightProperty().bind(gameWrapper.heightProperty());

        // --- NOVO (Espaçador Flexível) ---
        // 5. Cria um "espaçador" vazio que vai crescer
        HBox spacer = new HBox();
        // Diz ao wrapper do JOGO e ao ESPAÇADOR para crescerem e dividirem o espaço
        HBox.setHgrow(gameWrapper, Priority.ALWAYS);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 6. Adiciona os painéis na HBox
        // Ordem: Jogo (Esquerda) -> Espaçador (Centro) -> HUD (Direita)
        root.getChildren().addAll(gameWrapper, spacer, infoPanel);

        // 7. Configurar a cena e os controlos
        Scene scene = new Scene(root);

        // Carrega o CSS
        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/styles.css")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("[ERRO][MainApp] CSS não encontrado! Verifique se '/css/styles.css' existe em 'src/main/resources'.");
        }

        // --- 1. DEFINE OS NOVOS ATALHOS GLOBAIS ---
        // (Isso é mais limpo do que fazer 'if (event.isAltDown() && ...)')

        // Atalho: Alt + P
        final KeyCombination atalhoPause = new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN);
        // Atalho: Alt + R
        final KeyCombination atalhoRestart = new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN);


        // --- 2. CONFIGURA O "OUVINTE" DE TECLADO INTELIGENTE ---
        scene.setOnKeyPressed(event -> {

            // 2a. Verifica se o evento BATE com nossos atalhos globais
            if (atalhoPause.match(event)) {
                gameEngine.togglePause();

            } else if (atalhoRestart.match(event)) {
                gameEngine.start();

            } else {
                // 2b. Se não for um atalho global, passa para o motor do jogo
                // (O 'handleKeyPress' cuida das setas, 'C', espaço, etc.)
                gameEngine.handleKeyPress(event.getCode());
            }

            event.consume(); // Consome o evento de qualquer forma
        });

        // 8. Carregar as Fontes
        try {
            Font.loadFont(Objects.requireNonNull(
                    getClass().getResourceAsStream("/fonts/Orbitron_Regular.ttf")), 24);
            Font.loadFont(Objects.requireNonNull(
                    getClass().getResourceAsStream("/fonts/digital_7.ttf")), 24);
        } catch (Exception e) {
            System.err.println("[ERRO][MainApp] Falha ao carregar uma ou mais fontes! Verifique a pasta '/fonts/'.");
            System.err.println("Verifique se os nomes 'Orbitron_Regular.ttf' e 'digital_7.ttf' estão corretos.");
        }

        // 9. Configurar e exibir a janela principal
        primaryStage.setTitle("TetrisFX - HUD"); // Título atualizado
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); // É REDIMENSIONÁVEL
        primaryStage.setFullScreen(true); // Tela cheia
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);

        gamePanel.requestFocus();
        primaryStage.show();

        // 10. Iniciar o jogo
        gameEngine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}