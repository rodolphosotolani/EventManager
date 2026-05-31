-- 1. Insert Default Institution
INSERT INTO gestao.instituicao (id, nome, ativo, date_created, last_updated)
VALUES (1, 'Instituição Demo', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Default Event
INSERT INTO gestao.evento (id, nome, ativo, instituicao_id, date_created, last_updated)
VALUES (1, 'Evento Demo', true, 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Static System Permissions
INSERT INTO seguranca.permissao (id, tela, acao, created_at, updated_at) VALUES
(1, 'VENDAS', 'LISTAGEM', NOW(), NOW()),
(2, 'VENDAS', 'CADASTRAR', NOW(), NOW()),
(3, 'VENDAS', 'EDITAR', NOW(), NOW()),
(4, 'VENDAS', 'DELETAR', NOW(), NOW()),
(5, 'VENDAS', 'REIMPRIMIR', NOW(), NOW()),
(6, 'PRODUTOS', 'LISTAGEM', NOW(), NOW()),
(7, 'PRODUTOS', 'CADASTRAR', NOW(), NOW()),
(8, 'PRODUTOS', 'EDITAR', NOW(), NOW()),
(9, 'PRODUTOS', 'DELETAR', NOW(), NOW()),
(10, 'CLIENTES', 'LISTAGEM', NOW(), NOW()),
(11, 'CLIENTES', 'CADASTRAR', NOW(), NOW()),
(12, 'CLIENTES', 'EDITAR', NOW(), NOW()),
(13, 'CLIENTES', 'DELETAR', NOW(), NOW()),
(14, 'CONTAS', 'LISTAGEM', NOW(), NOW()),
(15, 'CONTAS', 'CADASTRAR', NOW(), NOW()),
(16, 'CONTAS', 'EDITAR', NOW(), NOW()),
(17, 'CONTAS', 'DELETAR', NOW(), NOW()),
(18, 'CAIXAS', 'LISTAGEM', NOW(), NOW()),
(19, 'CAIXAS', 'CADASTRAR', NOW(), NOW()),
(20, 'CAIXAS', 'EDITAR', NOW(), NOW()),
(21, 'CAIXAS', 'DELETAR', NOW(), NOW()),
(22, 'FLUXOS', 'LISTAGEM', NOW(), NOW()),
(23, 'FLUXOS', 'CADASTRAR', NOW(), NOW()),
(24, 'FLUXOS', 'EDITAR', NOW(), NOW()),
(25, 'FLUXOS', 'DELETAR', NOW(), NOW()),
(26, 'USUARIOS', 'LISTAGEM', NOW(), NOW()),
(27, 'USUARIOS', 'CADASTRAR', NOW(), NOW()),
(28, 'USUARIOS', 'EDITAR', NOW(), NOW()),
(29, 'USUARIOS', 'DELETAR', NOW(), NOW()),
(30, 'PERFIS', 'LISTAGEM', NOW(), NOW()),
(31, 'PERFIS', 'CADASTRAR', NOW(), NOW()),
(32, 'PERFIS', 'EDITAR', NOW(), NOW()),
(33, 'PERFIS', 'DELETAR', NOW(), NOW())
ON CONFLICT (tela, acao) DO NOTHING;

-- 4. Insert default Access Profile (Administrador) for Institution 1
INSERT INTO seguranca.perfil (id, instituicao_id, nome, created_at, updated_at)
VALUES (1, 1, 'Administrador', NOW(), NOW())
ON CONFLICT (nome, instituicao_id) DO NOTHING;

-- 5. Associate all permissions with the Administrador profile
INSERT INTO seguranca.tb_perfil_permissao (perfil_id, permissao_id)
SELECT 1, id FROM seguranca.permissao
ON CONFLICT DO NOTHING;

-- 6. Insert Default Admin User
INSERT INTO seguranca.usuario (id, email, nome, url_foto, ativo, created_at, updated_at)
VALUES (1, 'rts.sotware.tech@gmail.com', 'Administrador do Sistema', null, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- 7. Link Admin User to Institution 1
INSERT INTO seguranca.usuario_instituicao (id, usuario_id, instituicao_id, created_at, updated_at)
VALUES (1, 1, 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 8. Assign Administrador profile to Admin User for Event 1
INSERT INTO seguranca.perfil_usuario (id, usuario_id, perfil_id, evento_id, created_at, updated_at)
VALUES (1, 1, 1, 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 9. Synchronize Sequences to match the maximum ID in seed data (ensures safe future JPA inserts with increment size 50)
SELECT setval('gestao.instituicao_seq', COALESCE((SELECT MAX(id) FROM gestao.instituicao), 1));
SELECT setval('gestao.evento_seq', COALESCE((SELECT MAX(id) FROM gestao.evento), 1));
SELECT setval('seguranca.permissao_seq', COALESCE((SELECT MAX(id) FROM seguranca.permissao), 1));
SELECT setval('seguranca.perfil_seq', COALESCE((SELECT MAX(id) FROM seguranca.perfil), 1));
SELECT setval('seguranca.usuario_seq', COALESCE((SELECT MAX(id) FROM seguranca.usuario), 1));
SELECT setval('seguranca.usuario_instituicao_seq', COALESCE((SELECT MAX(id) FROM seguranca.usuario_instituicao), 1));
SELECT setval('seguranca.perfil_usuario_seq', COALESCE((SELECT MAX(id) FROM seguranca.perfil_usuario), 1));
