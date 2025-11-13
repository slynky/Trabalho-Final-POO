package org.teiacoltec.poo.tpf.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografar {
    /**
     * Hashes a senha com o algoritmo MD5.
     * * @param senha A senha.
     * @return 32 caracteres de uma string hexa.
     */
    public static String hashSenhaMD5(String senha) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(senha.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            // This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
            throw new RuntimeException(e);
        }
    }
}
