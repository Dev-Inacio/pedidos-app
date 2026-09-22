package com.pedidos.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class ItemPedido {

    @Setter(AccessLevel.NONE)
    private int id;

    @NotNull
    private Produto produto;

    @Positive
    private int quantidade;

    @Positive
    private double precoUnitarioNaCompra;

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitarioNaCompra = produto.getPreco();
    }

    public double calcularSubtotal(){
        return quantidade * precoUnitarioNaCompra;
    }
}
