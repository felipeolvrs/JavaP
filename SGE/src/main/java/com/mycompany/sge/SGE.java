package com.mycompany.sge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class SGE {

    public static void main(String[] args) {
        Estoque estoque = new Estoque();
        Carrinho carrinho = new Carrinho();
        DatabaseConnection dbConnection = new DatabaseConnection("jdbc:postgresql://localhost:5432/java", "postgres", "root");
        dbConnection.connect();

        // Carregar fornecedores e produtos do banco de dados
        estoque.carregarFornecedores(dbConnection);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Escolha uma opção:");
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
                        System.out.println("3. Listar produtos por descrição");
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
                                System.out.println("Opção de filtro inválida.");
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
                    estoque.adicionarFornecedor(dbConnection, novoFornecedor);
                    System.out.println("Fornecedor " + novoFornecedorNome + " adicionado.");
                    break;
                case 4:
                    estoque.listarFornecedores();
                    System.out.print("Digite o ID do fornecedor: ");
                    int fornecedorId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Digite o nome do produto: ");
                    String nome = scanner.nextLine();
                    System.out.print("Digite a descrição do produto: ");
                    String descricao = scanner.nextLine();
                    System.out.print("Digite o preço do produto: ");
                    double preco = scanner.nextDouble();
                    System.out.print("Digite a quantidade do produto: ");
                    int quantidade = scanner.nextInt();
                    estoque.adicionarProduto(dbConnection, fornecedorId, nome, descricao, preco, quantidade);
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
                        System.out.println("Produto não encontrado.");
                    }
                    break;
                case 6:
                    System.out.print("Digite o nome do produto para remover do carrinho: ");
                    String nomeRemover = scanner.nextLine();
                    Produto produtoRemover = estoque.buscarProduto(nomeRemover);
                    if (produtoRemover != null) {
                        carrinho.removerItem(produtoRemover);
                    } else {
                        System.out.println("Produto não encontrado no estoque.");
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
                    dbConnection.close();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    static class DatabaseConnection {
        private String url;
        private String user;
        private String password;
        private Connection connection;
        private Statement statement;

        public DatabaseConnection(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }

        public void connect() {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conexão estabelecida!");
                statement = connection.createStatement();

                // Criação das tabelas, caso não existam
                String sqlFornecedores = "CREATE TABLE IF NOT EXISTS fornecedores (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(255) NOT NULL" +
                        ");";
                statement.executeUpdate(sqlFornecedores);

                String sqlProdutos = "CREATE TABLE IF NOT EXISTS produtos (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(255) NOT NULL, " +
                        "descricao TEXT, " +
                        "preco DECIMAL(10, 2) NOT NULL, " +
                        "quantidade INT NOT NULL, " +
                        "fornecedor_id INT, " +
                        "FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id)" +
                        ");";
                statement.executeUpdate(sqlProdutos);

            } catch (SQLException e) {
                if (e.getErrorCode() == 1045) {
                    System.out.println("Erro de autenticação: " + e.getMessage());
                } else {
                    System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
                }
            }
        }

        public void close() {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("Conexão encerrada.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }

        public Statement getStatement() {
            return statement;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    static class Fornecedor {
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

    static class Produto {
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
            return String.format("Produto: %s, Descrição: %s, Preço: %.2f, Quantidade: %d", nome, descricao, preco, quantidade);
        }
    }

    static class Estoque {
        private final List<Fornecedor> fornecedores;

        public Estoque() {
            fornecedores = new ArrayList<>();
        }

        public void adicionarFornecedor(DatabaseConnection dbConnection, Fornecedor fornecedor) {
            fornecedores.add(fornecedor);

            try {
                PreparedStatement stmt = dbConnection.getConnection().prepareStatement("INSERT INTO fornecedores (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, fornecedor.getNome());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    fornecedor = new Fornecedor(generatedKeys.getInt(1), fornecedor.getNome());
                }
            } catch (SQLException e) {
                System.out.println("Erro ao adicionar fornecedor: " + e.getMessage());
            }
        }

        public void adicionarProduto(DatabaseConnection dbConnection, int fornecedorId, String nome, String descricao, double preco, int quantidade) {
            try {
                PreparedStatement stmt = dbConnection.getConnection().prepareStatement("INSERT INTO produtos (nome, descricao, preco, quantidade, fornecedor_id) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, nome);
                stmt.setString(2, descricao);
                stmt.setDouble(3, preco);
                stmt.setInt(4, quantidade);
                stmt.setInt(5, fornecedorId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Erro ao adicionar produto: " + e.getMessage());
            }
        }

        public void listarFornecedores() {
            fornecedores.sort(Comparator.comparing(Fornecedor::getNome));
            for (Fornecedor fornecedor : fornecedores) {
                System.out.println(fornecedor);
            }
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

        public void carregarFornecedores(DatabaseConnection dbConnection) {
            try {
                Statement stmt = dbConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM fornecedores");
                while (rs.next()) {
                    Fornecedor fornecedor = new Fornecedor(rs.getInt("id"), rs.getString("nome"));
                    fornecedores.add(fornecedor);
                }

                // Carregar os produtos de cada fornecedor
                for (Fornecedor fornecedor : fornecedores) {
                    ResultSet rsProdutos = dbConnection.getConnection().prepareStatement("SELECT * FROM produtos WHERE fornecedor_id = " + fornecedor.getId()).executeQuery();
                    while (rsProdutos.next()) {
                        Produto produto = new Produto(rsProdutos.getString("nome"), rsProdutos.getString("descricao"), rsProdutos.getDouble("preco"), rsProdutos.getInt("quantidade"));
                        fornecedor.adicionarProduto(produto);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erro ao carregar fornecedores e produtos: " + e.getMessage());
            }
        }

        public List<Fornecedor> getFornecedores() {
            return fornecedores;
        }

        // Filtros
        public void listarProdutosPorValor() {
            for (Fornecedor fornecedor : fornecedores) {
                fornecedor.getProdutos().stream()
                        .sorted(Comparator.comparingDouble(Produto::getPreco))
                        .forEach(System.out::println);
            }
        }

        public void listarProdutosPorNome() {
            for (Fornecedor fornecedor : fornecedores) {
                fornecedor.getProdutos().stream()
                        .sorted(Comparator.comparing(Produto::getNome))
                        .forEach(System.out::println);
            }
        }

        public void listarProdutosPorDescricao() {
            for (Fornecedor fornecedor : fornecedores) {
                fornecedor.getProdutos().stream()
                        .sorted(Comparator.comparing(Produto::getDescricao))
                        .forEach(System.out::println);
            }
        }

        public void listarProdutosPorFornecedor(int fornecedorId) {
            Fornecedor fornecedor = fornecedores.stream()
                    .filter(f -> f.getId() == fornecedorId)
                    .findFirst()
                    .orElse(null);
            if (fornecedor != null) {
                fornecedor.getProdutos().forEach(System.out::println);
            } else {
                System.out.println("Fornecedor não encontrado.");
            }
        }
    }

    static class Carrinho {
        private final List<Produto> itens;

        public Carrinho() {
            this.itens = new ArrayList<>();
        }

        public void adicionarItem(Produto produto, int quantidade) {
            produto.reduzirQuantidade(quantidade);
            for (int i = 0; i < quantidade; i++) {
                itens.add(produto);
            }
        }

        public void removerItem(Produto produto) {
            if (itens.contains(produto)) {
                itens.remove(produto);
                produto.reduzirQuantidade(1);
            }
        }

        public void listarItens() {
            for (Produto produto : itens) {
                System.out.println(produto);
            }
        }

        public double calcularTotal() {
            double total = 0.0;
            for (Produto produto : itens) {
                total += produto.getPreco();
            }
            return total;
        }
    }
}
