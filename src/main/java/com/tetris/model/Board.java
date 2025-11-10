package com.tetris.model;

import com.tetris.model.tetromino.Tetromino;
// O import de 'javafx.scene.paint.Color' foi REMOVIDO!
import com.tetris.model.PieceType; // <-- IMPORTA O NOVO ENUM
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Representa o tabuleiro (grelha) do jogo.
 * (Versão 2 - Refatorada para usar PieceType[][] em vez de Color[][])
 */
public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    // --- A MUDANÇA CENTRAL ESTÁ AQUI ---
    // O grid agora armazena o "Tipo" do bloco, não a "Cor".
    private final PieceType[][] grid;

    public Board() {
        grid = new PieceType[HEIGHT][WIDTH]; // <-- MUDOU DE Color[][]
    }

    // ========================================================================
    // ==     MÉTODOS DE COLISÃO (LÓGICA 100% INALTERADA)      ==
    // ========================================================================
    // A lógica de 'grid[boardY][boardX] != null' funciona perfeitamente
    // para qualquer tipo de objeto (Color ou PieceType).

    public boolean isValidPosition(int[][] shape, int pieceX, int pieceY) {
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
                    if (boardY >= 0 && grid[boardY][boardX] != null) {
                        return false;
                    }
                }
            }
        }
        return true; // Posição é válida!
    }

    public boolean isValidPosition(Tetromino piece) {
        return isValidPosition(piece.getShape(), piece.getX(), piece.getY());
    }

    // ========================================================================
    // ==     MÉTODOS DE MANIPULAÇÃO DO GRID (MODIFICADOS)      ==
    // ========================================================================

    /**
     * Fixa um tetrominó no tabuleiro.
     * IMPLEMENTA A SUA IDEIA: Todas as peças travadas são salvas
     * com o tipo 'LOCKED' (para que a View possa pintá-las de cinza).
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
                        // Em vez de salvar a cor/tipo da peça (piece.getType()),
                        // nós salvamos o tipo "LOCKED" (Travado).
                        grid[boardY][boardX] = PieceType.LOCKED;
                    }
                }
            }
        }
    }

    /**
     * Encontra as linhas cheias. A lógica é a mesma ('== null').
     * @return Uma lista com os números (índices 'y') das linhas cheias.
     */
    public List<Integer> findFullLines() {
        List<Integer> fullLines = new ArrayList<>();

        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean lineIsFull = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == null) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                fullLines.add(y);
            }
        }
        return fullLines;
    }

    /**
     * Remove fisicamente as linhas do grid e desce as de cima.
     * @param linesToRemove A lista de linhas (índices 'y') para remover.
     */
    /**
     * MÉTODO CORRIGIDO (O Bug do Multi-Line Clear)
     * Remove fisicamente as linhas do grid e desce as de cima.
     * Esta lógica (com o contador 'linesToMoveDown') garante que
     * a "descida" (drop down) funcione para múltiplas linhas.
     * (Agora adaptada para PieceType[][])
     */
    public void removeLines(List<Integer> linesToRemove) {
        // 1. Ordena a lista em ordem crescente (ex: [16, 17, 18, 19])
        Collections.sort(linesToRemove);

        // 2. Itera de baixo para cima no tabuleiro
        int linesToMoveDown = 0; // Contador de "espaços vazios"

        for (int y = HEIGHT - 1; y >= 0; y--) {
            if (linesToRemove.contains(y)) {
                // Se a linha 'y' está na lista de remoção,
                // apenas incrementamos o contador de "espaços".
                linesToMoveDown++;
            } else if (linesToMoveDown > 0) {
                // Se a linha 'y' NÃO está na lista, mas temos "espaços"
                // abaixo dela, nós a movemos para baixo.

                // Move a linha 'y' para a posição 'y + linesToMoveDown'
                System.arraycopy(grid[y], 0, grid[y + linesToMoveDown], 0, WIDTH);

                // E limpa a linha original 'y'
                grid[y] = new PieceType[WIDTH];
            }
        }

        // 3. Limpa as linhas do topo (que agora estão vazias)
        for(int y = 0; y < linesToMoveDown; y++) {
            grid[y] = new PieceType[WIDTH];
        }
    }

    /** Limpa o tabuleiro completamente (a lógica ' = null' é a mesma). */
    public void clear() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
    }

    /**
     * Retorna o grid lógico do jogo.
     * @return Um grid 2D de PieceType.
     */
    public PieceType[][] getGrid() { // <-- MUDOU O TIPO DE RETORNO
        return grid;
    }
}