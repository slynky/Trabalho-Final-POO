package org.teiacoltec.poo.tpf.escolares.membrosEscolares;

import org.teiacoltec.poo.tpf.pessoa.Pessoa;

public class Professor extends Pessoa{

    private String matricula;
    private String formacao;

    public Professor(String cpf, String nome, String nascimento, String email, String endereco, String matricula, String formacao,String senha) {
        super(cpf, nome, nascimento, email, endereco, senha);
        this.matricula = matricula;
        this.formacao = formacao;
    }

    //construtor de copia
    public Professor(Professor outro) {
        super(outro);
        this.matricula = outro.matricula;
        this.formacao = outro.formacao;
    }


    public String getMatricula() {
        return matricula;
    }

    public String getFormacao() {
        return formacao;
    }

    @Override
    public String toString(){
        return "[Monitor]: "+ super.toString() + "Matrícula: " + getMatricula() + "\nFormação: " + getFormacao() + "\n";
    }

}
