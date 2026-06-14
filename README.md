# EventManager - Especificação Técnica e Guia do Projeto

Bem-vindo ao **EventManager**, uma plataforma SaaS multitenant robusta desenvolvida em Java e Spring Boot para gestão de eventos, controle de vendas (PDV), controle de estoque, catalogação de produtos/serviços, auditoria financeira e controle de segurança de acessos.

Este documento é a **Bússola Técnica** do projeto. Ele detalha a arquitetura, regras de negócio inegociáveis (implícitas e explícitas), modelagem de banco de dados, o fluxo assíncrono do sistema, fluxo de checkout e o design system do frontend.
Ele foi estruturado para fornecer o máximo de contexto para desenvolvedores e agentes de Inteligência Artificial (como o Gemini) atuarem com assertividade na manutenção e evolução da base de código.

---

## 1. Visão Geral do Escopo e Proposta de Valor

O **EventManager** foi concebido para atender instituições (como paróquias, clubes, ONGs e pequenas empresas) que realizam eventos com vendas de produtos (ex: alimentos, bebidas, souvenirs) e serviços (ex: ingressos, estacionamento, inscrições).
O sistema utiliza a abordagem de **Monólito Modular** (Spring Modulith), focada na separação estrita de domínios (*Bounded Contexts*) e comunicação interna orientada a eventos para garantir escalabilidade sem a complexidade prematura de microsserviços.

### Principais Pilares do Sistema:
1. **Multitenancy Estrito**: Todas as informações pertencem a uma `Instituicao` específica. Os dados de diferentes instituições nunca se misturam.
2. **Ciclo de Vida de Eventos**: Cada venda, lote de estoque ou movimentação está obrigatoriamente vinculada a um `Evento` ativo dentro da instituição.
3. **Terminal PDV (Point of Sale)**: Interface rápida e reativa (HTMX) para montagem do carrinho de compras.
4. **Checkout Flexível**: Suporte a múltiplas formas de pagamento, cálculo dinâmico de troco, identificação opcional/obrigatória de clientes e integração com impressão de cupom não-fiscal (ESC/POS).

---

## 2. Arquitetura de Software e Tecnologias

O projeto utiliza um design híbrido entre o Monolito Modular (**Spring Modulith**) e a divisão clássica em camadas.

### Divisão estrutural:
* **Backend**: Java 25, Spring Boot 3.x/4.x, Spring Modulith.
* **Persistência**: Spring Data JPA / Hibernate 6+, PostgreSQL.
* **Frontend Web**: Thymeleaf (Layout Dialect), HTMX (para reatividade assíncrona parcial), Tailwind CSS v4.
* **Mapeamento**: MapStruct (geração estática rigorosa) e Lombok.
* **Segurança**: Spring Security 6 + Google OIDC (OAuth2).

### Stack Tecnológica Core:
*   **Java 25**: Utilização de recursos modernos do ecossistema JVM (records, padrão de correspondência para switch e instanceof, var keyword, text blocks).
*   **Spring Boot 3.x / 4.x**: Base do framework de injeção de dependências e configuração.
*   **Spring Data JPA / Hibernate 6+**: Abstração de banco de dados e mapeamento objeto-relacional (ORM).
*   **Spring Security 6 + Google OAuth2 / OIDC**: Autenticação unificada via contas Google.
*   **Thymeleaf + Layout Dialect**: Motor de renderização HTML no servidor de forma modular (layouts e fragmentos).
*   **HTMX**: Hipermídia sob demanda para requisições assíncronas assíncronas parciais no PDV sem recarregar a tela inteira.
*   **Tailwind CSS v4**: Compilação rápida de estilos utilitários via `@tailwindcss/cli`.
*   **MapStruct**: Geração de mapeadores de dados (Mappers) eficientes e estáticos.
*   **Lombok**: Redução de código boilerplate em entidades, DTOs e serviços.
*   **PostgreSQL**: Banco de dados relacional com divisão lógica em Schemas.

