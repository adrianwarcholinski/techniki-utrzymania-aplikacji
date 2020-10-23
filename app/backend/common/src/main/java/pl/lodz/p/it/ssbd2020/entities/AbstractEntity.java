package pl.lodz.p.it.ssbd2020.entities;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity {

    @Version
    @Column(nullable = false)
    private long version;

    public AbstractEntity() {
    }

    public AbstractEntity(long version) {
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

}
