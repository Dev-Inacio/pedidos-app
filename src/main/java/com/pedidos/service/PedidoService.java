package com.pedidos.service;

import com.pedidos.enums.StatusPedido;
import com.pedidos.exception.ConexaoException;
import com.pedidos.exception.DadoInvalidoException;
import com.pedidos.exception.PedidoEmAndamentoException;
import com.pedidos.exception.PedidoNaoEncontradoException;
import com.pedidos.exception.PedidoSemItensException;
import com.pedidos.model.Cliente;
import com.pedidos.model.ItemPedido;
import com.pedidos.model.Pedido;
import com.pedidos.model.Produto;
import com.pedidos.repository.ItemPedidoDao;
import com.pedidos.repository.PedidoDao;
import com.pedidos.util.GerenciadorTransacao;
import com.pedidos.util.ValidadorUtil;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PedidoService {

    private final ClienteService clienteService;
    private final PedidoDao pedidoDao;
    private final ProdutoService produtoService;
    private final ItemPedidoDao itemPedidoDao;
    private final GerenciadorTransacao gerenciadorTransacao;

    public Pedido buscarPorId(int pedidoId) {
        Pedido pedido = pedidoDao.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new PedidoNaoEncontradoException("Pedido com id " + pedidoId + " não encontrado");
        }
        return pedido;
    }

    public Pedido criarPedido(int clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Pedido pedido = new Pedido(cliente);
        return pedidoDao.salvar(pedido);
    }

    public Pedido adicionarItem(int pedidoId, int produtoId, int quantidade) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new PedidoEmAndamentoException("Pedido " + pedidoId + " não está aberto");
        }
        Produto produto = produtoService.buscarPorId(produtoId);
        ItemPedido itemPedido = new ItemPedido(produto, quantidade);
        Set<ConstraintViolation<ItemPedido>> violacoes = ValidadorUtil.getValidator().validate(itemPedido);
        if (!violacoes.isEmpty()) {
            throw new DadoInvalidoException("Dados inválidos: " + violacoes.iterator().next().getMessage());
        }

        gerenciadorTransacao.executarEmTransacao(() -> {
            produtoService.atualizarEstoque(produtoId, -quantidade);
            itemPedidoDao.salvar(itemPedido, pedidoId);
        });

        pedido.adicionarItem(itemPedido);
        return pedido;
    }

    public Pedido fecharPedido(int pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);

        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new PedidoEmAndamentoException("Pedido " + pedidoId + " não está aberto");
        }

        if (pedido.getItens().isEmpty()) {
            throw new PedidoSemItensException("Pedido " + pedidoId + " não possui itens");
        }

        pedido.setStatus(StatusPedido.FECHADO);
        return pedidoDao.atualizar(pedido);
    }

    public Pedido cancelarPedido(int pedidoId) {

        Pedido pedido = buscarPorId(pedidoId);

        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new PedidoEmAndamentoException("Pedido " + pedidoId + " não está aberto");
        }

        gerenciadorTransacao.executarEmTransacao(() -> {
            for (ItemPedido item : pedido.getItens()) {
                produtoService.atualizarEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            pedido.setStatus(StatusPedido.CANCELADO);
            pedidoDao.atualizar(pedido);
        });
        return pedido;
    }

    public List<Pedido> listarPorCliente(int clienteId) {
        clienteService.buscarPorId(clienteId);
        List<Pedido> pedidoList = pedidoDao.listarPorCliente(clienteId);
        return pedidoList;
    }
}