### Estrutura de Pacotes (Bounded Contexts):
A injeção de dependências cruzada entre domínios é **estritamente proibida** na camada de Controladores. 
A comunicação inter-módulos ocorre exclusivamente através de `Facades` (Consultas e validações) ou Eventos de Domínio via `ApplicationEventPublisher` (Disparo de rotinas nao dependentes do fluxo inicial) e devem ser instanciados nas classes de Serviços.

O sistema está estruturado em pacotes por contexto delimitado (Bounded Context):
*   `br.com.rts.eventmanager.gestao`: Contém a gestão de Instituições e Eventos. Acesso externo isolado via `GestaoFacade`.
*   `br.com.rts.eventmanager.catalogo`: Contém Categorias, Subcategorias, Produtos, Serviços, Estoque e Movimentação de Estoque. Acesso externo isolado via `ProdutoFacade`, `ServicoFacade` e `EstoqueFacade`.
*   `br.com.rts.eventmanager.financeiro`: Contém Caixa, Fluxo de Caixa, Vendas, Itens de Venda, Clientes e Contas a Receber. 
*   `br.com.rts.eventmanager.seguranca`: Autenticação, Perfis de Acesso (RBAC), Permissões específicas de telas e Provisionamento de Usuários. Acesso externo isolado via `SegurancaFacade`.

---

## 3. Padrões Arquiteturais e Restrições de Design

### Estratégia de Identificadores (IDs vs. UUIDs)
* **Roteamento e Banco de Dados (`Long`)**: O sistema utiliza **exclusivamente** chaves primárias e estrangeiras do tipo `Long` (com `GenerationType.IDENTITY`). O `Long` também será o único identificador trafegado nas URLs das APIs REST/MVC e nos *payloads* do HTMX.
* **Auditoria Passiva (`UUID`)**: Todas as entidades possuem um campo `UUID` forte gerado automaticamente na persistência. Seu uso atual restringe-se **apenas** à auditoria passiva no banco de dados e à composição do código de barras de validação de impressão. Não deve ser usado para roteamento ou lógicas de *join*.

### Isolamento de Controladores e Orquestração
* **Princípio da Responsabilidade Única (SRP)**: Um Controller (ex: `VendaController`) só tem permissão para injetar o seu próprio Serviço correspondente (`VendaService`) e o seu próprio Mapper (`VendaMapper`).
* **Enriquecimento de DTOs**: A conversao dos DTOs (Responses, Requests e DTOs das telas) devem ser convertidos diretamente no Controller (Somente ele tem acesso à essas classes). O preenchimento de dados compostos (ex: nomes, detalhamentos, `populateCartDetails`) ocorre **inteiramente dentro da camada de Serviço**. O Serviço invoca as dependências necessárias (Facades de outros módulos) e reconstrói a entidade final validada antes de retorná-lo ao Controller.

### Pureza dos Mapeadores (MapStruct)
* **Sem Injeção de Dependência**: É proibido o uso de `@Autowired` em interfaces/classes geradas pelo MapStruct.
* **Mapeamento Estrutural Direto**: Os mappers traduzem apenas propriedades nativas. Campos de chaves estrangeiras (`Long`) devem ser instanciados de volta em entidades ou DTOs aninhados vazios (ex: `item.setProduto(new Produto(id))`). O enriquecimento funcional desses sub-objetos deve ser feito explicitamente na camada de serviço.

---

## 4. Segurança e Isolamento Multitenant (SaaS)

A segurança do EventManager opera em duas camadas principais:

