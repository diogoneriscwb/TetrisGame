package com.tetris.view;

import com.tetris.controller.GameEngine;
import com.tetris.model.tetromino.Tetromino;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Painel dedicado à exibição de informações como pontuação, nível,
 * linhas eliminadas e a pré-visualização da próxima peça.
 * Também contém os botões de controlo do jogo.
 */
public class InfoPanel extends VBox {

    private final Canvas nextPieceCanvas;
    private final Canvas holdPieceCanvas; //indica ao canvas a variavel de captura
    private final GamePanel gamePanel; //Variável adicionada para troca de informações entre InfoPanel e GamePanel

    public InfoPanel(GameEngine gameEngine, GamePanel gamePanel) {
        this.gamePanel = gamePanel; // Inicialização da variável adicionada
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(200);

        Font labelFont = new Font("Arial", 18);

        Label scoreLabel = createInfoLabel("Score: 0", labelFont);
        Label levelLabel = createInfoLabel("Level: 1", labelFont);
        Label linesLabel = createInfoLabel("Lines: 0", labelFont);

        // Padrão Observer via Propriedades JavaFX
        scoreLabel.textProperty().bind(gameEngine.scoreProperty().asString("Score: %d"));
        levelLabel.textProperty().bind(gameEngine.levelProperty().asString("Level: %d"));
        linesLabel.textProperty().bind(gameEngine.linesClearedProperty().asString("Lines: %d"));

//        LÓGICA DO "HOLD"
        Label holdPieceLabel = new Label("Hold:");
        holdPieceLabel.setFont(labelFont);

        holdPieceCanvas = new Canvas(5 * GamePanel.BLOCK_SIZE, 5 * GamePanel.BLOCK_SIZE);

//        LÓGICA DO "NEXT"
        Label nextPieceLabel = new Label("Next Piece:");
        nextPieceLabel.setFont(labelFont);

        nextPieceCanvas = new Canvas(5 * GamePanel.BLOCK_SIZE, 5 * GamePanel.BLOCK_SIZE); //atualizado para matriz 5x5

        // Observa a propriedade da próxima peça para redesenhar o canvas
        gameEngine.nextPieceProperty().addListener((obs, oldPiece, newPiece) -> {
            drawNextPiece(newPiece);
        });

        gameEngine.heldPieceProperty().addListener((obs, oldPiece, newPiece) -> {
            drawHoldPiece(newPiece); // <-- NOVO LISTNER PARA PEÇA EM HOLD
        });

        Button pauseButton = new Button("Pause/Resume");
        pauseButton.setOnAction(e -> {
            gameEngine.togglePause();
            gamePanel.requestFocus(); // restaura o foco para o gamePanel após clicar no botão pause/resume
        });

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            gameEngine.start();
            gamePanel.requestFocus(); // restaura o foco para o gamePanel após clicar no botão Restart.
        });

        getChildren().addAll(scoreLabel, levelLabel, linesLabel,
                holdPieceLabel, holdPieceCanvas,  // <-- ADICIONA O HOLD À TELA DO JOGO
                nextPieceLabel, nextPieceCanvas,
                pauseButton, restartButton);
    }

    private Label createInfoLabel(String text, Font font) {
        Label label = new Label(text);
        label.setFont(font);
        return label;
    }

    private void drawNextPiece(Tetromino piece) {
        GraphicsContext gc = nextPieceCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, nextPieceCanvas.getWidth(), nextPieceCanvas.getHeight());

        if (piece != null) {
            int[][] shape = piece.getShape();
            Color color = piece.getColor();
            double blockSize = GamePanel.BLOCK_SIZE * 0.8; // Um pouco menor para caber bem
            double startX = (nextPieceCanvas.getWidth() - (shape[0].length * blockSize)) / 2;
            double startY = (nextPieceCanvas.getHeight() - (shape.length * blockSize)) / 2;

            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] != 0) {
                        gc.setFill(color);
                        gc.fillRect(startX + x * blockSize, startY + y * blockSize, blockSize, blockSize);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(startX + x * blockSize, startY + y * blockSize, blockSize, blockSize);
                    }
                }
            }
        }
    }

    private void drawHoldPiece(Tetromino piece) {
        // Usa o holdPieceCanvas
        GraphicsContext gc = holdPieceCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, holdPieceCanvas.getWidth(), holdPieceCanvas.getHeight());

        if (piece != null) {
            int[][] shape = piece.getShape();
            Color color = piece.getColor();
            double blockSize = GamePanel.BLOCK_SIZE * 0.8; // Um pouco menor para caber bem
            double startX = (holdPieceCanvas.getWidth() - (shape[0].length * blockSize)) / 2;
            double startY = (holdPieceCanvas.getHeight() - (shape.length * blockSize)) / 2;

            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] != 0) {
                        gc.setFill(color);
                        gc.fillRect(startX + x * blockSize, startY + y * blockSize, blockSize, blockSize);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(startX + x * blockSize, startY + y * blockSize, blockSize, blockSize);
                    }
                }
            }
        }
    }
}