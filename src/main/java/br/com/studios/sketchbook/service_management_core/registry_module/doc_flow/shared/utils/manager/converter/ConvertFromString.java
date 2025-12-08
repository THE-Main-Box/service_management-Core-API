package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.converter;

public class ConvertFromString {
    public static Object convertToType(Object value, String typeName) {
        // Lógica de conversão baseada no typeName
        if (value == null) return null;

        return switch (typeName.toLowerCase()) {
            case "integer" -> Integer.valueOf(value.toString());
            case "double" -> Double.valueOf(value.toString());
            case "float" -> Float.valueOf(value.toString());
            case "string" -> value.toString();
            default -> value; // Mantém como está
        };
    }
}