### 1. Spring Security 6 Autenticação (OAuth2 / OIDC) e Perfil de Acesso:
*   Toda autenticação ocorre via login do Google via OAuth2/OIDC.
*   O serviço `UserProvisioningService` intercepta o sucesso do login. Se o e-mail não existir na base de dados do banco de dados, cria um novo `Usuario` com status `ativo = true`.
*   Se o usuário for ativo, o serviço resolve e carrega todas as permissões concedidas a partir dos perfis atribuídos a ele, injetando as chaves de autoridade (como `ROLE_MASTER` ou `VENDAS_ACESSO`) no contexto de segurança do Spring.
* **Usuário MASTER via Variável de Ambiente**: É proibida a criação de flags booleanas (como `is_master` ou `is_admin`) na tabela de usuários. O sistema exige uma entidade `Perfil` chamada `MASTER`. Durante a inicialização da aplicação, o sistema deve ler a variável de ambiente (ex: `MASTER_EMAIL`) e atribuir este perfil exclusivamente a este usuário.
* **Acesso Restrito**: Somente o usuário `MASTER` tem autonomia para cadastrar e gerenciar Instituições e Grupos no sistema.

### 2. TimezoneAndAccessInterceptor (HandlerInterceptor):
*   Este interceptador é executado em todas as requisições web (exceto páginas de login, logout, arquivos estáticos e `/aguarde-vinculo`).
*   **Regra de Negócio Crítica (Implícita)**: Se o usuário logado não possuir a autoridade `ROLE_MASTER`, o sistema verifica se o usuário está associado a pelo menos uma instituição ativa (via `UsuarioInstituicao`).
*   Se o usuário não estiver associado a nenhuma instituição, o interceptador interrompe o fluxo e o redireciona imediatamente para a página `/aguarde-vinculo` (onde é informado que sua conta precisa ser liberada pelo administrador).
*   **GlobalWebModelAdvice**: Este assessor de controle é executado antes dos métodos dos controladores para colocar em sessão e em modelo os atributos globais:
    *   `activeInstituicaoId` (ID da instituição em uso).
    *   `activeEventoId` (ID do evento em uso).
    *   `tenant` (Entidade da instituição atual).
    *   `activeEvent` (Entidade do evento atual).
    *   Temas de Cores do Tenant: Injeta cores de marca específicas de acordo com o Tenant selecionado (ex: Instituição `1` recebe cores voltadas ao laranja `#e65100`, outras recebem azul/indigo).

### Isolamento de Dados por Tenant (Instituição)
O sistema blinda o acesso cruzado entre instituições através de regras de banco de dados baseadas em Sessão:
* Após o login, o ID da instituição ativa do usuário é embutido como um *claim* customizado na sessão do Spring Security.
* Todas as entidades do sistema implementam anotações do Hibernate (`@FilterDef` e `@Filter`) configuradas para injetar a cláusula `WHERE instituicao_id = :tenantId`. Isso assegura que o *Spring Data JPA* filtre automaticamente os registros sem depender de filtragem manual pelo desenvolvedor.

---

## 5. Modelagem de Banco de Dados (PostgreSQL)

O banco é segregado em quatro schemas distintos (`gestao`, `catalogo`, `financeiro`, `seguranca`).

### Polimorfismo Comercializável (`JOIN_TABLE`)
O banco de dados do EventManager é estruturado de forma rigorosa utilizando quatro Schemas PostgreSQL para manter a divisão de responsabilidades. 
Todas as tabelas herdam campos de auditoria controlados pelo Spring Data JPA Auditing (`date_created` e `last_updated`).
* Para garantir a integridade referencial estrita e a aplicação de restrições de integridade (NOT NULL) em nível de banco de dados, o catálogo adota a estratégia de herança de tabelas relacionadas (JOINED):
  * Tabela Pai (catalogo.item): Centraliza os atributos comuns a qualquer oferta comercializável do evento (id, uuid, instituicao_id, evento_id, nome, descricao, tipo_item, categoria_id, sub_categoria_id, valor_venda, ativo, e campos de auditoria). Mapeada na classe abstrata Item com @Inheritance(strategy = InheritanceType.JOINED).
  * Tabela Filha de Produtos (catalogo.item_produto): Contém os dados exclusivos de itens físicos (quantidade_minima). A chave primária desta tabela (item_id) funciona simultaneamente como chave estrangeira mapeada com @PrimaryKeyJoinColumn para a tabela pai. Mapeada na classe ItemProduto. 
  * Tabela Filha de Serviços (catalogo.item_servico): Contém os dados exclusivos de itens intangíveis. Compartilha o mesmo comportamento de chave primária associada (item_id). Mapeada na classe ItemServico.
