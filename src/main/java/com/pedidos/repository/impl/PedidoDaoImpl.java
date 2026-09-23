package com.pedidos.repository.impl;

import com.pedidos.enums.StatusPedido;
import com.pedidos.exception.ConexaoException;
import com.pedidos.model.Cliente;
import com.pedidos.model.ItemPedido;
import com.pedidos.model.Pedido;
import com.pedidos.repository.PedidoDao;
import com.pedidos.util.ConexaoDB;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PedidoDaoImpl implements PedidoDao {

    private final Connection connection;


    @Override
    public Pedido salvar(Pedido pedido) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO pedido "
                            + "(cliente_id, status, data_criacao) "
                            + "VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, pedido.getCliente().getId());
            preparedStatement.setString(2, pedido.getStatus().name());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(pedido.getDataCriacao()));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    pedido = new Pedido(id, pedido.getCliente(), pedido.getItens(), pedido.getStatus(), pedido.getDataCriacao());
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
        return pedido;
    }


    @Override
    public Pedido buscarPorId(int pedidoId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM pedido WHERE id = ?");

            preparedStatement.setInt(1, pedidoId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Cliente cliente = new ClienteDaoImpl(this.connection).buscarPorId(resultSet.getInt("cliente_id"));

                List<ItemPedido> itens = new ItemPedidoDaoImpl(this.connection).buscarPorPedidoId(pedidoId);

                StatusPedido statusPedido = StatusPedido.valueOf(resultSet.getString("status"));


                LocalDateTime dataCriacao = resultSet.getTimestamp("data_criacao").toLocalDateTime();

                Pedido pedido = new Pedido(pedidoId, cliente, itens, statusPedido, dataCriacao);

                return pedido;
            }
            return null;
        } catch (SQLException exception) {
            throw new ConexaoException(exception.getMessage());
        } finally {
            ConexaoDB.closeStatement(preparedStatement);
            ConexaoDB.closeResultSet(resultSet);
        }
    }

    @Override
    public Pedido atualizar(Pedido pedido) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE pedido SET status = ? WHERE id = ?");
            preparedStatement.setString(1, pedido.getStatus().name());
            preparedStatement.setInt(2, pedido.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
            } else {
                throw new ConexaoException("");
            }
        } catch (SQLException exception) {
            throw new ConexaoException(exception.getMessage());
        } finally {
            ConexaoDB.closeStatement(preparedStatement);
        }
        return pedido;
    }

    @Override
    public List<Pedido> listarPorCliente(int clienteId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM pedido WHERE cliente_id = ?");
            preparedStatement.setInt(1, clienteId);

            resultSet = preparedStatement.executeQuery();

            List<Pedido> pedidoList = new ArrayList<>();

            while (resultSet.next()){

                int id = resultSet.getInt("id");

                Cliente cliente = new ClienteDaoImpl(this.connection).buscarPorId(resultSet.getInt("cliente_id"));

                List<ItemPedido> itens = new ItemPedidoDaoImpl(this.connection).buscarPorPedidoId(id);

                StatusPedido statusPedido = StatusPedido.valueOf(resultSet.getString("status"));

                LocalDateTime dataCriacao = resultSet.getTimestamp("data_criacao").toLocalDateTime();

                Pedido pedido = new Pedido(id, cliente, itens, statusPedido, dataCriacao);
                pedidoList.add(pedido);
            }
            return pedidoList;
        }catch (SQLException exception){
            throw new ConexaoException(exception.getMessage());
        }finally {
            ConexaoDB.closeStatement(preparedStatement);
            ConexaoDB.closeResultSet(resultSet);
        }
    }
}
