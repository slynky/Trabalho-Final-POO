package org.teiacoltec.poo.tpf;

import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.exceptions.CredenciaisInvalidasException;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.menus.MainFrame;
import org.teiacoltec.poo.tpf.menus.MenuAlunoLogado;
import org.teiacoltec.poo.tpf.menus.MenuMonitorLogado;
import org.teiacoltec.poo.tpf.menus.MenuProfessorLogado;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;
import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.menus.MainFrame;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static List<Atividade> atividades = new  ArrayList<>();
    private static List<Turma> turmas = new ArrayList<>();


    public static void main(String[] args) throws SQLException {
        popularDadosIniciais();

        MainFrame mainFrame = new MainFrame(obterTodosOsUsuarios(turmas));


    }

    private static List<Pessoa> obterTodosOsUsuarios(List<Turma> turmas) {
        return turmas.stream()
                .flatMap(turma -> turma.getParticipantes().stream())
                .collect(Collectors.toList());
    }



    private static void popularDadosIniciais() {
        turmas.clear();


        Professor p1 = new Professor("12345678900", "Carlos Pereira", "10/05/1985", "carlos.p@escola.edu", "Rua das Flores, 123", "P001", "Doutorado", "1");
        Aluno a1 = new Aluno("11122233344", "Beatriz Costa", "20/03/2005", "bia.costa@email.com", "Av. Principal, 456", "A001", "Ciência da Computação", "1");
        Monitor m1 = new Monitor("44455566677", "Lucas Martins", "28/05/1999", "lucas.m@email.com", "Travessa dos Sonhos, 78", "M001", "Ciência da Computação", "1");

        Turma turmaPrincipal = new Turma(1, "Desenvolvimento de Software 2025", "POO e Java", "01/08/2025", "15/12/2025", null, null);
        turmaPrincipal.adicionarParticipante(p1);
        turmaPrincipal.adicionarParticipante(m1);

        turmas.add(turmaPrincipal);
    }
}