* Na entidade ItemVenda (schema financeiro), a propriedade de relacionamento aponta unicamente para a abstração Item via @ManyToOne, permitindo que o JPA gerencie o carregamento polimórfico de forma nativa e sem a persistência de colunas órfãs nulas.

### Detalhamento dos Schemas e Tabelas Principais:

### Schemas:
1.  **`gestao`**: Estrutura institucional básica e eventos.
2.  **`catalogo`**: Produtos, serviços, categorias e controle físico de estoques.
3.  **`financeiro`**: Registros de vendas, caixa, contas a receber (anotações de conta) e clientes.
4.  **`seguranca`**: Usuários, perfis e controle granular de permissões por tela.

#### Schema `gestao`:
*   `gestao.instituicao`: Entidade Tenant raíz.
*   `gestao.evento`: Eventos associados a uma instituição.

*Regra de Negócio Crítica (Explícita)*: Apenas um evento pode estar marcado como `ativo = true` por instituição por vez. Ao ativar/cadastrar um evento, a regra deve validar que todos os outros eventos pertencentes à mesma instituição estejam desativados.

#### Schema `catalogo`:
*   `catalogo.categoria` & `catalogo.sub_categoria`: Organização hierárquica. Subcategorias são opcionais.
*   `catalogo.produto`: Itens físicos. Possui `valor_venda_unitario` e `quantidade_minima` (para alerta de estoque baixo). Vincula-se a uma Categoria.
*   `catalogo.servico`: Itens intangíveis (ex: "Ingresso Show"). Possui `valor_venda` e vincula-se a uma Categoria. Não possui controle de estoque físico associado.
*   `catalogo.estoque`: Controla a quantidade de um produto específico para um evento. Possui `quantidade_inicial`, `quantidade_atual` e `valor_compra_unitario` (custo médio).
*   `catalogo.movimentacao`: Log de histórico de entradas, saídas ou perdas de estoque.

#### Schema `financeiro`:
*   `financeiro.venda`: Cabeçalho da transação. Possui `valor_total`, flag `vendido` (indica se a venda foi concluída ou se é um carrinho aberto), `forma_pagamento` (Enum) e `data_venda`.
*   `financeiro.item_venda`: Relaciona a venda ao produto ou serviço vendido, gravando a quantidade e aplicando a precificação unitária vigente.
*   `financeiro.cliente`: Cadastro de contatos. Possui `nome`, `apelido` (como prefere ser chamado), `celular`, `telefone` (fixo) e `email`.
*   `financeiro.conta`: Controla as contas de clientes (fiado/anotações). Vincula o cliente, a venda e o saldo devedor pendente.
*   `financeiro.caixa`: Shift/Turno de caixa. Controla abertura, fechamento, saldo inicial, saldo final informado pelo operador, saldo final calculado pelo sistema e diferença de caixa (quebra de caixa).
*   `financeiro.fluxo_caixa`: Lançamentos detalhados de entrada e saída por forma de pagamento vinculados a um caixa aberto.

#### Schema `seguranca`:
*   `seguranca.usuario`: Dados do usuário. Sincroniza e provisiona os campos `nome`, `email` e `url_foto` vindos do Google OIDC a cada login bem-sucedido.
*   `seguranca.usuario_instituicao`: Tabela de junção que define a qual instituição(ões) o usuário pertence.
*   `seguranca.perfil`: Papel de acesso (ex: "Master", "Operador de Caixa").
*   `seguranca.permissao`: Permissões do sistema baseadas no padrão `SimpleGrantedAuthority`. Formado pela tela/módulo + ação (ex: `VENDAS_CADASTRAR`, `PRODUTOS_EDITAR`).

