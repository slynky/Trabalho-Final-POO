package org.teiacoltec.poo.tpf.util;

import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarregarAoBanco {

    List<Turma> turmas = new ArrayList();

    public CarregarAoBanco(List<Turma> turmas) {
        this.turmas = turmas;
    }

    public void Salvar() { //use para promover um salvamento automatico antes do fechamento ou apenas use uma janela pop-up com a mensagem(deseja salvar as alteracoes?)
        try {
            for (Turma t : turmas) {
                if(!TurmaDAO.obterTurmaPorId(t.getId()).isPresent())
                    TurmaDAO.inserirTurma(t);
                for (Atividade a : t.getAtividades()) {
                    try {
                        if(!AtividadeDAO.buscarPorId(a.getId()).isPresent())
                        AtividadeDAO.inserirAtividade(a);
                    } catch (SQLException eAtividade) {
                        System.err.println("Erro ao salvar a atividade ID: " + a.getId() + " da turma " + t.getId());
                    }
                }
                for (Tarefa ta : t.getTarefas()) {
                    try {
                        if (!TarefaDAO.buscarPorId(ta.getId()).isPresent())
                        TarefaDAO.inserirTarefa(ta);
                    } catch (SQLException eTarefa) {
                        System.err.println("Erro ao salvar a tarefa ID: " + ta.getId() + " da turma " + t.getId());
                    }
                }
            }
        } catch (SQLException eTurma) {
            System.err.println("Erro ao salvar a turma no banco de dados: " + eTurma.getMessage());
        }
    }

}
