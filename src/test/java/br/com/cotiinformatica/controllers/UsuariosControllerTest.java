package br.com.cotiinformatica.controllers;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponse;
import br.com.cotiinformatica.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.exceptions.AcessoNegadoException;
import br.com.cotiinformatica.exceptions.EmailJaCadastradoException;
import br.com.cotiinformatica.services.UsuarioService;
@WebMvcTest(UsuariosController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuariosControllerTest {
    @Autowired
    private MockMvc mockMvc; // objeto para realizar as chamadas da API
    @Autowired
    private ObjectMapper objectMapper; // serializar e deserializar dados
    @SuppressWarnings("removal")
    @MockBean
    private UsuarioService usuarioService; // Criando o Mock da classe de serviço
    private Faker faker; // biblioteca para geração de dados
    private String endpoint; // endereço dos serviços
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        endpoint = "/api/v1/usuarios";
    }
    @Test
    @Order(1)
    @DisplayName("Deve executar POST [/api/v1/usuarios/criar] com retorno 201 [CREATED]")
    public void testPostCriarUsuario_Sucesso() throws Exception {
        // Criando os dados da requisição
        var request = new CriarUsuarioRequest();
        request.setNome(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setSenha("@Teste2025");
        // Criando os dados da resposta
        var response = new CriarUsuarioResponse();
        response.setId(UUID.randomUUID());
        response.setNome(request.getNome());
        response.setEmail(request.getEmail());
        response.setDataHoraCadastro(LocalDateTime.now());
        // Mockar o comportamento da camada de serviço
        when(usuarioService.criarUsuario(request)).thenReturn(response);
        // Fazendo a requisição para a API
        mockMvc.perform(post(endpoint + "/criar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.nome").value(response.getNome()))
                .andExpect(jsonPath("$.email").value(response.getEmail()));
    }
    @Test
    @Order(2)
    @DisplayName("Deve executar POST [/api/v1/usuarios/criar] com retorno 400 [BADREQUEST]")
    public void testPostCriarUsuario_RequisicaoInvalida() throws Exception {
        // Criando os dados da requisição (vazio)
        var request = new CriarUsuarioRequest();
        // Fazendo a requisição para a API
        mockMvc.perform(post(endpoint + "/criar").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }
    @Test
    @Order(3)
    @DisplayName("Deve executar POST [/api/v1/usuarios/criar] com retorno 422 [UNPROCESSABLE ENTITY]")
    public void testPostCriarUsuario_EntidadeInvalida() throws Exception {
        // Criando os dados da requisição
        var request = new CriarUsuarioRequest();
        request.setNome(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setSenha("@Teste2025");
        // Mockar o comportamento da camada de serviço
        when(usuarioService.criarUsuario(request))
                .thenThrow(new EmailJaCadastradoException(request.getEmail()));

        // Fazendo a requisição para a API para criar o mesmo usuário novamente
        mockMvc.perform(post(endpoint + "/criar").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isUnprocessableEntity());
    }
    @Test
    @Order(4)
    @DisplayName("Deve executar POST [/api/v1/usuarios/autenticar] com retorno 200 [OK]")
    public void testPostAutenticarUsuario_Sucesso() throws Exception {
        // Criando os dados da requisição
        var request = new AutenticarUsuarioRequest();
        request.setEmail(faker.internet().emailAddress());
        request.setSenha(faker.internet().password());
        // Criando os dados da resposta
        var response = new AutenticarUsuarioResponse();
        response.setId(UUID.randomUUID());
        response.setNome(faker.name().fullName());
        response.setEmail(request.getEmail());
        response.setAccessToken("<TOKEN>");
        // Mockar o comportamento da camada de serviço
        when(usuarioService.autenticarUsuario(request)).thenReturn(response);
        // Fazendo a requisição para a API
        mockMvc.perform(post(endpoint + "/autenticar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.nome").value(response.getNome()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()));
    }
    @Test
    @Order(5)
    @DisplayName("Deve executar POST [/api/v1/usuarios/autenticar] com retorno 400[BADREQUEST]")
    public void testPostAutenticarUsuario_RequisicaoInvalida() throws Exception {
        // Criando os dados da requisição (vazio)
        var request = new AutenticarUsuarioRequest();
        // Fazendo a requisição para a API
        mockMvc.perform(post(endpoint + "/autenticar").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }
    @Test
    @Order(6)
    @DisplayName("Deve executar POST [/api/v1/usuarios/autenticar] com retorno 401[UNAUTHORIZED]")
    public void testPostAutenticarUsuario_AcessoNaoAutorizado() throws Exception {
        // Criando os dados da requisição
        var request = new AutenticarUsuarioRequest();
        request.setEmail(faker.internet().emailAddress());
        request.setSenha("@Teste2025");
        // Mockar o comportamento da camada de serviço
        when(usuarioService.autenticarUsuario(request))
                .thenThrow(new AcessoNegadoException());

        // Fazendo a requisição para a API para autenticar um usuário que não existe
        mockMvc.perform(post(endpoint + "/autenticar").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized());

    }
}


