package sn.isi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.domain.FicheMedical;

/**
 * Spring Data SQL repository for the FicheMedical entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FicheMedicalRepository extends JpaRepository<FicheMedical, Long> {}
