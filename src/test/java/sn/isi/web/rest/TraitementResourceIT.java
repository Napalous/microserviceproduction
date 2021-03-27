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
import sn.isi.domain.Traitement;
import sn.isi.repository.TraitementRepository;

/**
 * Integration tests for the {@link TraitementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TraitementResourceIT {

    private static final String DEFAULT_TRAITEMENT = "AAAAAAAAAA";
    private static final String UPDATED_TRAITEMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATETRAITEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATETRAITEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/traitements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TraitementRepository traitementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTraitementMockMvc;

    private Traitement traitement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Traitement createEntity(EntityManager em) {
        Traitement traitement = new Traitement().traitement(DEFAULT_TRAITEMENT).datetraitement(DEFAULT_DATETRAITEMENT);
        return traitement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Traitement createUpdatedEntity(EntityManager em) {
        Traitement traitement = new Traitement().traitement(UPDATED_TRAITEMENT).datetraitement(UPDATED_DATETRAITEMENT);
        return traitement;
    }

    @BeforeEach
    public void initTest() {
        traitement = createEntity(em);
    }

    @Test
    @Transactional
    void createTraitement() throws Exception {
        int databaseSizeBeforeCreate = traitementRepository.findAll().size();
        // Create the Traitement
        restTraitementMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isCreated());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeCreate + 1);
        Traitement testTraitement = traitementList.get(traitementList.size() - 1);
        assertThat(testTraitement.getTraitement()).isEqualTo(DEFAULT_TRAITEMENT);
        assertThat(testTraitement.getDatetraitement()).isEqualTo(DEFAULT_DATETRAITEMENT);
    }

    @Test
    @Transactional
    void createTraitementWithExistingId() throws Exception {
        // Create the Traitement with an existing ID
        traitement.setId(1L);

        int databaseSizeBeforeCreate = traitementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTraitementMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTraitementIsRequired() throws Exception {
        int databaseSizeBeforeTest = traitementRepository.findAll().size();
        // set the field null
        traitement.setTraitement(null);

        // Create the Traitement, which fails.

        restTraitementMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatetraitementIsRequired() throws Exception {
        int databaseSizeBeforeTest = traitementRepository.findAll().size();
        // set the field null
        traitement.setDatetraitement(null);

        // Create the Traitement, which fails.

        restTraitementMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTraitements() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        // Get all the traitementList
        restTraitementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(traitement.getId().intValue())))
            .andExpect(jsonPath("$.[*].traitement").value(hasItem(DEFAULT_TRAITEMENT)))
            .andExpect(jsonPath("$.[*].datetraitement").value(hasItem(DEFAULT_DATETRAITEMENT.toString())));
    }

    @Test
    @Transactional
    void getTraitement() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        // Get the traitement
        restTraitementMockMvc
            .perform(get(ENTITY_API_URL_ID, traitement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(traitement.getId().intValue()))
            .andExpect(jsonPath("$.traitement").value(DEFAULT_TRAITEMENT))
            .andExpect(jsonPath("$.datetraitement").value(DEFAULT_DATETRAITEMENT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTraitement() throws Exception {
        // Get the traitement
        restTraitementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTraitement() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();

        // Update the traitement
        Traitement updatedTraitement = traitementRepository.findById(traitement.getId()).get();
        // Disconnect from session so that the updates on updatedTraitement are not directly saved in db
        em.detach(updatedTraitement);
        updatedTraitement.traitement(UPDATED_TRAITEMENT).datetraitement(UPDATED_DATETRAITEMENT);

        restTraitementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTraitement.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTraitement))
            )
            .andExpect(status().isOk());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
        Traitement testTraitement = traitementList.get(traitementList.size() - 1);
        assertThat(testTraitement.getTraitement()).isEqualTo(UPDATED_TRAITEMENT);
        assertThat(testTraitement.getDatetraitement()).isEqualTo(UPDATED_DATETRAITEMENT);
    }

    @Test
    @Transactional
    void putNonExistingTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, traitement.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTraitementWithPatch() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();

        // Update the traitement using partial update
        Traitement partialUpdatedTraitement = new Traitement();
        partialUpdatedTraitement.setId(traitement.getId());

        partialUpdatedTraitement.datetraitement(UPDATED_DATETRAITEMENT);

        restTraitementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTraitement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTraitement))
            )
            .andExpect(status().isOk());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
        Traitement testTraitement = traitementList.get(traitementList.size() - 1);
        assertThat(testTraitement.getTraitement()).isEqualTo(DEFAULT_TRAITEMENT);
        assertThat(testTraitement.getDatetraitement()).isEqualTo(UPDATED_DATETRAITEMENT);
    }

    @Test
    @Transactional
    void fullUpdateTraitementWithPatch() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();

        // Update the traitement using partial update
        Traitement partialUpdatedTraitement = new Traitement();
        partialUpdatedTraitement.setId(traitement.getId());

        partialUpdatedTraitement.traitement(UPDATED_TRAITEMENT).datetraitement(UPDATED_DATETRAITEMENT);

        restTraitementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTraitement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTraitement))
            )
            .andExpect(status().isOk());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
        Traitement testTraitement = traitementList.get(traitementList.size() - 1);
        assertThat(testTraitement.getTraitement()).isEqualTo(UPDATED_TRAITEMENT);
        assertThat(testTraitement.getDatetraitement()).isEqualTo(UPDATED_DATETRAITEMENT);
    }

    @Test
    @Transactional
    void patchNonExistingTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, traitement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTraitement() throws Exception {
        int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
        traitement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraitementMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(traitement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Traitement in the database
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTraitement() throws Exception {
        // Initialize the database
        traitementRepository.saveAndFlush(traitement);

        int databaseSizeBeforeDelete = traitementRepository.findAll().size();

        // Delete the traitement
        restTraitementMockMvc
            .perform(delete(ENTITY_API_URL_ID, traitement.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Traitement> traitementList = traitementRepository.findAll();
        assertThat(traitementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