---

## 6. Regras de Negócio Críticas e Inegociáveis

### Módulo de Gestão: Exclusividade de Evento
* Uma instituição pode conter diversos eventos, mas **apenas um evento pode estar ativo por vez**.
* **Validação Síncrona**: O sistema não desativa eventos anteriores de forma oculta. A tentativa de ativar ou cadastrar um evento enquanto já existe outro ativo lançará a exceção síncrona `EventoAtivoExistenteException`, obrigando o operador a encerrar o evento atual de forma consciente na interface.

### Módulo de Catálogo: Estoque, Impressão e Estornos
* **Baixa de Lotes (FIFO Estrito)**: O consumo de estoque ignora valores de compra. A prioridade é cronológica: as baixas iteram sobre os lotes utilizando a cláusula `ORDER BY date_created ASC`.
* **Concorrência**: Por se tratar de um sistema com baixa simultaneidade de terminais de caixa operando as mesmas vendas simultaneamente, o uso de Optimistic Locking (`@Version`) foi descartado.
* **Segurança Anti-Fraude de Fichas**: O código impresso para retirada nos balcões segue a máscara irreversível: `UUID da Venda` + `-` + `UUID do Produto` + `-` + `Contador`. Ao ler a ficha no balcão, ela é invalidada atômica e individualmente no banco.
* **Regra de Devolução/Desistência**: Não existem reversões ou "apagões" de histórico. O estorno de uma ficha gera uma **nova entrada** na entidade `Movimentacao` com `Tipo = ENTRADA` e `Motivo = DEVOLUCAO`, devolvendo o saldo físico para a quantidade atual, e gera um registro respectivo de `Tipo = ESTORNO` no `FluxoCaixa`.

### Módulo Financeiro: Auditoria e Fluxo de Caixa Cego
* **Fechamento Cego de Caixa**: É proibido exibir o valor matemático computado pelo sistema (`saldo_final_calculado`) na interface do operador do caixa. O operador deve inserir manualmente o valor das notas e moedas físicas apuradas através do campo `saldo_final_informado`.
* A `diferenca_caixa` (informado vs. calculado) é computada pelo backend. Este dado é exibido nos dashboards e relatórios **exclusivamente** para usuários autenticados com os perfis `ADMINISTRADOR` ou `MASTER`.

---

## 7. Dinâmica Operacional do "Anotar na Conta" (Fiado)

O fluxo da forma de pagamento "Anotar na Conta" exige o desmembramento transacional, pois representa uma receita reconhecida pelo evento que ainda não se tornou dinheiro físico no caixa.

1.  **Registro Original da Dívida (Checkout)**:
    * A tela de checkout obriga a identificação de um `Cliente`.
    * O fechamento registra a `Venda` (status `pago = false`) e cria uma `Conta` vinculada ao cliente, preenchendo o `saldo_devedor` com o total da transação.
    * Gera um lançamento no `FluxoCaixa` apontando `Tipo = VENDA` e `Forma_Pagamento = ANOTAR_CONTA`.
2.  **Amortização (Pagamentos Parciais)**:
    * Quando o cliente abate a conta (ex: paga R$ 50 de uma dívida de R$ 150 em dinheiro), o sistema subtrai o montante do `saldo_devedor`.
    * É gerado um **novo registro** no `FluxoCaixa` com `Tipo = RECEBIMENTO_CONTA` e a forma de pagamento física utilizada (`DINHEIRO`, `PIX`). Este novo registro entra no cálculo automático da gaveta do operador atual.
3.  **Quitação Completa**:
    * Quando o `saldo_devedor` atingir `0.00`, o sistema marca automaticamente o atributo `pago = true` e preenche a `data_hora_pagamento`.

---

## 7. Desacoplamento Assíncrono de Operações (Checkout)

