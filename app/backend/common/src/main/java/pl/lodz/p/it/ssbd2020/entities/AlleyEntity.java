package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "alley", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_ALLEY_NAME, columnNames = "name")
})
@NamedQueries({
        @NamedQuery(name = "AlleyEntity.findAll", query = "SELECT a FROM AlleyEntity a"),
        @NamedQuery(name = "AlleyEntity.findById", query = "SELECT a FROM AlleyEntity a WHERE a.id = :id"),
        @NamedQuery(name = "AlleyEntity.findByName", query = "SELECT a FROM AlleyEntity a WHERE a.name = :name"),
        @NamedQuery(name = "AlleyEntity.findByActive", query = "SELECT a FROM AlleyEntity a WHERE a.active = :active ORDER BY a.name")
})
@TableGenerator(name = "AlleyIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "AlleyEntity")
public class AlleyEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AlleyIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 50)
    @NotBlank
    @Size(max = 50, message = "Name maximum length is 50")
    private String name;

    @Column(length = 400)
    @NotBlank
    @Size(max = 400, message = "Description maximum length is 400")
    private String description;

    @OneToMany(mappedBy = "alley", cascade = CascadeType.REMOVE)
    @NotNull
    private List<ReservationEntity> reservations = new ArrayList<>();


    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id_alley_difficulty_level")
    @NotNull
    @Valid
    private AlleyDifficultyLevelEntity difficultyLevel;

    @Column(nullable = false)
    private boolean active;

    public AlleyEntity() {
    }

    public AlleyEntity(String name) {
        this.name = name;
    }

    public AlleyEntity(String name, String description) {
        this.name = name;
        this.description=description;
        this.active = true;
    }

    public AlleyEntity(Long id, String name, String description, long version) {
        super(version);
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public AlleyEntity(String name, AlleyDifficultyLevelEntity difficultyLevel) {
        this.name = name;
        this.difficultyLevel = difficultyLevel;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public AlleyDifficultyLevelEntity getDifficultyLevel() {
        return difficultyLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficultyLevel(AlleyDifficultyLevelEntity difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlleyEntity)) return false;
        AlleyEntity that = (AlleyEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, name, this.getVersion());
    }


    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów toru, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedDataAlley(AlleyEntity source) {
        reservations = source.reservations;
        active = source.active;
    }
}
