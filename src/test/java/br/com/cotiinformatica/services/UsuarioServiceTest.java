package br.com.cotiinformatica.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import br.com.cotiinformatica.exceptions.AcessoNegadoException;
import br.com.cotiinformatica.exceptions.EmailJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.github.javafaker.Faker;
import br.com.cotiinformatica.components.JwtBearerComponent;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.repositories.UsuarioRepository;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioServiceTest {
    //Atributos que são mockados no teste
    private UsuarioRepository usuarioRepository;
    private JwtBearerComponent jwtBearerComponent;

    @BeforeEach
    public void setUp() {
        //Método para configurar e preparar os testes
        //Criando os MOCKS (simulações)
        usuarioRepository = mock(UsuarioRepository.class);
        jwtBearerComponent = mock(JwtBearerComponent.class);
    }

    @Test
    @Order(1)
    @DisplayName("Deve criar um usuário com sucesso.")
    public void testCriarUsuarioComSucesso() {

        //Arrange
        var usuarioService = new UsuarioService(usuarioRepository, jwtBearerComponent);

        var faker = new Faker();

        //Dados de entrada (requisição)
        var request = new CriarUsuarioRequest();
        request.setNome(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setSenha(faker.internet().password());

        //Dados do usuário que será gravado no banco de dados
        var usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());

        //Definindo o comportamento esperado do repositório (mock)
        //Quando o email informado for verificado no banco entao ele não está cadastrado
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        //Qualquer usuário gravado no banco deve retornar os dados do objeto 'usuario'
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        //Act (Ação)
        var response = usuarioService.criarUsuario(request);

        //Arrange (Verificações / Asserções)
        assertNotNull(response);

        //Verificando se os dados do usuário gravado no banco foram retornados
        assertEquals(usuario.getId(), response.getId());
        assertEquals(usuario.getNome(), response.getNome());
        assertEquals(usuario.getEmail(), response.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Deve retornar erro quando tenta criar um usuário com email já existente.")
    public void testCriarUsuarioComEmailJaExistente() {

        var usuarioService = new UsuarioService(usuarioRepository, jwtBearerComponent);

        //Gerando um usuário para teste
        var faker = new Faker();

        //Dados de entrada (requisição)
        var request = new CriarUsuarioRequest();
        request.setNome(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setSenha(faker.internet().password());

        //Definindo o comportamento esperado do repositório (mock)
        //Quando o email informado for verificado ele já deverá estar cadastrado no banco
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        //Executando a criação do usuário e verificar se foi retornado uma exceção
        var exception = assertThrows(
                EmailJaCadastradoException.class, () -> {
                    //Ação que deverá retornar a exceção
                    usuarioService.criarUsuario(request);
                }
        );

        //Verificar a mensagem de erro obtida
        assertEquals("O email '" + request.getEmail() + "' já está cadastrado. Tente outro.", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Deve autenticar um usuário com sucesso.")
    public void testAutenticarUsuarioComSucesso() {
        //Arrange
        var usuarioService = new UsuarioService(usuarioRepository, jwtBearerComponent);

        var faker = new Faker();

        //Dados de entrada (requisição)
        var request = new AutenticarUsuarioRequest();
        request.setEmail(faker.internet().emailAddress());
        request.setSenha(faker.internet().password());

        //Dados do usuário
        var usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome(faker.name().fullName());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());

        //Definindo o comportamento esperado para gerar o tempo de expiração do TOKEN
        when(jwtBearerComponent.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        //Definindo o comportamento esperado para gerar o TOKEN
        when(jwtBearerComponent.getAccessToken(usuario.getId().toString(), "USER")).thenReturn("jwt_bearer_token");

        //Definindo o comportamento esperado do repositório (mock)
        when(usuarioRepository.findByEmailAndSenha(request.getEmail(), request.getSenha()))
                .thenReturn(Optional.of(usuario));

        //Act (Ação)
        var response = usuarioService.autenticarUsuario(request);

        //Arrange (Verificações / Asserções)
        assertNotNull(response);

        //Verificando se os dados do usuário gravado no banco foram retornados
        assertEquals(usuario.getId(), response.getId());
        assertEquals(usuario.getNome(), response.getNome());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertNotNull(response.getAccessToken());
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar acesso negado quando tenta autenticar um usuário inválido.")
    public void testAutenticarUsuarioComAcessoNegado() {
        var usuarioService = new UsuarioService(usuarioRepository, jwtBearerComponent);

        //Gerando um usuário para teste
        var faker = new Faker();

        //Dados de entrada (requisição)
        var request = new AutenticarUsuarioRequest();
        request.setEmail(faker.internet().emailAddress());
        request.setSenha(faker.internet().password());

        when(usuarioRepository.findByEmailAndSenha(request.getEmail(), request.getSenha()))
                .thenReturn(Optional.empty());

        //Executando a autenticação do usuário e verificar se foi retornado uma exceção
        var exception = assertThrows(
                AcessoNegadoException.class, () -> {
                    //Ação que deverá retornar a exceção
                    usuarioService.autenticarUsuario(request);
                }
        );

        //Verificar a mensagem de erro obtida
        assertEquals("Acesso negado. Usuário inválido.", exception.getMessage());
    }
}



