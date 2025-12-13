package com.kiwisha.project.service.impl;

import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.service.ImagenProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class ImagenProductoServiceImpl implements ImagenProductoService {

    private static final int MAX_DIMENSION = 1200;
    private static final float JPEG_QUALITY = 0.82f;

    private final Path uploadsRoot;

    public ImagenProductoServiceImpl(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadsRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String guardarImagenPrincipal(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("No se recibió ninguna imagen");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException("El archivo adjunto no es una imagen válida");
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage source = ImageIO.read(inputStream);
            if (source == null) {
                throw new BusinessException("No se pudo leer la imagen. Formato no soportado.");
            }

            boolean hasAlpha = source.getColorModel() != null && source.getColorModel().hasAlpha();
            BufferedImage resized = resize(source, hasAlpha);

            String ext = hasAlpha ? "png" : "jpg";
            String fileName = UUID.randomUUID() + "." + ext;
            Path targetDir = uploadsRoot.resolve("products");
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(fileName);

            if (hasAlpha) {
                ImageIO.write(resized, "png", target.toFile());
            } else {
                writeJpeg(resized, target, JPEG_QUALITY);
            }

            // Ruta relativa que se usará con /files/**
            return "products/" + fileName;

        } catch (IOException ex) {
            log.error("Error guardando imagen de producto", ex);
            throw new BusinessException("No se pudo guardar la imagen del producto");
        }
    }

    private static BufferedImage resize(BufferedImage source, boolean hasAlpha) {
        int width = source.getWidth();
        int height = source.getHeight();

        double scale = Math.min(1.0d, Math.min((double) MAX_DIMENSION / width, (double) MAX_DIMENSION / height));
        int newW = Math.max(1, (int) Math.round(width * scale));
        int newH = Math.max(1, (int) Math.round(height * scale));

        int type = hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage resized = new BufferedImage(newW, newH, type);

        Graphics2D g2d = resized.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!hasAlpha) {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, newW, newH);
            }

            g2d.drawImage(source, 0, 0, newW, newH, null);
        } finally {
            g2d.dispose();
        }

        return resized;
    }

    private static void writeJpeg(BufferedImage image, Path target, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No hay ImageWriter disponible para JPG");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(Math.min(1f, Math.max(0f, quality)));
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(Files.newOutputStream(target))) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
