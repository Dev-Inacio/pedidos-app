package com.pedidos.service;

import com.pedidos.exception.DadoInvalidoException;
import com.pedidos.exception.EstoqueInsuficienteException;
import com.pedidos.exception.ProdutoComPedidoVinculadoException;
import com.pedidos.exception.ProdutoNaoEncontradoException;
import com.pedidos.model.Produto;
import com.pedidos.repository.ItemPedidoDao;
import com.pedidos.repository.ProdutoDao;
import com.pedidos.util.ValidadorUtil;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoDao produtoDao;
    private final ItemPedidoDao itemPedidoDao;

    public Produto cadastrar(Produto produto) {

        Set<ConstraintViolation<Produto>> violacoes = ValidadorUtil.getValidator().validate(produto);
        if (!violacoes.isEmpty()) {
            throw new DadoInvalidoException("Dados inválidos: " + violacoes.iterator().next().getMessage());
        }
        return produtoDao.cadastrar(produto);
    }

    public Produto buscarPorId(int id) {
        Produto produto = produtoDao.buscarPorId(id);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto com id " + id + " não encontrado");
        }
        return produto;
    }

    public Produto atualizarEstoque(int produtoId, int quantidade) {
        Produto buscando = buscarPorId(produtoId);

        int novaQuantidade = buscando.getQuantidadeEmEstoque() + quantidade;

        if (novaQuantidade < 0) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto " + produtoId);
        }
        return produtoDao.atualizarEstoque(produtoId, quantidade);
    }

    public void deletar(int id){
        buscarPorId(id);
        if (itemPedidoDao.existePorProduto(id)){
            throw new ProdutoComPedidoVinculadoException("Produto com id " + id + " possui pedidos vinculados")  ;
        }
        produtoDao.deletar(id);
    }
}
