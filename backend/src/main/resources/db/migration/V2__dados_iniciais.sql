-- V2: dados de exemplo para desenvolvimento e testes

INSERT INTO servico (nome, descricao, preco, duracao_minutos) VALUES
                                                                  ('Esmaltação simples',        'Esmalte comum nas mãos',      25.00,  45),
                                                                  ('Esmaltação em gel',         'Com cabine UV',               60.00,  60),
                                                                  ('Alongamento em fibra',      'Aplicação completa',         150.00, 120),
                                                                  ('Manutenção de alongamento', 'Reposição e acabamento',      90.00,  90),
                                                                  ('Pedicure',                  'Cuidado completo dos pés',    40.00,  60);

INSERT INTO profissional (nome, especialidade) VALUES
                                                   ('Ana',     'Nail designer'),
                                                   ('Beatriz', 'Alongamento');

-- Ana faz os serviços de esmaltação e pedicure
INSERT INTO profissional_servico (profissional_id, servico_id)
SELECT p.id, s.id
FROM profissional p
         JOIN servico s ON s.nome IN ('Esmaltação simples', 'Esmaltação em gel', 'Pedicure')
WHERE p.nome = 'Ana';

-- Beatriz faz todos
INSERT INTO profissional_servico (profissional_id, servico_id)
SELECT p.id, s.id
FROM profissional p
         CROSS JOIN servico s
WHERE p.nome = 'Beatriz';

-- Expediente de segunda a sexta, em dois turnos
INSERT INTO horario_trabalho (profissional_id, dia_semana, hora_inicio, hora_fim)
SELECT p.id, d.dia, t.ini, t.fim
FROM profissional p
         CROSS JOIN (VALUES ('MONDAY'), ('TUESDAY'), ('WEDNESDAY'), ('THURSDAY'), ('FRIDAY')) AS d(dia)
         CROSS JOIN (VALUES (TIME '09:00', TIME '12:00'), (TIME '14:00', TIME '18:00')) AS t(ini, fim)
WHERE p.nome IN ('Ana', 'Beatriz');