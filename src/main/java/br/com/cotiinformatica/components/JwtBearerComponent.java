package br.com.cotiinformatica.components;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Component
public class JwtBearerComponent {
    @Value("${jwt.secretkey}")
    private String jwtSecretkey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /*
     * Método para retornar a data de expiração do TOKEN JWT
     */
    public Date getExpiration() {
        var dataAtual = new Date();
        return new Date(dataAtual.getTime() + jwtExpiration);
    }

    /*
     * Método para geração do TOKEN JWT
     */
    public String getAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) //nome do usuário
                .claim("role", role) //perfil do usuário
                .setIssuedAt(new Date()) //data de geração do token
                .setExpiration(getExpiration()) //data de expiração do token
                .signWith(SignatureAlgorithm.HS256, jwtSecretkey) //assinatura do token
                .compact(); //gerando e retornando o token
    }
    /*
     * Método para ler e retornar a propriedade 'name' contida
     * no TOKEN JWT que armazena o nome do usuário autenticado
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecretkey)
                .parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}



