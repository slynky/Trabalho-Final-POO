package org.teiacoltec.poo.tpf.escolares;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Atividade{

    private final int id;
    private String nome;
    private String desc;
    private LocalDate inicio;
    private LocalDate fim;
    private float valor;

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Atividade(int id, String nome, String desc, String inicio, String fim, float valor) {
        this.id = id;
        this.nome = nome;
        this.desc = desc;
        this.inicio = LocalDate.parse(inicio, FORMATADOR);
        this.fim = LocalDate.parse(fim, FORMATADOR);
        this.valor = valor;
    }

    // Construtor de Cópia
    public Atividade(Atividade outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.desc = outra.desc;
        this.inicio = outra.inicio;
        this.fim = outra.fim;
        this.valor = outra.valor;
    }


    //modificadores

    public int getId() {return this.id;}
    public String getNome() {return this.nome;}
    public String getDesc() {return this.desc;}
    public LocalDate getInicio() {return this.inicio;}
    public LocalDate getFim() {return this.fim;}
    public float getValor() {return this.valor;}

    public void setNome(String nome) {this.nome = nome;}
    public void setDesc(String desc) {this.desc = desc;}
    public void setInicio(String inicio) {this.inicio = LocalDate.parse(inicio, FORMATADOR);}
    public void setFim(String fim) {this.fim = LocalDate.parse(fim, FORMATADOR);}
    public void setValor(float valor) {this.valor = valor;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Atividade ID: ").append(id).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Descrição: ").append(desc).append("\n");
        sb.append("Início: ").append(inicio.format(FORMATADOR)).append("\n");
        sb.append("Fim: ").append(fim.format(FORMATADOR)).append("\n");
        sb.append("Valor: ").append(valor).append("\n");
        return sb.toString();
    }

}
