package com.pedidos.util;

import com.pedidos.exception.ConexaoException;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class GerenciadorTransacao {

    private final Connection connection;

    public void executarEmTransacao(Runnable acao) {
        try {
            connection.setAutoCommit(false);
            acao.run();
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                exception.addSuppressed(e2);
            }
            throw new ConexaoException(exception.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e3) {
                e3.printStackTrace();
            }
        }
    }
}
