package com.tetris.controller;

import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.tetromino.Tetromino;
import com.tetris.model.tetromino.TetrominoFactory;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import java.util.List;

/**
 * Motor do Jogo (GameEngine).
 * Classe central que coordena toda a lógica, o estado e o loop principal do jogo.
 * Conecta o modelo (Board, Tetromino) com a visão (GamePanel, InfoPanel).
 */
public class GameEngine {

    private final Board board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;

    // Propriedades JavaFX para notificar a UI sobre mudanças (Padrão Observer)
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.PLAYING);
    private final ObjectProperty<Tetromino> nextPieceProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<List<Integer>> linesToClearProperty = new SimpleObjectProperty<>(null);
    private Tetromino heldPiece; // Onde a peça fica guardada
    private final ObjectProperty<Tetromino> heldPieceProperty = new SimpleObjectProperty<>(null); // Para a UI
    private boolean canHold; // Regra "uma vez por peça"
    private boolean isTestMode = false; // Flag para pular lógicas de JavaFX em testes

    private final AnimationTimer gameLoop;
    private long lastDropTime;

    public GameEngine() {
        this.board = new Board();
        this.gameLoop = createGameLoop();
    }

    private AnimationTimer createGameLoop() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameState.get() == GameState.PLAYING) {
                    if (now - lastDropTime >= getDropInterval()) {
                        moveDown();
                        lastDropTime = now;
                    }
                }
            }
        };
    }

    public void initializeGame() {
        board.clear();
        score.set(0);
        level.set(1);
        linesCleared.set(0);

        heldPiece = null; // <-- NOVO
        heldPieceProperty.set(null); // <-- NOVO
        canHold = true; // <-- NOVO

        currentPiece = TetrominoFactory.getRandomTetromino();
        nextPiece = TetrominoFactory.getRandomTetromino();
        nextPieceProperty.set(nextPiece);
        gameState.set(GameState.PLAYING);

    }

    public void start() {
        initializeGame();
        lastDropTime = System.nanoTime();
        gameLoop.start();
    }


    public void handleKeyPress(KeyCode code) {
        if (gameState.get() != GameState.PLAYING) {
            return;
        }
        switch (code) {
            case LEFT:  moveLeft(); break;
            case RIGHT: moveRight(); break;
            case DOWN:  moveDown(); score.set(score.get() + 1); break; // Pontos por queda suave
            case UP:    rotate(); break;
            case SPACE: hardDrop(); break;
            case C:     hold(); break; // <-- TECLA DE CAPTURA DA PEÇA
        }
    }

    public void togglePause() {
        if (gameState.get() == GameState.PLAYING) {
            gameState.set(GameState.PAUSED);
            if (!isTestMode) { // <-- PROTEÇÃO ADICIONADA
                gameLoop.stop();
            }
        } else if (gameState.get() == GameState.PAUSED) {
            gameState.set(GameState.PLAYING);
            if (!isTestMode) { // <-- PROTEÇÃO ADICIONADA
                lastDropTime = System.nanoTime();
                gameLoop.start();
            }
        }
    }

    private void move(int dx, int dy) {
        currentPiece.setX(currentPiece.getX() + dx);
        currentPiece.setY(currentPiece.getY() + dy);
        if (!board.isValidPosition(currentPiece)) {
            // Reverte o movimento se for inválido
            currentPiece.setX(currentPiece.getX() - dx);
            currentPiece.setY(currentPiece.getY() - dy);
            // Se o movimento inválido foi para baixo, a peça travou
            if (dy > 0) {
                lockPiece();
            }
        }
    }

    private void moveLeft() { move(-1, 0); }
    private void moveRight() { move(1, 0); }
    private void moveDown() { move(0, 1); }

    private void hardDrop() {
        while (board.isValidPosition(currentPiece)) {
            currentPiece.setY(currentPiece.getY() + 1);
            score.set(score.get() + 2); // Pontos por queda rápida
        }
        currentPiece.setY(currentPiece.getY() - 1); // Volta uma posição
        lockPiece();
    }

    /**
     * NOVO MÉTODO rotate() (Lógica "Verifica-e-Depois-Gira")
     * Usa o novo método do Board para testar "wall kicks"
     * antes de aplicar a rotação.
     */
    private void rotate() {
        // 1. Pega a forma futura SEM girar a peça
        int[][] futuraForma = currentPiece.getNextRotationShape();

        // 2. Define os "chutes" que queremos testar.
        //    Ordem: Posição Atual (0), Chute para Direita (+1), Chute para Esquerda (-1)
        int[] chutesX = {0, 1, -1, 2, -2};

        // 3. Itera por cada "chute"
        for (int chute : chutesX) {
            int novoX = currentPiece.getX() + chute;
            int novoY = currentPiece.getY(); // (Não estamos testando chutes verticais)

            // 4. Pergunta ao tabuleiro: "Esta forma FUTURA é válida nesta NOVA posição?"
            //    (Agora está chamando o método "Trabalhador" do Board)
            if (board.isValidPosition(futuraForma, novoX, novoY)) {

                // 5. SIM! A rotação é válida!
                currentPiece.setX(novoX);  // Aplica o "chute" (move a peça se chute != 0)
                currentPiece.rotate();      // AGORA SIM, aplica a rotação
                return; // Sai do método, o trabalho está feito.
            }
        }

        // 6. Se saiu do loop, é porque NENHUM chute funcionou.
        //    Não fazemos nada. A rotação falha silenciosamente.
    }

    /*
     * Trava a peça atual no tabuleiro e lida com a limpeza de linhas.
     * Esta é a nova versão que suportará animações.
     */
    /**
     * Trava a peça e inicia o processo de limpeza de linha (com animação).
     * Esta é a versão final que o teste está esperando.
     */
    /*private*/ void lockPiece() {
        // 1. Coloca a peça no tabuleiro
        board.placePiece(currentPiece);

        // 2. PERGUNTA ao tabuleiro quais linhas estão cheias
        List<Integer> linesToClear = board.findFullLines();
        int cleared = linesToClear.size();

        // 3. Verifica se alguma linha precisa ser limpa
        if (cleared > 0) {
            // === INÍCIO DA LÓGICA DE ANIMAÇÃO ===

            // 4. Pausamos o estado do jogo
            gameState.set(GameState.LINE_CLEARING); // <-- O teste quer ver isso!

            // 5. "Avisamos" o GamePanel quais linhas animar
            linesToClearProperty.set(linesToClear);

            // 6. SÓ paramos o loop SE NÃO ESTIVER EM TESTE
            // (Isso corrige o bug do 'pulseTimer' que teríamos)
            if (!isTestMode) {
                gameLoop.stop();
            }

            // O método termina AQUI.
            // O 'spawnNewPiece()' NÃO é chamado.
            // Ele agora espera o 'onAnimationFinished()' ser chamado.

        } else {
            // Nenhuma linha para limpar, apenas cria a próxima peça
            spawnNewPiece();
        }
    }

    /**
     * NOVO MÉTODO PÚBLICO
     * Chamado pelo GamePanel (View) quando a animação de limpeza termina.
     * O nosso teste 'testLineClearingAnimationFlow' também simula essa chamada.
     */
    public void onAnimationFinished() {
        // 1. Pega as linhas que acabaram de ser animadas
        List<Integer> linesToClear = linesToClearProperty.get();
        // Adiciona uma checagem de segurança (boa prática)
        int cleared = (linesToClear != null) ? linesToClear.size() : 0;

        if (cleared > 0) {
            // 2. MANDA o tabuleiro remover as linhas
            board.removeLines(linesToClear);

            // 3. Atualiza a pontuação
            updateScore(cleared);
        }

        // 4. Limpa a propriedade de aviso
        linesToClearProperty.set(null);

        // 5. Cria a próxima peça
        spawnNewPiece();

        // 6. Retoma o jogo
        gameState.set(GameState.PLAYING);

        // 7. SÓ liga o loop SE NÃO ESTIVER EM TESTE
        //    (Esta é a correção para o bug do pulseTimer)
        if (!isTestMode) {
            lastDropTime = System.nanoTime();
            gameLoop.start();
        }
    }

    /*private*/ void spawnNewPiece() {
        currentPiece = nextPiece;
        nextPiece = TetrominoFactory.getRandomTetromino();
        nextPieceProperty.set(nextPiece);

        canHold = true; // <-- CAPTURA UMA VEZ POR PEÇA

        if (!board.isValidPosition(currentPiece)) {
            gameState.set(GameState.GAME_OVER);
            if (!isTestMode) { // <-- PROTEÇÃO ADICIONADA
                gameLoop.stop();
            }
        }
    }

    /*private*/ void updateScore(int cleared) {
        linesCleared.set(linesCleared.get() + cleared);
        int points = switch (cleared) {
            case 1 -> 40;
            case 2 -> 100;
            case 3 -> 300;
            case 4 -> 1200; // Conhecido como "Tetris"
            default -> 0;
        };
        score.set(score.get() + points * level.get());
        updateLevel();
    }

    /*private*/ void updateLevel() {
        level.set((linesCleared.get() / 10) + 1);
    }

    private long getDropInterval() {
        int currentLevel = level.get();
        double seconds;
        if (currentLevel <= 3) {
            seconds = 1.0;
        } else if (currentLevel <= 6) {
            seconds = 0.7;
        } else {
            seconds = 0.4;
        }
        return (long) (seconds * 1_000_000_000L); // em nanossegundos
    }

    /**
     * NOVO MÉTODO: Lógica de "Segurar" (Hold)
     * Troca a peça atual pela peça guardada.
     */
    private void hold() {
        // 1. Verifica a regra "uma vez por peça".
        if (!canHold) {
            return; // Já usou o hold nesta rodada, não faz nada.
        }

        if (heldPiece == null) {
            // 2. Caso 1: Slot de "Hold" está vazio (primeira vez no jogo)
            heldPiece = currentPiece;
            spawnNewPiece(); // Pega uma peça nova do "next"
        } else {
            // 3. Caso 2: Troca a peça atual pela peça guardada
            Tetromino temp = currentPiece;
            currentPiece = heldPiece;
            heldPiece = temp;

            // 4. Reseta a posição da peça que saiu do "Hold" para o topo
            //    (Assumindo que 3,0 é a sua posição inicial de spawn)
            currentPiece.setX(3);
            currentPiece.setY(0);

            // 5. Verifica se a peça que saiu do "Hold" é válida no topo
            if (!board.isValidPosition(currentPiece)) {
                // Se a peça trocada colide imediatamente, é Game Over.
                gameState.set(GameState.GAME_OVER);
                if (!isTestMode) { // <-- PROTEÇÃO ADICIONADA
                    gameLoop.stop();
                }
            }
        }

        // 6. Atualiza a UI para mostrar a nova peça guardada
        heldPieceProperty.set(heldPiece);

        // 7. Trava o "Hold" até que uma nova peça apareça
        canHold = false;
    }

    /**
     * NOVO MÉTODO: Permite que o teste "avise" ao GameEngine
     * que ele não deve tentar iniciar os timers de animação.
     */
    public void setTestMode(boolean isTest) {
        this.isTestMode = isTest;
    }

    // Getters para as propriedades, para a UI poder observá-las
    public Board getBoard() { return board; }
    public Tetromino getCurrentPiece() { return currentPiece; }
    public IntegerProperty scoreProperty() { return score; }
    public IntegerProperty levelProperty() { return level; }
    public IntegerProperty linesClearedProperty() { return linesCleared; }
    public ObjectProperty<GameState> gameStateProperty() { return gameState; }
    public ObjectProperty<Tetromino> nextPieceProperty() { return nextPieceProperty; }
    // Getter para a propriedade da peça guardada (para a UI)
    public ObjectProperty<Tetromino> heldPieceProperty() {return heldPieceProperty;}
    public ObjectProperty<List<Integer>> linesToClearProperty() {return linesToClearProperty;}



}