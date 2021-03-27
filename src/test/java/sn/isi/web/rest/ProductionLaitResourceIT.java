package sn.isi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.IntegrationTest;
import sn.isi.domain.ProductionLait;
import sn.isi.repository.ProductionLaitRepository;

/**
 * Integration tests for the {@link ProductionLaitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductionLaitResourceIT {

    private static final Integer DEFAULT_QUANTITE = 1;
    private static final Integer UPDATED_QUANTITE = 2;

    private static final Instant DEFAULT_DATEPRODUCTION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATEPRODUCTION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/production-laits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductionLaitRepository productionLaitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductionLaitMockMvc;

    private ProductionLait productionLait;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductionLait createEntity(EntityManager em) {
        ProductionLait productionLait = new ProductionLait().quantite(DEFAULT_QUANTITE).dateproduction(DEFAULT_DATEPRODUCTION);
        return productionLait;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductionLait createUpdatedEntity(EntityManager em) {
        ProductionLait productionLait = new ProductionLait().quantite(UPDATED_QUANTITE).dateproduction(UPDATED_DATEPRODUCTION);
        return productionLait;
    }

    @BeforeEach
    public void initTest() {
        productionLait = createEntity(em);
    }

    @Test
    @Transactional
    void createProductionLait() throws Exception {
        int databaseSizeBeforeCreate = productionLaitRepository.findAll().size();
        // Create the ProductionLait
        restProductionLaitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isCreated());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeCreate + 1);
        ProductionLait testProductionLait = productionLaitList.get(productionLaitList.size() - 1);
        assertThat(testProductionLait.getQuantite()).isEqualTo(DEFAULT_QUANTITE);
        assertThat(testProductionLait.getDateproduction()).isEqualTo(DEFAULT_DATEPRODUCTION);
    }

    @Test
    @Transactional
    void createProductionLaitWithExistingId() throws Exception {
        // Create the ProductionLait with an existing ID
        productionLait.setId(1L);

        int databaseSizeBeforeCreate = productionLaitRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductionLaitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = productionLaitRepository.findAll().size();
        // set the field null
        productionLait.setQuantite(null);

        // Create the ProductionLait, which fails.

        restProductionLaitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateproductionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productionLaitRepository.findAll().size();
        // set the field null
        productionLait.setDateproduction(null);

        // Create the ProductionLait, which fails.

        restProductionLaitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductionLaits() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        // Get all the productionLaitList
        restProductionLaitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productionLait.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.[*].dateproduction").value(hasItem(DEFAULT_DATEPRODUCTION.toString())));
    }

    @Test
    @Transactional
    void getProductionLait() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        // Get the productionLait
        restProductionLaitMockMvc
            .perform(get(ENTITY_API_URL_ID, productionLait.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productionLait.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(DEFAULT_QUANTITE))
            .andExpect(jsonPath("$.dateproduction").value(DEFAULT_DATEPRODUCTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProductionLait() throws Exception {
        // Get the productionLait
        restProductionLaitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProductionLait() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();

        // Update the productionLait
        ProductionLait updatedProductionLait = productionLaitRepository.findById(productionLait.getId()).get();
        // Disconnect from session so that the updates on updatedProductionLait are not directly saved in db
        em.detach(updatedProductionLait);
        updatedProductionLait.quantite(UPDATED_QUANTITE).dateproduction(UPDATED_DATEPRODUCTION);

        restProductionLaitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductionLait.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProductionLait))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
        ProductionLait testProductionLait = productionLaitList.get(productionLaitList.size() - 1);
        assertThat(testProductionLait.getQuantite()).isEqualTo(UPDATED_QUANTITE);
        assertThat(testProductionLait.getDateproduction()).isEqualTo(UPDATED_DATEPRODUCTION);
    }

    @Test
    @Transactional
    void putNonExistingProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productionLait.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductionLaitWithPatch() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();

        // Update the productionLait using partial update
        ProductionLait partialUpdatedProductionLait = new ProductionLait();
        partialUpdatedProductionLait.setId(productionLait.getId());

        partialUpdatedProductionLait.quantite(UPDATED_QUANTITE).dateproduction(UPDATED_DATEPRODUCTION);

        restProductionLaitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductionLait.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductionLait))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
        ProductionLait testProductionLait = productionLaitList.get(productionLaitList.size() - 1);
        assertThat(testProductionLait.getQuantite()).isEqualTo(UPDATED_QUANTITE);
        assertThat(testProductionLait.getDateproduction()).isEqualTo(UPDATED_DATEPRODUCTION);
    }

    @Test
    @Transactional
    void fullUpdateProductionLaitWithPatch() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();

        // Update the productionLait using partial update
        ProductionLait partialUpdatedProductionLait = new ProductionLait();
        partialUpdatedProductionLait.setId(productionLait.getId());

        partialUpdatedProductionLait.quantite(UPDATED_QUANTITE).dateproduction(UPDATED_DATEPRODUCTION);

        restProductionLaitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductionLait.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductionLait))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
        ProductionLait testProductionLait = productionLaitList.get(productionLaitList.size() - 1);
        assertThat(testProductionLait.getQuantite()).isEqualTo(UPDATED_QUANTITE);
        assertThat(testProductionLait.getDateproduction()).isEqualTo(UPDATED_DATEPRODUCTION);
    }

    @Test
    @Transactional
    void patchNonExistingProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productionLait.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductionLait() throws Exception {
        int databaseSizeBeforeUpdate = productionLaitRepository.findAll().size();
        productionLait.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLaitMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLait))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductionLait in the database
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductionLait() throws Exception {
        // Initialize the database
        productionLaitRepository.saveAndFlush(productionLait);

        int databaseSizeBeforeDelete = productionLaitRepository.findAll().size();

        // Delete the productionLait
        restProductionLaitMockMvc
            .perform(delete(ENTITY_API_URL_ID, productionLait.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductionLait> productionLaitList = productionLaitRepository.findAll();
        assertThat(productionLaitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
