import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CorridaSapos extends Frame {

    private static final int LARGURA_PISTA = 600;
    private static final int NUM_SAPOS = 5;
    
    private Pista pista;
    private Sapo[] sapos;
    private int colocacaoAtual;

    public CorridaSapos() {
        super("Corrida de 5 Sapinhos - AWT (Java 5)");
        setSize(LARGURA_PISTA + 100, 400);
        setLayout(new BorderLayout());

        // 1. CORREÇÃO: Configura a pista (Canvas) PRIMEIRO!
        pista = new Pista();
        add(pista, BorderLayout.CENTER);

        // 2. Instancia os sapos DEPOIS da pista existir
        sapos = new Sapo[NUM_SAPOS];
        Color[] cores = {Color.GREEN, Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.CYAN};
        
        for (int i = 0; i < NUM_SAPOS; i++) {
            sapos[i] = new Sapo(i + 1, cores[i]);
        }

        // Botão para iniciar a corrida
        Button btnIniciar = new Button("Iniciar Corrida!");
        add(btnIniciar, BorderLayout.SOUTH);

        // Evento do botão
        btnIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarCorrida();
            }
        });

        // Evento para fechar a janela
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    // Método que dispara as Threads
    private void iniciarCorrida() {
        colocacaoAtual = 1; // Reseta o pódio
        for (int i = 0; i < NUM_SAPOS; i++) {
            sapos[i].resetar();
            // Cria uma nova Thread para o sapo e inicia
            new Thread(sapos[i]).start();
        }
    }

    // Classe Interna: Pista de Corrida (Área de Desenho)
    class Pista extends Canvas {
        private Image buffer;
        private Graphics gBuffer;

        // Sobrescreve o update para implementar Double Buffering (Evita a tela piscando no AWT)
        public void update(Graphics g) {
            if (buffer == null) {
                buffer = createImage(getWidth(), getHeight());
                gBuffer = buffer.getGraphics();
            }
            gBuffer.setColor(Color.WHITE);
            gBuffer.fillRect(0, 0, getWidth(), getHeight());
            paint(gBuffer);
            g.drawImage(buffer, 0, 0, this);
        }

        public void paint(Graphics g) {
            // Desenha a linha de chegada
            g.setColor(Color.RED);
            g.drawLine(LARGURA_PISTA, 0, LARGURA_PISTA, getHeight());
            g.drawString("CHEGADA", LARGURA_PISTA + 5, 20);

            // Desenha cada sapinho
            for (int i = 0; i < NUM_SAPOS; i++) {
                if (sapos[i] != null) {
                    sapos[i].desenhar(g);
                }
            }
        }
    }

    // Classe Interna: O Sapinho que corre (Runnable)
    class Sapo implements Runnable {
        private int id;
        private int posX;
        private int posY;
        private Color cor;
        private boolean terminou;
        private int minhaColocacao;

        public Sapo(int id, Color cor) {
            this.id = id;
            this.cor = cor;
            this.posY = id * 50; // Espaçamento vertical entre os sapos
            resetar();
        }

        public void resetar() {
            this.posX = 20; // Posição inicial de largada
            this.terminou = false;
            this.minhaColocacao = 0;
            pista.repaint();
        }

        public void desenhar(Graphics g) {
            g.setColor(cor);
            // Desenha o corpo do sapo (uma elipse simples)
            g.fillOval(posX, posY, 30, 20);
            
            g.setColor(Color.BLACK);
            g.drawString("Sapo " + id, 5, posY + 15); // Nome do sapo à esquerda
            
            // Se terminou, exibe a colocação dele
            if (terminou) {
                g.drawString(minhaColocacao + "º Lugar!", posX + 35, posY + 15);
            }
        }

        // Lógica de execução da Thread
        public void run() {
            while (posX < LARGURA_PISTA) {
                // Sapo dá um pulo de tamanho aleatório (0 a 30 pixels)
                int pulo = (int) (Math.random() * 30);
                posX += pulo;
                
                // Solicita que a tela seja redesenhada
                pista.repaint();
                
                // Sapo descansa por um tempo aleatório (0 a 200 milissegundos)
                try {
                    Thread.sleep((int) (Math.random() * 200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Bloco sincronizado para garantir que dois sapos não empatem na mesma colocação
            synchronized (CorridaSapos.this) {
                minhaColocacao = colocacaoAtual++;
                terminou = true;
            }
            pista.repaint();
        }
    }

    public static void main(String[] args) {
        CorridaSapos corrida = new CorridaSapos();
        corrida.setVisible(true);
    }
}