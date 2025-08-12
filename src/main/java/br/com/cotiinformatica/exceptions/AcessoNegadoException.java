package br.com.cotiinformatica.exceptions;

public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    @Override
    public String getMessage() {
        return "Acesso negado. Usuário inválido.";
    }
}
