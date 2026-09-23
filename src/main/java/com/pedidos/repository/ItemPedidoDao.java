package com.pedidos.repository;

import com.pedidos.model.ItemPedido;

import java.util.List;

public interface ItemPedidoDao {

    ItemPedido salvar(ItemPedido item, int pedidoId);

    List<ItemPedido> buscarPorPedidoId(int pedidoId);
}