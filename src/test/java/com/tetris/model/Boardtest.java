package com.tetris.model; // Mesmo pacote do Board.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// O 'import javafx.scene.paint.Color;' FOI REMOVIDO!
import com.tetris.model.PieceType; // <-- IMPORT NOVO
import java.util.List; // <-- IMPORT JÁ EXISTENTE
import static org.junit.jupiter.api.Assertions.*;

/**

 Testa a lógica do tabuleiro (Board).

 (Versão 2 - Refatorada para usar PieceType em vez de Color)
 */
class BoardTest {

    private Board board;

    // Forma de teste simples: um único bloco 1x1
    private final int[][] singleBlockShape = new int[][]{{1}};

    /**

     Este método é executado ANTES de CADA teste.

     Garante que temos um tabuleiro limpo e novo para cada cenário.
     */
    @BeforeEach
    void setUp() {
        board = new Board();
    }

// --- REQUISITO 1: Teste de colisão com bordas ---
// (Esta parte não muda)

    @Test
    void testCollisionWithLeftBorder() {
        assertFalse(board.isValidPosition(singleBlockShape, -1, 5),
                "Deveria retornar FALSE para colisão com a borda esquerda (x < 0).");
    }

    @Test
    void testCollisionWithRightBorder() {
        assertFalse(board.isValidPosition(singleBlockShape, Board.WIDTH, 5),
                "Deveria retornar FALSE para colisão com a borda direita (x >= WIDTH).");
    }

    @Test
    void testCollisionWithBottomBorder() {
        assertFalse(board.isValidPosition(singleBlockShape, 5, Board.HEIGHT),
                "Deveria retornar FALSE para colisão com a borda inferior (y >= HEIGHT).");
    }

    @Test
    void testNoCollisionWithTopBorder() {
        assertTrue(board.isValidPosition(singleBlockShape, 5, -1),
                "Deveria retornar TRUE para posições acima do topo (y < 0).");
    }

    @Test
    void testValidPositionInCenter() {
        assertTrue(board.isValidPosition(singleBlockShape, 5, 5),
                "Deveria retornar TRUE para uma posição válida no centro.");
    }

// --- REQUISITO 2: Teste de colisão com outras peças (Espaços Limitados) ---
// (Esta parte FOI ATUALIZADA para usar PieceType)

    @Test
    void testCollisionWithOtherPieces() {
// 1. Setup: Coloca uma "parede" de blocos no tabuleiro
// Vamos preencher a coluna 5 com o tipo LOCKED
        PieceType[][] grid = board.getGrid();
        for (int y = 10; y < 15; y++) {
            grid[y][5] = PieceType.LOCKED; // <-- MUDOU DE Color.BLUE
        }

        // 2. Ação e Verificação
        assertFalse(board.isValidPosition(singleBlockShape, 5, 12),
                "Deveria retornar FALSE para colisão com uma peça já existente.");


    }

    @Test
    void testNoCollisionWhenAdjacent() {
// 1. Setup: Coloca um bloco em [5][10]
        board.getGrid()[10][5] = PieceType.LOCKED; // <-- MUDOU DE Color.BLUE

        // 2. Ação e Verificação
        assertTrue(board.isValidPosition(singleBlockShape, 5, 9),
                "Deveria retornar TRUE para uma posição válida adjacente a outra peça.");
        assertTrue(board.isValidPosition(singleBlockShape, 6, 10),
                "Deveria retornar TRUE para uma posição válida ao lado de outra peça.");


    }

// --- REQUISITO 3: Teste de eliminação de múltiplas linhas ---
// (Esta seção inteira foi ATUALIZADA para usar PieceType)

    /**

     Preenche uma linha inteira do tabuleiro com um tipo de bloco.
     */
    private void fillLine(int y) {
        PieceType[][] grid = board.getGrid();
        for (int x = 0; x < Board.WIDTH; x++) {
            grid[y][x] = PieceType.LOCKED; // <-- MUDOU DE Color.RED
        }
    }

    @Test
    void testSingleLineClear() {
// 1. Setup: Preenche a linha 19 e coloca um marcador (J) na 18
        fillLine(19);
        board.getGrid()[18][5] = PieceType.J; // <-- MUDOU DE Color.BLUE (J é um marcador)

        // 2. Ação: Agora em duas etapas!
        List<Integer> linesToClear = board.findFullLines(); // Etapa 1: Encontra
        board.removeLines(linesToClear);                    // Etapa 2: Remove

        // 3. Verificação
        assertEquals(1, linesToClear.size(), "Deveria encontrar 1 linha cheia.");
        assertTrue(linesToClear.contains(19), "A lista deveria conter a linha 19.");
        // O bloco 'J' que estava na linha 18 deve ter caído para a linha 19
        assertNotNull(board.getGrid()[19][5], "A linha 18 não caiu para a linha 19.");
        assertEquals(PieceType.J, board.getGrid()[19][5], "O bloco da linha 18 não caiu corretamente."); // <-- MUDOU A VERIFICAÇÃO
        assertNull(board.getGrid()[18][5], "A linha 18 não foi limpa após a queda.");


    }

    @Test
    void testMultiLineClear_Tetris() {
// 1. Setup: Preenche 4 linhas (16-19) e coloca um marcador (L) na 15
        fillLine(19);
        fillLine(18);
        fillLine(17);
        fillLine(16);
        board.getGrid()[15][0] = PieceType.L; // <-- MUDOU DE Color.GREEN (L é um marcador)

        // 2. Ação: Em duas etapas!
        List<Integer> linesToClear = board.findFullLines(); // Etapa 1: Encontra
        board.removeLines(linesToClear);                    // Etapa 2: Remove

        // 3. Verificação
        assertEquals(4, linesToClear.size(), "Deveria encontrar 4 linhas cheias.");
        // O bloco 'L' que estava na linha 15 deve ter caído para a linha 19
        assertNotNull(board.getGrid()[19][0], "A linha 15 não caiu para a linha 19.");
        assertEquals(PieceType.L, board.getGrid()[19][0], "O bloco da linha 15 não caiu corretamente."); // <-- MUDOU A VERIFICAÇÃO
        assertNull(board.getGrid()[15][0], "A linha 15 original não foi limpa.");


    }

    @Test
    void testNoLinesCleared() {
// 1. Setup: Preenche uma linha quase completa
        for (int x = 0; x < Board.WIDTH - 1; x++) {
            board.getGrid()[19][x] = PieceType.LOCKED; // <-- MUDOU DE Color.RED
        }

        // 2. Ação: Em duas etapas!
        List<Integer> linesToClear = board.findFullLines(); // Etapa 1: Encontra
        board.removeLines(linesToClear);                    // Etapa 2: Remove

        // 3. Verificação
        assertEquals(0, linesToClear.size(), "Deveria encontrar 0 linhas limpas se a linha não está cheia.");


    }
}