public class Eletrodomestico {
    int codigo;
    String nome;
    int quantidadeEstoque;
    double precoUnitario;

    public Eletrodomestico(int codigo, String nome, int quantidadeEstoque, double precoUnitario) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeEstoque = quantidadeEstoque;
        this.precoUnitario = precoUnitario;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto " + codigo + " [" + nome + "] - Estoque: " + quantidadeEstoque + ", Preço: R$" + precoUnitario;
    }
}