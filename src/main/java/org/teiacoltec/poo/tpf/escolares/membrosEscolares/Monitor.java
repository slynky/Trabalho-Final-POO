package org.teiacoltec.poo.tpf.escolares.membrosEscolares;

import org.teiacoltec.poo.tpf.pessoa.Pessoa;

public class Monitor extends Pessoa{

    private String matricula;
    private String curso;

    // Construtor para inicializar os atributos herdados e os específicos da classe
    public Monitor(String nome, String cpf, String dataNascimento, String email, String endereco, String matricula, String curso, String senha) {
        super(nome, cpf, dataNascimento, email, endereco, senha);
        this.matricula = matricula;
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "[Monitor]: "+ super.toString() + "Matrícula: " + matricula + "\nCurso: " + curso + "\n";
    }

    // Getters e Setters para os atributos específicos
    public String getMatricula() {
        return matricula;
    }

    public String getCurso() {
        return curso;
    }
}