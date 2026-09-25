package com.pedidos.exception;

public class PedidoSemItensException extends RuntimeException {
    public PedidoSemItensException(String message) {
        super(message);
    }
}
