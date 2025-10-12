package com.tetris;

import com.tetris.controller.GameEngine;
import com.tetris.view.GamePanel;
import com.tetris.view.InfoPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe principal que inicia a aplicação JavaFX.
 * Responsável por criar a janela, instanciar o GameEngine e
 * montar os componentes da interface gráfica (GamePanel e InfoPanel).
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // 1. Instanciar o motor do jogo
        GameEngine gameEngine = new GameEngine();

        // 2. Criar e posicionar os painéis da UI
        GamePanel gamePanel = new GamePanel(gameEngine);
        InfoPanel infoPanel = new InfoPanel(gameEngine, gamePanel); // Adicionado gamePanel para comunicação entre as classes.

        root.setCenter(gamePanel);
        root.setRight(infoPanel);

        // 3. Configurar a cena e os controlos
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
        /*
            //Teste do keyListener via terminal
            System.out.println("Tecla Pressionada: " + event.getCode());
        */
            // 1. Passa a tecla para o motor do jogo
            gameEngine.handleKeyPress(event.getCode());

            // 2. CONSOME O EVENTO para que o JavaFX não mude o foco
            event.consume();
        });

        // 4. Configurar e exibir a janela principal
        primaryStage.setTitle("TetrisFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);


        gamePanel.requestFocus(); // LINHA ADICIONA DEPOIS POR FALTA DE FUNCIONALIDADE DOS COMANDOS DIRECIONAIS

        primaryStage.show();

        // 5. Iniciar o jogo
        gameEngine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}