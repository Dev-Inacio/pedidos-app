package com.pedidos.service;

import com.pedidos.enums.StatusPedido;
import com.pedidos.exception.PedidoEmAndamentoException;
import com.pedidos.exception.PedidoSemItensException;
import com.pedidos.model.Cliente;
import com.pedidos.model.ItemPedido;
import com.pedidos.model.Pedido;
import com.pedidos.model.Produto;
import com.pedidos.repository.ItemPedidoDao;
import com.pedidos.repository.PedidoDao;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PedidoService {
    private final ClienteService clienteService;
    private final PedidoDao pedidoDao;
    private final ProdutoService produtoService;
    private final ItemPedidoDao itemPedidoDao;

    public Pedido criarPedido (int clienteId){
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Pedido pedido = new Pedido(cliente);
        return pedidoDao.salvar(pedido);
    }

    public Pedido adicionarItem(int pedidoId, int produtoId, int quantidade){

        Pedido pedido = pedidoDao.buscarPorId(pedidoId);
        if (pedido.getStatus() != StatusPedido.ABERTO){
            throw new PedidoEmAndamentoException("");
        }
        Produto produto = produtoService.buscarPorId(produtoId);
        produtoService.atualizarEstoque(produtoId, -quantidade);

        ItemPedido itemPedido = new ItemPedido(produto,quantidade);
        itemPedidoDao.salvar(itemPedido,pedidoId);
        pedido.adicionarItem(itemPedido);
        return pedidoDao.atualizar(pedido);
    }

    public Pedido fecharPedido(int pedidoId){
        Pedido pedido = pedidoDao.buscarPorId(pedidoId);

        if (pedido.getStatus() != StatusPedido.ABERTO){
            throw new PedidoEmAndamentoException("");
        }

        if (pedido.getItens().isEmpty()){
            throw new PedidoSemItensException("");
        }

        pedido.setStatus(StatusPedido.FECHADO);
        return pedidoDao.atualizar(pedido);
    }

    public Pedido cancelarPedido(int pedidoId){

        Pedido pedido = pedidoDao.buscarPorId(pedidoId);

        if (pedido.getStatus() != StatusPedido.ABERTO){
            throw new PedidoEmAndamentoException("");
        }

        for (ItemPedido item : pedido.getItens()){
            produtoService.atualizarEstoque(item.getProduto().getId(), item.getQuantidade());
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoDao.atualizar(pedido);
    }

    public List<Pedido> listarPorCliente(int clienteId){
        clienteService.buscarPorId(clienteId);
       List<Pedido> pedidoList = pedidoDao.listarPorCliente(clienteId);
       return pedidoList;
    }
}
