package pl.lodz.p.it.ssbd2020.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "AlleyDifficultyLevelEntity.findAll", query = "SELECT adl FROM AlleyDifficultyLevelEntity adl"),
        @NamedQuery(name = "AlleyDifficultyLevelEntity.id", query = "SELECT adl FROM AlleyDifficultyLevelEntity adl WHERE adl.id = :id"),
        @NamedQuery(name = "AlleyDifficultyLevelEntity.findByName", query = "SELECT adl FROM AlleyDifficultyLevelEntity adl WHERE adl.name = :name")
})
@Table(name = "alley_difficulty_level")
public class AlleyDifficultyLevelEntity {

    @Id
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 20)
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    private String name;

    @Version
    @Column(nullable = false)
    private long version;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private AlleyDifficultyLevelEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlleyDifficultyLevelEntity)) return false;
        AlleyDifficultyLevelEntity that = (AlleyDifficultyLevelEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, name, version);
    }
}
