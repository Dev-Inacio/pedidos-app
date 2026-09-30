package com.pedidos.repository;

import com.pedidos.model.Produto;

public interface ProdutoDao {

    Produto cadastrar(Produto produto);

    Produto buscarPorId(int id);

    Produto atualizar(Produto produto);

    Produto atualizarEstoque(int produtoId, int quantidade);

    void deletar(int id);
}
