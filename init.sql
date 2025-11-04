CREATE DATABASE IF NOT EXISTS sistema_escolar;
USE sistema_escolar;

CREATE TABLE Pessoa (
    cpf VARCHAR(11) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nascimento DATE,
    email VARCHAR(255) NOT NULL UNIQUE,
    endereco VARCHAR(255),
    senha VARCHAR(255) NOT NULL,
    tipo_pessoa ENUM('ALUNO', 'PROFESSOR', 'MONITOR') NOT NULL
);

CREATE TABLE Aluno (
    cpf VARCHAR(11) PRIMARY KEY,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    curso VARCHAR(100),
    FOREIGN KEY (cpf) REFERENCES Pessoa(cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Professor (
    cpf VARCHAR(11) PRIMARY KEY,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    formacao VARCHAR(255),
    FOREIGN KEY (cpf) REFERENCES Pessoa(cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Monitor (
    cpf VARCHAR(11) PRIMARY KEY,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    curso VARCHAR(100),
    FOREIGN KEY (cpf) REFERENCES Pessoa(cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Turma (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE,
    data_fim DATE,
    id_turma_pai INT,
    FOREIGN KEY (id_turma_pai) REFERENCES Turma(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE Atividade (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE,
    data_fim DATE,
    valor DECIMAL(5, 2) COMMENT 'Valor/pontos base deste tipo de atividade'
);

CREATE TABLE Tarefa (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    id_turma INT NOT NULL,
    id_atividade INT NOT NULL,
    nota DECIMAL(5, 2) COMMENT 'Pontuação máxima para esta tarefa específica na turma',
    FOREIGN KEY (id_turma) REFERENCES Turma(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (id_atividade) REFERENCES Atividade(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Turma_Participantes (
    id_turma INT NOT NULL,
    cpf_pessoa VARCHAR(11) NOT NULL,
    PRIMARY KEY (id_turma, cpf_pessoa),
    FOREIGN KEY (id_turma) REFERENCES Turma(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (cpf_pessoa) REFERENCES Pessoa(cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Nota_Aluno (
    id INT PRIMARY KEY,
    id_tarefa INT NOT NULL,
    cpf_aluno VARCHAR(11) NOT NULL,
    nota_obtida DECIMAL(5, 2) NOT NULL,
    data_entrega DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_tarefa) REFERENCES Tarefa(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (cpf_aluno) REFERENCES Aluno(cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    UNIQUE KEY uq_aluno_tarefa (id_tarefa, cpf_aluno)
);
