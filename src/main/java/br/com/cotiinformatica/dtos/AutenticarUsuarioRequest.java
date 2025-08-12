package br.com.cotiinformatica.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class AutenticarUsuarioRequest {
	@NotBlank(message = "O e-mail é obrigatório.")
	@Email(message = "Informe um e-mail válido.")
	private String email;
	@NotBlank(message = "A senha é obrigatória.")
	@Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres.")
	private String senha;
}



