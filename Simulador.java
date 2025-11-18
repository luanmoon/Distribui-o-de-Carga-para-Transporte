import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Simulador {

    private static final String ARQUIVO_CATALOGO = "catalogo.csv";
    private static final String ARQUIVO_VENDAS = "vendas.csv";

    public static void main(String[] args) {
        ArvoreAVL arvore = new ArvoreAVL();
        
        Logger.clearLog();
        Logger.log("Iniciando simulação de controle de estoque AVL...");

        carregarCatalogo(arvore, ARQUIVO_CATALOGO);
        Logger.log("Total de produtos na árvore após catálogo: " + arvore.getTotalProdutos());

        processarVendas(arvore, ARQUIVO_VENDAS);
        
        arvore.listarEmOrdem();

        Logger.log("Simulação encerrada.");
        if (arvore.estaVazia()) {
            Logger.log("Árvore AVL vazia. Todos os produtos foram consumidos.");
        } else {
            Logger.log(arvore.getTotalProdutos() + " tipos de produtos restantes no estoque.");
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
                    Logger.log("Formato de número inválido na linha: " + linha);
                }
            }
        } catch (IOException e) {
            Logger.log("Não foi possível ler o arquivo de catálogo: " + e.getMessage());
        }
    }

    private static void processarVendas(ArvoreAVL arvore, String nomeArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha = br.readLine();
            
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                
                try {
                    int codigo = Integer.parseInt(campos[0]);
                    int quantidadeConsumida = Integer.parseInt(campos[1]);

                    Eletrodomestico produto = arvore.buscar(codigo);

                    if (produto == null) {
                        Logger.log("Produto " + codigo + " fora de estoque.");
                    } else {
                        int estoqueAtual = produto.getQuantidadeEstoque();
                        int novoEstoque = estoqueAtual - quantidadeConsumida;
                        
                        produto.setQuantidadeEstoque(novoEstoque);

                        if (novoEstoque <= 0) {
                            Logger.log("Produto " + codigo + " removido - estoque esgotado.");
                            arvore.remover(codigo);
                        } else {
                            Logger.log("Consumo parcial: Produto " + codigo + " (" + produto.getNome() + "), restam " + novoEstoque + " unidades.");
                        }
                    }
                    Logger.log("AVL atualmente com " + arvore.getTotalProdutos() + " produtos.");

                } catch (NumberFormatException e) {
                    Logger.log("Formato de número inválido na linha de venda: " + linha);
                }
            }
        } catch (IOException e) {
            Logger.log("Não foi possível ler o arquivo de vendas: " + e.getMessage());
        }
    }
}