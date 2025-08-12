package br.com.cotiinformatica.services;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import br.com.cotiinformatica.components.JwtBearerComponent;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponse;
import br.com.cotiinformatica.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.exceptions.AcessoNegadoException;
import br.com.cotiinformatica.exceptions.EmailJaCadastradoException;
import br.com.cotiinformatica.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtBearerComponent jwtBearerComponent;

    /*
     * Método para implementar o cadastro do usuário
     */
    public CriarUsuarioResponse criarUsuario(CriarUsuarioRequest request) {

        //Verificando se o email já está cadastrado
        if(usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        //Capturando os dados do usuário
        var usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setNome(request.getEmail());
        usuario.setNome(request.getSenha());

        //Gravando no banco de dados
        var usuarioCriado = usuarioRepository.save(usuario);

        //Retornando os dados da resposta
        var response = new CriarUsuarioResponse();
        response.setId(usuarioCriado.getId());
        response.setNome(usuarioCriado.getNome());
        response.setEmail(usuarioCriado.getEmail());
        response.setDataHoraCadastro(LocalDateTime.now());

        return response;
    }

    /*
     * Método para implementar o cadastro do usuário
     */
    public AutenticarUsuarioResponse autenticarUsuario(AutenticarUsuarioRequest request) {
        //Buscando o usuário no banco de dados através do email e da senha
        var usuario = usuarioRepository.findByEmailAndSenha(request.getEmail(), request.getSenha())
                .orElseThrow(() -> new AcessoNegadoException());

        var expiration = jwtBearerComponent.getExpiration();
        LocalDateTime dataHoraExpiracao = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        var accessToken = jwtBearerComponent.getAccessToken(usuario.getId().toString(), "USER");

        //Retornando os dados do usuário
        var response = new AutenticarUsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setDataHoraAcesso(LocalDateTime.now());
        response.setDataHoraExpiracao(dataHoraExpiracao);
        response.setAccessToken(accessToken);

        return response;
    }
}



