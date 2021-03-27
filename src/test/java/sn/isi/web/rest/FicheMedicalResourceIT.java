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
import sn.isi.domain.FicheMedical;
import sn.isi.repository.FicheMedicalRepository;

/**
 * Integration tests for the {@link FicheMedicalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FicheMedicalResourceIT {

    private static final String DEFAULT_OBSERVATION = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATECONSULTATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATECONSULTATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/fiche-medicals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FicheMedicalRepository ficheMedicalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFicheMedicalMockMvc;

    private FicheMedical ficheMedical;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheMedical createEntity(EntityManager em) {
        FicheMedical ficheMedical = new FicheMedical().observation(DEFAULT_OBSERVATION).dateconsultation(DEFAULT_DATECONSULTATION);
        return ficheMedical;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheMedical createUpdatedEntity(EntityManager em) {
        FicheMedical ficheMedical = new FicheMedical().observation(UPDATED_OBSERVATION).dateconsultation(UPDATED_DATECONSULTATION);
        return ficheMedical;
    }

    @BeforeEach
    public void initTest() {
        ficheMedical = createEntity(em);
    }

    @Test
    @Transactional
    void createFicheMedical() throws Exception {
        int databaseSizeBeforeCreate = ficheMedicalRepository.findAll().size();
        // Create the FicheMedical
        restFicheMedicalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isCreated());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeCreate + 1);
        FicheMedical testFicheMedical = ficheMedicalList.get(ficheMedicalList.size() - 1);
        assertThat(testFicheMedical.getObservation()).isEqualTo(DEFAULT_OBSERVATION);
        assertThat(testFicheMedical.getDateconsultation()).isEqualTo(DEFAULT_DATECONSULTATION);
    }

    @Test
    @Transactional
    void createFicheMedicalWithExistingId() throws Exception {
        // Create the FicheMedical with an existing ID
        ficheMedical.setId(1L);

        int databaseSizeBeforeCreate = ficheMedicalRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFicheMedicalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkObservationIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheMedicalRepository.findAll().size();
        // set the field null
        ficheMedical.setObservation(null);

        // Create the FicheMedical, which fails.

        restFicheMedicalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateconsultationIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheMedicalRepository.findAll().size();
        // set the field null
        ficheMedical.setDateconsultation(null);

        // Create the FicheMedical, which fails.

        restFicheMedicalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFicheMedicals() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        // Get all the ficheMedicalList
        restFicheMedicalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ficheMedical.getId().intValue())))
            .andExpect(jsonPath("$.[*].observation").value(hasItem(DEFAULT_OBSERVATION)))
            .andExpect(jsonPath("$.[*].dateconsultation").value(hasItem(DEFAULT_DATECONSULTATION.toString())));
    }

    @Test
    @Transactional
    void getFicheMedical() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        // Get the ficheMedical
        restFicheMedicalMockMvc
            .perform(get(ENTITY_API_URL_ID, ficheMedical.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ficheMedical.getId().intValue()))
            .andExpect(jsonPath("$.observation").value(DEFAULT_OBSERVATION))
            .andExpect(jsonPath("$.dateconsultation").value(DEFAULT_DATECONSULTATION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFicheMedical() throws Exception {
        // Get the ficheMedical
        restFicheMedicalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFicheMedical() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();

        // Update the ficheMedical
        FicheMedical updatedFicheMedical = ficheMedicalRepository.findById(ficheMedical.getId()).get();
        // Disconnect from session so that the updates on updatedFicheMedical are not directly saved in db
        em.detach(updatedFicheMedical);
        updatedFicheMedical.observation(UPDATED_OBSERVATION).dateconsultation(UPDATED_DATECONSULTATION);

        restFicheMedicalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFicheMedical.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFicheMedical))
            )
            .andExpect(status().isOk());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
        FicheMedical testFicheMedical = ficheMedicalList.get(ficheMedicalList.size() - 1);
        assertThat(testFicheMedical.getObservation()).isEqualTo(UPDATED_OBSERVATION);
        assertThat(testFicheMedical.getDateconsultation()).isEqualTo(UPDATED_DATECONSULTATION);
    }

    @Test
    @Transactional
    void putNonExistingFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ficheMedical.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFicheMedicalWithPatch() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();

        // Update the ficheMedical using partial update
        FicheMedical partialUpdatedFicheMedical = new FicheMedical();
        partialUpdatedFicheMedical.setId(ficheMedical.getId());

        restFicheMedicalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFicheMedical.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheMedical))
            )
            .andExpect(status().isOk());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
        FicheMedical testFicheMedical = ficheMedicalList.get(ficheMedicalList.size() - 1);
        assertThat(testFicheMedical.getObservation()).isEqualTo(DEFAULT_OBSERVATION);
        assertThat(testFicheMedical.getDateconsultation()).isEqualTo(DEFAULT_DATECONSULTATION);
    }

    @Test
    @Transactional
    void fullUpdateFicheMedicalWithPatch() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();

        // Update the ficheMedical using partial update
        FicheMedical partialUpdatedFicheMedical = new FicheMedical();
        partialUpdatedFicheMedical.setId(ficheMedical.getId());

        partialUpdatedFicheMedical.observation(UPDATED_OBSERVATION).dateconsultation(UPDATED_DATECONSULTATION);

        restFicheMedicalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFicheMedical.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheMedical))
            )
            .andExpect(status().isOk());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
        FicheMedical testFicheMedical = ficheMedicalList.get(ficheMedicalList.size() - 1);
        assertThat(testFicheMedical.getObservation()).isEqualTo(UPDATED_OBSERVATION);
        assertThat(testFicheMedical.getDateconsultation()).isEqualTo(UPDATED_DATECONSULTATION);
    }

    @Test
    @Transactional
    void patchNonExistingFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ficheMedical.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isBadRequest());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFicheMedical() throws Exception {
        int databaseSizeBeforeUpdate = ficheMedicalRepository.findAll().size();
        ficheMedical.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFicheMedicalMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ficheMedical))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FicheMedical in the database
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFicheMedical() throws Exception {
        // Initialize the database
        ficheMedicalRepository.saveAndFlush(ficheMedical);

        int databaseSizeBeforeDelete = ficheMedicalRepository.findAll().size();

        // Delete the ficheMedical
        restFicheMedicalMockMvc
            .perform(delete(ENTITY_API_URL_ID, ficheMedical.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FicheMedical> ficheMedicalList = ficheMedicalRepository.findAll();
        assertThat(ficheMedicalList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
