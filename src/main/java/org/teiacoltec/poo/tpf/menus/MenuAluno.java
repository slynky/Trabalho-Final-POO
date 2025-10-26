package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuAluno extends Menu {

    private final List<Turma> turmas;

    public MenuAluno(List<Turma> turmas) {
        this.turmas = turmas;
    }

    @Override
    protected String getNomeEntidade() {
        return "Aluno";
    }

    @Override
    protected void criar() {
        if (turmas.isEmpty()) {
            System.out.println("É necessário existir pelo menos uma turma para adicionar um aluno.");
            return;
        }

        System.out.println("\n--- Criar Novo " + getNomeEntidade() + " ---");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Data de Nascimento (dd/mm/aaaa): ");
        String nascimento = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        System.out.print("Matrícula: ");
        String matricula = scanner.nextLine();
        System.out.print("Curso: ");
        String curso = scanner.nextLine();
        System.out.print("Defina uma senha para o aluno: ");
        String senha = scanner.nextLine();

        System.out.print("Digite o ID da turma na qual o aluno será inserido: ");
        int turmaId = Integer.parseInt(scanner.nextLine());

        Optional<Turma> turmaOpt = turmas.stream().filter(t -> t.getId() == turmaId).findFirst();

        if (turmaOpt.isPresent()) {
            Aluno novoAluno = new Aluno(cpf, nome, nascimento, email, endereco, senha, matricula, curso);
            turmaOpt.get().adicionarParticipante(novoAluno);
            System.out.println(getNomeEntidade() + " " + nome + " criado e adicionado à turma " + turmaOpt.get().getNome() + " com sucesso!");
        } else {
            System.out.println("Erro: Turma com ID " + turmaId + " não encontrada.");
        }
    }

    /**
     * Este metodo funciona como um menu para as opções de listagem.
     * Ele delega a ação para métodos privados com base na escolha do usuário.
     */
    @Override
    protected void ler() {
        System.out.println("\n--- Opções de Listagem de Alunos ---");
        System.out.println("""
                tudo  - Listar todos os alunos cadastrados, agrupados por turma.
                matri - Buscar um aluno específico por sua matrícula.
                """);
        System.out.print("Digite sua escolha: ");
        String escolha = scanner.nextLine().trim().toLowerCase();

        switch (escolha) {
            case "tudo":
                listarTodosOsAlunos();
                break;
            case "matri":
                buscarAlunoPorMatricula();
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    private void listarTodosOsAlunos() {
        System.out.println("\n--- Lista Completa de Alunos ---");
        if (turmas.isEmpty()) {
            System.out.println("Nenhuma turma, e portanto nenhum aluno, cadastrado.");
            return;
        }

        turmas.forEach(turma -> {
            System.out.println("\n# Turma: " + turma.getNome() + " (ID: " + turma.getId() + ")");
            // O método getParticipantes() agora retorna uma List<Pessoa>.
            List<Pessoa> participantes = turma.getParticipantes();

            // Filtra e coleta apenas os alunos da lista de participantes.
            List<Aluno> alunosNaTurma = participantes.stream()
                    .filter(p -> p instanceof Aluno)
                    .map(p -> (Aluno) p)
                    .collect(Collectors.toList());

            // Usa .isEmpty() para verificar se a lista de alunos está vazia.
            if (alunosNaTurma.isEmpty()) {
                System.out.println("  (Nenhum aluno nesta turma)");
            } else {
                alunosNaTurma.forEach(aluno -> System.out.println("  -> " + aluno.obterInformacoes()));
            }
        });
    }

    private void buscarAlunoPorMatricula() {
        System.out.print("\nDigite a matrícula do aluno a ser buscado: ");
        String matricula = scanner.nextLine();

        boolean encontrado = false;
        for (Turma turma : turmas) {
            // Usa .stream() diretamente na lista retornada por getParticipantes().
            Optional<Aluno> alunoOpt = turma.getParticipantes().stream()
                    .filter(p -> p instanceof Aluno)
                    .map(p -> (Aluno) p)
                    .filter(a -> a.getMatricula().equalsIgnoreCase(matricula))
                    .findFirst();

            if (alunoOpt.isPresent()) {
                System.out.println("\n--- Aluno Encontrado ---");
                System.out.println(alunoOpt.get().obterInformacoes());
                System.out.println("--> Pertence à Turma: " + turma.getNome() + " (ID: " + turma.getId() + ")");
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("\nAluno com a matrícula '" + matricula + "' não foi encontrado em nenhuma turma.");
        }
    }


    @Override
    protected void atualizar() {
        System.out.println("\n--- Atualizar " + getNomeEntidade() + " ---");
        System.out.print("Digite a matrícula do aluno a ser atualizado: ");
        String matricula = scanner.nextLine();

        // O flatMap agora opera diretamente no stream da lista de participantes.
        Optional<Aluno> alunoOpt = turmas.stream()
                .flatMap(turma -> turma.getParticipantes().stream())
                .filter(p -> p instanceof Aluno)
                .map(p -> (Aluno) p)
                .filter(a -> a.getMatricula().equals(matricula))
                .findFirst();

        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            System.out.println("Encontrado: " + aluno.getNome() + ". Deixe em branco para não alterar.");

            System.out.print("Novo Email (" + aluno.getEmail() + "): ");
            String email = scanner.nextLine();
            if(!email.isBlank()) aluno.setEmail(email);

            System.out.print("Novo Endereço (" + aluno.getEndereco() + "): ");
            String endereco = scanner.nextLine();
            if(!endereco.isBlank()) aluno.setEndereco(endereco);

            System.out.println("Aluno atualizado com sucesso!");
        } else {
            System.out.println("Aluno com matrícula " + matricula + " não encontrado em nenhuma turma.");
        }
    }

    @Override
    protected void deletar() {
        System.out.println("\n--- Deletar " + getNomeEntidade() + " ---");
        System.out.print("Digite a matrícula do aluno a ser deletado: ");
        String matricula = scanner.nextLine();

        boolean removidoComSucesso = false;
        // Itera por cada turma para tentar remover o aluno.
        for (Turma turma : turmas) {
            // Delega a lógica de remoção para o método da própria classe Turma.
            // Este método é mais eficiente e encapsulado.
            if (turma.removerParticipantePorMatricula(matricula)) {
                System.out.println("Aluno deletado da turma '" + turma.getNome() + "' com sucesso!");
                removidoComSucesso = true;
                break; // Encerra o loop pois o aluno foi encontrado e removido.
            }
        }

        if (!removidoComSucesso) {
            System.out.println("Aluno com matrícula " + matricula + " não encontrado em nenhuma turma.");
        }
    }
}