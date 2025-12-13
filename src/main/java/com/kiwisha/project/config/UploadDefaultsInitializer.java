package com.kiwisha.project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Asegura que existan archivos por defecto en uploads/ para evitar placeholders remotos.
 */
@Component
@Slf4j
public class UploadDefaultsInitializer implements ApplicationRunner {

    private final Path uploadsRoot;

    public UploadDefaultsInitializer(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadsRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Path defaultsDir = uploadsRoot.resolve("defaults");
            Files.createDirectories(defaultsDir);

            copyIfMissing("static/defaults/producto-default.svg", defaultsDir.resolve("producto-default.svg"));
        } catch (Exception ex) {
            log.warn("No se pudieron inicializar imágenes por defecto en uploads/", ex);
        }
    }

    private void copyIfMissing(String classpathResource, Path target) throws Exception {
        if (Files.exists(target)) {
            return;
        }

        ClassPathResource resource = new ClassPathResource(classpathResource);
        if (!resource.exists()) {
            log.warn("Recurso no encontrado: {}", classpathResource);
            return;
        }

        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Default creado: {}", target);
        }
    }
}
