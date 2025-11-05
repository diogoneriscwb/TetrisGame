package com.tetris.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a lógica do motor do jogo (GameEngine).
 */
class GameEngineTest {

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        // Cria um GameEngine novo para cada teste
        gameEngine = new GameEngine();

        // Inicializa manualmente os valores para um estado conhecido (Nível 1)
        gameEngine.initializeGame();
       /* gameEngine.scoreProperty().set(0);
        gameEngine.levelProperty().set(1);
        gameEngine.linesClearedProperty().set(0);
        */
    }

    // --- REQUISITO 4: Teste de cálculo correto de pontuação ---

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
        // 1. Setup: Força o Nível 5
        gameEngine.levelProperty().set(5);

        // 2. Ação: Limpa uma linha (que vale 40)
        gameEngine.updateScore(1);

        // 3. Verificação: Deve ser 40 * 5 = 200
        assertEquals(200, gameEngine.scoreProperty().get(), "Nível 5, 1 linha = 40 * 5 = 200 pontos.");
    }

    @Test
    void testLevelUpdate() {
        // Nível deve aumentar a cada 10 linhas limpas

        // Simula 9 linhas limpas
        gameEngine.linesClearedProperty().set(9);
        gameEngine.updateLevel();
        assertEquals(1, gameEngine.levelProperty().get(), "Nível deve ser 1 após 9 linhas.");

        // Simula a 10ª linha
        gameEngine.linesClearedProperty().set(10);
        gameEngine.updateLevel();
        assertEquals(2, gameEngine.levelProperty().get(), "Nível deve ser 2 após 10 linhas.");

        // Simula 19 linhas
        gameEngine.linesClearedProperty().set(19);
        gameEngine.updateLevel();
        assertEquals(2, gameEngine.levelProperty().get(), "Nível deve ser 2 após 19 linhas.");

        // Simula a 20ª linha
        gameEngine.linesClearedProperty().set(20);
        gameEngine.updateLevel();
        assertEquals(3, gameEngine.levelProperty().get(), "Nível deve ser 3 após 20 linhas.");
    }

    // --- REQUISITO 6: Teste de condições de game over ---

    @Test
    void testGameOverCondition() {
        // 1. Setup: Simula um tabuleiro bloqueado no topo.
        // Vamos preencher a linha 1 (a segunda linha do topo)
        // onde as peças geralmente nascem.

        // Precisamos de uma "instância" real do tabuleiro do GameEngine
        // Para isso, precisamos chamar start()

        // --- Vamos refazer o setup deste teste ---
        // gameEngine.start(); // Isso spawna uma peça, o que é bom

        // ---
        // Abordagem mais limpa: Não vamos chamar start().
        // Vamos apenas pegar o 'board' que o GameEngine criou
        // e sujá-lo manualmente.
        // ---

        com.tetris.model.Board board = gameEngine.getBoard();

        // Preenche a linha 1 (onde a peça 'T' ou 'L' bateria)
        for (int x = 3; x < 6; x++) {
            board.getGrid()[1][x] = javafx.scene.paint.Color.RED;
        }

        // 2. Ação: Tenta "nascer" uma nova peça
        // O método 'spawnNewPiece' é 'private'.
        // Assim como o 'updateScore', vamos refatorá-lo.

        // **AÇÃO NECESSÁRIA:**
        // Vá no seu GameEngine.java e mude a assinatura do método:
        // DE:   private void spawnNewPiece()
        // PARA: void spawnNewPiece() // (remova a palavra 'private')

        gameEngine.spawnNewPiece(); // Agora podemos chamar

        // 3. Verificação:
        // O método spawnNewPiece() deve ter verificado a colisão
        // e mudado o estado do jogo.
        assertEquals(com.tetris.model.GameState.GAME_OVER,
                gameEngine.gameStateProperty().get(),
                "O estado do jogo deveria ser GAME_OVER após falha no spawn.");
    }
}