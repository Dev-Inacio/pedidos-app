package com.pedidos.service;

import com.pedidos.model.Cliente;
import com.pedidos.model.Pedido;
import com.pedidos.repository.PedidoDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PedidoService {
    private final ClienteService clienteService;
    private final PedidoDao pedidoDao;

    public Pedido criarPedido (int clienteId){
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Pedido pedido = new Pedido(cliente);
        return pedidoDao.salvar(pedido);
    }
}
