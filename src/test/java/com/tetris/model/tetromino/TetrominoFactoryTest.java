package com.tetris.model.tetromino; // Pacote correto

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a fábrica de Tetrominós (TetrominoFactory),
 * cobrindo o requisito de geração aleatória de peças.
 */
class TetrominoFactoryTest {

    // --- REQUISITO 5: Teste de geração aleatória de peças ---

    @Test
    void testRandomTetrominoGeneration() {
        // Este é um teste estatístico. Não podemos prever a peça,
        // mas podemos verificar se todas as 7 peças SÃO CRIADAS.

        // 1. Setup: Cria um "mapa" para contar cada tipo de peça
        Map<Class<? extends Tetromino>, Integer> pieceCounts = new HashMap<>();
        pieceCounts.put(IPiece.class, 0);
        pieceCounts.put(JPiece.class, 0);
        pieceCounts.put(LPiece.class, 0);
        pieceCounts.put(OPiece.class, 0);
        pieceCounts.put(SPiece.class, 0);
        pieceCounts.put(TPiece.class, 0);
        pieceCounts.put(ZPiece.class, 0);

        // 2. Ação: Chama a fábrica muitas vezes (ex: 1000)
        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            Tetromino piece = TetrominoFactory.getRandomTetromino();

            // Incrementa o contador para o tipo de peça que foi criado
            Class<? extends Tetromino> pieceClass = piece.getClass();
            pieceCounts.put(pieceClass, pieceCounts.get(pieceClass) + 1);
        }

        // 3. Verificação:
        // Verifica se TODAS as 7 peças foram criadas pelo menos uma vez.
        // Se a aleatoriedade estiver funcionando, em 1000 iterações
        // é estatisticamente (quase) impossível que uma peça não apareça.

        for (Map.Entry<Class<? extends Tetromino>, Integer> entry : pieceCounts.entrySet()) {
            assertTrue(entry.getValue() > 0,
                    "A fábrica falhou em criar qualquer instância de " + entry.getKey().getSimpleName());
        }

        System.out.println("Distribuição de peças em " + iterations + " iterações:");
        System.out.println(pieceCounts);
    }
}