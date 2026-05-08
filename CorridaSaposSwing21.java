import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.SequencedCollection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Record imutável (Java 14+)
record Resultado(int colocacao, String nome, int pulos) {}

public class CorridaSaposSwing21 extends JFrame {
    
    private static final int LARGURA_PISTA = 600;
    private static final int DISTANCIA_TOTAL = LARGURA_PISTA - 50;
    private static final int NUM_SAPOS = 5;

    private final Pista pista;
    private final JButton btnIniciar;
    
    // Arrays para manter o estado visual
    private final int[] posicoesX = new int[NUM_SAPOS];
    private final boolean[] terminou = new boolean[NUM_SAPOS];
    
    // Variável para armazenar a imagem carregada na memória
    private Image imagemSapo;

    public CorridaSaposSwing21() {
        super("Corrida de Sapos: Java 21 (Com Imagens)");
        setSize(LARGURA_PISTA + 50, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Carregamento do Asset (Imagem) na inicialização
        carregarImagem();

        pista = new Pista();
        add(pista, BorderLayout.CENTER);

        btnIniciar = new JButton("Iniciar Corrida!");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(btnIniciar, BorderLayout.SOUTH);

        btnIniciar.addActionListener(e -> iniciarCorrida());
    }

    private void carregarImagem() {
        try {
            // Tenta ler o arquivo sapo.png que deve estar na mesma pasta do projeto
            imagemSapo = ImageIO.read(new File("sapo.png"));
        } catch (IOException e) {
            System.err.println("Aviso de Sistema: Imagem 'sapo.png' não encontrada.");
            System.err.println("O sistema fará o fallback para o modo de desenho geométrico (Elipses).");
            imagemSapo = null;
        }
    }

    private void iniciarCorrida() {
        btnIniciar.setEnabled(false);
        Arrays.fill(posicoesX, 20);
        Arrays.fill(terminou, false);
        pista.repaint();

        Thread.startVirtualThread(this::orquestrarCorrida);
    }

    private void orquestrarCorrida() {
        SequencedCollection<Resultado> podio = new ConcurrentLinkedDeque<>();
        var colocacao = new AtomicInteger(1);
        var largada = new CountDownLatch(1);
        var chegada = new CountDownLatch(NUM_SAPOS);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < NUM_SAPOS; i++) {
                final int id = i;
                executor.submit(() -> correr(id, largada, chegada, podio, colocacao));
            }

            Thread.sleep(500);
            largada.countDown(); 
            chegada.await();     
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SwingUtilities.invokeLater(() -> mostrarPodio(podio));
    }

    private void correr(int id, CountDownLatch largada, CountDownLatch chegada,
                        SequencedCollection<Resultado> podio, AtomicInteger colocacao) {
        try {
            largada.await(); 
            
            int pulos = 0;
            var random = ThreadLocalRandom.current();

            while (posicoesX[id] < DISTANCIA_TOTAL) {
                int pulo = random.nextInt(5, 25);
                posicoesX[id] = Math.min(posicoesX[id] + pulo, DISTANCIA_TOTAL);
                pulos++;

                SwingUtilities.invokeLater(pista::repaint);
                Thread.sleep(random.nextInt(30, 150));
            }

            terminou[id] = true;
            SwingUtilities.invokeLater(pista::repaint); 

            int minhaColocacao = colocacao.getAndIncrement();
            podio.addLast(new Resultado(minhaColocacao, "Sapo " + (id + 1), pulos));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            chegada.countDown();
        }
    }

    private void mostrarPodio(SequencedCollection<Resultado> podio) {
        StringBuilder sb = new StringBuilder("🏆 RESULTADO FINAL 🏆\n\n");
        
        for (var res : podio) {
            String medalha = switch (res.colocacao()) {
                case 1 -> "🥇 Ouro";
                case 2 -> "🥈 Prata";
                case 3 -> "🥉 Bronze";
                default -> "🎗️ Participação";
            };
            sb.append(String.format("%s -> %s (Terminou em %d pulos)%n", medalha, res.nome(), res.pulos()));
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Fim da Corrida!", JOptionPane.INFORMATION_MESSAGE);
        btnIniciar.setEnabled(true);
    }

    // Classe Interna: Pista de Corrida
    class Pista extends JPanel {
        private final Color[] cores = {Color.GREEN, Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.CYAN};

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g; 
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Desenha a linha de chegada
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(DISTANCIA_TOTAL + 30, 0, DISTANCIA_TOTAL + 30, getHeight());
            g2d.drawString("CHEGADA", DISTANCIA_TOTAL - 10, 330);

            // Desenha os sapos
            for (int i = 0; i < NUM_SAPOS; i++) {
                int posY = 30 + (i * 60);
                
                // 2. Renderização Condicional: Imagem vs Geometria (Fallback)
                if (imagemSapo != null) {
                    // Desenha a imagem na posição do sapo (Ajustado para 35x35 pixels)
                    g2d.drawImage(imagemSapo, posicoesX[i], posY - 10, 35, 35, this);
                } else {
                    // Fallback de Segurança: Se a imagem não carregar, usa a elipse original
                    g2d.setColor(cores[i]);
                    g2d.fillOval(posicoesX[i], posY, 30, 20);
                }

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString("Sapo " + (i + 1), posicoesX[i] - 10, posY - 15);

                if (terminou[i]) {
                    g2d.drawString("Chegou!", posicoesX[i] + 45, posY + 15);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CorridaSaposSwing21().setVisible(true);
        });
    }
}