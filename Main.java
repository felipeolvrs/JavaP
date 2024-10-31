import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Fornecedor {
    private final int id;
    private final String nome;
    private final List<Produto> produtos;

    public Fornecedor(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.produtos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    @Override
    public String toString() {
        return "Fornecedor{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}

class Produto {
    private final String nome;
    private final String descricao;
    private final double preco;
    private int quantidade;

    public Produto(String nome, String descricao, double preco, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void reduzirQuantidade(int quantidade) {
        this.quantidade -= quantidade;
    }

    @Override
    public String toString() {
        return String.format("Produto: %s, Descri��o: %s, Pre�o: %.2f, Quantidade: %d", nome, descricao, preco, quantidade);
    }
}

class Estoque {
    private final List<Fornecedor> fornecedores;

    public Estoque() {
        fornecedores = new ArrayList<>();
    }

    public void adicionarFornecedor(Fornecedor fornecedor) {
        fornecedores.add(fornecedor);
    }

    public List<Fornecedor> getFornecedores() {
        return fornecedores;
    }

    public void listarFornecedores() {
        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado.");
        } else {
            System.out.println("Fornecedores:");
            for (Fornecedor fornecedor : fornecedores) {
                System.out.println(fornecedor);
            }
        }
    }

    public void listarProdutosPorValor() {
        fornecedores.stream()
            .flatMap(f -> f.getProdutos().stream())
            .sorted(Comparator.comparing(Produto::getPreco))
            .forEach(System.out::println);
    }

    public void listarProdutosPorNome() {
        fornecedores.stream()
            .flatMap(f -> f.getProdutos().stream())
            .sorted(Comparator.comparing(Produto::getNome))
            .forEach(System.out::println);
    }

    public void listarProdutosPorDescricao() {
        fornecedores.stream()
            .flatMap(f -> f.getProdutos().stream())
            .sorted(Comparator.comparing(Produto::getDescricao))
            .forEach(System.out::println);
    }

    public Produto buscarProduto(String nome) {
        for (Fornecedor fornecedor : fornecedores) {
            for (Produto produto : fornecedor.getProdutos()) {
                if (produto.getNome().equalsIgnoreCase(nome)) {
                    return produto;
                }
            }
        }
        return null;
    }

    public void adicionarProduto(int fornecedorId, String nome, String descricao, double preco, int quantidade) {
        Fornecedor fornecedor = fornecedores.stream()
                .filter(f -> f.getId() == fornecedorId)
                .findFirst()
                .orElse(null);
        
        if (fornecedor != null) {
            Produto novoProduto = new Produto(nome, descricao, preco, quantidade);
            fornecedor.adicionarProduto(novoProduto);
            System.out.println("Produto " + nome + " adicionado ao fornecedor " + fornecedor.getNome() + ".");
        } else {
            System.out.println("Fornecedor n�o encontrado.");
        }
    }

    public void listarProdutosPorFornecedor(int fornecedorId) {
        Fornecedor fornecedor = fornecedores.stream()
                .filter(f -> f.getId() == fornecedorId)
                .findFirst()
                .orElse(null);
        if (fornecedor != null) {
            System.out.println("Produtos do fornecedor: " + fornecedor.getNome());
            fornecedor.getProdutos().forEach(System.out::println);
        } else {
            System.out.println("Fornecedor n�o encontrado.");
        }
    }

    public Produto buscarProdutoEmOutroFornecedor(String nome, int fornecedorId) {
        for (Fornecedor fornecedor : fornecedores) {
            if (fornecedor.getId() != fornecedorId) {
                for (Produto produto : fornecedor.getProdutos()) {
                    if (produto.getNome().equalsIgnoreCase(nome)) {
                        return produto;
                    }
                }
            }
        }
        return null;
    }
}

class ItemCarrinho {
    private final Produto produto;
    private final int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double calcularTotal() {
        return produto.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("%s (Quantidade: %d) - Total: %.2f", produto.getNome(), quantidade, calcularTotal());
    }
}

class Carrinho {
    private final List<ItemCarrinho> itens;

    public Carrinho() {
        itens = new ArrayList<>();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (produto.getQuantidade() >= quantidade) {
            produto.reduzirQuantidade(quantidade);
            itens.add(new ItemCarrinho(produto, quantidade));
            System.out.println(quantidade + " unidade(s) de " + produto.getNome() + " adicionado(s) ao carrinho.");
        } else {
            System.out.println("Quantidade solicitada n�o dispon�vel.");
        }
    }

    public void removerItem(Produto produto) {
        for (ItemCarrinho item : itens) {
            if (item.getProduto().getNome().equalsIgnoreCase(produto.getNome())) {
                itens.remove(item);
                System.out.println(produto.getNome() + " removido do carrinho.");
                return;
            }
        }
        System.out.println(produto.getNome() + " n�o est� no carrinho.");
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemCarrinho item : itens) {
            total += item.calcularTotal();
        }
        return total;
    }

    public void listarItens() {
        if (itens.isEmpty()) {
            System.out.println("O carrinho est� vazio.");
        } else {
            System.out.println("Itens no carrinho:");
            for (ItemCarrinho item : itens) {
                System.out.println(item);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Estoque estoque = new Estoque();
        Carrinho carrinho = new Carrinho();

        Fornecedor fornecedor1 = new Fornecedor(1, "Samsung");
        fornecedor1.adicionarProduto(new Produto("Galaxy S21", "Smartphone", 799.99, 50));
        fornecedor1.adicionarProduto(new Produto("Galaxy Tab", "Tablet", 399.99, 30));
        estoque.adicionarFornecedor(fornecedor1);

        Fornecedor fornecedor2 = new Fornecedor(2, "Apple");
        fornecedor2.adicionarProduto(new Produto("iPhone 13", "Smartphone", 999.99, 40));
        fornecedor2.adicionarProduto(new Produto("MacBook Air", "Laptop", 1199.99, 20));
        estoque.adicionarFornecedor(fornecedor2);

        Fornecedor fornecedor3 = new Fornecedor(3, "Sony");
        fornecedor3.adicionarProduto(new Produto("PlayStation 5", "Console de jogos", 499.99, 25));
        fornecedor3.adicionarProduto(new Produto("WH-1000XM4", "Fone de ouvido", 349.99, 15));
        estoque.adicionarFornecedor(fornecedor3);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Escolha uma op��o:");
            System.out.println("1. Listar fornecedores");
            System.out.println("2. Filtrar produtos");
            System.out.println("3. Adicionar novo fornecedor");
            System.out.println("4. Adicionar produto a um fornecedor");
            System.out.println("5. Adicionar ao carrinho");
            System.out.println("6. Remover do carrinho");
            System.out.println("7. Listar itens do carrinho");
            System.out.println("8. Calcular total do carrinho");
            System.out.println("0. Sair");
            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    estoque.listarFornecedores();
                    break;
                case 2:
                    while (true) {
                        System.out.println("Escolha um filtro:");
                        System.out.println("1. Listar produtos por valor");
                        System.out.println("2. Listar produtos por nome");
                        System.out.println("3. Listar produtos por descri��o");
                        System.out.println("4. Listar produtos de um fornecedor");
                        System.out.println("0. Voltar ao menu principal");
                        int filtroEscolha = scanner.nextInt();
                        scanner.nextLine();

                        switch (filtroEscolha) {
                            case 1:
                                estoque.listarProdutosPorValor();
                                break;
                            case 2:
                                estoque.listarProdutosPorNome();
                                break;
                            case 3:
                                estoque.listarProdutosPorDescricao();
                                break;
                            case 4:
                                estoque.listarFornecedores();
                                System.out.print("Digite o ID do fornecedor: ");
                                int fornecedorId = scanner.nextInt();
                                scanner.nextLine();
                                estoque.listarProdutosPorFornecedor(fornecedorId);
                                break;
                            case 0:
                                break;
                            default:
                                System.out.println("Op��o de filtro inv�lida.");
                        }

                        if (filtroEscolha == 0) {
                            break;
                        }
                    }
                    break;
                case 3:
                    System.out.print("Digite o nome do novo fornecedor: ");
                    String novoFornecedorNome = scanner.nextLine();
                    int novoFornecedorId = estoque.getFornecedores().size() + 1;
                    Fornecedor novoFornecedor = new Fornecedor(novoFornecedorId, novoFornecedorNome);
                    estoque.adicionarFornecedor(novoFornecedor);
                    System.out.println("Fornecedor " + novoFornecedorNome + " adicionado.");
                    break;
                case 4:
                    estoque.listarFornecedores();
                    System.out.print("Digite o ID do fornecedor: ");
                    int fornecedorId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Digite o nome do produto: ");
                    String nome = scanner.nextLine();
                    System.out.print("Digite a descri��o do produto: ");
                    String descricao = scanner.nextLine();
                    System.out.print("Digite o pre�o do produto: ");
                    double preco = scanner.nextDouble();
                    System.out.print("Digite a quantidade do produto: ");
                    int quantidade = scanner.nextInt();
                    estoque.adicionarProduto(fornecedorId, nome, descricao, preco, quantidade);
                    break;
                case 5:
                    System.out.print("Digite o nome do produto para adicionar ao carrinho: ");
                    String nomeCarrinho = scanner.nextLine();
                    Produto produto = estoque.buscarProduto(nomeCarrinho);
                    if (produto != null) {
                        System.out.print("Digite a quantidade: ");
                        int qtd = scanner.nextInt();
                        scanner.nextLine();
                        carrinho.adicionarItem(produto, qtd);
                    } else {
                        System.out.println("Produto n�o encontrado.");
                    }
                    break;
                case 6:
                    System.out.print("Digite o nome do produto para remover do carrinho: ");
                    String nomeRemover = scanner.nextLine();
                    Produto produtoRemover = estoque.buscarProduto(nomeRemover);
                    if (produtoRemover != null) {
                        carrinho.removerItem(produtoRemover);
                    } else {
                        System.out.println("Produto n�o encontrado no estoque.");
                    }
                    break;
                case 7:
                    carrinho.listarItens();
                    break;
                case 8:
                    double total = carrinho.calcularTotal();
                    System.out.printf("Total do carrinho: %.2f\n", total);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Op��o inv�lida.");
            }
        }
    }
}

