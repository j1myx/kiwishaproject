package com.kiwisha.project.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilidad para generar slugs SEO-friendly a partir de textos.
 * Un slug es una versión URL-amigable de un texto.
 */
public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    /**
     * Genera un slug a partir de un texto.
     * 
     * @param input El texto original
     * @return El slug generado
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Genera un slug único agregando un sufijo numérico si es necesario.
     * 
     * @param baseSlug El slug base
     * @param counter El contador para el sufijo
     * @return El slug único
     */
    public static String generateUniqueSlug(String baseSlug, int counter) {
        if (counter == 0) {
            return baseSlug;
        }
        return baseSlug + "-" + counter;
    }
}
