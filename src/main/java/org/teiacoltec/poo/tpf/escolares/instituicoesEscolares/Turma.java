package org.teiacoltec.poo.tpf.escolares.instituicoesEscolares;

import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.exceptions.PessoaNaoEncontradaException;
import org.teiacoltec.poo.tpf.exceptions.PessoaJaParticipanteException;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;
import org.teiacoltec.poo.tpf.escolares.Atividade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa uma turma em uma instituição de ensino.
 * Agrega participantes, atividades, tarefas e pode ter uma estrutura hierárquica
 * com turmas pai e filhas.
 */
public class Turma {

    protected int id;
    protected String nome;
    protected String desc;
    protected LocalDate inicio;
    protected LocalDate fim;
    protected Turma turmaPai;
    protected List<Turma> turmasFilhas = new ArrayList<>();
    protected List<Pessoa> participantes = new ArrayList<>();
    protected List<Atividade> atividades = new ArrayList<>();
    protected List<Tarefa> tarefas = new ArrayList<>();
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Turma professor;

    // --- Construtores ---
    public Turma(int id, String nome, String desc, String dataInicio, String dataFim, ArrayList<Pessoa> participantesIniciais, Turma turmaPai) {
        this.id = id;
        this.nome = nome;
        this.desc = desc;
        this.inicio = LocalDate.parse(dataInicio, FORMATADOR);
        this.fim = LocalDate.parse(dataFim, FORMATADOR);
        this.turmaPai = turmaPai;
        if (this.turmaPai != null) {
            this.turmaPai.associaSubturma(this);
        }
        if (participantesIniciais != null) {
            this.participantes = participantesIniciais;
        }
    }

    public Turma(Turma outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.desc = outra.desc;
        this.inicio = outra.inicio;
        this.fim = outra.fim;
        this.turmaPai = outra.turmaPai;
        this.participantes = outra.participantes.stream()
                .map(p -> {
                    if (p instanceof Aluno a) return new Aluno(a);
                    if (p instanceof Professor prof) return new Professor(prof);
                    if (p instanceof Monitor m) return m;
                    return null;
                })
                .collect(Collectors.toList());
        this.atividades = outra.atividades.stream().map(Atividade::new).collect(Collectors.toList());
        this.tarefas = outra.tarefas.stream().map(Tarefa::new).collect(Collectors.toList());
        this.turmasFilhas = outra.turmasFilhas.stream().map(Turma::new).collect(Collectors.toList());
    }


    // --- Getters e Setters  ---
    public String getInicio() { return String.valueOf(inicio); }
    public String getFim() { return String.valueOf(fim); }
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return this.nome; }
    public List<Professor> getProfessoresResponsaveis() {
        return participantes.stream()
                .filter(p -> p instanceof Professor)
                .map(p -> (Professor) p)
                .collect(Collectors.toList());
    }
    public String getDesc() { return this.desc; }
    public Turma getTurmaPai() { return this.turmaPai; }
    public List<Pessoa> getParticipantes() { return new ArrayList<>(this.participantes); }
    public List<Atividade> getAtividades() { return new ArrayList<>(this.atividades); }
    public List<Tarefa> getTarefas() { return new ArrayList<>(this.tarefas); }
    public void setNome(String nome) { this.nome = nome; }
    public void setDesc(String desc) { this.desc = desc; }


    //Metodos adicionais
    public void adicionarParticipante(Pessoa p) {
        if (participantes.contains(p)) { throw new PessoaJaParticipanteException("A pessoa " + p.getNome() + " já está na turma."); }
        participantes.add(p);
    }
    public void removerParticipante(Pessoa p) {
        if (!participantes.remove(p)) { throw new PessoaNaoEncontradaException("O participante não se encontra na turma."); }
    }
    public boolean participa(Pessoa p) { return participantes.contains(p); }
    public boolean isParticipante(String cpf) { return participantes.stream().anyMatch(p -> p.getCpf().equals(cpf)); }
    public void associaSubturma(Turma t) { if (!turmasFilhas.contains(t)) { turmasFilhas.add(t); } }
    public void adicionarAtividade(Atividade atividade) { atividades.add(atividade); }
    public void adicionarTarefa(Tarefa tarefa) { tarefas.add(tarefa); }
    public boolean removerTarefa(int idTarefa) { return tarefas.removeIf(tarefa -> tarefa.getId() == idTarefa); }
    public boolean removerAtividade(int idAtividade) { return atividades.removeIf(atividade -> atividade.getId() == idAtividade); }
    public boolean removerParticipantePorMatricula(String matricula) { return participantes.removeIf(p -> { if (p instanceof Professor prof) return prof.getMatricula().equalsIgnoreCase(matricula); if (p instanceof Aluno aluno) return aluno.getMatricula().equalsIgnoreCase(matricula); if (p instanceof Monitor monitor) return monitor.getMatricula().equalsIgnoreCase(matricula); return false; }); }
    public void listarParticipantes() { System.out.println("\n--- Participantes da Turma: " + this.nome + " ---"); if (participantes.isEmpty()) { System.out.println("(Nenhum participante nesta turma)"); return; } this.participantes.forEach(p -> { String tipo = p.getClass().getSimpleName(); System.out.printf("- [%s] %s (CPF: %s)\n", tipo, p.getNome(), p.getCpf()); }); }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turma {\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Nome: ").append(nome).append("\n");
        sb.append("  Descrição: ").append(desc).append("\n");

        List<Professor> professores = getProfessoresResponsaveis();
        if (professores.isEmpty()) {
            sb.append("  Professor(es) Responsável(is): Nenhum definido\n");
        } else {
            String nomesProfessores = professores.stream()
                    .map(Pessoa::getNome)
                    .collect(Collectors.joining(", "));
            sb.append("  Professor(es) Responsável(is): ").append(nomesProfessores).append("\n");
        }

        sb.append("  Início: ").append(inicio.format(FORMATADOR)).append("\n");
        sb.append("  Fim: ").append(fim.format(FORMATADOR)).append("\n");
        sb.append("  Turma Pai: ").append(turmaPai != null ? turmaPai.nome : "Nenhuma").append("\n");

        if (!turmasFilhas.isEmpty()) {
            sb.append("  Turmas Filhas:\n");
            turmasFilhas.forEach(tf -> sb.append("    - ID: ").append(tf.id).append(", Nome: ").append(tf.nome).append("\n"));
        } else {
            sb.append("  Turmas Filhas: Nenhuma\n");
        }

        sb.append("  Participantes: ").append(participantes.size()).append("\n");
        sb.append("  Atividades: ").append(atividades.size()).append("\n");
        sb.append("  Tarefas: ").append(tarefas.size()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public Turma getProfessor() {
        return professor;
    }

    public void setProfessor(Turma professor) {
        this.professor = professor;
    }


}