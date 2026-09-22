package com.pedidos.repository;

import com.pedidos.model.Cliente;

public interface ClienteDao {

    Cliente cadastrar(Cliente cliente);

    Cliente buscarPorId(int id);

    Cliente buscarPorEmail(String email);

    Cliente atualizar(Cliente cliente);

    void deletar(int id);
}
