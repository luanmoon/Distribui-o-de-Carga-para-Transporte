public class NoAVL {
    Eletrodomestico dado;
    NoAVL esquerdo;
    NoAVL direito;
    int altura;

    public NoAVL(Eletrodomestico dado) {
        this.dado = dado;
        this.altura = 0; 
        this.esquerdo = null;
        this.direito = null;
    }
}