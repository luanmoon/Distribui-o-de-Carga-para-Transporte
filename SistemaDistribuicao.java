import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class SistemaDistribuicao {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HeapMaxima heap = new HeapMaxima(16);

        while (true) {
            System.out.println();
            System.out.println("=== Sistema de Distribuição de Cargas ===");
            System.out.println("1 - Carregar cargas de arquivo CSV");
            System.out.println("2 - Inserir nova carga");
            System.out.println("3 - Exibir carga de maior prioridade");
            System.out.println("4 - Remover carga de maior prioridade");
            System.out.println("5 - Exibir todas as cargas ordenadas por prioridade");
            System.out.println("6 - Sair");
            System.out.print("Escolha: ");

            String escolha = sc.nextLine().trim();
            System.out.println();

            switch (escolha) {
                case "1":
                    System.out.print("Caminho do arquivo CSV: ");
                    String caminho = sc.nextLine().trim();
                    carregarCSV(caminho, heap);
                    break;
                case "2":
                    inserirManual(sc, heap);
                    break;
                case "3":
                    Carga topo = heap.consultarTopo();
                    if (topo == null) {
                        System.out.println("Heap vazio.");
                    } else {
                        imprimirCabecalho();
                        System.out.println(topo.toTabela());
                    }
                    break;
                case "4":
                    Carga removida = heap.removerMaximo();
                    if (removida == null) {
                        System.out.println("Heap vazio. Nada a remover.");
                    } else {
                        System.out.println("Removida a carga de maior prioridade:");
                        imprimirCabecalho();
                        System.out.println(removida.toTabela());
                    }
                    break;
                case "5":
                    if (heap.tamanho() == 0) {
                        System.out.println("Nenhuma carga cadastrada.");
                    } else {
                        System.out.println("Cargas ordenadas por prioridade (maior -> menor):");
                        imprimirCabecalho();
                        HeapMaxima copia = heap.copiar(); 
                        while (copia.tamanho() > 0) {
                            Carga c = copia.removerMaximo();
                            System.out.println(c.toTabela());
                        }
                    }
                    break;
                case "6":
                    System.out.println("Saindo...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void imprimirCabecalho() {
        System.out.printf("%-4s| %-4s| %-7s| %-6s| %-9s| %s%n",
                "ID", "Tipo", "Urgencia", "Peso", "Prioridade", "Descricao");
        System.out.println("-----------------------------------------------------------------");
    }

    private static void carregarCSV(String caminho, HeapMaxima heap) {
        int linhas = 0;
        int carregadas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String line;
            while ((line = br.readLine()) != null) {
                linhas++;
                line = line.trim();
                if (line.isEmpty()) continue;

                
                String[] partes = line.split(",", 5);
                if (partes.length < 5) {
                    System.out.println("Linha " + linhas + " inválida (menos de 5 campos): " + line);
                    continue;
                }
                try {
                    int id = Integer.parseInt(partes[0].trim());
                    int tipo = Integer.parseInt(partes[1].trim());
                    int urgencia = Integer.parseInt(partes[2].trim());
                    int peso = Integer.parseInt(partes[3].trim());
                    String descricao = partes[4].trim();

                    
                    if (urgencia < 1 || urgencia > 3) {
                        System.out.println("Linha " + linhas + " ignorada: urgência fora do intervalo 1-3.");
                        continue;
                    }
                    if (peso < 0) {
                        System.out.println("Linha " + linhas + " ignorada: peso negativo.");
                        continue;
                    }
                    if (id < 0) {
                        System.out.println("Linha " + linhas + " ignorada: id inválido.");
                        continue;
                    }
                    

                    Carga c = new Carga(id, tipo, urgencia, peso, descricao);
                    heap.inserir(c);
                    carregadas++;
                } catch (NumberFormatException ex) {
                    System.out.println("Linha " + linhas + " ignorada (formato numérico inválido): " + line);
                }
            }
            System.out.println("Processamento do CSV finalizado. Linhas processadas: " + linhas + ", carregadas: " + carregadas);
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    private static void inserirManual(Scanner sc, HeapMaxima heap) {
        try {
            System.out.print("ID (inteiro): ");
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Tipo (inteiro): ");
            int tipo = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Urgência (1-baixa,2-média,3-alta): ");
            int urgencia = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Peso (kg, inteiro): ");
            int peso = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Descrição: ");
            String descricao = sc.nextLine().trim();

            if (urgencia < 1 || urgencia > 3) {
                System.out.println("Urgência inválida. Deve ser 1, 2 ou 3.");
                return;
            }
            if (peso < 0) {
                System.out.println("Peso inválido (negativo).");
                return;
            }
            Carga c = new Carga(id, tipo, urgencia, peso, descricao);
            heap.inserir(c);
            System.out.println("Carga inserida com prioridade: " + c.prioridade);
        } catch (NumberFormatException ex) {
            System.out.println("Entrada inválida (esperado inteiro). Operação cancelada.");
        }
    }
}

class Carga {
    int id; 
    int tipo; 
    int urgencia; 
    int peso; 
    String descricao;
    int prioridade; 

    public Carga(int id, int tipo, int urgencia, int peso, String descricao) {
        this.id = id;
        this.tipo = tipo;
        this.urgencia = urgencia;
        this.peso = peso;
        this.descricao = descricao;
        calcularPrioridade();
    }

    public void calcularPrioridade() {
        this.prioridade = (this.urgencia * 10) + (this.peso * 2) + (this.tipo * 5);
    }

    public int comparar(Carga outro) {
        if (this.prioridade != outro.prioridade) return Integer.compare(this.prioridade, outro.prioridade);
        if (this.urgencia != outro.urgencia) return Integer.compare(this.urgencia, outro.urgencia);
        if (this.peso != outro.peso) return Integer.compare(this.peso, outro.peso);
        
        return Integer.compare(-this.id, -outro.id); 
    }

    public String toTabela() {
        return String.format("%-4d| %-4d| %-7d| %-6d| %-9d| %s",
                id, tipo, urgencia, peso, prioridade, descricao);
    }

    @Override
    public String toString() {
        return "Carga{id=" + id + ", tipo=" + tipo + ", urgencia=" + urgencia +
                ", peso=" + peso + ", prioridade=" + prioridade + ", desc=" + descricao + "}";
    }
}
class HeapMaxima {
    private Carga[] heap; 
    private int quantidade;
    private int capacidade;

    public HeapMaxima(int capacidadeInicial) {
        if (capacidadeInicial < 2) capacidadeInicial = 2;
        this.capacidade = capacidadeInicial;
        this.heap = new Carga[this.capacidade + 1]; 
        this.quantidade = 0;
    }

    public void inserir(Carga novaCarga) {
        garantirCapacidade();
        quantidade++;
        heap[quantidade] = novaCarga;
        subir(quantidade);
    }

    public Carga removerMaximo() {
        if (quantidade == 0) return null;
        Carga max = heap[1];
        heap[1] = heap[quantidade];
        heap[quantidade] = null;
        quantidade--;
        if (quantidade > 0) descer(1);
        return max;
    }

    public Carga consultarTopo() {
        return (quantidade == 0) ? null : heap[1];
    }

    public int tamanho() {
        return quantidade;
    }

    private void subir(int i) {
        while (i > 1) {
            int pai = i / 2;
            if (maior(i, pai)) {
                trocar(i, pai);
                i = pai;
            } else break;
        }
    }

    private void descer(int i) {
        while (true) {
            int esq = 2 * i;
            int dir = esq + 1;
            int maior = i;

            if (esq <= quantidade && maior(esq, maior)) maior = esq;
            if (dir <= quantidade && maior(dir, maior)) maior = dir;

            if (maior != i) {
                trocar(i, maior);
                i = maior;
            } else break;
        }
    }

    private void trocar(int i, int j) {
        Carga tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private boolean maior(int i, int j) {
        Carga a = heap[i];
        Carga b = heap[j];
        if (a == null || b == null) return false;
        return a.comparar(b) > 0;
    }

    private void garantirCapacidade() {
        if (quantidade + 1 >= heap.length) {
            int novaCap = capacidade * 2;
            Carga[] novo = new Carga[novaCap + 1];
            for (int k = 1; k <= quantidade; k++) novo[k] = heap[k];
            heap = novo;
            capacidade = novaCap;
        }
    }

    
    public HeapMaxima copiar() {
        HeapMaxima h = new HeapMaxima(Math.max(this.capacidade, 2));
        h.quantidade = this.quantidade;
        for (int i = 1; i <= this.quantidade; i++) {
            Carga c = this.heap[i];
            
            Carga copia = new Carga(c.id, c.tipo, c.urgencia, c.peso, c.descricao);
            h.heap[i] = copia;
        }
        return h;
    }
}
