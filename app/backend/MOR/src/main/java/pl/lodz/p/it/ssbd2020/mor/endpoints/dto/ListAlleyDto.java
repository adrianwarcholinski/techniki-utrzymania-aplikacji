package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane toru przeznaczone do wyświetlenia jako rekord listy.
 */
public class ListAlleyDto implements Serializable {

    /**
     * Pole reprezentujące nazwe toru.
     */
    @NotBlank
    @Size(max = 50, message = "Name maximum length is 50")
    private String name;

    /**
     * Pole reprezentujące nazwę poziomu trudności toru.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    private String difficultyLevelName;


    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AlleyEntity}, na obiekt klasy {@link ListAlleyDto}.
     *
     * @param alleyEntity obiekt klasy {@link AlleyEntity} z danymi toru.
     * @return obiekt klasy {@link ListAlleyDto} z danymi toru.
     */
    public static ListAlleyDto map(AlleyEntity alleyEntity) {
        ListAlleyDto listAlleyDto = new ListAlleyDto();
        listAlleyDto.setName(alleyEntity.getName());
        listAlleyDto.setDifficultyLevelName(alleyEntity.getDifficultyLevel().getName());
        return listAlleyDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficultyLevelName() {
        return difficultyLevelName;
    }

    public void setDifficultyLevelName(String difficultyLevelName) {
        this.difficultyLevelName = difficultyLevelName;
    }
}