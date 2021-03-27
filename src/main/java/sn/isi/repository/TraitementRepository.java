package sn.isi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.domain.Traitement;

/**
 * Spring Data SQL repository for the Traitement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TraitementRepository extends JpaRepository<Traitement, Long> {}
