package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Klasa reprezentująca dane przeznaczone do edycji toru.
 */
public class EditAlleyDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator toru.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące nazwę toru.
     */
    @NotBlank
    @Size(max = 50, message = "Name maximum length is 50")
    @Pattern(regexp = RegexPatterns.ALLEY_NAME, message = "Name is not valid")
    private String name;

    /**
     * Pole reprezentujące opis toru.
     */
    @Size(max = 400, message = "Description maximum length is 400")
    @Pattern(regexp = RegexPatterns.ALLEY_DESCRIPTION, message = "Description is not valid")
    private String description;

    /**
     * Pole reprezentujące poziom trudności toru.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.ALLEY_DIFFICULTY_LEVEL_NAME, message = "Difficulty level name is not valid")
    private String difficultyLevel;

    /**
     * Pole reprezentujące numer wersji toru.
     */
    @NotBlank
    private String version;

    /**
     * Pole zawierające wszystkie poziomy trudności dla torów.
     */
    private List<String> difficultyLevels;

    public EditAlleyDto(){}

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link EditAlleyDto}, na obiekt klasy {@link AlleyEntity}.
     *
     * @param editAlleyDto obiekt klasy {@link EditAlleyDto} z danymi toru.
     * @return obiekt klasy {@link AlleyEntity} z danymi toru.
     */
    public static AlleyEntity convertToAlleyEntity(EditAlleyDto editAlleyDto) {
        AlleyEntity alleyEntity = new AlleyEntity(
                Long.parseLong(editAlleyDto.id),
                editAlleyDto.name,
                editAlleyDto.description,
                Long.parseLong(editAlleyDto.version)
        );
        return alleyEntity;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AlleyEntity}, na obiekt klasy {@link EditAlleyDto}.
     *
     * @param alleyEntity obiekt klasy {@link AlleyEntity} z danymi toru.
     * @return obiekt klasy {@link EditAlleyDto} z danymi toru.
     */
    public static EditAlleyDto fromAlleyEntity(AlleyEntity alleyEntity){
        EditAlleyDto editAlleyDto = new EditAlleyDto();
        editAlleyDto.id = alleyEntity.getId().toString();
        editAlleyDto.name = alleyEntity.getName();
        editAlleyDto.description = alleyEntity.getDescription();
        editAlleyDto.difficultyLevel = alleyEntity.getDifficultyLevel().getName();
        editAlleyDto.version = Long.toString(alleyEntity.getVersion());
        return editAlleyDto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<String> getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(List<String> difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
