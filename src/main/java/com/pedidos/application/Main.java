package com.pedidos.application;

import com.pedidos.model.Cliente;
import com.pedidos.model.Produto;
import com.pedidos.repository.ClienteDao;
import com.pedidos.repository.ItemPedidoDao;
import com.pedidos.repository.PedidoDao;
import com.pedidos.repository.ProdutoDao;
import com.pedidos.repository.impl.ClienteDaoImpl;
import com.pedidos.repository.impl.ItemPedidoDaoImpl;
import com.pedidos.repository.impl.PedidoDaoImpl;
import com.pedidos.repository.impl.ProdutoDaoImpl;
import com.pedidos.service.ClienteService;
import com.pedidos.service.PedidoService;
import com.pedidos.service.ProdutoService;
import com.pedidos.util.ConexaoDB;
import com.pedidos.util.FormatadorMenu;
import com.pedidos.util.GerenciadorTransacao;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import java.io.IOException;
import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        java.util.logging.Logger.getLogger("org.hibernate.validator").setLevel(java.util.logging.Level.WARNING);
        Scanner scanner = new Scanner(System.in);
        Terminal terminal = TerminalBuilder.builder().build();

        Connection connection = ConexaoDB.getConnection();

        ProdutoDao produtoDao = new ProdutoDaoImpl(connection);
        ClienteDao clienteDao = new ClienteDaoImpl(connection);
        ItemPedidoDao itemPedidoDao = new ItemPedidoDaoImpl(connection, produtoDao);
        PedidoDao pedidoDao = new PedidoDaoImpl(connection, clienteDao, itemPedidoDao);

        GerenciadorTransacao gerenciadorTransacao = new GerenciadorTransacao(connection);

        ClienteService clienteService = new ClienteService(clienteDao, pedidoDao);
        ProdutoService produtoService = new ProdutoService(produtoDao, itemPedidoDao);
        PedidoService pedidoService = new PedidoService(clienteService, pedidoDao, produtoService, itemPedidoDao, gerenciadorTransacao);

        String opcaoPrincipal;

        do {
            terminal.puts(Capability.clear_screen);
            terminal.flush();

            String cabecalhoPrincipal = "📦   SISTEMA DE PEDIDOS  📦";
            System.out.println(FormatadorMenu.linha(cabecalhoPrincipal));
            System.out.println(cabecalhoPrincipal);
            System.out.println(FormatadorMenu.linha(cabecalhoPrincipal));
            System.out.println("[1] → Cliente:");
            System.out.println("[2] → Produto:");
            System.out.println("[3] → Pedido:");
            System.out.println("[0] → Sair:");
            System.out.println("\n" + FormatadorMenu.linha(cabecalhoPrincipal));
            System.out.print(" → Opção: ");
            opcaoPrincipal = scanner.nextLine();

            switch (opcaoPrincipal) {
                case "1":
                    menuCliente(scanner, terminal, clienteService);
                    break;
                case "2":
                    menuProduto(scanner, terminal, produtoService);
                    break;
                case "3":
                    menuPedido();
                    break;
                case "0":
                    System.out.println("Até logo! 👋");
                    break;
                default:
                    System.out.println("✖ Opção inválida.");
            }
        } while (!opcaoPrincipal.equals("0"));

        ConexaoDB.closeConnection();
    }

    private static void menuCliente(Scanner scanner, Terminal terminal, ClienteService clienteService) {
        String opcaoCliente;

        do {
            terminal.puts(Capability.clear_screen);
            terminal.flush();

            String cabecalhoCliente = "👤 Cliente" + " ".repeat(17);
            System.out.println(FormatadorMenu.linha(cabecalhoCliente));
            System.out.println(cabecalhoCliente);
            System.out.println(FormatadorMenu.linha(cabecalhoCliente));
            System.out.println("[1] → Cadastrar:");
            System.out.println("[2] → Buscar por Id:");
            System.out.println("[3] → Buscar por Email:");
            System.out.println("[4] → Atualizar:");
            System.out.println("[5] → Deletar:");
            System.out.println("[0] → Voltar:");
            System.out.println("\n" + FormatadorMenu.linha(cabecalhoCliente));
            System.out.print(" → Opção: ");
            opcaoCliente = scanner.nextLine();

            try {
                switch (opcaoCliente) {
                    case "1":
                        System.out.print("Nome → ");
                        String nome = scanner.nextLine();
                        System.out.print("Email → ");
                        String email = scanner.nextLine();
                        System.out.print("Telefone → ");
                        String telefone = scanner.nextLine();
                        Cliente cliente = new Cliente(nome, email, telefone);
                        Cliente clienteCadastrado = clienteService.cadastrar(cliente);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(clienteCadastrado);
                        System.out.println("✔ Cliente cadastrado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "2":
                        System.out.print("Id → ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Cliente clienteEncontrado = clienteService.buscarPorId(id);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(clienteEncontrado);
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "3":
                        System.out.print("Email → ");
                        String buscarEmail = scanner.nextLine();
                        Cliente clientePorEmail = clienteService.buscarPorEmail(buscarEmail);
                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(clientePorEmail);
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "4":
                        System.out.print("Id → ");
                        int idAtualizar = Integer.parseInt(scanner.nextLine());
                        cliente = clienteService.buscarPorId(idAtualizar);

                        System.out.println("[1] → Nome");
                        System.out.println("[2] → Email");
                        System.out.println("[3] → Telefone");
                        System.out.print("Campo → ");
                        String campo = scanner.nextLine();

                        switch (campo) {
                            case "1":
                                System.out.print("Novo nome → ");
                                cliente.setNome(scanner.nextLine());
                                break;
                            case "2":
                                System.out.print("Novo email → ");
                                cliente.setEmail(scanner.nextLine());
                                break;
                            case "3":
                                System.out.print("Novo telefone → ");
                                cliente.setTelefone(scanner.nextLine());

                                break;
                            default:
                                System.out.println("✖ Campo inválido.");
                        }

                        clienteService.atualizar(cliente);
                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(cliente);
                        System.out.println("✔ Cliente atualizado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "5":
                        System.out.print("Id → ");
                        int idDeletar = Integer.parseInt(scanner.nextLine());
                        clienteService.deletar(idDeletar);
                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println("✔ Cliente deletado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "0":
                        break;

                    default:
                        System.out.println("✖ Opção inválida.");
                }
            } catch (RuntimeException exception) {
                System.out.println("✖ Erro: " + exception.getMessage());
            }
        } while (!opcaoCliente.equals("0"));
    }

    private static void menuProduto(Scanner scanner, Terminal terminal, ProdutoService produtoService) {
        String opcaoProduto;
        do {

            terminal.puts(Capability.clear_screen);
            terminal.flush();

            String cabecalhoProduto = "📦 Produto" + " ".repeat(17);
            System.out.println(FormatadorMenu.linha(cabecalhoProduto));
            System.out.println(cabecalhoProduto);
            System.out.println(FormatadorMenu.linha(cabecalhoProduto));
            System.out.println("[1] → Cadastrar:");
            System.out.println("[2] → Buscar por Id:");
            System.out.println("[3] → Atualizar Nome Ou Preço:");
            System.out.println("[4] → Atualizar Estoque:");
            System.out.println("[5] → Deletar:");
            System.out.println("[0] → Voltar:");
            System.out.println("\n" + FormatadorMenu.linha(cabecalhoProduto));
            System.out.print(" → Opção: ");
            opcaoProduto = scanner.nextLine();
            try {
                switch (opcaoProduto) {
                    case "1":
                        System.out.print("Nome → ");
                        String nomeProduto = scanner.nextLine();
                        System.out.print("Preço → ");
                        double precoProduto = Double.parseDouble(scanner.nextLine());
                        System.out.print("Quantidade em estoque → ");
                        int quantidadeProduto = Integer.parseInt(scanner.nextLine());
                        Produto produto = new Produto(nomeProduto, precoProduto, quantidadeProduto);
                        Produto produtoCadastrado = produtoService.cadastrar(produto);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(produtoCadastrado);
                        System.out.println("✔ Produto cadastrado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "2":
                        System.out.print("Id → ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Produto produtoPorId = produtoService.buscarPorId(id);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(produtoPorId);
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "3":
                        System.out.print("Id → ");
                        int idAtualizarProduto = Integer.parseInt(scanner.nextLine());
                        Produto produtoAtualizar = produtoService.buscarPorId(idAtualizarProduto);

                        System.out.println("[1] → Nome");
                        System.out.println("[2] → Preço");
                        System.out.print("Campo → ");
                        String campoProduto = scanner.nextLine();

                        switch (campoProduto) {
                            case "1":
                                System.out.print("Novo nome → ");
                                produtoAtualizar.setNome(scanner.nextLine());
                                break;
                            case "2":
                                System.out.print("Novo Preço → ");
                                produtoAtualizar.setPreco(Double.parseDouble(scanner.nextLine()));
                                break;
                            default:
                                System.out.println("✖ Campo inválido.");
                        }
                        produtoService.atualizar(produtoAtualizar);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println(produtoAtualizar);
                        System.out.println("✔ Produto atualizado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;
                    case "4":
                        System.out.print("Id → ");
                        int idAtualizarEstoqueProduto = Integer.parseInt(scanner.nextLine());

                        System.out.println("[1] → Adicionar");
                        System.out.println("[2] → Remover");
                        System.out.print("Operação → ");
                        String operacaoEstoque = scanner.nextLine();

                        System.out.print("Quantidade → ");
                        int quantidadeEstoque = Integer.parseInt(scanner.nextLine());

                        Produto produtoEstoqueAtualizado;
                        switch (operacaoEstoque) {
                            case "1":
                                produtoEstoqueAtualizado = produtoService.atualizarEstoque(idAtualizarEstoqueProduto, quantidadeEstoque);
                                break;
                            case "2":
                                produtoEstoqueAtualizado = produtoService.atualizarEstoque(idAtualizarEstoqueProduto, -quantidadeEstoque);
                                break;
                            default:
                                System.out.println("✖ Operação inválida.");
                                produtoEstoqueAtualizado = null;
                        }

                        if (produtoEstoqueAtualizado != null) {
                            terminal.puts(Capability.clear_screen);
                            terminal.flush();
                            System.out.println(produtoEstoqueAtualizado);
                            System.out.println("✔ Estoque atualizado com sucesso.");
                            System.out.println("Pressione Enter para continuar...");
                            scanner.nextLine();
                        }
                        break;

                    case "5":
                        System.out.print("Id → ");
                        int idDeletarProduto = Integer.parseInt(scanner.nextLine());
                        produtoService.deletar(idDeletarProduto);

                        terminal.puts(Capability.clear_screen);
                        terminal.flush();
                        System.out.println("✔ Produto deletado com sucesso.");
                        System.out.println("Pressione Enter para continuar...");
                        scanner.nextLine();
                        break;

                    case "0":
                        break;
                    default:
                        System.out.println("✖ Opção inválida.");
                }
            } catch (RuntimeException exception) {
                System.out.println("✖ Erro: " + exception.getMessage());
            }
        } while (!opcaoProduto.equals("0"));
    }

    private static void menuPedido(Scanner scanner, Terminal terminal, PedidoService pedidoService) {


    }
}