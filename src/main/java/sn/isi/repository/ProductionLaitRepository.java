package sn.isi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.domain.ProductionLait;

/**
 * Spring Data SQL repository for the ProductionLait entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductionLaitRepository extends JpaRepository<ProductionLait, Long> {}
