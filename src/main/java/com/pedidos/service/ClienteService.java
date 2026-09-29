package com.pedidos.service;

import com.pedidos.exception.ClienteNaoEncontradoException;
import com.pedidos.exception.DadoInvalidoException;
import com.pedidos.exception.EmailJaCadastradoException;
import com.pedidos.exception.PedidoEmAndamentoException;
import com.pedidos.model.Cliente;
import com.pedidos.model.Pedido;
import com.pedidos.repository.ClienteDao;
import com.pedidos.repository.PedidoDao;
import com.pedidos.util.ValidadorUtil;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ClienteService {

    private final ClienteDao clienteDao;
    private final PedidoDao pedidoDao;

    public Cliente cadastrar(Cliente cliente) {

        Set<ConstraintViolation<Cliente>> violacoes = ValidadorUtil.getValidator().validate(cliente);
        if (!violacoes.isEmpty()) {
            throw new DadoInvalidoException("Dados do cliente inválidos");
        }

        Cliente clienteExistente = clienteDao.buscarPorEmail(cliente.getEmail());
        if (clienteExistente != null) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + cliente.getEmail());
        }
        return clienteDao.cadastrar(cliente);
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = clienteDao.buscarPorId(id);
        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente com id " + id + " não encontrado");
        }
        return cliente;
    }

    public Cliente buscarPorEmail(String email) {
        Cliente cliente = clienteDao.buscarPorEmail(email);
        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente com email " + email + " não encontrado");
        }
        return cliente;
    }

    public Cliente atualizar(Cliente cliente) {
        Set<ConstraintViolation<Cliente>> violacoes = ValidadorUtil.getValidator().validate(cliente);
        if (!violacoes.isEmpty()) {
            throw new DadoInvalidoException("Dados inválidos: " + violacoes.iterator().next().getMessage());
        }

        Cliente clienteExistente = buscarPorId(cliente.getId());

        if (!cliente.getEmail().equalsIgnoreCase(clienteExistente.getEmail())) {
            Cliente outroCliente = clienteDao.buscarPorEmail(cliente.getEmail());
            if (outroCliente != null) {
                throw new EmailJaCadastradoException("Email já cadastrado: " + cliente.getEmail());
            }
        }
        clienteDao.atualizar(cliente);
        return cliente;
    }

    public void deletar(int id) {
        buscarPorId(id);
        List<Pedido> pedidoList = pedidoDao.listarPorCliente(id);

        if (!pedidoList.isEmpty()){
            throw new PedidoEmAndamentoException("Cliente com id " + id + " possui pedidos vinculados");
        }
        clienteDao.deletar(id);
    }
}