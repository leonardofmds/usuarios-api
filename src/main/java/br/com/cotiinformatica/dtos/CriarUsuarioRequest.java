package br.com.cotiinformatica.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class CriarUsuarioRequest {

	@NotBlank(message = "O nome é obrigatório.")
	@Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	@NotBlank(message = "A senha é obrigatória.")
	@Size(min = 8, max = 20, message = "A senha deve ter entre 8 e 20 caracteres.")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
			message = "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial."
	)
	private String senha;
}



