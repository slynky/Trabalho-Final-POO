package org.teiacoltec.poo.tpf.util;

import org.teiacoltec.poo.tpf.exceptions.CredenciaisInvalidasException;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Autenticacao {

    /**
     * Autentica um usuário com base no login (CPF) e senha.
     *
     * @param login O CPF do usuário.
     * @param senha A senha do usuário.
     * @param usuarios A lista completa de usuários cadastrados no sistema.
     * @return O objeto Pessoa correspondente se as credenciais forem válidas.
     * @throws CredenciaisInvalidasException Se o login ou a senha estiverem incorretos.
     */
    public static Pessoa autenticar(String login, String senhaRecebida, List<Pessoa> usuarios) throws CredenciaisInvalidasException {
        final String senha = Criptografar.hashSenhaMD5(senhaRecebida);

        Optional<Pessoa> usuarioEncontrado = usuarios.stream()
                .filter(user -> Objects.equals(user.getCpf(), login) && Objects.equals(user.getSenha(), senha))
                .findFirst();


        return usuarioEncontrado.orElseThrow(() -> new CredenciaisInvalidasException("CPF ou senha inválidos."));
    }

    /**
     * Encerra a sessão do usuário. Em um app de console, isso significa
     * retornar ao loop de login.
     *
     * @param usuario O usuário que está fazendo logout.
     */
    public static void logout(Pessoa usuario) {
        if (usuario != null) {
            System.out.println(">>> " + usuario.getNome() + " deslogado com sucesso.");
        }
    }
}