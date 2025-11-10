package com.tetris.controller;

import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.PieceType; // <-- IMPORT NOVO
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**

 Testa a lógica do motor do jogo (GameEngine).

 (Versão 2 - Refatorada para usar PieceType)
 */
class GameEngineTest {

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
// Cria um GameEngine novo para cada teste
        gameEngine = new GameEngine();

        // Inicializa o estado lógico do jogo (cria peças, etc.)
        gameEngine.initializeGame();

        // Liga o "modo de teste" para pular timers de animação
        gameEngine.setTestMode(true);


    }

    /**

     MÉTODO AUXILIAR (ATUALIZADO)

     Preenche uma linha inteira do tabuleiro com o tipo "LOCKED".
     */
    private void fillLine(Board board, int y) {
// Agora usa o grid de PieceType
        PieceType[][] grid = board.getGrid();
        for (int x = 0; x < Board.WIDTH; x++) {
            grid[y][x] = PieceType.LOCKED; // <-- MUDOU DE Color.RED
        }
    }

// --- REQUISITO 4: Teste de cálculo correto de pontuação ---
// (Esta parte não muda, pois testa apenas números)

    @Test
    void testScoreForSingleLine_Level1() {
        gameEngine.updateScore(1);
        assertEquals(40, gameEngine.scoreProperty().get(), "Nível 1, 1 linha = 40 pontos.");
    }

    @Test
    void testScoreForTetris_Level1() {
        gameEngine.updateScore(4);
        assertEquals(1200, gameEngine.scoreProperty().get(), "Nível 1, 4 linhas = 1200 pontos.");
    }

    @Test
    void testScoreForMultiLines_Level1() {
        gameEngine.updateScore(2);
        assertEquals(100, gameEngine.scoreProperty().get(), "Nível 1, 2 linhas = 100 pontos.");

        gameEngine.scoreProperty().set(0); // Reseta o score

        gameEngine.updateScore(3);
        assertEquals(300, gameEngine.scoreProperty().get(), "Nível 1, 3 linhas = 300 pontos.");


    }

    @Test
    void testScoreMultiplierWithLevel() {
        gameEngine.levelProperty().set(5);
        gameEngine.updateScore(1);
        assertEquals(200, gameEngine.scoreProperty().get(), "Nível 5, 1 linha = 40 * 5 = 200 pontos.");
    }

    @Test
    void testLevelUpdate() {
        gameEngine.linesClearedProperty().set(9);
        gameEngine.updateLevel();
        assertEquals(1, gameEngine.levelProperty().get(), "Nível deve ser 1 após 9 linhas.");

        gameEngine.linesClearedProperty().set(10);
        gameEngine.updateLevel();
        assertEquals(2, gameEngine.levelProperty().get(), "Nível deve ser 2 após 10 linhas.");

        gameEngine.linesClearedProperty().set(19);
        gameEngine.updateLevel();
        assertEquals(2, gameEngine.levelProperty().get(), "Nível deve ser 2 após 19 linhas.");

        gameEngine.linesClearedProperty().set(20);
        gameEngine.updateLevel();
        assertEquals(3, gameEngine.levelProperty().get(), "Nível deve ser 3 após 20 linhas.");


    }

// --- REQUISITO 6: Teste de condições de game over ---
// (Esta parte FOI ATUALIZADA para usar PieceType)

    @Test
    void testGameOverCondition() {
// 1. Setup: Simula um tabuleiro bloqueado no topo.
        Board board = gameEngine.getBoard();

        // Preenche a linha 1 com o tipo "LOCKED"
        for (int x = 3; x < 6; x++) {
            board.getGrid()[1][x] = PieceType.LOCKED; // <-- MUDOU DE Color.RED
        }

        // 2. Ação: Tenta "nascer" uma nova peça
        gameEngine.spawnNewPiece();

        // 3. Verificação:
        assertEquals(GameState.GAME_OVER,
                gameEngine.gameStateProperty().get(),
                "O estado do jogo deveria ser GAME_OVER após falha no spawn.");


    }

// --- TESTE BÔNUS: Teste do Fluxo de Animação ---
// (Esta parte FOI ATUALIZADA para usar o 'fillLine' correto)

    @Test
    void testLineClearingAnimationFlow() {
// 1. SETUP:
        Board board = gameEngine.getBoard();
        fillLine(board, 19); // (Agora usa o 'fillLine' que preenche com PieceType.LOCKED)

        gameEngine.getCurrentPiece().setY(15);

        // 2. AÇÃO (Parte 1): Trava a peça
        gameEngine.lockPiece();

        // 3. VERIFICAÇÃO (Parte 1): O jogo pausou corretamente?
        assertEquals(GameState.LINE_CLEARING,
                gameEngine.gameStateProperty().get(),
                "O estado do jogo deveria ser LINE_CLEARING.");

        assertNotNull(gameEngine.linesToClearProperty().get(),
                "A propriedade de linhas a limpar não foi definida.");

        assertEquals(1, gameEngine.linesToClearProperty().get().size(),
                "A lista de linhas a limpar deveria conter 1 linha.");

        // 4. AÇÃO (Parte 2): Simula a animação terminando
        gameEngine.onAnimationFinished();

        // 5. VERIFICAÇÃO (Parte 2): O jogo retomou corretamente?
        assertEquals(GameState.PLAYING,
                gameEngine.gameStateProperty().get(),
                "O estado do jogo deveria voltar para PLAYING.");

        assertEquals(40, gameEngine.scoreProperty().get(),
                "A pontuação de 40 (Nível 1) deveria ter sido adicionada.");

        assertNull(board.getGrid()[19][0],
                "A linha 19 não foi fisicamente removida do tabuleiro.");

        assertNull(gameEngine.linesToClearProperty().get(),
                "A propriedade de linhas a limpar não foi resetada para null.");


    }
}