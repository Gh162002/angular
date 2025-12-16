package tn.esprit.spring.event.demo.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private static final String SECRET_KEY ="SZKocK5l8aTvUXxordS5P7RqIh6/CRzvz/P6xwICRT2cTZx+oSwIYjYnJx5uL0N/wENxYFBApEkO1pggqaj7x0vprdMSBv0ePWZeyoFHUN5ETDp7OMtntwCs/b1tLdTlKR8FVjPc25GiLdTQ00/pDM0nPSVyW0MAOCT9Nf/QBelvdQwqnY5zpbJ3voxlX72SFKS6Qu9z7Z54OzNbQxifoUmNaP62EVSN0WcwJdzDGN0As0NSoRRZvsPv9C+5rBvb4UmU3Nuebtc7uMvwkCQlk8XxPhWIx5Dx2oljZrZvrabiqpxPU2rYZO/scZw9yjJrayiOCOJY1vADns4PIQ5WrRRvVUjq6FlcmX+iypXQFUo=" ;
    private static final long EXPIRATION = 86400000 ;
    private static final long REFRESH_TOKEN = 604800000;

    public String ExtractUsername(String token) {
        return  extractClaim(token, Claims::getSubject ) ;
    }
    public <T>  T extractClaim(String  token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private String buildToken(
            Map <String , Object> extraClaims ,
            UserDetails userDetails,
            long expiration ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration( new Date( System.currentTimeMillis()+expiration))
                .signWith( getSignInKey(),SignatureAlgorithm.HS256)
                .compact();

    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails

    ) {
        return buildToken(extraClaims, userDetails, EXPIRATION);
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateRefreshToken(UserDetails  userDetails){
        return buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN);
    }

    public Boolean isTokenValid(String Token, UserDetails  userDetails) {
        final String username = ExtractUsername(Token);
        return( username.equals(userDetails.getUsername())&&!isTokenExpired(Token));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
    private Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

}
