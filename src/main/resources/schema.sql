-- Create schemas if they do not exist
CREATE SCHEMA IF NOT EXISTS gestao;
CREATE SCHEMA IF NOT EXISTS catalogo;
CREATE SCHEMA IF NOT EXISTS financeiro;
CREATE SCHEMA IF NOT EXISTS seguranca;

-- Create sequences for entities using sequence generator strategy (matching Hibernate's default allocationSize of 50)
CREATE SEQUENCE IF NOT EXISTS gestao.instituicao_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS gestao.evento_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.categoria_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.sub_categoria_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.produto_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.servico_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.estoque_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS catalogo.movimentacao_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.venda_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.item_venda_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.caixa_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.fluxo_caixa_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.cliente_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS financeiro.conta_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seguranca.perfil_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seguranca.permissao_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seguranca.usuario_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seguranca.usuario_instituicao_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seguranca.perfil_usuario_seq START WITH 1 INCREMENT BY 1;

-- 1. Instituicao Table (Schema: gestao)
CREATE TABLE IF NOT EXISTS gestao.instituicao (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('gestao.instituicao_seq'),
    nome varchar(100) NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 2. Evento Table (Schema: gestao)
CREATE TABLE IF NOT EXISTS gestao.evento (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('gestao.evento_seq'),
    nome varchar(100) NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    instituicao_id bigint NOT NULL CONSTRAINT fk_evento_instituicao REFERENCES gestao.instituicao(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 3. Categoria Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.categoria (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.categoria_seq'),
    instituicao_id bigint NOT NULL CONSTRAINT fk_categoria_instituicao REFERENCES gestao.instituicao(id),
    nome varchar(100) NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 4. Sub-Categoria Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.sub_categoria (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.sub_categoria_seq'),
    instituicao_id bigint NOT NULL CONSTRAINT fk_sub_categoria_instituicao REFERENCES gestao.instituicao(id),
    nome varchar(100),
    ativo boolean NOT NULL DEFAULT true,
    categoria_id bigint CONSTRAINT fk_sub_categoria_categoria REFERENCES catalogo.categoria(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 5. Produto Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.produto (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.produto_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao_id bigint CONSTRAINT fk_produto_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint CONSTRAINT fk_produto_evento REFERENCES gestao.evento(id),
    nome varchar(100) NOT NULL,
    especificacao varchar(50),
    valor_venda_unitario numeric(10,2) NOT NULL,
    quantidade_minima integer,
    categoria_id bigint NOT NULL CONSTRAINT fk_produto_categoria REFERENCES catalogo.categoria(id),
    sub_categoria_id bigint CONSTRAINT fk_produto_sub_categoria REFERENCES catalogo.sub_categoria(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 6. Servico Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.servico (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.servico_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao_id bigint CONSTRAINT fk_servico_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint CONSTRAINT fk_servico_evento REFERENCES gestao.evento(id),
    nome varchar(100) NOT NULL,
    valor_venda numeric(10,2) NOT NULL,
    categoria_id bigint NOT NULL CONSTRAINT fk_servico_categoria REFERENCES catalogo.categoria(id),
    sub_categoria_id bigint CONSTRAINT fk_servico_sub_categoria REFERENCES catalogo.sub_categoria(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 7. Estoque Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.estoque (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.estoque_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao bigint NOT NULL CONSTRAINT fk_estoque_instituicao REFERENCES gestao.instituicao(id),
    evento bigint NOT NULL CONSTRAINT fk_estoque_evento REFERENCES gestao.evento(id),
    produto_id bigint NOT NULL CONSTRAINT fk_estoque_produto REFERENCES catalogo.produto(id),
    quantidade_inicial integer NOT NULL,
    quantidade_atual integer NOT NULL,
    valor_compra_unitario numeric(10,2),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 8. Movimentacao Table (Schema: catalogo)
CREATE TABLE IF NOT EXISTS catalogo.movimentacao (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('catalogo.movimentacao_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao bigint NOT NULL CONSTRAINT fk_movimentacao_instituicao REFERENCES gestao.instituicao(id),
    evento bigint NOT NULL CONSTRAINT fk_movimentacao_evento REFERENCES gestao.evento(id),
    estoque_id bigint CONSTRAINT fk_movimentacao_estoque REFERENCES catalogo.estoque(id),
    produtos_id bigint CONSTRAINT fk_movimentacao_produto REFERENCES catalogo.produto(id),
    tipo_movimentacao varchar(255) NOT NULL,
    motivo_movimentacao varchar(255) NOT NULL,
    quantidade integer NOT NULL,
    valor_unitario numeric(10,2),
    data_movimentacao timestamp without time zone NOT NULL,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 9. Venda Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.venda (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.venda_seq'),
    instituicao_id bigint NOT NULL CONSTRAINT fk_venda_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint NOT NULL CONSTRAINT fk_venda_evento REFERENCES gestao.evento(id),
    valor_total numeric(10,2) NOT NULL,
    vendido boolean NOT NULL,
    forma_pagamento varchar(255),
    data_venda timestamp without time zone NOT NULL,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

ALTER TABLE financeiro.venda ADD COLUMN IF NOT EXISTS instituicao_id bigint;
ALTER TABLE financeiro.venda ADD COLUMN IF NOT EXISTS evento_id bigint;

-- 10. Item Venda Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.item_venda (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.item_venda_seq'),
    instituicao_id bigint NOT NULL CONSTRAINT fk_item_venda_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint NOT NULL CONSTRAINT fk_item_venda_evento REFERENCES gestao.evento(id),
    quantidade integer NOT NULL,
    produto_id bigint NOT NULL CONSTRAINT fk_item_venda_produto REFERENCES catalogo.produto(id),
    venda_id bigint CONSTRAINT fk_item_venda_venda REFERENCES financeiro.venda(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

ALTER TABLE financeiro.item_venda ADD COLUMN IF NOT EXISTS instituicao_id bigint;
ALTER TABLE financeiro.item_venda ADD COLUMN IF NOT EXISTS evento_id bigint;

-- 11. Cliente Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.cliente (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.cliente_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao_id bigint NOT NULL CONSTRAINT fk_cliente_instituicao REFERENCES gestao.instituicao(id),
    nome varchar(255) NOT NULL,
    apelido varchar(255),
    celular varchar(255) NOT NULL,
    telefone varchar(255),
    email varchar(255),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

ALTER TABLE financeiro.cliente ADD COLUMN IF NOT EXISTS instituicao_id bigint;

-- 12. Conta Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.conta (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.conta_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao_id bigint NOT NULL CONSTRAINT fk_conta_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint NOT NULL CONSTRAINT fk_conta_evento REFERENCES gestao.evento(id),
    cliente_id bigint NOT NULL CONSTRAINT fk_conta_cliente REFERENCES financeiro.cliente(id),
    venda_id bigint NOT NULL CONSTRAINT fk_conta_venda REFERENCES financeiro.venda(id),
    saldo_devedor numeric(10,2) NOT NULL,
    pago boolean NOT NULL DEFAULT false,
    data_pagamento timestamp without time zone,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

ALTER TABLE financeiro.conta ADD COLUMN IF NOT EXISTS instituicao_id bigint;

-- 13. Caixa Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.caixa (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.caixa_seq'),
    uuid uuid NOT NULL UNIQUE,
    instituicao_id bigint NOT NULL CONSTRAINT fk_caixa_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint NOT NULL CONSTRAINT fk_caixa_evento REFERENCES gestao.evento(id),
    usuario_abertura bigint,
    usuario_fechamento bigint,
    status_caixa varchar(50),
    data_abertura timestamp without time zone,
    data_fechamento timestamp without time zone,
    saldo_inicial numeric(10,2),
    saldo_final_calculado numeric(10,2),
    saldo_final_informado numeric(10,2),
    diferenca_caixa numeric(10,2)
);

ALTER TABLE financeiro.caixa ADD COLUMN IF NOT EXISTS instituicao_id bigint;
ALTER TABLE financeiro.caixa ADD COLUMN IF NOT EXISTS evento_id bigint;

-- 14. Fluxo Caixa Table (Schema: financeiro)
CREATE TABLE IF NOT EXISTS financeiro.fluxo_caixa (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('financeiro.fluxo_caixa_seq'),
    instituicao_id bigint NOT NULL CONSTRAINT fk_fluxo_caixa_instituicao REFERENCES gestao.instituicao(id),
    evento_id bigint NOT NULL CONSTRAINT fk_fluxo_caixa_evento REFERENCES gestao.evento(id),
    caixa_id bigint CONSTRAINT fk_fluxo_caixa_caixa REFERENCES financeiro.caixa(id),
    venda_id bigint CONSTRAINT fk_fluxo_caixa_venda REFERENCES financeiro.venda(id),
    tipo_fluxo_caixa varchar(255),
    forma_pagamento varchar(255),
    observacao varchar(500),
    data_venda timestamp without time zone,
    usuario_id bigint
);

ALTER TABLE financeiro.fluxo_caixa ADD COLUMN IF NOT EXISTS instituicao_id bigint;
ALTER TABLE financeiro.fluxo_caixa ADD COLUMN IF NOT EXISTS evento_id bigint;

-- 15. Usuario Table (Schema: seguranca)
CREATE TABLE IF NOT EXISTS seguranca.usuario (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('seguranca.usuario_seq'),
    email varchar(100) NOT NULL UNIQUE,
    nome varchar(150) NOT NULL,
    url_foto varchar(500),
    ativo boolean NOT NULL DEFAULT true,
    ultimo_acesso timestamp without time zone,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

-- 16. Usuario Instituicao Table (Schema: seguranca)
CREATE TABLE IF NOT EXISTS seguranca.usuario_instituicao (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('seguranca.usuario_instituicao_seq'),
    usuario_id bigint NOT NULL CONSTRAINT fk_usuario_instituicao_usuario REFERENCES seguranca.usuario(id),
    instituicao_id bigint NOT NULL CONSTRAINT fk_usuario_instituicao_instituicao REFERENCES gestao.instituicao(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS seguranca.perfil (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('seguranca.perfil_seq'),
    instituicao_id bigint CONSTRAINT fk_perfil_instituicao REFERENCES gestao.instituicao(id),
    nome varchar(50) NOT NULL,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_perfil_nome_instituicao_null ON seguranca.perfil (nome) WHERE instituicao_id IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_perfil_nome_instituicao_not_null ON seguranca.perfil (nome, instituicao_id) WHERE instituicao_id IS NOT NULL;

-- 18. Permissao Table (Schema: seguranca)
CREATE TABLE IF NOT EXISTS seguranca.permissao (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('seguranca.permissao_seq'),
    tela varchar(50) NOT NULL,
    acao varchar(50) NOT NULL,
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL,
    CONSTRAINT uq_permissao_tela_acao UNIQUE (tela, acao)
);

-- 19. Perfil Permissao Table (Schema: seguranca)
CREATE TABLE IF NOT EXISTS seguranca.tb_perfil_permissao (
    perfil_id bigint NOT NULL CONSTRAINT fk_perfil_permissao_perfil REFERENCES seguranca.perfil(id),
    permissao_id bigint NOT NULL CONSTRAINT fk_perfil_permissao_permissao REFERENCES seguranca.permissao(id),
    PRIMARY KEY (perfil_id, permissao_id)
);

-- 20. Perfil Usuario Table (Schema: seguranca)
CREATE TABLE IF NOT EXISTS seguranca.perfil_usuario (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('seguranca.perfil_usuario_seq'),
    usuario_id bigint NOT NULL CONSTRAINT fk_perfil_usuario_usuario REFERENCES seguranca.usuario(id),
    perfil_id bigint NOT NULL CONSTRAINT fk_perfil_usuario_perfil REFERENCES seguranca.perfil(id),
    instituicao_id bigint CONSTRAINT fk_perfil_usuario_instituicao REFERENCES gestao.instituicao(id),
    date_created timestamp with time zone NOT NULL,
    last_updated timestamp with time zone NOT NULL
);

