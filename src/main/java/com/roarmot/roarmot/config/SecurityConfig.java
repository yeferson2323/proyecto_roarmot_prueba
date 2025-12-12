package com.roarmot.roarmot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF: Configuración SEGURA
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/enviar-codigo", 
                    "/validar-codigo",
                    "/guardar-usuario",
                    "/panel/ProductProcess",
                    "/api/email/**",
                    "/api/sos/buscar-servicios"  // APIs públicas sin CSRF
                )
            )
            .authorizeHttpRequests(authorize -> authorize
                // ORDEN CRÍTICO: De más específico a más general
                
                // 1. APIs PÚBLICAS (sin autenticación)
                .requestMatchers("/api/email/**").permitAll()

                //  - APIs de Reportes para Vendedores
                .requestMatchers("/api/reportes/**").hasAuthority("ROLE_Vendedor")
    
                .requestMatchers("/api/motos/**").authenticated()  // APIs protegidas
                
                // 2. RECURSOS ESTÁTICOS (sin autenticación)
                .requestMatchers("/css/**", "/js/**", "/imagenes/**", "/uploads/**", "/products/**", "/webjars/**",
                    "/favicon.ico", "/favicon.icon", "/error"
                ).permitAll()

                .requestMatchers("/sos", "/api/sos/**").authenticated() // solo a usuarios registrados
                
                // 3. PÁGINAS PÚBLICAS (sin autenticación)
                .requestMatchers("/", "/login", "/RegistroPaso1", "/RegistroPaso2", "/RegistroPaso3").permitAll()
                
                // 4. ENDPOINTS POST PÚBLICOS (sin autenticación)
                .requestMatchers(HttpMethod.POST, "/enviar-codigo", "/validar-codigo", "/guardar-usuario").permitAll()
                
                // 5. ÁREA ADMINISTRATIVA (requiere autenticación y rol)
                .requestMatchers("/panel/**").hasAuthority("ROLE_Vendedor")
                
                // 6. TODAS LAS DEMÁS RUTAS (requieren autenticación)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") 
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // 7. CONFIGURACIONES ADICIONALES DE SEGURIDAD
            .sessionManagement(session -> session
                .maximumSessions(1) // Solo una sesión por usuario
                .maxSessionsPreventsLogin(false) // Permite nuevo login, cierra sesión anterior
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
            
                        // 1. SCRIPT-SRC: Permite la carga del script principal de Google Maps
                        "script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com https://kit.fontawesome.com https://maps.googleapis.com; " + 
                        
                        // 2. CONNECT-SRC: Permite la conexión AJAX de la API de Maps
                        "connect-src 'self' https://fonts.googleapis.com https://maps.googleapis.com https://*.googleapis.com; " + 
                        
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.tailwindcss.com https://cdnjs.cloudflare.com; " + 
                        
                        "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " + 
                        
                        // 3. IMG-SRC: Permite los tiles del mapa, iconos y data URI (marcadores)
                        "img-src 'self' data: https: https://maps.gstatic.com https://*.googleapis.com; " + 
                        
                        "frame-ancestors 'none'" // Notar que NO hay punto y coma aquí si es la última directiva.

                    ) // Imágenes locales y externas
                )
                .frameOptions(frame -> frame.deny()) // Protección contra clickjacking
            );
                
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}