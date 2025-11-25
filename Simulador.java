import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulador {

    private static final String ARQUIVO_CATALOGO = "catalogo.csv";
    private static final String ARQUIVO_VENDAS = "vendas.csv";

    private static class DadosVenda {
        int codigo;
        int quantidade;

        public DadosVenda(int codigo, int quantidade) {
            this.codigo = codigo;
            this.quantidade = quantidade;
        }
    }

    public static void main(String[] args) {
        ArvoreAVL arvore = new ArvoreAVL();
        
        Logger.clearLog();
        Logger.log("Iniciando simulação de controle de estoque AVL...");

        carregarCatalogo(arvore, ARQUIVO_CATALOGO);
        Logger.log("Total de produtos na árvore após catálogo: " + arvore.getTotalProdutos());

        executarSimulacao(arvore, ARQUIVO_VENDAS);
        
        if (arvore.estaVazia()) {
            Logger.log("\nSimulação encerrada: árvore AVL vazia. Todos os produtos foram consumidos.");
        } else {
            Logger.log("\nSimulação interrompida. Ainda restam " + arvore.getTotalProdutos() + " tipos de produtos.");
        }
    }

    private static void carregarCatalogo(ArvoreAVL arvore, String nomeArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha = br.readLine();
            
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                try {
                    int codigo = Integer.parseInt(campos[0]);
                    String nome = campos[1];
                    int estoque = Integer.parseInt(campos[2]);
                    double preco = Double.parseDouble(campos[3].replace(",", "."));

                    if (estoque > 0) {
                        Eletrodomestico produto = new Eletrodomestico(codigo, nome, estoque, preco);
                        arvore.inserir(produto);
                    }
                } catch (NumberFormatException e) {
                }
            }
        } catch (IOException e) {
            Logger.log("ERRO: Não foi possível ler o arquivo de catálogo: " + e.getMessage());
        }
    }

    private static void executarSimulacao(ArvoreAVL arvore, String nomeArquivo) {
        List<DadosVenda> listaVendas = carregarListaVendas(nomeArquivo);

        if (listaVendas.isEmpty()) {
            Logger.log("ERRO: Nenhuma venda carregada. A simulação não pode ocorrer.");
            return;
        }

        Random random = new Random();
        int tentativasSemSucesso = 0;
        int maxTentativas = listaVendas.size() * 50;

        while (!arvore.estaVazia()) {
            
            DadosVenda venda = listaVendas.get(random.nextInt(listaVendas.size()));
            
            boolean houveAlteracao = processarVendaIndividual(arvore, venda.codigo, venda.quantidade);

            if (houveAlteracao) {
                tentativasSemSucesso = 0;
            } else {
                tentativasSemSucesso++;
            }

            if (tentativasSemSucesso > maxTentativas) {
                Logger.log("\nALERTA: O arquivo de vendas não cobre os produtos restantes. Encerrando para evitar loop infinito.");
                break;
            }

            try { Thread.sleep(50); } catch (InterruptedException e) {} 
        }
    }

    private static List<DadosVenda> carregarListaVendas(String nomeArquivo) {
        List<DadosVenda> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha = br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                try {
                    int codigo = Integer.parseInt(campos[0]);
                    int quantidade = Integer.parseInt(campos[1]);
                    lista.add(new DadosVenda(codigo, quantidade));
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            Logger.log("ERRO ao ler arquivo de vendas.");
        }
        return lista;
    }

    private static boolean processarVendaIndividual(ArvoreAVL arvore, int codigo, int quantidadeConsumida) {
        Eletrodomestico produto = arvore.buscar(codigo);

        if (produto == null) {
            return false;
        } else {
            int estoqueAtual = produto.getQuantidadeEstoque();
            int novoEstoque = estoqueAtual - quantidadeConsumida;
            
            if (novoEstoque < 0) novoEstoque = 0;

            produto.setQuantidadeEstoque(novoEstoque);

            if (novoEstoque == 0) {
                Logger.log("Produto " + codigo + " removido - estoque esgotado.");
                arvore.remover(codigo);
            } else {
                Logger.log("Consumo parcial: Produto " + codigo + " (" + produto.getNome() + "), restam " + novoEstoque + " unidades.");
            }
            Logger.log("AVL atualmente com " + arvore.getTotalProdutos() + " produtos.");
            return true;
        }
    }
}