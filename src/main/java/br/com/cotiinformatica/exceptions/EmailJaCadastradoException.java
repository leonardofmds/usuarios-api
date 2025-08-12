package br.com.cotiinformatica.exceptions;

public class EmailJaCadastradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String email;

    public EmailJaCadastradoException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return "O email '" + email + "' já está cadastrado. Tente outro.";
    }
}
