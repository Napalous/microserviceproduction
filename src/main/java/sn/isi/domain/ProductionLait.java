package sn.isi.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProductionLait.
 */
@Entity
@Table(name = "production_lait")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductionLait implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @NotNull
    @Column(name = "dateproduction", nullable = false)
    private Instant dateproduction;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionLait id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getQuantite() {
        return this.quantite;
    }

    public ProductionLait quantite(Integer quantite) {
        this.quantite = quantite;
        return this;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Instant getDateproduction() {
        return this.dateproduction;
    }

    public ProductionLait dateproduction(Instant dateproduction) {
        this.dateproduction = dateproduction;
        return this;
    }

    public void setDateproduction(Instant dateproduction) {
        this.dateproduction = dateproduction;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductionLait)) {
            return false;
        }
        return id != null && id.equals(((ProductionLait) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductionLait{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", dateproduction='" + getDateproduction() + "'" +
            "}";
    }
}
