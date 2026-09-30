package com.pedidos.model;

import com.pedidos.enums.StatusPedido;
import com.pedidos.exception.DadoInvalidoException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Pedido {

    @Setter(AccessLevel.NONE)
    private int id;

    @NotNull
    private Cliente cliente;

    @NotNull
    private List<ItemPedido> itens;

    @NotNull
    private StatusPedido status;

    @NotNull
    @PastOrPresent
    private LocalDateTime dataCriacao;

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.status = StatusPedido.ABERTO;
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item){
        if (item == null) {
            throw new DadoInvalidoException("...");
        }
        itens.add(item);
    }
    public double calcularTotal(){
        double total = 0.0;
        for (ItemPedido item : itens){
            total+=item.calcularSubtotal();
        }
        return total;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido #%d | Cliente: %s | Status: %s | Data: %s%n",
                id, cliente.getNome(), status, dataCriacao.format(formatter)));

        if (itens.isEmpty()) {
            sb.append("  (sem itens)");
        } else {
            for (ItemPedido item : itens) {
                sb.append(item).append(System.lineSeparator());
            }
            sb.append(String.format("Total: R$ %.2f", calcularTotal()));
        }
        return sb.toString();
    }
}
