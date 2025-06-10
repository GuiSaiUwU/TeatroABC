package br.eng.dgjl.teatro.classes;

import br.com.caelum.stella.validation.CPFValidator;

import java.util.Locale;

public class Utility {
    static CPFValidator cpfValidator = new CPFValidator();

    /**
    * Checa se um nome é valido, nomes invalidos contem números e/ou caracteres invalidos
    * @param name Nome do usuário
    * @return booleano que indica a validez
    */
    static public boolean checkName(String name) {
        if (name.isEmpty()) return false;
        if (name.chars().anyMatch(Character::isDigit)) return false;

        char[] caracteresBanidos = "!\"'@#$%¨&*()-=_+?/|'{}[]`´~^\\;:,.".toCharArray();

        for (char caracteresBanido : caracteresBanidos) {
            if (name.indexOf(caracteresBanido) >= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checa se o CPF é valido utilizando o biblioteca stella-core
     * @param CPF CPF do usuário
     * @return booleano que indica a validez
     */
    static public boolean checkCPF(String CPF) {
        boolean isValid;
        try {
            cpfValidator.assertValid(CPF);
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean checkTelefone(String telefone) {
        if (telefone.length() != 11) return false;
        if (telefone.charAt(2) != '9') return false;

        return true;
    }
}