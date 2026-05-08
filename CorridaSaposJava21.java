import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// 1. Records: Estruturas de dados imutáveis e enxutas (Substitui os atributos privados e getters)
record Resultado(int colocacao, String nome, int pulos) {}

public class CorridaSaposJava21 {
    private static final int DISTANCIA_TOTAL = 50;
    private static final int NUM_SAPOS = 5;

    public static void main(String[] args) throws InterruptedException {
        
        // 2. Text Blocks: Strings multilinha limpas e sem concatenações (+ \n)
        System.out.println("""
        ==================================================
           🐸 CORRIDA DE SAPOS: EDIÇÃO JAVA 21 🐸
           [Powered by Virtual Threads & Project Loom]
        ==================================================
        """);

        // 3. Sequenced Collections (Java 21): Coleções com ordem de inserção garantida (First/Last)
        SequencedCollection<Resultado> podio = new ConcurrentLinkedDeque<>();
        
        // Utilitários modernos de concorrência (sem necessidade de synchronized)
        var colocacao = new AtomicInteger(1);
        var largada = new CountDownLatch(1);
        var chegada = new CountDownLatch(NUM_SAPOS);

        // 4. Virtual Threads (Java 21): Try-with-resources com Executor de Threads Virtuais
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            for (int i = 1; i <= NUM_SAPOS; i++) {
                final int id = i; 
                // Submete a tarefa concorrente leve à JVM
                executor.submit(() -> correr(id, largada, chegada, podio, colocacao));
            }

            System.out.println("Alinhando sapos no terminal (Threads Virtuais criadas)...");
            Thread.sleep(1000);
            System.out.println("LARGADA!\n");
            
            largada.countDown(); // Libera todos os sapos exatamente ao mesmo tempo
            chegada.await();     // A Thread principal (main) aguarda todos terminarem
        }

        System.out.println("""
        
        ==================================================
                       🏆 PÓDIO FINAL 🏆
        ==================================================
        """);

        // 5. var type inference & Pattern Matching for Switch (Java 21)
        for (var res : podio) {
            // O Switch agora retorna valores diretamente e permite usar a "Arrow syntax" (->)
            String medalha = switch (res.colocacao()) {
                case 1 -> "🥇 Ouro";
                case 2 -> "🥈 Prata";
                case 3 -> "🥉 Bronze";
                default -> "🎗️ Participação";
            };
            
            System.out.printf("%s -> %s (Terminou em %d pulos)%n", medalha, res.nome(), res.pulos());
        }
    }

    // Método executado pelas Virtual Threads
    private static void correr(int id, CountDownLatch largada, CountDownLatch chegada, 
                               SequencedCollection<Resultado> podio, AtomicInteger colocacao) {
        try {
            largada.await(); // Aguarda o tiro de largada sincronizado
            
            int distancia = 0;
            int pulos = 0;
            var random = ThreadLocalRandom.current();
            
            while (distancia < DISTANCIA_TOTAL) {
                int pulo = random.nextInt(0, 11);
                distancia += pulo;
                pulos++;
                
                System.out.printf("Sapo %d saltou %d m. (Total: %d/%d)%n", 
                                  id, pulo, Math.min(distancia, DISTANCIA_TOTAL), DISTANCIA_TOTAL);
                
                // Pausa aleatória entre 100 e 500ms
                Thread.sleep(random.nextInt(100, 500));
            }

            // Garante atomicidade ao pegar a colocação
            int minhaColocacao = colocacao.getAndIncrement();
            
            // 6. Uso do método addLast() nativo das Sequenced Collections
            podio.addLast(new Resultado(minhaColocacao, "Sapo " + id, pulos));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            chegada.countDown(); // Avisa ao semáforo global que este sapo terminou
        }
    }
}