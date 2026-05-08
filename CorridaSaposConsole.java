public class CorridaSaposConsole {

    private static final int DISTANCIA_TOTAL = 50; // Distância a ser percorrida
    private static final int NUM_SAPOS = 5;

    public static void main(String[] args) {
        Sapo[] sapos = new Sapo[NUM_SAPOS];
        
        // Instancia os sapos
        for (int i = 0; i < NUM_SAPOS; i++) {
            sapos[i] = new Sapo(i + 1);
        }

        System.out.println("=====================================");
        System.out.println("    INICIO DA CORRIDA SEQUENCIAL     ");
        System.out.println("=====================================\n");

        boolean corridaTerminou = false;
        int colocacaoAtual = 1;

        // O "Game Loop" Sequencial no Terminal
        while (!corridaTerminou) {
            corridaTerminou = true; // Assume que todos terminaram, até provar o contrário

            // Move um sapo de cada vez
            for (int i = 0; i < NUM_SAPOS; i++) {
                if (!sapos[i].isTerminou()) {
                    corridaTerminou = false; // Pelo menos um sapo ainda está correndo
                    sapos[i].pular();
                    
                    // Checa se este sapo cruzou a linha de chegada neste turno
                    if (sapos[i].getDistanciaPercorrida() >= DISTANCIA_TOTAL) {
                        sapos[i].setColocacao(colocacaoAtual++);
                        System.out.println("\n>>> O Sapo " + sapos[i].getId() + " cruzou a linha de chegada em " + sapos[i].getColocacao() + "º LUGAR! <<<\n");
                    }
                }
            }

            // Pausa a execução para conseguirmos ler o terminal (500 milissegundos)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("=====================================");
        System.out.println("           FIM DA CORRIDA!           ");
        System.out.println("=====================================");
    }
}

// Classe que representa o Sapo como uma estrutura de dados e comportamento básico
class Sapo {
    private int id;
    private int distanciaPercorrida;
    private boolean terminou;
    private int colocacao;

    public Sapo(int id) {
        this.id = id;
        this.distanciaPercorrida = 0;
        this.terminou = false;
        this.colocacao = 0;
    }

    public void pular() {
        // Sapo dá um pulo aleatório entre 0 e 10 unidades
        int pulo = (int) (Math.random() * 10);
        this.distanciaPercorrida += pulo;
        
        System.out.println("Sapo " + id + " pulou " + pulo + " unidades. (Distância total: " + this.distanciaPercorrida + "/" + 50 + ")");
    }

    public void setColocacao(int colocacao) {
        this.colocacao = colocacao;
        this.terminou = true;
    }

    public boolean isTerminou() {
        return terminou;
    }

    public int getId() {
        return id;
    }

    public int getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public int getColocacao() {
        return colocacao;
    }
}