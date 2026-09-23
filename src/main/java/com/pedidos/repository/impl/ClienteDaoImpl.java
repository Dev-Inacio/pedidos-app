package com.pedidos.repository.impl;

import com.pedidos.exception.ConexaoException;
import com.pedidos.model.Cliente;
import com.pedidos.repository.ClienteDao;
import com.pedidos.util.ConexaoDB;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public class ClienteDaoImpl implements ClienteDao {

    private final Connection connection;

    @Override
    public Cliente cadastrar(Cliente cliente) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO cliente"
                            + "(nome,email,telefone)"
                            + "VALUES "
                            + "(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, cliente.getNome());
            preparedStatement.setString(2, cliente.getEmail());
            preparedStatement.setString(3, cliente.getTelefone());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    cliente = new Cliente(id, cliente.getNome(), cliente.getEmail(), cliente.getTelefone());
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
        return cliente;
    }

    @Override
    public Cliente buscarPorId(int id) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM cliente WHERE id = ?");

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                int clienteId = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                String telefone = resultSet.getString("telefone");

                Cliente cliente = new Cliente(clienteId, nome, email, telefone);

                return cliente;
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
    public Cliente buscarPorEmail(String email) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM cliente WHERE email = ?");

            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String clienteEmail = resultSet.getString("email");
                String telefone = resultSet.getString("telefone");

                Cliente cliente = new Cliente(id, nome, clienteEmail, telefone);

                return cliente;
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
    public Cliente atualizar(Cliente cliente) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE cliente SET nome= ?,email =?, telefone =? WHERE id =?");

            preparedStatement.setString(1, cliente.getNome());
            preparedStatement.setString(2, cliente.getEmail());
            preparedStatement.setString(3, cliente.getTelefone());
            preparedStatement.setInt(4,cliente.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
            } else {
                throw new ConexaoException("");
            }
        } catch (SQLException exception) {
            throw new ConexaoException(exception.getMessage());
        }finally {
            ConexaoDB.closeStatement(preparedStatement);
        }
        return cliente;
    }

    @Override
    public void deletar(int id) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM cliente WHERE id = ?");

            preparedStatement.setInt(1, id);

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
    }
}
