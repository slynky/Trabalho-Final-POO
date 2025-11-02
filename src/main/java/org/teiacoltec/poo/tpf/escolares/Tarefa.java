package org.teiacoltec.poo.tpf.escolares;

import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;

public class Tarefa{

    private int id;
    private String nome;
    private Turma turma;
    private Atividade atividade;
    private float nota;


   public Tarefa(int id, String nome, Turma turma, Atividade atividade, float nota) {
        this.id = id;
        this.nome = nome;
        this.turma = turma;
        this.atividade = atividade;
        this.nota = nota;
   }
   // Getters e setters
   public void setNome(String nome) {
        this.nome = nome;
    }
   public void setNota(float nota) {
        this.nota = nota;
    }

   public int getId() {return id;}
   public String getNome() {return nome;}
    public float getNota() {return nota;}

    public int getTurmaId() {return turma.getId();}
    public int getAtividadeId() {return atividade.getId();}

    public Tarefa(Tarefa outra) {
       this.id = outra.id;
        this.nome = outra.nome;
        this.turma = outra.turma;
        this.atividade = outra.atividade;
        this.nota = outra.nota;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tarefa ID: ").append(id).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Turma: ").append(turma.getNome()).append("\n");
        sb.append("Atividade:\n").append(atividade);
        sb.append("Nota: ").append(nota).append("\n");

        return sb.toString();
    }

}

