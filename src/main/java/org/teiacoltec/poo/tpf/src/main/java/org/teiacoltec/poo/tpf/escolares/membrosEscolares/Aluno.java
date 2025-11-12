package org.teiacoltec.poo.tpf.escolares.membrosEscolares;

import org.teiacoltec.poo.tpf.pessoa.Pessoa;

public class Aluno extends Pessoa{

    private String matricula;
    private String curso;

    public Aluno(String cpf, String nome, String nascimento, String email, String endereco, String matricula, String curso, String senha) {
        super(cpf, nome, nascimento, email, endereco, senha);
        this.matricula = matricula;
        this.curso = curso;
    }

    public Aluno(Aluno outro) {
        super(outro);
        this.matricula = outro.matricula;
        this.curso = outro.curso;
    }

    @Override
    public String obterInformacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("Aluno {\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("CPF: ").append(cpf).append("\n");
        sb.append("Nascimento: ").append(nascimento).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Endereço: ").append(endereco).append("\n");
        sb.append("Matrícula: ").append(matricula).append("\n");
        sb.append("Curso: ").append(curso).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCurso() {
        return curso;
    }

    @Override
    public String toString() {
        // Chama o toString() da classe Pessoa e adiciona os campos específicos de Aluno
        return "[Aluno] " + super.toString() + "Matrícula: " + this.matricula + "\n Curso: " + this.curso + "\n";
    }
}
