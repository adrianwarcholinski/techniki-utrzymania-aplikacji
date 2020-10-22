package pl.lodz.p.it.ssbd2020.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Konwerter służący do tłumaczenia między obiektami typów LocalDateTime oraz Timestamp
 */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    /**
     * Metoda tłumaczaca obiekt LocalDateTime na tożsamy obiekt Timestamp
     *
     * @param localDateTime obiekt klasy LocalDateTime
     * @return tożsamy obiekt Timestamp
     */
    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    /**
     * Metoda tłumaczaca Timestamp na tożsamy obiekt LocalDateTime
     *
     * @param timestamp obiekt klasy Timestamp
     * @return tożsamy obiekt LocalDateTime
     */
    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
