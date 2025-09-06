package tr.mhrs.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF korumasını kapat (REST API için)
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS ayarları
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Yetkilendirme kuralları
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()  // Auth endpoint'leri açık
                .requestMatchers("/api/auth/test").permitAll()  // Test endpoint'i açık
                .requestMatchers("/actuator/**").permitAll()  // Health check açık
                .requestMatchers("/error").permitAll()  // Error sayfası açık
                .requestMatchers("/**").permitAll()  // ŞİMDİLİK HER ŞEY AÇIK
                .anyRequest().authenticated()  // Diğerleri auth gerektirsin
            )
            
            // Session yönetimi (REST API için stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Form login'i kapat
            .formLogin(AbstractHttpConfigurer::disable)
            
            // HTTP Basic auth'u kapat
            .httpBasic(AbstractHttpConfigurer::disable);
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Tüm origin'lere izin ver
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}