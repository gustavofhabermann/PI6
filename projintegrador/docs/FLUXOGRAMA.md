# Fluxograma e classes do projeto

Este documento resume o fluxo do sistema Mesa & Sabor e identifica a responsabilidade de cada classe Java. As setas indicam uso ou relacionamento entre classes.

## Arquitetura e dependências

```mermaid
flowchart LR
    Browser[Usuario / navegador]
    Views[Templates Thymeleaf e CSS]
    DB[(Banco H2 / JPA)]

    subgraph Inicializacao[Inicializacao e configuracao]
        App[ProjintegradorApplication]
        Security[SecurityConfig]
        Web[WebConfig]
        Seed[DemoDataInitializer]
    end

    subgraph WebLayer[Camada web]
        Public[PublicController]
        MesaC[MesaController]
        PedidoC[PedidoController]
        ProdutoC[ProdutoController]
        MarmitaC[MarmitaController]
        RelatorioC[RelatorioController]
        Advice[NavigationModelAdvice]
        Form[CadastroForm]
    end

    subgraph DataLayer[Repositorios]
        UserR[UserRepository]
        MesaR[MesaRepository]
        PedidoR[PedidoRepository]
        ItemR[PedidoProdutoRepository]
        ProdutoR[ProdutoRepository]
        MarmitaR[MarmitaRepository]
    end

    subgraph Domain[Entidades JPA]
        UserE[User]
        MesaE[Mesa]
        PedidoE[Pedido]
        ItemE[PedidoProduto]
        ProdutoE[Produto]
        MarmitaE[Marmita]
    end

    Browser --> Security
    Security --> Public
    Security --> UserR
    App --> Security
    App --> Web
    App --> Seed
    Browser --> Public
    Browser --> MesaC
    Browser --> PedidoC
    Browser --> ProdutoC
    Browser --> MarmitaC
    Browser --> RelatorioC
    Advice --> Views
    Public --> Form
    Public --> UserR
    MesaC --> MesaR
    MesaC --> PedidoR
    PedidoC --> MesaR
    PedidoC --> PedidoR
    PedidoC --> ProdutoR
    ProdutoC --> ProdutoR
    ProdutoC --> ItemR
    MarmitaC --> MarmitaR
    RelatorioC --> PedidoR
    RelatorioC --> ProdutoR
    Seed --> MesaR
    Seed --> PedidoR
    Seed --> ProdutoR
    Seed --> MarmitaR
    UserR --> UserE
    MesaR --> MesaE
    PedidoR --> PedidoE
    ItemR --> ItemE
    ProdutoR --> ProdutoE
    MarmitaR --> MarmitaE
    PedidoE --> MesaE
    PedidoE --> ItemE
    ItemE --> ProdutoE
    MesaR --> DB
    PedidoR --> DB
    ItemR --> DB
    ProdutoR --> DB
    MarmitaR --> DB
    UserR --> DB
    Public --> Views
    MesaC --> Views
    PedidoC --> Views
    ProdutoC --> Views
    MarmitaC --> Views
    RelatorioC --> Views
```

## Fluxo de uso

```mermaid
flowchart TD
    Start([Inicio]) --> Public[PublicController: pagina inicial, login ou cadastro]
    Public --> Register{Cadastro enviado?}
    Register -- Sim --> Validate[Validar CadastroForm]
    Validate --> Valid{Dados validos e email novo?}
    Valid -- Nao --> FormError[Exibir formulario com erros]
    Valid -- Sim --> SaveUser[PublicController salva User via UserRepository]
    SaveUser --> Login[SecurityConfig autentica usuario]
    Register -- Nao --> Login
    Login --> Auth{Autenticado?}
    Auth -- Nao --> Public
    Auth -- Sim --> Home[Redireciona para /home/mesas]
    Home --> Choice{Area escolhida}
    Choice --> Mesas[MesaController: listar, criar, editar ou excluir mesas]
    Choice --> Products[ProdutoController: listar, criar, editar ou excluir produtos]
    Choice --> Lunch[MarmitaController: registrar e atualizar marmitas]
    Choice --> Orders[PedidoController: criar pedido e atualizar estoque]
    Orders --> OrderSave[Pedido e itens PedidoProduto persistidos]
    OrderSave --> OrderAction{Acao no pedido}
    OrderAction -- Concluir --> Finish[Pedido muda para concluido]
    OrderAction -- Excluir aberto --> Restore[Restaurar estoque e excluir pedido]
    Choice --> Reports[RelatorioController]
    Reports --> ReportType{Relatorio solicitado}
    ReportType --> PdfProducts[PDF de produtos]
    ReportType --> PdfDay[PDF de vendas do dia]
    ReportType --> PdfMonth[PDF de vendas do mes]
    ReportType --> Dashboard[Painel do sistema]
```

## Classes, uma por uma

### Inicializacao e configuracao

