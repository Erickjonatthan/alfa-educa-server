package com.projeto.alfaeduca.infra.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    static {
        try {
            Dotenv dotenv = loadDotenv();
            if (dotenv != null) {
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar arquivo .env: " + e.getMessage());
        }
    }

    private static Dotenv loadDotenv() {
        String[] possibleDirectories = {
                ".", // Diretório atual (local) - PRIORIDADE
                System.getProperty("user.dir"), // Diretório de trabalho
                "", // Root path
                "/app" // Docker - por último
        };

        for (String directory : possibleDirectories) {
            try {
                Dotenv dotenv;

                if (!directory.isEmpty()) {
                    dotenv = Dotenv.configure()
                            .directory(directory)
                            .ignoreIfMissing()
                            .load();
                } else {
                    dotenv = Dotenv.configure()
                            .ignoreIfMissing()
                            .load();                }

                return dotenv;
            } catch (Exception e) {
            }
        }

        return null;
    }
}