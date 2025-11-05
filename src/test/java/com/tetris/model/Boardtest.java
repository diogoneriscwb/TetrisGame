package com.tetris.model; // Mesmo pacote do Board.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a lógica do tabuleiro (Board), cobrindo os requisitos
 * de colisão, eliminação de linhas e posicionamento.
 */
class BoardTest {

    private Board board;

    // Forma de teste simples: um único bloco 1x1
    private final int[][] singleBlockShape = new int[][]{{1}};

    /**
     * Este método é executado ANTES de CADA teste.
     * Garante que temos um tabuleiro limpo e novo para cada cenário.
     */
    @BeforeEach
    void setUp() {
        board = new Board();
    }

    // --- REQUISITO 1: Teste de colisão com bordas ---

    @Test
    void testCollisionWithLeftBorder() {
        // Testa colocar um bloco em x = -1 (fora da borda esquerda)
        assertFalse(board.isValidPosition(singleBlockShape, -1, 5),
                "Deveria retornar FALSE para colisão com a borda esquerda (x < 0).");
    }

    @Test
    void testCollisionWithRightBorder() {
        // Testa colocar um bloco em x = 10 (WIDTH), o que é fora da borda
        // Nossas coordenadas são de 0 a 9.
        assertFalse(board.isValidPosition(singleBlockShape, Board.WIDTH, 5),
                "Deveria retornar FALSE para colisão com a borda direita (x >= WIDTH).");
    }

    @Test
    void testCollisionWithBottomBorder() {
        // Testa colocar um bloco em y = 20 (HEIGHT)
        assertFalse(board.isValidPosition(singleBlockShape, 5, Board.HEIGHT),
                "Deveria retornar FALSE para colisão com a borda inferior (y >= HEIGHT).");
    }

    @Test
    void testNoCollisionWithTopBorder() {
        // O topo (y < 0) é uma posição válida para a peça nascer (spawn)
        // O seu método isValidPosition já lida com isso (boardY >= 0)
        assertTrue(board.isValidPosition(singleBlockShape, 5, -1),
                "Deveria retornar TRUE para posições acima do topo (y < 0).");
    }

    @Test
    void testValidPositionInCenter() {
        // Um teste de sanidade: verifica uma posição 100% válida
        assertTrue(board.isValidPosition(singleBlockShape, 5, 5),
                "Deveria retornar TRUE para uma posição válida no centro.");
    }

    // --- REQUISITO 2: Teste de colisão com outras peças (Espaços Limitados) ---

    @Test
    void testCollisionWithOtherPieces() {
        // 1. Setup: Coloca uma "parede" de blocos no tabuleiro
        // Vamos preencher a coluna 5
        Color[][] grid = board.getGrid();
        for (int y = 10; y < 15; y++) {
            grid[y][5] = Color.BLUE; // Simula uma peça travada ali
        }

        // 2. Ação e Verificação
        // Tenta colocar um novo bloco em [5][12], o que deveria colidir
        assertFalse(board.isValidPosition(singleBlockShape, 5, 12),
                "Deveria retornar FALSE para colisão com uma peça já existente.");
    }

    @Test
    void testNoCollisionWhenAdjacent() {
        // 1. Setup: Coloca um bloco em [5][10]
        board.getGrid()[10][5] = Color.BLUE;

        // 2. Ação e Verificação
        // Tenta colocar um novo bloco em [5][9] (logo acima)
        assertTrue(board.isValidPosition(singleBlockShape, 5, 9),
                "Deveria retornar TRUE para uma posição válida adjacente a outra peça.");

        // Tenta colocar um novo bloco em [6][10] (ao lado)
        assertTrue(board.isValidPosition(singleBlockShape, 6, 10),
                "Deveria retornar TRUE para uma posição válida ao lado de outra peça.");
    }

    // --- REQUISITO 3: Teste de eliminação de múltiplas linhas ---

    /**
     * Preenche uma linha inteira do tabuleiro com uma cor.
     * Método auxiliar para facilitar a criação dos testes de limpeza.
     * @param y A linha (row) a ser preenchida.
     */
    private void fillLine(int y) {
        Color[][] grid = board.getGrid();
        for (int x = 0; x < Board.WIDTH; x++) {
            grid[y][x] = Color.RED; // Usa uma cor qualquer
        }
    }

    @Test
    void testSingleLineClear() {
        // 1. Setup: Preenche a última linha (19)
        fillLine(19);
        // Adiciona um "marcador" na linha 18 para ver se ela cai
        board.getGrid()[18][5] = Color.BLUE;

        // 2. Ação: Limpa as linhas
        int cleared = board.clearLines();

        // 3. Verificação
        assertEquals(1, cleared, "Deveria retornar 1 linha limpa.");
        // O bloco azul que estava na linha 18 deve ter caído para a linha 19
        assertNotNull(board.getGrid()[19][5], "A linha 18 não caiu para a linha 19.");
        assertEquals(Color.BLUE, board.getGrid()[19][5], "O bloco da linha 18 não caiu corretamente.");
        // A linha 18 agora deve estar vazia
        assertNull(board.getGrid()[18][5], "A linha 18 não foi limpa após a queda.");
    }

    @Test
    void testMultiLineClear_Tetris() {
        // 1. Setup: Preenche 4 linhas (16, 17, 18, 19) - um "Tetris"
        fillLine(19);
        fillLine(18);
        fillLine(17);
        fillLine(16);
        // Adiciona um marcador na linha 15 para ver se ela cai 4 posições
        board.getGrid()[15][0] = Color.GREEN;

        // 2. Ação: Limpa as linhas
        int cleared = board.clearLines();

        // 3. Verificação
        assertEquals(4, cleared, "Deveria retornar 4 linhas limpas (Tetris).");
        // O bloco verde que estava na linha 15 deve ter caído para a linha 19
        assertNotNull(board.getGrid()[19][0], "A linha 15 não caiu para a linha 19.");
        assertEquals(Color.GREEN, board.getGrid()[19][0], "O bloco da linha 15 não caiu corretamente.");
        // A linha 15 (onde o bloco estava) deve estar vazia
        assertNull(board.getGrid()[15][0], "A linha 15 original não foi limpa.");
    }

    @Test
    void testNoLinesCleared() {
        // 1. Setup: Preenche uma linha quase completa
        for (int x = 0; x < Board.WIDTH - 1; x++) {
            board.getGrid()[19][x] = Color.RED;
        }

        // 2. Ação: Limpa as linhas
        int cleared = board.clearLines();

        // 3. Verificação
        assertEquals(0, cleared, "Deveria retornar 0 linhas limpas se a linha não está cheia.");
    }
}