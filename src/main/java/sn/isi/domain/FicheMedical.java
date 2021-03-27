package sn.isi.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FicheMedical.
 */
@Entity
@Table(name = "fiche_medical")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FicheMedical implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "observation", nullable = false)
    private String observation;

    @NotNull
    @Column(name = "dateconsultation", nullable = false)
    private Instant dateconsultation;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FicheMedical id(Long id) {
        this.id = id;
        return this;
    }

    public String getObservation() {
        return this.observation;
    }

    public FicheMedical observation(String observation) {
        this.observation = observation;
        return this;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Instant getDateconsultation() {
        return this.dateconsultation;
    }

    public FicheMedical dateconsultation(Instant dateconsultation) {
        this.dateconsultation = dateconsultation;
        return this;
    }

    public void setDateconsultation(Instant dateconsultation) {
        this.dateconsultation = dateconsultation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FicheMedical)) {
            return false;
        }
        return id != null && id.equals(((FicheMedical) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FicheMedical{" +
            "id=" + getId() +
            ", observation='" + getObservation() + "'" +
            ", dateconsultation='" + getDateconsultation() + "'" +
            "}";
    }
}
