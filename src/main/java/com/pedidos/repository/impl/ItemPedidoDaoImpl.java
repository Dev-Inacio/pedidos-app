package com.pedidos.repository.impl;

import com.pedidos.exception.ConexaoException;
import com.pedidos.model.ItemPedido;
import com.pedidos.model.Produto;
import com.pedidos.repository.ItemPedidoDao;
import com.pedidos.util.ConexaoDB;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ItemPedidoDaoImpl implements ItemPedidoDao {

    private final Connection connection;

    @Override
    public ItemPedido salvar(ItemPedido itemPedido, int pedidoId) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO item_pedido "
                            + "(pedido_id, produto_id, quantidade, preco_unitario_na_compra) "
                            + "VALUES "
                            + "(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, pedidoId);
            preparedStatement.setInt(2, itemPedido.getProduto().getId());
            preparedStatement.setInt(3, itemPedido.getQuantidade());
            preparedStatement.setDouble(4, itemPedido.getPrecoUnitarioNaCompra());


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    Produto produto = itemPedido.getProduto();
                    int quantidade = itemPedido.getQuantidade();
                    double precoUnitarioNaCompra = itemPedido.getPrecoUnitarioNaCompra();
                    itemPedido = new ItemPedido(id, produto, quantidade, precoUnitarioNaCompra);

                }
                ConexaoDB.closeResultSet(resultSet);
            } else {
                throw new ConexaoException("");
            }
        } catch (SQLException exception) {
            throw new ConexaoException(exception.getMessage());
        } finally {
            ConexaoDB.closeStatement(preparedStatement);
        }
        return itemPedido;
    }

    @Override
    public List<ItemPedido> buscarPorPedidoId(int pedidoId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM item_pedido WHERE pedido_id = ?");
            preparedStatement.setInt(1, pedidoId);

            resultSet = preparedStatement.executeQuery();

            List<ItemPedido> itens = new ArrayList<>();
            ProdutoDaoImpl produtoDaoImpl = new ProdutoDaoImpl(this.connection);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int produtoId = resultSet.getInt("produto_id");
                Produto produto = produtoDaoImpl.buscarPorId(produtoId);
                int quantidade = resultSet.getInt("quantidade");
                double precoUnitarioNaCompra = resultSet.getDouble("preco_unitario_na_compra");
                itens.add(new ItemPedido(id, produto, quantidade, precoUnitarioNaCompra));
            }
            return itens;
        } catch (SQLException exception) {
            throw new ConexaoException(exception.getMessage());
        } finally {
            ConexaoDB.closeStatement(preparedStatement);
            ConexaoDB.closeResultSet(resultSet);
        }
    }
}
