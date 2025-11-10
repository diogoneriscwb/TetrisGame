package com.tetris.model.tetromino;

// import javafx.scene.paint.Color; // <-- IMPORT REMOVIDO
import com.tetris.model.PieceType; // <-- IMPORT NOVO
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**

 Testa a classe LPiece (versão 2 - com estados de rotação).

 (Refatorado para PieceType)
 */
class LPieceTest {

    private LPiece piece;

    // --- (Definições das 4 formas da LPiece) ---
    private final int[][] shapeState0 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0}
    };
    private final int[][] shapeState1 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0}
    };
    private final int[][] shapeState2 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0}
    };
    private final int[][] shapeState3 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
    };
// ------------------------------------------

    @BeforeEach
    void setUp() {
// Cria uma nova LPiece fresca antes de cada teste
        piece = new LPiece();
    }

    @Test
    void testConstructorAndInitialState() {
// 1. Testa o TIPO (em vez da cor)
        assertEquals(PieceType.L, piece.getType(), "O tipo da LPiece deveria ser L.");

        // 2. Testa a forma inicial (Estado 0)
        assertShapeEquals(shapeState0, piece.getShape(), "Forma inicial (Estado 0) está incorreta.");


    }

    @Test
    void testSingleRotation() {
// 1. Ação
        piece.rotate(); // Rotaciona de 0 para 1

        // 2. Verificação
        assertShapeEquals(shapeState1, piece.getShape(), "Forma após 1 rotação (Estado 1) está incorreta.");


    }

    @Test
    void testFullRotationCycle() {
// 1. Ação
        piece.rotate(); // Estado 1
        assertShapeEquals(shapeState1, piece.getShape(), "Falha ao rotacionar para Estado 1.");

        piece.rotate(); // Estado 2
        assertShapeEquals(shapeState2, piece.getShape(), "Falha ao rotacionar para Estado 2.");

        piece.rotate(); // Estado 3
        assertShapeEquals(shapeState3, piece.getShape(), "Falha ao rotacionar para Estado 3.");

        piece.rotate(); // Volta ao Estado 0

        // 2. Verificação
        assertShapeEquals(shapeState0, piece.getShape(), "Falha ao retornar ao Estado 0 após 4 rotações.");


    }

    @Test
    void testGetNextRotationShape() {
// 1. Pega a forma "futura"
        int[][] nextShape = piece.getNextRotationShape();

        // 2. Verifica se a forma futura (Estado 1) está correta
        assertShapeEquals(shapeState1, nextShape, "getNextRotationShape() não retornou o Estado 1.");

        // 3. VERIFICAÇÃO MAIS IMPORTANTE:
        // Confirma que a peça NÃO MUDOU de estado, ela só "olhou" o futuro.
        assertShapeEquals(shapeState0, piece.getShape(), "A peça mudou de estado, mas deveria apenas ter 'olhado' a próxima forma.");


    }

    /**

     Método auxiliar (helper) para comparar duas matrizes 2D.
     */
    private void assertShapeEquals(int[][] expected, int[][] actual, String message) {
        assertEquals(expected.length, actual.length, message + " (diferença no número de linhas)");
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], message + " (diferença na linha " + i + ")");
        }
    }
}