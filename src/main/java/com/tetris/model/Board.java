package com.tetris.model;

import com.tetris.model.tetromino.Tetromino;
import javafx.scene.paint.Color;

/**
 * Representa o tabuleiro (grelha) do jogo.
 * Responsável por gerir o estado dos blocos fixos, detetar colisões
 * e processar a eliminação de linhas completas.
 */
public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private final Color[][] grid;

    public Board() {
        grid = new Color[HEIGHT][WIDTH];
    }

    // ========================================================================
    // ==                      INÍCIO DA REFATORAÇÃO                         ==
    // ========================================================================

    /**
     * MÉTODO NOVO ("Trabalhador")
     * Verifica se uma forma (matriz) específica é válida em uma posição (x, y) específica.
     * Este é o "cérebro" da lógica de colisão e será usado pelo "Wall Kick".
     *
     * @param shape  A matriz int[][] da forma a ser testada.
     * @param pieceX A coordenada X do canto superior esquerdo da forma.
     * @param pieceY A coordenada Y do canto superior esquerdo da forma.
     * @return true se a posição for válida, false caso contrário.
     */
    public boolean isValidPosition(int[][] shape, int pieceX, int pieceY) {
        // A lógica de colisão (os loops 'for') agora vive AQUI.
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) { // É um bloco da peça

                    int boardX = pieceX + x; // Usa o parâmetro pieceX
                    int boardY = pieceY + y; // Usa o parâmetro pieceY

                    // 1. Verificação de colisão com as bordas
                    if (boardX < 0 || boardX >= WIDTH || boardY >= HEIGHT) {
                        return false;
                    }

                    // 2. Verificação de colisão com peças já posicionadas
                    // (O 'boardY >= 0' é importante para não checar fora do grid)
                    if (boardY >= 0 && grid[boardY][boardX] != null) {
                        return false;
                    }
                }
            }
        }
        return true; // Posição é válida!
    }

    /**
     * MÉTODO ANTIGO (Refatorado para "Delegador")
     * Verifica se a posição ATUAL de um tetrominó é válida.
     * Este método agora simplesmente "delega" o trabalho para o método de cima.
     *
     * @param piece O tetrominó a ser verificado.
     * @return true se a posição for válida, false caso contrário.
     */
    public boolean isValidPosition(Tetromino piece) {
        // Os loops 'for' foram removidos daqui.
        // Ele apenas chama o "Trabalhador" com as informações atuais da peça.
        return isValidPosition(piece.getShape(), piece.getX(), piece.getY());
    }

    // ========================================================================
    // ==                       FIM DA REFATORAÇÃO                           ==
    // ========================================================================


    /**
     * Fixa um tetrominó no tabuleiro, transferindo sua cor para a grelha.
     * @param piece O tetrominó a ser fixado.
     */
    public void placePiece(Tetromino piece) {
        int[][] shape = piece.getShape();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    int boardX = piece.getX() + x;
                    int boardY = piece.getY() + y;
                    if (boardY >= 0) {
                        grid[boardY][boardX] = piece.getColor();
                    }
                }
            }
        }
    }

    /**
     * Verifica e remove todas as linhas completas do tabuleiro.
     * @return O número de linhas que foram removidas.
     */
    public int clearLines() {
        int linesCleared = 0;
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean lineIsFull = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == null) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                linesCleared++;
                // Remove a linha e desce as de cima
                for (int rowToMove = y; rowToMove > 0; rowToMove--) {
                    System.arraycopy(grid[rowToMove - 1], 0, grid[rowToMove], 0, WIDTH);
                }
                // Adiciona uma nova linha vazia no topo
                grid[0] = new Color[WIDTH];
                y++; // Re-verifica a mesma linha, pois ela agora contém o conteúdo da de cima
            }
        }
        return linesCleared;
    }

    /** Limpa o tabuleiro completamente para um novo jogo. */
    public void clear() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
    }

    public Color[][] getGrid() {
        return grid;
    }
}