package sn.isi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.web.rest.TestUtil;

class ProductionLaitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductionLait.class);
        ProductionLait productionLait1 = new ProductionLait();
        productionLait1.setId(1L);
        ProductionLait productionLait2 = new ProductionLait();
        productionLait2.setId(productionLait1.getId());
        assertThat(productionLait1).isEqualTo(productionLait2);
        productionLait2.setId(2L);
        assertThat(productionLait1).isNotEqualTo(productionLait2);
        productionLait1.setId(null);
        assertThat(productionLait1).isNotEqualTo(productionLait2);
    }
}
