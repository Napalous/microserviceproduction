package sn.isi.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.isi.domain.FicheMedical;
import sn.isi.repository.FicheMedicalRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.FicheMedical}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FicheMedicalResource {

    private final Logger log = LoggerFactory.getLogger(FicheMedicalResource.class);

    private static final String ENTITY_NAME = "microserviceproductionFicheMedical";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FicheMedicalRepository ficheMedicalRepository;

    public FicheMedicalResource(FicheMedicalRepository ficheMedicalRepository) {
        this.ficheMedicalRepository = ficheMedicalRepository;
    }

    /**
     * {@code POST  /fiche-medicals} : Create a new ficheMedical.
     *
     * @param ficheMedical the ficheMedical to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ficheMedical, or with status {@code 400 (Bad Request)} if the ficheMedical has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fiche-medicals")
    public ResponseEntity<FicheMedical> createFicheMedical(@Valid @RequestBody FicheMedical ficheMedical) throws URISyntaxException {
        log.debug("REST request to save FicheMedical : {}", ficheMedical);
        if (ficheMedical.getId() != null) {
            throw new BadRequestAlertException("A new ficheMedical cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FicheMedical result = ficheMedicalRepository.save(ficheMedical);
        return ResponseEntity
            .created(new URI("/api/fiche-medicals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fiche-medicals/:id} : Updates an existing ficheMedical.
     *
     * @param id the id of the ficheMedical to save.
     * @param ficheMedical the ficheMedical to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ficheMedical,
     * or with status {@code 400 (Bad Request)} if the ficheMedical is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ficheMedical couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fiche-medicals/{id}")
    public ResponseEntity<FicheMedical> updateFicheMedical(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FicheMedical ficheMedical
    ) throws URISyntaxException {
        log.debug("REST request to update FicheMedical : {}, {}", id, ficheMedical);
        if (ficheMedical.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ficheMedical.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ficheMedicalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FicheMedical result = ficheMedicalRepository.save(ficheMedical);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ficheMedical.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fiche-medicals/:id} : Partial updates given fields of an existing ficheMedical, field will ignore if it is null
     *
     * @param id the id of the ficheMedical to save.
     * @param ficheMedical the ficheMedical to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ficheMedical,
     * or with status {@code 400 (Bad Request)} if the ficheMedical is not valid,
     * or with status {@code 404 (Not Found)} if the ficheMedical is not found,
     * or with status {@code 500 (Internal Server Error)} if the ficheMedical couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fiche-medicals/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<FicheMedical> partialUpdateFicheMedical(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FicheMedical ficheMedical
    ) throws URISyntaxException {
        log.debug("REST request to partial update FicheMedical partially : {}, {}", id, ficheMedical);
        if (ficheMedical.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ficheMedical.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ficheMedicalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FicheMedical> result = ficheMedicalRepository
            .findById(ficheMedical.getId())
            .map(
                existingFicheMedical -> {
                    if (ficheMedical.getObservation() != null) {
                        existingFicheMedical.setObservation(ficheMedical.getObservation());
                    }
                    if (ficheMedical.getDateconsultation() != null) {
                        existingFicheMedical.setDateconsultation(ficheMedical.getDateconsultation());
                    }

                    return existingFicheMedical;
                }
            )
            .map(ficheMedicalRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ficheMedical.getId().toString())
        );
    }

    /**
     * {@code GET  /fiche-medicals} : get all the ficheMedicals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ficheMedicals in body.
     */
    @GetMapping("/fiche-medicals")
    public ResponseEntity<List<FicheMedical>> getAllFicheMedicals(Pageable pageable) {
        log.debug("REST request to get a page of FicheMedicals");
        Page<FicheMedical> page = ficheMedicalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fiche-medicals/:id} : get the "id" ficheMedical.
     *
     * @param id the id of the ficheMedical to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ficheMedical, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fiche-medicals/{id}")
    public ResponseEntity<FicheMedical> getFicheMedical(@PathVariable Long id) {
        log.debug("REST request to get FicheMedical : {}", id);
        Optional<FicheMedical> ficheMedical = ficheMedicalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ficheMedical);
    }

    /**
     * {@code DELETE  /fiche-medicals/:id} : delete the "id" ficheMedical.
     *
     * @param id the id of the ficheMedical to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fiche-medicals/{id}")
    public ResponseEntity<Void> deleteFicheMedical(@PathVariable Long id) {
        log.debug("REST request to delete FicheMedical : {}", id);
        ficheMedicalRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
