package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane przeznaczone do edycji opinii.
 */
public class EditOpinionDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator opinii.
     */
    @NotBlank
    private String id;

    /**
     * Numer identyfikacyjny opinii, która podlega edycji
     */
    private long opinionNumber;

    /**
     * Zaktualizowana treść opinii
     */
    @Pattern(regexp = RegexPatterns.OPINION_CONTENT, message = "Invalid opinion content")
    @NotBlank
    private String content;

    /**
     * Zaktualizowana ocena (tzw. liczba gwiazdek)
     */
    @Min(value = 1)
    @Max(value = 5)
    private int rate;

    /**
     * Zaszyfrowana postać numeru wersji
     */
    @NotBlank
    private String version;

    public EditOpinionDto() {
    }

    public EditOpinionDto(@NotBlank String id,
                          long opinionNumber,
                          @Pattern(regexp = RegexPatterns.OPINION_CONTENT, message = "Invalid opinion content") @NotBlank String content,
                          @Min(value = 1) @Max(value = 5) int rate,
                          @NotBlank String version) {
        this.id = id;
        this.opinionNumber = opinionNumber;
        this.content = content;
        this.rate = rate;
        this.version = version;
    }

    public long getOpinionNumber() {
        return opinionNumber;
    }

    public void setOpinionNumber(long opinionNumber) {
        this.opinionNumber = opinionNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link EditOpinionDto}, na obiekt klasy {@link OpinionEntity}.
     *
     * @param editOpinionDto obiekt klasy {@link EditOpinionDto} z danymi opinii.
     * @return obiekt klasy {@link OpinionEntity} z danymi opinii.
     */
    public static OpinionEntity map(EditOpinionDto editOpinionDto) {
        OpinionEntity opinion = new OpinionEntity(Long.parseLong(editOpinionDto.getId()), editOpinionDto.opinionNumber, Long.parseLong(editOpinionDto.version));
        opinion.setContent(editOpinionDto.content);
        opinion.setRate(editOpinionDto.rate);
        return opinion;
    }
}
