package com.pedidos.repository.impl;

import com.pedidos.exception.ConexaoException;
import com.pedidos.model.Produto;
import com.pedidos.repository.ProdutoDao;
import com.pedidos.util.ConexaoDB;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public class ProdutoDaoImpl implements ProdutoDao {

    private final Connection connection;

    @Override
    public Produto cadastrar(Produto produto) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO produto "
                            + "(nome, preco, quantidade_em_estoque)"
                            + "VALUES "
                            + "(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setDouble(2, produto.getPreco());
            preparedStatement.setInt(3, produto.getQuantidadeEmEstoque());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String nome = produto.getNome();
                    double preco = produto.getPreco();
                    int quantidadeEmEstoque = produto.getQuantidadeEmEstoque();


                    produto = new Produto(id, nome, preco, quantidadeEmEstoque);
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
        return produto;
    }

    @Override
    public Produto buscarPorId(int produtoId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM produto WHERE id = ?");

            preparedStatement.setInt(1, produtoId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                double preco = resultSet.getDouble("preco");
                int quantidadeEmEstoque = resultSet.getInt("quantidade_em_estoque");

                Produto produto = new Produto(id, nome, preco, quantidadeEmEstoque);

                return produto;
            }
            return null;
        }catch (SQLException exception){
            throw new ConexaoException(exception.getMessage());
        }finally {
            ConexaoDB.closeStatement(preparedStatement);
            ConexaoDB.closeResultSet(resultSet);
        }
    }

    @Override
    public Produto atualizarEstoque(int produtoId, int quantidade) {
        return null;
    }

    @Override
    public void deletar(int id) {

    }
}
