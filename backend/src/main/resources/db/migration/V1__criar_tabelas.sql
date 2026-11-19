-- V1: tabelas do AgendaNails

CREATE TABLE servico (
                         id               BIGSERIAL      PRIMARY KEY,
                         nome             VARCHAR(80)    NOT NULL,
                         descricao        VARCHAR(255),
                         preco            NUMERIC(10,2)  NOT NULL CHECK (preco > 0),
                         duracao_minutos  INTEGER        NOT NULL CHECK (duracao_minutos > 0),
                         ativo            BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
                         id        BIGSERIAL     PRIMARY KEY,
                         nome      VARCHAR(120)  NOT NULL,
                         telefone  VARCHAR(20)   NOT NULL UNIQUE,
                         email     VARCHAR(120)
);

CREATE TABLE profissional (
                              id            BIGSERIAL     PRIMARY KEY,
                              nome          VARCHAR(120)  NOT NULL,
                              especialidade VARCHAR(80),
                              foto_url      VARCHAR(255),
                              ativo         BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE TABLE profissional_servico (
                                      profissional_id  BIGINT  NOT NULL REFERENCES profissional (id),
                                      servico_id       BIGINT  NOT NULL REFERENCES servico (id),
                                      PRIMARY KEY (profissional_id, servico_id)
);

CREATE TABLE horario_trabalho (
                                  id               BIGSERIAL    PRIMARY KEY,
                                  profissional_id  BIGINT       NOT NULL REFERENCES profissional (id),
                                  dia_semana       VARCHAR(10)  NOT NULL
                                      CHECK (dia_semana IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
                                  hora_inicio      TIME         NOT NULL,
                                  hora_fim         TIME         NOT NULL,
                                  CHECK (hora_fim > hora_inicio)
);

CREATE TABLE usuario (
                         id     BIGSERIAL     PRIMARY KEY,
                         email  VARCHAR(120)  NOT NULL UNIQUE,
                         senha  VARCHAR(100)  NOT NULL,
                         papel  VARCHAR(20)   NOT NULL DEFAULT 'ADMIN'
);

CREATE TABLE agendamento (
                             id               BIGSERIAL     PRIMARY KEY,
                             cliente_id       BIGINT        NOT NULL REFERENCES cliente (id),
                             servico_id       BIGINT        NOT NULL REFERENCES servico (id),
                             profissional_id  BIGINT        NOT NULL REFERENCES profissional (id),
                             inicio           TIMESTAMP     NOT NULL,
                             fim              TIMESTAMP     NOT NULL,
                             status           VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE'
                                 CHECK (status IN ('PENDENTE','CONFIRMADO','CONCLUIDO','CANCELADO')),
                             observacoes      VARCHAR(500),
                             CHECK (fim > inicio)
);

-- índices para as consultas mais usadas
CREATE INDEX idx_agendamento_prof_inicio ON agendamento (profissional_id, inicio);
CREATE INDEX idx_agendamento_cliente     ON agendamento (cliente_id);
CREATE INDEX idx_horario_prof_dia        ON horario_trabalho (profissional_id, dia_semana);