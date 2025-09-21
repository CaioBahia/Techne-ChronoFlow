package com.techne.ChronoFlow.domain.arquivo.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // Usaremos @Convert na entidade para aplicar
public class ConteudoRetornoConverter implements AttributeConverter<ConteudoRetorno, String> {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(ConteudoRetorno attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao converter ConteudoRetorno para JSON", e);
        }
    }

    @Override
    public ConteudoRetorno convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ConteudoRetorno.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao converter JSON para ConteudoRetorno", e);
        }
    }
}
