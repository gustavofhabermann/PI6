# Sistema de Gerenciamento de Mesas

Aplicação web para organizar a operação de um restaurante: cadastro de mesas e produtos, pedidos do salão, pedidos de marmitas e relatórios em PDF.

## Funcionalidades

- Cadastro de usuários, login por e-mail e senha e logout.
- Cadastro, edição, listagem e remoção de mesas.
- Cadastro, edição, listagem e remoção de produtos, com categoria, preços e quantidade em estoque.
- Cadastro, edição, listagem e remoção de pedidos de marmita.
- Criação de pedidos associados a uma mesa, com um ou mais produtos e quantidades.
- Listagem de pedidos abertos, com ações para concluir ou excluir.
- Relatórios em PDF de produtos, vendas do dia e vendas do mês.
- Interface web responsiva com Thymeleaf.

## Tecnologias

- Java 21
- Spring Boot 3.3.4
- Spring MVC e Thymeleaf
- Spring Data JPA e Hibernate
- Spring Security
- Bean Validation
- H2 em memória
- OpenPDF
- Maven Wrapper

## Requisitos

- JDK 21 instalado e disponível no `PATH`.
- Navegador web.

O Maven Wrapper já está incluído; não é necessário instalar o Maven separadamente. Na primeira execução, o Maven baixa as dependências do projeto.

## Como executar

No Windows, abra o PowerShell na pasta `projintegrador` e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

No macOS ou Linux:

```bash
./mvnw spring-boot:run
```

Quando a aplicação iniciar, abra [http://localhost:8080](http://localhost:8080).

Para gerar o pacote executável no Windows:

```powershell
.\mvnw.cmd clean package
java -jar target/projintegrador-0.0.1-SNAPSHOT.jar
```

## Primeiro acesso

1. Abra a página inicial e selecione **Criar conta**.
2. Cadastre nome, e-mail e senha.
3. Entre com o e-mail e a senha cadastrados.
4. Cadastre mesas e produtos antes de criar pedidos.

As páginas de operação exigem autenticação. A página inicial, o login, o cadastro e os arquivos estáticos são públicos.

## Páginas principais

| Caminho | Descrição |
| --- | --- |
| `/` | Página inicial |
| `/login` | Login |
| `/cadastro` | Cadastro de usuário |
| `/home/mesas` | Listagem e gerenciamento de mesas |
| `/home/produtos` | Listagem e gerenciamento de produtos |
| `/home/marmitas` | Listagem e gerenciamento de marmitas |
| `/home/pedidos` | Pedidos em aberto |
| `/home/relatorios` | Links para relatórios PDF |
| `/home/relatorios/dashboard` | Painel de navegação do sistema |

Os relatórios PDF estão disponíveis em `/home/relatorios/exportar/produtos`, `/home/relatorios/exportar/vendasdia` e `/home/relatorios/exportar/vendasmes`.

## Banco de dados

O projeto usa H2 em memória, configurado em `src/main/resources/application.properties`. O Hibernate cria ou atualiza as tabelas com `spring.jpa.hibernate.ddl-auto=update`.

Como o banco é temporário, os cadastros são apagados quando a aplicação é encerrada. Para desenvolvimento, não é necessária instalação nem configuração manual de um servidor de banco de dados.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/sistemdemesa/projintegrador/
│   │   ├── config/       # Segurança e configuração web
│   │   ├── controller/   # Rotas e fluxo das páginas
│   │   ├── entity/       # Entidades JPA
│   │   └── repository/   # Acesso aos dados
│   └── resources/
│       ├── static/css/   # Estilos da interface
│       ├── templates/    # Páginas Thymeleaf
│       └── application.properties
└── test/                 # Testes automatizados
```

## Executar os testes

No Windows:

```powershell
.\mvnw.cmd test
```

No macOS ou Linux:

```bash
./mvnw test
```