O ato de pressionar "Confirmar Venda" envolve escrita pesada e comunicação com drivers de impressora. 
Para garantir a resiliência do terminal HTMX e evitar travamentos na fila do PDV, a execução de tarefas colaterais é orquestrada de forma assíncrona.

1.  **Gatilho do Evento (Publicador)**:
    Ao fechar a parte puramente financeira, o serviço dispara o evento através do ecosssitema interno:
    ```java
    applicationEventPublisher.publishEvent(new VendaConfirmadaEvent(venda.getId(), tenantId, eventoId));
    ```
2.  **Processamento Background (Listeners)**:
    * O *Listener* de `Catalogo` processa em plano de fundo a quebra dos itens, subtraindo os lotes via FIFO estrito e registrando os logs em `Movimentacao`.
    * O *Listener* de `Impressão` converte o resumo da venda para *bytes* não-fiscais compatíveis com impressoras térmicas (ESC/POS) e salva na sessão (*Flash Attributes*).
3.  **Tolerância a Falhas**:
    Caso a rotina JavaScript acoplada ao HTMX falhe ao disparar os bytes para a impressora local, a interface possui um painel lateral de contingência chamado **"Últimas Fichas Emitidas"**. Este painel permite que o operador aperte o botão "Reimprimir", que busca os *bytes* diretamente da base de dados e força o envio.

## 8. Mapeamento Limpo e Boas Práticas (MapStruct)

Uma das diretrizes arquiteturais mais estritas do projeto é a **pureza dos mapeadores (Mappers)** gerados pelo MapStruct (ex: `VendaMapper`, `ItemVendaMapper`, `ProdutoMapper`):

*   **Proibição de Injeção**: Nenhum mapper pode ter `@Autowired`, `@Inject` ou injeções de construtores de outros serviços ou Facades (como `ProdutoFacade` ou `ServicoFacade`) para resolver entidades complexas a partir de IDs.
*   **Regra de Mapeamento Simples**: Os mappers traduzem apenas dados estruturais diretamente mapeáveis. No caso de relacionamentos de chaves estrangeiras (`ItemVenda` para `ItemVendaDTO`), o mapper converte o ID (`Long`) da entidade para a propriedade `.id` do DTO aninhado (`ProdutoDTO.id` ou `ServicoDTO.id`), e vice-versa.
*   **Comportamento de DTOs Aninhados**: Devido à forma como o MapStruct gera a inicialização de objetos filhos, os campos `item.produto` e `item.servico` dentro de `ItemVendaDTO` **nunca serão nulos**. Em vez disso, se a chave estrangeira for nula, o mapper instanciará um `new ProdutoDTO()` ou `new ServicoDTO()` com todas as propriedades internas (incluindo `id` e `nome`) definidas como `null`.

---

## 9. Fluxo de Trabalho do PDV e Checkout

O fluxo de fechamento de venda do terminal PDV para a tela de checkout obedece a regras de negócios específicas e design detalhado:

### Persistência do Carrinho de Compras:
O carrinho é mantido na sessão do usuário através da anotação `@SessionAttributes("venda")` no `VendaController`. Isso permite que o operador insira ou remova itens no PDV por requisições parciais assíncronas do HTMX e depois navegue para a página `/checkout` via link GET padrão sem perder o estado das seleções.

