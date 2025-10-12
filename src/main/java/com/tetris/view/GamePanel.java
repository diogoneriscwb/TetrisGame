package com.tetris.view;

import com.tetris.controller.GameEngine;
import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.tetromino.Tetromino;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Componente da Interface Gráfica responsável por renderizar o estado visual do jogo.
 * Utiliza um Canvas JavaFX para desenhar o tabuleiro, as peças fixas e a peça em movimento.
 */
public class GamePanel extends Canvas {

    public static final int BLOCK_SIZE = 30;
    private final GameEngine gameEngine;
    private final GraphicsContext gc;

    public GamePanel(GameEngine gameEngine) {
        super(Board.WIDTH * BLOCK_SIZE, Board.HEIGHT * BLOCK_SIZE);
        this.gameEngine = gameEngine;
        this.gc = getGraphicsContext2D();

        // Um loop de renderização para manter 60 FPS
        AnimationTimer renderer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };
        renderer.start();
    }

    private void draw() {
        // Limpa o canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Desenha o tabuleiro (peças já fixadas)
        Board board = gameEngine.getBoard();
        Color[][] grid = board.getGrid();
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (grid[y][x] != null) {
                    drawBlock(x, y, grid[y][x]);
                }
            }
        }

        // Desenha a peça atual
        Tetromino currentPiece = gameEngine.getCurrentPiece();
        if (currentPiece != null) {
            drawTetromino(currentPiece);
        }

        // Desenha sobreposições de estado (Pausado, Game Over)
        GameState state = gameEngine.gameStateProperty().get();
        if (state == GameState.PAUSED) {
            drawOverlay("PAUSED");
        } else if (state == GameState.GAME_OVER) {
            drawOverlay("GAME OVER");
        }
    }

    private void drawTetromino(Tetromino piece) {
        int[][] shape = piece.getShape();
        Color color = piece.getColor();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    drawBlock(piece.getX() + x, piece.getY() + y, color);
                }
            }
        }
    }

    private void drawBlock(int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        gc.setStroke(Color.WHITE);
        gc.strokeRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void drawOverlay(String text) {
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, getWidth() / 2, getHeight() / 2);
    }
}