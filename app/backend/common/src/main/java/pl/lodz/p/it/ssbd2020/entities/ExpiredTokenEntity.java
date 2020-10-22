package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "expired_token", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_TOKEN, columnNames = "token")
})
@NamedQueries({
        @NamedQuery(name = "ExpiredTokenEntity.findByToken", query = "SELECT a FROM ExpiredTokenEntity a WHERE a.token=:token")
})
@TableGenerator(name = "ExpiredTokenIdGen", table = "generator", pkColumnName = "class_name", valueColumnName =
        "id_range", pkColumnValue = "ExpiredTokenEntity")
public class ExpiredTokenEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ExpiredTokenIdGen")
    @Column(updatable = false)
    private Long id;

    @NotBlank
    @Size(max = 250, message = "Token maximum length is 250")
    @Column(nullable = false, updatable = false, length = 250)
    private String token;

    public ExpiredTokenEntity() {
    }

    public ExpiredTokenEntity(@NotBlank @Size(max = 250, message = "Token maximum length is 250") String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpiredTokenEntity that = (ExpiredTokenEntity) o;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s]",
                this.getClass().getName(), id, token);
    }
}
