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

public class CorridaSaposSequencial extends Frame {

    private static final int LARGURA_PISTA = 600;
    private static final int NUM_SAPOS = 5;
    
    private Pista pista;
    private Sapo[] sapos;
    private int colocacaoAtual;

    public CorridaSaposSequencial() {
        super("Corrida Sequencial (Sem Threads) - AWT (Java 5)");
        setSize(LARGURA_PISTA + 100, 400);
        setLayout(new BorderLayout());

        // 1. Instancia a pista
        pista = new Pista();
        add(pista, BorderLayout.CENTER);

        // 2. Instancia os sapos
        sapos = new Sapo[NUM_SAPOS];
        Color[] cores = {Color.GREEN, Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.CYAN};
        
        for (int i = 0; i < NUM_SAPOS; i++) {
            sapos[i] = new Sapo(i + 1, cores[i]);
        }

        // 3. Botão para iniciar
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

    // Método Sequencial: Um laço único controla todos os sapos
    private void iniciarCorrida() {
        colocacaoAtual = 1;
        
        // Reseta todos os sapos
        for (int i = 0; i < NUM_SAPOS; i++) {
            sapos[i].resetar();
        }

        boolean corridaTerminou = false;

        // O "Game Loop" Sequencial
        while (!corridaTerminou) {
            corridaTerminou = true; // Assume que terminou, até provar o contrário

            // Move um sapo de cada vez, na ordem (1, 2, 3, 4, 5)
            for (int i = 0; i < NUM_SAPOS; i++) {
                if (!sapos[i].isTerminou()) {
                    corridaTerminou = false; // Se alguém ainda corre, a corrida não acabou
                    sapos[i].mover();
                    
                    // Checa se este sapo cruzou a linha agora
                    if (sapos[i].getPosX() >= LARGURA_PISTA) {
                        sapos[i].setColocacao(colocacaoAtual++);
                    }
                }
            }

            // Força a atualização gráfica imediatamente (hack necessário por não usar Threads)
            Graphics g = pista.getGraphics();
            if (g != null) {
                pista.update(g);
            }

            // Pausa a execução inteira (o programa todo dorme) para a animação não ser instantânea
            try {
                Thread.sleep(50); // Todos os sapos pausam ao mesmo tempo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("Corrida Finalizada!");
    }

    // Classe Interna: Pista de Corrida
    class Pista extends Canvas {
        private Image buffer;
        private Graphics gBuffer;

        public void update(Graphics g) {
            if (buffer == null) {
                buffer = createImage(getWidth(), getHeight());
                if (buffer == null) return; // Proteção para inicialização
                gBuffer = buffer.getGraphics();
            }
            gBuffer.setColor(Color.WHITE);
            gBuffer.fillRect(0, 0, getWidth(), getHeight());
            paint(gBuffer);
            g.drawImage(buffer, 0, 0, this);
        }

        public void paint(Graphics g) {
            g.setColor(Color.RED);
            g.drawLine(LARGURA_PISTA, 0, LARGURA_PISTA, getHeight());
            g.drawString("CHEGADA", LARGURA_PISTA + 5, 20);

            for (int i = 0; i < NUM_SAPOS; i++) {
                if (sapos[i] != null) {
                    sapos[i].desenhar(g);
                }
            }
        }
    }

    // Classe Interna: O Sapinho (Agora é apenas um objeto de dados, não um Runnable)
    class Sapo {
        private int id;
        private int posX;
        private int posY;
        private Color cor;
        private boolean terminou;
        private int minhaColocacao;

        public Sapo(int id, Color cor) {
            this.id = id;
            this.cor = cor;
            this.posY = id * 50;
            resetar();
        }

        public void resetar() {
            this.posX = 20;
            this.terminou = false;
            this.minhaColocacao = 0;
        }

        public void mover() {
            int pulo = (int) (Math.random() * 30);
            this.posX += pulo;
        }

        public void setColocacao(int colocacao) {
            this.minhaColocacao = colocacao;
            this.terminou = true;
        }

        public boolean isTerminou() {
            return terminou;
        }

        public int getPosX() {
            return posX;
        }

        public void desenhar(Graphics g) {
            g.setColor(cor);
            g.fillOval(posX, posY, 30, 20);
            
            g.setColor(Color.BLACK);
            g.drawString("Sapo " + id, 5, posY + 15);
            
            if (terminou) {
                g.drawString(minhaColocacao + "º Lugar!", Math.min(posX + 35, LARGURA_PISTA + 10), posY + 15);
            }
        }
    }

    public static void main(String[] args) {
        CorridaSaposSequencial corrida = new CorridaSaposSequencial();
        corrida.setVisible(true);
    }
}