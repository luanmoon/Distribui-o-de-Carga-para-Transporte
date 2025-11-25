public class ArvoreAVL {

    private NoAVL raiz;
    private int totalProdutos;

    public ArvoreAVL() {
        this.raiz = null;
        this.totalProdutos = 0;
    }

    public int getTotalProdutos() {
        return totalProdutos;
    }

    public boolean estaVazia() {
        return this.raiz == null;
    }

    private int altura(NoAVL no) {
        return (no == null) ? -1 : no.altura;
    }

    private void atualizarAltura(NoAVL no) {
        if (no != null) {
            no.altura = 1 + Math.max(altura(no.esquerdo), altura(no.direito));
        }
    }

    private int fatorBalanceamento(NoAVL no) {
        return (no == null) ? 0 : altura(no.esquerdo) - altura(no.direito);
    }

    private NoAVL rotacaoDireita(NoAVL y) {
        NoAVL x = y.esquerdo;
        NoAVL T2 = x.direito;

        x.direito = y;
        y.esquerdo = T2;

        atualizarAltura(y);
        atualizarAltura(x);

        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        NoAVL y = x.direito;
        NoAVL T2 = y.esquerdo;

        y.esquerdo = x;
        x.direito = T2;

        atualizarAltura(x);
        atualizarAltura(y);

        return y;
    }

    private NoAVL rotacaoDuplaEsquerdaDireita(NoAVL z) {
        z.esquerdo = rotacaoEsquerda(z.esquerdo);
        return rotacaoDireita(z);
    }

    private NoAVL rotacaoDuplaDireitaEsquerda(NoAVL z) {
        z.direito = rotacaoDireita(z.direito);
        return rotacaoEsquerda(z);
    }

    public void inserir(Eletrodomestico produto) {
        this.raiz = inserir(this.raiz, produto);
    }

    private NoAVL inserir(NoAVL no, Eletrodomestico produto) {
        if (no == null) {
            Logger.log("Inserindo produto " + produto.getCodigo() + " - " + produto.getNome());
            this.totalProdutos++;
            return new NoAVL(produto);
        }

        if (produto.getCodigo() < no.dado.getCodigo()) {
            no.esquerdo = inserir(no.esquerdo, produto);
        } else if (produto.getCodigo() > no.dado.getCodigo()) {
            no.direito = inserir(no.direito, produto);
        } else {
            Logger.log("AVISO: Produto com código " + produto.getCodigo() + " já existe.");
            return no;
        }

        atualizarAltura(no);

        int fb = fatorBalanceamento(no);

        if (fb > 1 && produto.getCodigo() < no.esquerdo.dado.getCodigo()) {
            return rotacaoDireita(no);
        }

        if (fb < -1 && produto.getCodigo() > no.direito.dado.getCodigo()) {
            return rotacaoEsquerda(no);
        }

        if (fb > 1 && produto.getCodigo() > no.esquerdo.dado.getCodigo()) {
            return rotacaoDuplaEsquerdaDireita(no);
        }

        if (fb < -1 && produto.getCodigo() < no.direito.dado.getCodigo()) {
            return rotacaoDuplaDireitaEsquerda(no);
        }

        return no;
    }

    public void remover(int codigo) {
        this.raiz = remover(this.raiz, codigo);
    }

    private NoAVL remover(NoAVL no, int codigo) {
        if (no == null) {
            return null;
        }

        if (codigo < no.dado.getCodigo()) {
            no.esquerdo = remover(no.esquerdo, codigo);
        } else if (codigo > no.dado.getCodigo()) {
            no.direito = remover(no.direito, codigo);
        } else {
            if (no.esquerdo == null || no.direito == null) {
                this.totalProdutos--;
                NoAVL temp = (no.esquerdo != null) ? no.esquerdo : no.direito;

                if (temp == null) {
                    no = null;
                } else {
                    no = temp;
                }
            } else {
                NoAVL temp = noMinimo(no.direito);
                no.dado = temp.dado;
                no.direito = remover(no.direito, temp.dado.getCodigo());
            }
        }

        if (no == null) {
            return null;
        }

        atualizarAltura(no);

        int fb = fatorBalanceamento(no);

        if (fb > 1 && fatorBalanceamento(no.esquerdo) >= 0) {
            return rotacaoDireita(no);
        }

        if (fb > 1 && fatorBalanceamento(no.esquerdo) < 0) {
            return rotacaoDuplaEsquerdaDireita(no);
        }

        if (fb < -1 && fatorBalanceamento(no.direito) <= 0) {
            return rotacaoEsquerda(no);
        }

        if (fb < -1 && fatorBalanceamento(no.direito) > 0) {
            return rotacaoDuplaDireitaEsquerda(no);
        }

        return no;
    }

    private NoAVL noMinimo(NoAVL no) {
        NoAVL atual = no;
        while (atual.esquerdo != null) {
            atual = atual.esquerdo;
        }
        return atual;
    }

    public Eletrodomestico buscar(int codigo) {
        NoAVL no = buscar(this.raiz, codigo);
        return (no == null) ? null : no.dado;
    }

    private NoAVL buscar(NoAVL no, int codigo) {
        if (no == null) {
            return null;
        }

        if (codigo < no.dado.getCodigo()) {
            return buscar(no.esquerdo, codigo);
        } else if (codigo > no.dado.getCodigo()) {
            return buscar(no.direito, codigo);
        } else {
            return no;
        }
    }

    public void listarEmOrdem() {
        Logger.log("\n--- Catálogo de Produtos Disponíveis (Em Ordem) ---");
        listarEmOrdem(this.raiz);
        Logger.log("--------------------------------------------------\n");
    }

    private void listarEmOrdem(NoAVL no) {
        if (no != null) {
            listarEmOrdem(no.esquerdo);
            Logger.log(no.dado.toString());
            listarEmOrdem(no.direito);
        }
    }
}