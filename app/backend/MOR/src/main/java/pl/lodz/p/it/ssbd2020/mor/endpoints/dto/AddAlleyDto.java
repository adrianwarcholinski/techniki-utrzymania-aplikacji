package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane do utworzenia toru.
 */
public class AddAlleyDto implements Serializable {

    /**
     * Pole reprezentujące nazwę toru.
     */
    @NotBlank
    @Size(max = 50, message = "Alley name maximum length is 50")
    @Pattern(regexp = RegexPatterns.ALLEY_NAME, message = "Alley name is not valid")
    private String name;

    /**
     * Pole reprezentujące opis toru.
     */
    @NotBlank
    @Size(max = 50, message = "Alley description maximum length is 400")
    @Pattern(regexp = RegexPatterns.ALLEY_DESCRIPTION, message = "Alley description is not valid")
    private String description;

    /**
     * Pole reprezentujące przypisany do toru poziom trudności.
     */
    @NotBlank
    private String alleyDifficultyLevelName;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public String getAlleyDifficultyLevelName() {
        return alleyDifficultyLevelName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAlleyDifficultyLevelName(String alleyDifficultyLevelName) {
        this.alleyDifficultyLevelName = alleyDifficultyLevelName;
    }

    public AddAlleyDto(String name, String description, String alleyDifficultyLevelName) {
        this.name = name;
        this.description = description;
        this.alleyDifficultyLevelName = alleyDifficultyLevelName;
    }

    public AddAlleyDto() {
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AddAlleyDto}, na obiekt klasy {@link AlleyEntity}.
     *
     * @param addAlleyDto obiekt klasy {@link AddAlleyDto} z danymi toru.
     * @return obiekt klasy {@link AlleyEntity} z danymi toru.
     */
    public static AlleyEntity map(AddAlleyDto addAlleyDto) {
        return new AlleyEntity(
                addAlleyDto.getName(),
                addAlleyDto.getDescription()
        );
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AlleyEntity}, na obiekt klasy {@link AddAlleyDto}.
     *
     * @param alleyEntity obiekt klasy {@link AlleyEntity} z danymi toru.
     * @return obiekt klasy {@link AddAlleyDto} z danymi toru.
     */
    public static AddAlleyDto map(AlleyEntity alleyEntity) {
        return new AddAlleyDto(alleyEntity.getName(),
                alleyEntity.getDescription(),
                alleyEntity.getDifficultyLevel().getName());
    }
}
