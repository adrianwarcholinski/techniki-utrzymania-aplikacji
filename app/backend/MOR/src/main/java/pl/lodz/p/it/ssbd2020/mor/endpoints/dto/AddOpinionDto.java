package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane do utworzenia opinii.
 */
public class AddOpinionDto implements Serializable {

    /**
     * Pole reprezentujące nazwę modelu broni, którego dotyczy opinia
     */
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME, message = "Invalid weapon model name")
    @NotBlank
    private String weaponModelName;

    /**
     * Pole reprezentujące zawartość tekstowa opinii
     */
    @Pattern(regexp = RegexPatterns.OPINION_CONTENT, message = "Invalid opinion content")
    @NotBlank
    private String content;

    /**
     * Pole reprezentujące ocena (tzw. liczba gwiazdek) modelu broni
     */
    @Min(value = 1)
    @Max(value = 5)
    private int rate;

    public String getWeaponModelName() {
        return weaponModelName;
    }

    public String getContent() {
        return content;
    }

    public int getRate() {
        return rate;
    }

    public void setWeaponModelName(String weaponModelName) {
        this.weaponModelName = weaponModelName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AddOpinionDto}, na obiekt klasy {@link OpinionEntity}.
     *
     * @return obiekt klasy {@link OpinionEntity} z danymi toru.
     */
    public OpinionEntity map() {
        OpinionEntity opinion = new OpinionEntity();
        opinion.setWeaponModel(new WeaponModelEntity(weaponModelName));
        opinion.setContent(content);
        opinion.setRate(rate);
        return opinion;
    }
}
