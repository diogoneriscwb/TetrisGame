package com.tetris.view;

import com.tetris.controller.GameEngine;
import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.PieceType; // <-- IMPORT NOVO
import com.tetris.model.tetromino.Tetromino;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow; // <-- IMPORT NOVO (para o "Botão 3D")
import javafx.scene.image.Image; // <-- IMPORT NOVO (para as texturas)
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import com.tetris.model.PieceType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import java.util.*;

/**

 Componente da Interface Gráfica responsável por renderizar o estado visual do jogo.

 (Versão 3 - Refatorada para usar PieceType e Texturas de Gradiente)
 */
public class GamePanel extends Canvas {

    // --- Variáveis de Responsividade ---
    private double currentBlockSize = 30;
    private double offsetX = 0;
    private double offsetY = 0;

    private final GameEngine gameEngine;
    private final GraphicsContext gc;
    private List<Integer> flashingLines;
    private final Random randomGlitch = new Random();

    // --- 1. O NOVO MAPA DE TEXTURAS ---
    private Map<PieceType, Image> textureMap;
    private Image lockedTexture; // Para sua ideia de "peças cinzas"
    private Image ghostTexture; // Para o futuro "Ghost Piece"

    // --- 2. OS EFEITOS DE "BOTÃO 3D" ---
    private final InnerShadow buttonShadowEffect = new InnerShadow(
            7.0, 3.0, 3.0, Color.web("0x00000088") // Sombra escura
    );
    private final InnerShadow buttonHighlightEffect = new InnerShadow(
            5.0, -2.0, -2.0, Color.web("0xFFFFFF55") // Destaque claro
    );

    public GamePanel(GameEngine gameEngine) {
        super(); // Construtor vazio (tamanho é responsivo)

        this.gameEngine = gameEngine;
        this.gc = getGraphicsContext2D();
        setId("game-panel"); // Aplica a borda 3D CSS

        // --- 3. CHAMA O NOVO MÉTODO DE CARREGAMENTO ---
        loadTextures();

        // --- Listeners de Responsividade (iguais) ---
        widthProperty().addListener(obs -> recalculateSizeAndDraw());
        heightProperty().addListener(obs -> recalculateSizeAndDraw());

        // --- Loop de Renderização (igual) ---
        AnimationTimer renderer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };
        renderer.start();

