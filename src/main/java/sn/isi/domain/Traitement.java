package sn.isi.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Traitement.
 */
@Entity
@Table(name = "traitement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Traitement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "traitement", nullable = false)
    private String traitement;

    @NotNull
    @Column(name = "datetraitement", nullable = false)
    private Instant datetraitement;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Traitement id(Long id) {
        this.id = id;
        return this;
    }

    public String getTraitement() {
        return this.traitement;
    }

    public Traitement traitement(String traitement) {
        this.traitement = traitement;
        return this;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public Instant getDatetraitement() {
        return this.datetraitement;
    }

    public Traitement datetraitement(Instant datetraitement) {
        this.datetraitement = datetraitement;
        return this;
    }

    public void setDatetraitement(Instant datetraitement) {
        this.datetraitement = datetraitement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Traitement)) {
            return false;
        }
        return id != null && id.equals(((Traitement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Traitement{" +
            "id=" + getId() +
            ", traitement='" + getTraitement() + "'" +
            ", datetraitement='" + getDatetraitement() + "'" +
            "}";
    }
}