### Interface de Checkout:
A tela `/vendas/checkout` é dividida em um grid assimétrico moderno de 3 colunas (padrão de design StitchMCP):
*   **Lado Esquerdo (2/3 da largura)**: Card de **Resumo dos Itens**. Lista detalhadamente cada item comprado, indicando o nome do produto ou serviço, sua especificação (se houver), quantidade no formato fixo `Qtd: XX` (ex: `Qtd: 01`), o subtotal unitário calculado no DTO e uma linha de divisória sutil.
*   **Lado Direito (1/3 da largura)**: Uma coluna vertical contendo, nesta ordem:
    1.  Card de **Forma de Pagamento**: Exibe os botões de seleção de rádio de forma visualmente premium. Cada opção possui um ícone SVG ampliado (`w-6 h-6`) correspondente ao tipo de pagamento (`DINHEIRO`, `PIX`, `CARTAO_DEBITO`, `CARTAO_CREDITO` ou `ANOTAR_CONTA`). A seleção do botão de rádio aplica dinamicamente uma borda com a cor primária da instituição (`has-[:checked]:border-primary`).
    2.  Card de **Identificação do Cliente**: Oculto por padrão. Exibe um campo de seleção de clientes cadastrados.
        *   *Regra de Negócio Crítica (Explícita)*: Se a forma de pagamento selecionada for **Anotar na Conta** (`ANOTAR_CONTA`), o card de identificação é exibido dinamicamente via JavaScript e o campo torna-se obrigatório (`required`). É proibido confirmar uma venda nesta modalidade sem vincular a conta de um cliente ativo.
    3.  Card de **Valores Recebidos (Troco)**: Oculto por padrão. Exibe-se se a forma de pagamento selecionada for **Dinheiro** (`DINHEIRO`).
        *   *Regra de Negócio Crítica (Implícita)*: O campo `valorRecebido` possui uma **máscara monetária BRL** aplicada em tempo real (`formatCurrency` via Javascript) que exibe o valor formatado como `R$ X.XXX,XX`.
        *   O sistema calcula o troco dinamicamente na tela (`Valor Recebido - Total a Pagar`). Se o valor informado for menor que o total a pagar, o troco exibe "Valor insuficiente" na cor vermelha e a submissão do formulário é bloqueada.
    4.  Card de **Resumo do Pedido**: Exibe uma recapitulação rápida em lista de todos os itens e quantidades, o valor `Total a Pagar` em destaque e o botão de ação principal "Confirmar Venda".

### Integração com Impressoras Térmicas:
Ao confirmar a venda, o controlador executa `printerService.generateEscPosBytes(vendaFinalizada)`. Os bytes brutos gerados são convertidos para Base64 e passados via atributo flash (`printPayload`) de volta ao navegador. Se o payload de impressão estiver presente na página do PDV pós-redirecionamento, uma rotina Javascript envia-o ao driver da impressora local conectada ao terminal.

---

## 10. Instruções de Execução e Desenvolvimento Local

### Pré-requisitos:
*   Java Development Kit (JDK) 21 ou superior (JDK 25 recomendado).
*   Docker e Docker Compose instalados na máquina.
*   Node.js instalado (para compilação dos assets do Tailwind).

### Passos para Rodar o Projeto:

1.  **Inicializar o Banco de Dados (PostgreSQL)**:
    Certifique-se de que os serviços do Docker Compose estão ativos para levantar a instância do banco de dados na porta mapeada (o Spring Boot utiliza o recurso de Docker Compose Connection Details, conectando-se automaticamente aos serviços expostos no arquivo `compose.yaml`):
    ```powershell
    docker compose up -d
    ```

2.  **Compilar e Assistir o CSS (Tailwind CSS v4)**:
    Sempre que houver alterações em classes utilitárias nas telas do Thymeleaf, compile os estilos utilizando os scripts do `package.json`:
    ```powershell
    # Instalar dependências de build do CSS
    npm install
    
    # Compilação única
    npm run build
    
    # Compilar em modo assistido (watch) durante o desenvolvimento
    npm run watch
    ```

3.  **Executar os Testes Unitários e de Integração**:
    Execute a suíte de testes do Gradle para garantir a saúde das regras de negócio mapeadas:
    ```powershell
    .\gradlew test
    ```

4.  **Iniciar a Aplicação**:
    Inicie o servidor Spring Boot local. Caso a porta padrão `8080` esteja ocupada em sua máquina, inicialize especificando uma porta alternativa (como `8081`):
    ```powershell
    .\gradlew bootRun --args='--server.port=8081'
    ```

A aplicação estará disponível em `http://localhost:8081` (ou na porta correspondente selecionada).