        // --- Listener de Animação (igual) ---
        gameEngine.linesToClearProperty().addListener((obs, oldList, linesToClear) -> {
            if (linesToClear != null && !linesToClear.isEmpty()) {
                playLineClearAnimation(linesToClear);
            }
        });


    }

    /**

     NOVO MÉTODO: Carrega todas as suas 7 imagens de gradiente.
     */
    private void loadTextures() {
        textureMap = new HashMap<>();
        try {
// === ATENÇÃO, ROCKER! ===
// Verifique se os nomes dos seus arquivos .png/.jpeg
// (na pasta 'src/main/resources/assets/')
// batem EXATAMENTE com os nomes abaixo!

            textureMap.put(PieceType.I, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_I.png"))));
            textureMap.put(PieceType.J, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_J.png"))));
            textureMap.put(PieceType.L, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_L.png"))));
            textureMap.put(PieceType.O, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_O.png"))));
            textureMap.put(PieceType.S, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_S.png"))));
            textureMap.put(PieceType.T, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_T.png"))));
            textureMap.put(PieceType.Z, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_Z.png"))));

            // (Opcional, mas recomendado para sua ideia de "peças cinzas")
            lockedTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/bloco_cinza.png")));

            // (Opcional, para o "Ghost Piece" - um bloco branco transparente)
            // ghostTexture = new Image(getClass().getResourceAsStream("/assets/bloco_fantasma.png"));


        } catch (Exception e) {
// --- CÓDIGO DE LOG ROBUSTO (CORRIGIDO) ---
            System.err.println("[ERRO FATAL][GamePanel] Falha ao carregar texturas das peças!");
            System.err.println("Verifique se os 7+ arquivos de gradiente estão em 'src/main/resources/assets/'");
// Imprime apenas a mensagem de erro limpa, sem o stack trace
            System.err.println("Causa: " + e.getClass().getSimpleName() + " - " + e.getMessage());
// e.printStackTrace(); // <-- REMOVIDO!
        }
    }

    /**

     NOVO MÉTODO: O "coração" da responsividade.

     (Lógica 100% igual à que já tínhamos)
     */
    // ... (no seu GamePanel.java) ...

    /**
     * MÉTODO "CORAÇÃO" DA RESPONSIVIDADE (VERSÃO CORRIGIDA)
     * Chamado pelos Listeners quando a janela muda de tamanho.
     */
    private void recalculateSizeAndDraw() {
        double h = getHeight();
        double w = getWidth();

        // 1. Se a janela ainda não tem tamanho, não faz nada.
        if (h == 0 || w == 0) {
            return;
        }

        // 2. Calcula o tamanho do bloco para PREENCHER A ALTURA
        currentBlockSize = h / Board.HEIGHT;

        // 3. O "vão" vertical (offsetY) é 0 (alinhado ao topo do seu espaço)
        offsetY = 0;

        // 4. O "vão" horizontal (offsetX) é 0 (alinhado à esquerda)
        offsetX = 0;

        // 5. ATIVA A FLAG: O layout está pronto!
        //    (Isso "quebra" o loop infinito e avisa o 'draw()')
        layoutCalculado = true;
    }


    /**
     * MÉTODO 'draw()' (VERSÃO CORRIGIDA)
     * Agora usa a flag 'layoutCalculado' para evitar o "pulo".
     */
    private void draw() {
        // 1. Limpa o canvas INTEIRO (para o fundo do root-pane aparecer)
        gc.clearRect(0, 0, getWidth(), getHeight());

        // 2. A CORREÇÃO DO "PULO" (e do loop infinito)
        // Se os listeners ainda não rodaram (no 'recalculateSizeAndDraw')
        // e não ativaram a flag 'layoutCalculado', NÃO DESENHE NADA.
        if (!layoutCalculado) {
            return;
        }

        // 3. Calcula o tamanho real (dinâmico) do tabuleiro
        double boardWidth = currentBlockSize * Board.WIDTH;
        double boardHeight = currentBlockSize * Board.HEIGHT;

        // 4. Desenha o fundo do tabuleiro (PRETO SÓLIDO)
        gc.setFill(Color.BLACK);
        // Desenha o fundo APENAS na área do jogo, na posição (0, 0) do seu espaço
        gc.fillRect(offsetX, offsetY, boardWidth, boardHeight);

        // 5. Desenha o grid (peças já fixadas)
        Board board = gameEngine.getBoard();
        PieceType[][] grid = board.getGrid(); // (Já deve estar refatorado)
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (grid[y][x] != null) {
                    drawBlock(x, y, grid[y][x]);
                }
            }
        }

        GameState state = gameEngine.gameStateProperty().get();

        // 6. Desenha a peça caindo
        Tetromino currentPiece = gameEngine.getCurrentPiece();
        if (state == GameState.PLAYING && currentPiece != null) {
            drawTetromino(currentPiece);
        }

        // 7. Desenha a animação de "flash" (Laser Vermelho)
        if (state == GameState.LINE_CLEARING && flashingLines != null) {
            drawFlashingLines(flashingLines);
        }

        // 8. Desenha sobreposições de estado
        if (state == GameState.PAUSED) {
            drawOverlay("PAUSED");
        } else if (state == GameState.GAME_OVER) {
            drawOverlay("GAME OVER");
        }
    }

    /**

     MÉTODO ATUALIZADO: Agora usa PieceType
     */
    private void drawTetromino(Tetromino piece) {
        int[][] shape = piece.getShape();

// --- MUDANÇA CENTRAL ---
        PieceType type = piece.getType(); // <-- Pega o TIPO, não a Cor

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
// Passa o TIPO para o drawBlock
                    drawBlock(piece.getX() + x, piece.getY() + y, type);
                }
            }
        }
    }

    /**

     MÉTODO ATUALIZADO: O "CORAÇÃO DA SUA IDEIA"

     Desenha o bloco com textura de gradiente + efeito 3D (botão).
     */
    private void drawBlock(int x, int y, PieceType type) {
        if (type == null) return; // Bloco vazio

// 1. Pega a textura correta do Mapa
        Image texture = textureMap.get(type);

// 2. Implementa sua ideia de "peças cinzas travadas"
        if (type == PieceType.LOCKED) {
            texture = lockedTexture;
        }
// (Futuro) if (type == PieceType.GHOST) { texture = ghostTexture; }

        double px = offsetX + (x * currentBlockSize);
        double py = offsetY + (y * currentBlockSize);

        if (texture != null) {
// 3. Desenha a IMAGEM do gradiente (em vez de 'fillRect')
            gc.drawImage(texture, px, py, currentBlockSize, currentBlockSize);
        } else {
// Plano B (se a imagem falhar em carregar)
            gc.setFill(Color.MAGENTA); // Cor de erro óbvia
            gc.fillRect(px, py, currentBlockSize, currentBlockSize);
        }

// --- 4. A MÁGICA DO "BOTÃO 3D" ---
        gc.save();

// Aplica o efeito de SOMBRA (canto inferior direito)
        gc.setEffect(buttonShadowEffect);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(px, py, currentBlockSize, currentBlockSize);

// Aplica o efeito de DESTAQUE (canto superior esquerdo)
        gc.setEffect(buttonHighlightEffect);
        gc.fillRect(px, py, currentBlockSize, currentBlockSize);

        gc.restore(); // Limpa os efeitos

// 5. Desenha o contorno preto final
        gc.setStroke(Color.web("0x000000AA")); // Preto transparente
        gc.strokeRect(px, py, currentBlockSize, currentBlockSize);
    }

    /**

     MÉTODO drawOverlay() (sem mudanças, já estava perfeito)
     */
    private void drawOverlay(String text) {
        double boardWidth = currentBlockSize * Board.WIDTH;
        double boardHeight = currentBlockSize * Board.HEIGHT;
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(offsetX, offsetY, boardWidth, boardHeight);

        DropShadow glow = new DropShadow();
        glow.setBlurType(BlurType.GAUSSIAN);
        gc.setTextAlign(TextAlignment.CENTER);

        if (text.equals("GAME OVER")) {
            Color alertColor = Color.web("#FF1111");
            glow.setColor(alertColor);
            glow.setRadius(35);
            glow.setSpread(0.6);
            gc.setEffect(glow);
            gc.setFill(alertColor);
            gc.setFont(Font.font("Digital-7", currentBlockSize * 2.5));

            if (randomGlitch.nextDouble() > 0.20) {
                gc.fillText(text, getWidth() / 2, getHeight() / 2);
            }
            if (randomGlitch.nextDouble() > 0.85) {
                gc.setEffect(null);
                gc.setFill(Color.CYAN.deriveColor(1, 1, 1, 0.5));
                double glitchX = (getWidth() / 2) + randomGlitch.nextInt(-10, 11);
                double glitchY = (getHeight() / 2) + randomGlitch.nextInt(-5, 6);
                gc.fillText(text, glitchX, glitchY);
            }


        } else {
            Color hudColor = Color.web("#00BFFF");
            glow.setColor(hudColor);
            glow.setRadius(15);
            glow.setSpread(0.5);
            gc.setEffect(glow);
            gc.setFill(hudColor);
            gc.setFont(Font.font("Orbitron", currentBlockSize * 2));
            gc.fillText(text, getWidth() / 2, getHeight() / 2);
        }
        gc.setEffect(null);
    }

    /**

     MÉTODO playLineClearAnimation() (sem mudanças, já estava perfeito)
     */
    private void playLineClearAnimation(List<Integer> linesToClear) {
        this.flashingLines = linesToClear;
        PauseTransition flashAnimation = new PauseTransition(Duration.millis(500));
        flashAnimation.setOnFinished(event -> {
            this.flashingLines = null;
            gameEngine.onAnimationFinished();
        });
        flashAnimation.play();
    }

    /**

     MÉTODO drawFlashingLines() (sem mudanças, já estava perfeito)
     */
    private void drawFlashingLines(List<Integer> lines) {
        double boardWidth = currentBlockSize * Board.WIDTH;
        Color laserColor = Color.web("#FF1111");
        gc.save();
        DropShadow glow = new DropShadow();
        glow.setColor(laserColor);
        glow.setRadius(25);
        glow.setSpread(0.5);
        glow.setBlurType(BlurType.GAUSSIAN);
        gc.setEffect(glow);
        gc.setFill(laserColor);
        for (int y : lines) {
            gc.fillRect(offsetX, (y * currentBlockSize) + offsetY, boardWidth, currentBlockSize);
        }
        gc.restore();
    }

    // Flag para o "pulo" do primeiro frame
    private boolean layoutCalculado = false;

    // Sobrescrevemos o método 'isResizable' para ajudar no layout
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return height * ( (double)Board.WIDTH / Board.HEIGHT );
    }

    @Override
    public double prefHeight(double width) {
        return width * ( (double)Board.HEIGHT / Board.WIDTH );
    }
}