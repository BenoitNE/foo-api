package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    // Vous pouvez externaliser ces valeurs dans application.properties
    @Value("${cors.allowedOrigins:http://localhost:3000,http://localhost:4200}") // Exemple: frontends React et Angular
    private String[] allowedOrigins;

    @Value("${cors.allowedMethods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String[] allowedMethods;

    @Value("${cors.allowedHeaders:Origin,Content-Type,Accept,Authorization,X-Requested-With}")
    private String[] allowedHeaders;

    @Value("${cors.exposedHeaders:Content-Disposition}") // Exemple: si vous exposez des headers spécifiques
    private String[] exposedHeaders;

    @Value("${cors.allowCredentials:true}")
    private boolean allowCredentials;

    @Value("${cors.maxAge:3600}") // 1 heure
    private long maxAge;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Appliquer CORS à tous les endpoints sous /api/
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods(allowedMethods)
                        .allowedHeaders(allowedHeaders)
                        .exposedHeaders(exposedHeaders) // Headers que le navigateur est autorisé à accéder
                        .allowCredentials(allowCredentials) // Autoriser les cookies/authentification
                        .maxAge(maxAge); // Durée pendant laquelle la réponse à une requête pre-flight peut être mise en cache
            }
        };
    }

    // Vous pouvez ajouter ici d'autres configurations WebMvcConfigurer si nécessaire,
    // par exemple, des convertisseurs de format, des intercepteurs, etc.
}
