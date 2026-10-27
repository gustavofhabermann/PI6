package com.sistemdemesa.projintegrador.config;

import com.sistemdemesa.projintegrador.entity.Marmita;
import com.sistemdemesa.projintegrador.entity.Mesa;
import com.sistemdemesa.projintegrador.entity.Pedido;
import com.sistemdemesa.projintegrador.entity.PedidoProduto;
import com.sistemdemesa.projintegrador.entity.Produto;
import com.sistemdemesa.projintegrador.repository.MarmitaRepository;
import com.sistemdemesa.projintegrador.repository.MesaRepository;
import com.sistemdemesa.projintegrador.repository.PedidoRepository;
import com.sistemdemesa.projintegrador.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DemoDataInitializer {
    @Bean
    @ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
    CommandLineRunner seedDemoData(
            MesaRepository mesas,
            ProdutoRepository produtos,
            MarmitaRepository marmitas,
            PedidoRepository pedidos) {
        return args -> {
            if (mesas.count() > 0 || produtos.count() > 0 || marmitas.count() > 0 || pedidos.count() > 0) {
                return;
            }

            List<Mesa> mesasDemo = mesas.saveAll(List.of(
                    mesa(1, 4),
                    mesa(2, 2),
                    mesa(3, 6)));

            List<Produto> produtosDemo = produtos.saveAll(List.of(
                    produto("Café espresso", "Bebidas", "4.50", 40),
                    produto("Suco de laranja", "Bebidas", "8.50", 25),
                    produto("Hambúrguer da casa", "Pratos", "29.90", 18),
                    produto("Batata rústica", "Acompanhamentos", "16.00", 30),
                    produto("Brownie", "Sobremesas", "12.00", 20)));

            marmitas.saveAll(List.of(
                    marmita("Cliente demonstração 1", "11900000001", "Rua de Demonstração, 100", "Frango grelhado", "24.90", "preparando"),
                    marmita("Cliente demonstração 2", "11900000002", "Rua de Demonstração, 200", "Carne assada", "27.90", "pronto")));

            criarPedido(mesasDemo.get(1), "concluido", List.of(
                    new ItemPedido(produtosDemo.get(2), 1),
                    new ItemPedido(produtosDemo.get(3), 1),
                    new ItemPedido(produtosDemo.get(1), 1)), produtos, pedidos);

            criarPedido(mesasDemo.get(2), "concluido", List.of(
                    new ItemPedido(produtosDemo.get(4), 2),
                    new ItemPedido(produtosDemo.get(0), 2)), produtos, pedidos);

            criarPedido(mesasDemo.get(0), "aberto", List.of(
                    new ItemPedido(produtosDemo.get(2), 2),
                    new ItemPedido(produtosDemo.get(1), 2)), produtos, pedidos);
        };
    }

    private static Mesa mesa(int numero, int lugares) {
        Mesa mesa = new Mesa();
        mesa.setNumero(numero);
        mesa.setQuantidade(lugares);
        return mesa;
    }

    private static Produto produto(String nome, String categoria, String preco, int estoque) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setPrecoCompra(new BigDecimal(preco).multiply(new BigDecimal("0.55")));
        produto.setPrecoVenda(new BigDecimal(preco));
        produto.setQuantidade(estoque);
        return produto;
    }

    private static Marmita marmita(String cliente, String telefone, String endereco, String tipo, String preco, String status) {
        Marmita marmita = new Marmita();
        marmita.setNomeCliente(cliente);
        marmita.setTelefone(telefone);
        marmita.setEndereco(endereco);
        marmita.setTipo(tipo);
        marmita.setPreco(new BigDecimal(preco));
        marmita.setStatus(status);
        return marmita;
    }

    private static void criarPedido(
            Mesa mesa,
            String status,
            List<ItemPedido> itens,
            ProdutoRepository produtos,
            PedidoRepository pedidos) {
        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setStatus(status);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido itemPedido : itens) {
            Produto produto = itemPedido.produto();
            if (produto.getQuantidade() < itemPedido.quantidade()) {
                throw new IllegalStateException("Estoque insuficiente para os dados de demonstração.");
            }

            BigDecimal subtotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemPedido.quantidade()));
            PedidoProduto item = new PedidoProduto();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemPedido.quantidade());
            item.setPrecoUnitarioVendido(produto.getPrecoVenda());
            item.setSubtotal(subtotal);
            pedido.getProdutosRelacao().add(item);
            produto.setQuantidade(produto.getQuantidade() - itemPedido.quantidade());
            total = total.add(subtotal);
        }

        produtos.saveAll(itens.stream().map(ItemPedido::produto).distinct().toList());
        pedido.setTotal(total);
        pedidos.save(pedido);
    }

    private record ItemPedido(Produto produto, int quantidade) {
    }
}
