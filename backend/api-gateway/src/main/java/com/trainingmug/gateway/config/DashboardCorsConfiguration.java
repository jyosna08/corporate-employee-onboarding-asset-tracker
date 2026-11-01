package com.trainingmug.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;

/** Applies the dashboard's browser CORS policy. */
@Configuration
public class DashboardCorsConfiguration implements WebFluxConfigurer {
  private final String[] allowedOrigins;

  public DashboardCorsConfiguration(@Value("${CORS_ALLOWED_ORIGINS}") String origins) {
    this.allowedOrigins = Arrays.stream(origins.split(","))
      .map(String::trim)
      .filter(origin -> !origin.isEmpty())
      .toArray(String[]::new);
  }

  /** Restricts dashboard responses to the configured origins and read-only methods. */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/dashboard/**")
      .allowedOrigins(allowedOrigins)
      .allowedMethods("GET", "OPTIONS")
      .allowedHeaders("Authorization", "Content-Type", "Accept")
      .exposedHeaders("Location")
      .allowCredentials(true);
  }
}