| Classe | Responsabilidade |
|---|---|
| `ProjintegradorApplication` | Inicia a aplicacao Spring Boot. |
| `SecurityConfig` | Configura o BCrypt, carrega usuarios pelo `UserRepository`, protege rotas e define login e logout. |
| `WebConfig` | Ponto de configuracao para ajustes adicionais do Spring MVC; atualmente nao declara regras extras. |
| `DemoDataInitializer` | Quando `app.demo-data.enabled=true`, popula o banco vazio com mesas, produtos, marmitas e pedidos de demonstracao. |
| `DemoDataInitializer.ItemPedido` | Record interno usado para agrupar um produto e a quantidade durante a criacao dos dados de demonstracao. |

### Controllers e formulario

| Classe | Responsabilidade e dependencias |
|---|---|
| `PublicController` | Exibe inicio, login e cadastro; valida o formulario, verifica email duplicado, codifica a senha e salva o usuario. Usa `UserRepository`, `PasswordEncoder` e `CadastroForm`. |
| `NavigationModelAdvice` | Disponibiliza o caminho HTTP atual como `currentPath` para os templates destacarem a secao ativa do menu. |
| `MesaController` | Lista, cria, edita e exclui mesas. Impede alteracao do ID e bloqueia exclusao de mesa que tenha pedidos. Usa `MesaRepository` e `PedidoRepository`. |
| `PedidoController` | Lista pedidos abertos, cria pedido com itens, valida estoque, atualiza quantidades, conclui ou exclui pedidos abertos. Usa `PedidoRepository`, `MesaRepository` e `ProdutoRepository`. |
| `ProdutoController` | Lista, cria, edita e exclui produtos; impede alterar ID e nao permite excluir produto associado a item de pedido. Usa `ProdutoRepository` e `PedidoProdutoRepository`. |
| `MarmitaController` | Lista, cria, edita e exclui registros de marmitas. Usa `MarmitaRepository`. |
| `RelatorioController` | Exibe relatorios e painel e gera PDFs de produtos, vendas do dia e vendas do mes. Usa `ProdutoRepository` e `PedidoRepository`. |
| `CadastroForm` | Objeto de formulario de cadastro com validacoes de nome, email e senha. |
| `RelatorioController.ReportFooter` | Classe interna que desenha o rodape e o numero da pagina nos PDFs. |

### Repositorios

| Classe | Entidade e operacoes especificas |
|---|---|
| `UserRepository` | Acesso JPA a `User`; busca usuario por email com `findByEmail`. |
| `MesaRepository` | Acesso JPA a `Mesa`; verifica numero duplicado e numero duplicado ignorando um ID. |
| `PedidoRepository` | Acesso JPA a `Pedido`; lista por status e verifica se uma mesa possui pedidos. |
| `PedidoProdutoRepository` | Acesso JPA a `PedidoProduto`; verifica se um produto esta associado a um item de pedido. |
| `ProdutoRepository` | Acesso JPA a `Produto`; busca produtos por IDs com bloqueio pessimista para proteger atualizacao de estoque em pedidos concorrentes. |
| `MarmitaRepository` | Acesso JPA a `Marmita`; usa as operacoes CRUD herdadas de `JpaRepository`. |

### Entidades

| Classe | Dados principais e relacionamentos |
|---|---|
| `User` | Usuario autenticavel com nome, email unico e senha codificada. |
| `Mesa` | Mesa com numero unico e quantidade de lugares. |
| `Produto` | Produto com nome, categoria, preco de compra, preco de venda e quantidade em estoque. |
| `Marmita` | Pedido de marmita para cliente, com telefone, endereco, tipo, preco e status. |
| `Pedido` | Pedido de uma `Mesa`, com status, total, data de criacao e lista de `PedidoProduto`. |
| `PedidoProduto` | Linha de pedido que relaciona um `Pedido` e um `Produto`, guardando quantidade, preco unitario vendido e subtotal. |

## Relacionamentos entre entidades

```mermaid
erDiagram
    MESA ||--o{ PEDIDO : recebe
    PEDIDO ||--|{ PEDIDO_PRODUTO : contem
    PRODUTO ||--o{ PEDIDO_PRODUTO : vendido_em
    USER {
        long id
        string name
        string email
        string password
    }
    MESA {
        long id
        int numero
        int quantidade
    }
    PEDIDO {
        long id
        string status
        decimal total
        datetime created_at
    }
    PEDIDO_PRODUTO {
        long id
        int quantidade
        decimal preco_unitario_vendido
        decimal subtotal
    }
    PRODUTO {
        long id
        string nome
        string categoria
        decimal preco_venda
        int quantidade
    }
    MARMITA {
        long id
        string nome_cliente
        string telefone
        string endereco
        string tipo
        decimal preco
        string status
    }
```

`User` e `Marmita` nao possuem relacionamento JPA com as demais entidades no codigo atual.
