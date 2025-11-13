package org.teiacoltec.poo.tpf.pessoa;

import org.teiacoltec.poo.tpf.util.Criptografar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Pessoa {

    protected String cpf;
    protected String nome;
    protected LocalDate nascimento;
    protected String email;
    protected String endereco;
    protected String senha;

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Pessoa(String cpf, String nome, String dataDeNascimento, String email, String endereco, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.nascimento = LocalDate.parse(dataDeNascimento, FORMATADOR); // converte string em LocalDate
        this.email = email;
        this.endereco = endereco;
        this.senha = Criptografar.hashSenhaMD5(senha);
    }

    // Construtor de Cópia
    public Pessoa(Pessoa outra) {
        this.senha = outra.senha;
        this.cpf = outra.cpf;
        this.nome = outra.nome;
        this.nascimento = outra.nascimento;
        this.email = outra.email;
        this.endereco = outra.endereco;
    }



    public String obterInformacoes() {
        return this.toString();
    }
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getNascimento() { return String.valueOf(nascimento); }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getSenha() {
        return senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("CPF: ").append(cpf).append("\n");
        sb.append("Nascimento: ").append(nascimento.format(FORMATADOR)).append("\n"); // formata LocalDate como string
        sb.append("Email: ").append(email).append("\n");
        sb.append("Endereço: ").append(endereco).append("\n");
        return sb.toString();
    }
}
