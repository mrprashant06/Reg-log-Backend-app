package com.ecommerce.reglog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 * Note: CORS is centrally managed in SecurityConfig via CorsConfigurationSource
 * to prevent conflicting origin registrations and ensure credentials security.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // Centrally managed in SecurityConfig
}
