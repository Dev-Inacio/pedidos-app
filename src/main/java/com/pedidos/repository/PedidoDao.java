package com.pedidos.repository;


import com.pedidos.model.Pedido;

import java.util.List;

public interface PedidoDao {

    Pedido salvar(Pedido pedido);

    Pedido buscarPorId(int pedidoId);

    Pedido atualizar(Pedido pedido);

    List<Pedido> listarPorCliente(int clienteId);
}
