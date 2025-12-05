package br.com.studios.sketchbook.service_management_core.application.api_utils.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDocumentManagerUtils {
    /// Salva (cria ou sobrescreve) um arquivo com o conteúdo dado
    public static void save(String content, Path path) throws IOException {
        Files.createDirectories(path.getParent()); // garante diretório
        Files.writeString(path, content);
    }

    /// Lê um arquivo e retorna como String
    public static String read(Path path) throws IOException {
        return Files.readString(path);
    }

    /// Verifica se o arquivo existe
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /// Deleta o arquivo, se existir
    public static void delete(Path path) throws IOException {
        if (exists(path)) {
            Files.delete(path);
        }
    }
}
