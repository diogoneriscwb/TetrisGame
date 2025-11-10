package com.tetris.model.tetromino;

import com.tetris.model.PieceType; // <-- IMPORT NOVO
// import javafx.scene.paint.Color; // <-- IMPORT REMOVIDO
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**

 Testa a classe abstrata Tetromino (versão 2 - com estados de rotação).

 (Refatorado para PieceType)
 */
class TetrominoTest {

    // --- Formas de Teste Simples ---
    private static final int[][] SHAPE_A = new int[][]{{1}}; // Forma "A"
    private static final int[][] SHAPE_B = new int[][]{{2}}; // Forma "B"

    // Matriz de formas (3D) que será passada para o construtor
    private static final int[][][] TEST_SHAPES = new int[][][]{
            SHAPE_A,
            SHAPE_B
    };

    // --- Classe "Stub" para Teste (Atualizada) ---
// Agora passa um PieceType (ex: I) para o construtor da superclasse
    private static class StubTetromino extends Tetromino {
        public StubTetromino() {
// Passa as formas de teste e um tipo qualquer
            super(TEST_SHAPES, PieceType.I); // <-- MUDOU DE Color.TRANSPARENT
        }
    }
// ---------------------------------

    private Tetromino tetromino;

    @BeforeEach
    void setUp() {
// Cria um novo stub de teste
        tetromino = new StubTetromino();
    }

    @Test
    void testConstructorAndInitialState() {
// 1. Testa a posição inicial
        assertEquals(3, tetromino.getX(), "Posição X inicial deve ser 3.");
        assertEquals(0, tetromino.getY(), "Posição Y inicial deve ser 0.");

        // 2. Testa se a forma inicial é a primeira da lista (SHAPE_A)
        assertArrayEquals(SHAPE_A, tetromino.getShape(), "Forma inicial deve ser a SHAPE_A.");

        // 3. NOVO TESTE: Testa se o TIPO está correto
        assertEquals(PieceType.I, tetromino.getType(), "O tipo do stub deveria ser I.");


    }

    @Test
    void testRotateChangesState() {
// 1. Ação
        tetromino.rotate(); // Rotaciona de 0 para 1

        // 2. Verificação
        // Agora, getShape() deve retornar a segunda forma da lista (SHAPE_B)
        assertArrayEquals(SHAPE_B, tetromino.getShape(), "Forma após 1 rotação deve ser SHAPE_B.");


    }

    @Test
    void testRotationCyclesBack() {
// 1. Ação
        tetromino.rotate(); // Estado 1 (SHAPE_B)
        tetromino.rotate(); // Deve voltar ao Estado 0 (SHAPE_A)

        // 2. Verificação
        assertArrayEquals(SHAPE_A, tetromino.getShape(), "Rotação não fez o ciclo de volta para SHAPE_A.");


    }

    @Test
    void testGetNextRotationShape() {
// 1. Ação
        int[][] nextShape = tetromino.getNextRotationShape();

        // 2. Verificação
        assertArrayEquals(SHAPE_B, nextShape, "getNextRotationShape() não retornou SHAPE_B.");

        // 3. Verificação Principal
        assertArrayEquals(SHAPE_A, tetromino.getShape(), "getShape() mudou de estado, mas deveria apenas ter 'olhado'.");


    }

    @Test
    void testSettersAndGetters() {
        tetromino.setX(10);
        tetromino.setY(20);

        assertEquals(10, tetromino.getX(), "setX(10) falhou.");
        assertEquals(20, tetromino.getY(), "setY(20) falhou.");


    }
}