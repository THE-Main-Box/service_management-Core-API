package br.com.studios.sketchbook.service_management_core.application.api_utils.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ClassToStringConverter implements AttributeConverter<Class<?>, String> {

    @Override
    public String convertToDatabaseColumn(Class<?> attribute) {
        return attribute != null ? attribute.getName(): null;
    }

    @Override
    public Class<?> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? Class.forName(dbData) : null;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Classe não encontrada: " + dbData, e);
        }
    }
}
