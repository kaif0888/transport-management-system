//package com.tms.JwtSecurity.config;
//
//import java.util.Date;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//public class JwtUtil {
//
//    private static final String SECRET_KEY = "my_secret_key";
//
//    @SuppressWarnings("deprecation")
//	public static String generateToken(String mobileNumber) {
//        return Jwts.builder()
//                .setSubject(mobileNumber)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//}
//

package com.tms.JwtSecurity.config;

import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    
    private static final String SECRET_KEY = "KJDWIF3IY47RGYFCB3974FUBC3U49FBIB3UG94FBIVH74GRYFG34YRFV384YFVY38FWJ3U9BVRIF3";
    
    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public static String generateToken(String mobileNumber) {
        return Jwts.builder()
                .setSubject(mobileNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}