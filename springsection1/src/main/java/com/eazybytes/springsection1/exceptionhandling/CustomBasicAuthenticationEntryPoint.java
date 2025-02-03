package com.eazybytes.springsection1.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;


public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // kendi unauthorized kodumuzu yazmak için AuthenticatonEntrypoint interfacei kullanılarak yazılıyor bu sınıf BasicAuthenticationEntry sınıfını kullanıyor aynı zamanda LoginurlAuthenticationEntrypointde kullanılmaktadır
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // json response içindeki dinamik değerler için kullanılıyor
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Unauthorized";
        String path = request.getRequestURI();
        response.setHeader("eazybank-error-reason", "authentication failed");
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");


        // construct json response için kullanılıyor
        String jsonResponse =
                String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                        currentTimeStamp, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), message, path
                      );
        response.getWriter().write(jsonResponse);
    }
}
