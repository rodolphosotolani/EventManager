-- 1. Insert Static System Permissions
INSERT INTO seguranca.permissao (id, tela, acao, date_created, last_updated) VALUES
-- VENDAS
(1, 'VENDAS', 'ACESSO', NOW(), NOW()),
(2, 'VENDAS', 'LISTAR', NOW(), NOW()),
(3, 'VENDAS', 'CADASTRAR', NOW(), NOW()),
(4, 'VENDAS', 'EDITAR', NOW(), NOW()),
(5, 'VENDAS', 'DELETAR', NOW(), NOW()),
(6, 'VENDAS', 'IMPRIMIR', NOW(), NOW()),
-- PRODUTOS
(7, 'PRODUTOS', 'ACESSO', NOW(), NOW()),
(8, 'PRODUTOS', 'LISTAR', NOW(), NOW()),
(9, 'PRODUTOS', 'CADASTRAR', NOW(), NOW()),
(10, 'PRODUTOS', 'EDITAR', NOW(), NOW()),
(11, 'PRODUTOS', 'DELETAR', NOW(), NOW()),
-- CATEGORIAS
(12, 'CATEGORIAS', 'ACESSO', NOW(), NOW()),
(13, 'CATEGORIAS', 'LISTAR', NOW(), NOW()),
(14, 'CATEGORIAS', 'CADASTRAR', NOW(), NOW()),
(15, 'CATEGORIAS', 'EDITAR', NOW(), NOW()),
(16, 'CATEGORIAS', 'DELETAR', NOW(), NOW()),
-- ESTOQUES
(17, 'ESTOQUES', 'ACESSO', NOW(), NOW()),
(18, 'ESTOQUES', 'LISTAR', NOW(), NOW()),
(19, 'ESTOQUES', 'CADASTRAR', NOW(), NOW()),
(20, 'ESTOQUES', 'EDITAR', NOW(), NOW()),
(21, 'ESTOQUES', 'DELETAR', NOW(), NOW()),
-- CLIENTES
(22, 'CLIENTES', 'ACESSO', NOW(), NOW()),
(23, 'CLIENTES', 'LISTAR', NOW(), NOW()),
(24, 'CLIENTES', 'CADASTRAR', NOW(), NOW()),
(25, 'CLIENTES', 'EDITAR', NOW(), NOW()),
(26, 'CLIENTES', 'DELETAR', NOW(), NOW()),
-- EVENTOS
(27, 'EVENTOS', 'ACESSO', NOW(), NOW()),
(28, 'EVENTOS', 'LISTAR', NOW(), NOW()),
(29, 'EVENTOS', 'CADASTRAR', NOW(), NOW()),
(30, 'EVENTOS', 'EDITAR', NOW(), NOW()),
(31, 'EVENTOS', 'DELETAR', NOW(), NOW()),
-- INSTITUICOES
(32, 'INSTITUICOES', 'ACESSO', NOW(), NOW()),
(33, 'INSTITUICOES', 'LISTAR', NOW(), NOW()),
(34, 'INSTITUICOES', 'CADASTRAR', NOW(), NOW()),
(35, 'INSTITUICOES', 'EDITAR', NOW(), NOW()),
(36, 'INSTITUICOES', 'DELETAR', NOW(), NOW()),
-- USUARIOS
(37, 'USUARIOS', 'ACESSO', NOW(), NOW()),
(38, 'USUARIOS', 'LISTAR', NOW(), NOW()),
(39, 'USUARIOS', 'CADASTRAR', NOW(), NOW()),
(40, 'USUARIOS', 'EDITAR', NOW(), NOW()),
(41, 'USUARIOS', 'DELETAR', NOW(), NOW()),
-- PERFIS
(42, 'PERFIS', 'ACESSO', NOW(), NOW()),
(43, 'PERFIS', 'LISTAR', NOW(), NOW()),
(44, 'PERFIS', 'CADASTRAR', NOW(), NOW()),
(45, 'PERFIS', 'EDITAR', NOW(), NOW()),
(46, 'PERFIS', 'DELETAR', NOW(), NOW()),
-- PERMISSOES
(47, 'PERMISSOES', 'ACESSO', NOW(), NOW()),
(48, 'PERMISSOES', 'LISTAR', NOW(), NOW()),
(49, 'PERMISSOES', 'CADASTRAR', NOW(), NOW()),
(50, 'PERMISSOES', 'EDITAR', NOW(), NOW()),
(51, 'PERMISSOES', 'DELETAR', NOW(), NOW()),
-- CONTAS
(52, 'CONTAS', 'ACESSO', NOW(), NOW()),
(53, 'CONTAS', 'LISTAR', NOW(), NOW()),
(54, 'CONTAS', 'CADASTRAR', NOW(), NOW()),
(55, 'CONTAS', 'EDITAR', NOW(), NOW()),
(56, 'CONTAS', 'DELETAR', NOW(), NOW()),
-- CAIXAS
(57, 'CAIXAS', 'ACESSO', NOW(), NOW()),
(58, 'CAIXAS', 'LISTAR', NOW(), NOW()),
(59, 'CAIXAS', 'CADASTRAR', NOW(), NOW()),
(60, 'CAIXAS', 'EDITAR', NOW(), NOW()),
(61, 'CAIXAS', 'DELETAR', NOW(), NOW())
    ON CONFLICT (tela, acao) DO NOTHING;


-- 6. Synchronize Sequences to match the maximum ID in seed data (ensures safe future JPA inserts with increment size 50)
SELECT setval('gestao.instituicao_seq', COALESCE((SELECT MAX(id) FROM gestao.instituicao), 1));
SELECT setval('gestao.evento_seq', COALESCE((SELECT MAX(id) FROM gestao.evento), 1));
SELECT setval('seguranca.permissao_seq', COALESCE((SELECT MAX(id) FROM seguranca.permissao), 1));
SELECT setval('seguranca.perfil_seq', COALESCE((SELECT MAX(id) FROM seguranca.perfil), 1));
SELECT setval('seguranca.usuario_seq', COALESCE((SELECT MAX(id) FROM seguranca.usuario), 1));
SELECT setval('seguranca.usuario_instituicao_seq', COALESCE((SELECT MAX(id) FROM seguranca.usuario_instituicao), 1));
SELECT setval('seguranca.perfil_usuario_seq', COALESCE((SELECT MAX(id) FROM seguranca.perfil_usuario), 1));
