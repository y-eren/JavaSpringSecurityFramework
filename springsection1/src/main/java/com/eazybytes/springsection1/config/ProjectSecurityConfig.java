package com.eazybytes.springsection1.config;


import com.eazybytes.springsection1.exceptionhandling.CustomAccessDeniedHandler;
import com.eazybytes.springsection1.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {

    // Buradaki kod SpringBootWebSecuriyConfiguration sınıfından gelmektedir.
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       // http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); // hepsine permitlemiş oluyoruz denyAll() metoduda mevcutuur
        http    .sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(3).maxSessionsPreventsLogin(true)) // 1 olarak concurrent sessionı ayarlıyoruz expired url'de kullanabiliriz .expiredUrl("/)
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // only http
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((requests) -> requests.requestMatchers("/myAccount", "/myBalance", "/myCards", "/myLoans").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll()
        );
        http.formLogin(withDefaults()); // ui kısmı için herhangi bir status, header bilgisi göndermek gerekmediği için hangi loagin pagein gösterileceği gibi özellikler kullanılır
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); // bunu yaparak spring securitye kendi basic authenticatonentrypoint ile haberleşmesinin yolu
        // http.exceptionHandling(ehc -> ehc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); // exception handling config bu global bir exceptionhandlingdir
        // global yapmanın avantajı sadece loginde değil bütün framework boyunca exception sağlamasıdır ve bu daha iyidir
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler())); // global olarak exception üretmektedir
        return http.build();
    }

// Bu kodlar InMemory kullanım için geçerlidir
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user").password("{noop}Eren@12345").authorities("read").build();
//        UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$12$Krk7EUXSLBKk.wN9aRAGSudnVOBV.S8I.SxYkMu.gOWGmWJfox2si")
//                .authorities("admin").build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }


    // Bu kullanım sadece hazır Jdbc tablolarını kullanıyorusan geçerlidir.
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//
//        return new JdbcUserDetailsManager(dataSource);
//    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // compromised kişiler için bakılıyor
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
