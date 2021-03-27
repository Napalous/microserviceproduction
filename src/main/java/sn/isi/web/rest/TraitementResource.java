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
import sn.isi.domain.Traitement;
import sn.isi.repository.TraitementRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.Traitement}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TraitementResource {

    private final Logger log = LoggerFactory.getLogger(TraitementResource.class);

    private static final String ENTITY_NAME = "microserviceproductionTraitement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TraitementRepository traitementRepository;

    public TraitementResource(TraitementRepository traitementRepository) {
        this.traitementRepository = traitementRepository;
    }

    /**
     * {@code POST  /traitements} : Create a new traitement.
     *
     * @param traitement the traitement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new traitement, or with status {@code 400 (Bad Request)} if the traitement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/traitements")
    public ResponseEntity<Traitement> createTraitement(@Valid @RequestBody Traitement traitement) throws URISyntaxException {
        log.debug("REST request to save Traitement : {}", traitement);
        if (traitement.getId() != null) {
            throw new BadRequestAlertException("A new traitement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Traitement result = traitementRepository.save(traitement);
        return ResponseEntity
            .created(new URI("/api/traitements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /traitements/:id} : Updates an existing traitement.
     *
     * @param id the id of the traitement to save.
     * @param traitement the traitement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traitement,
     * or with status {@code 400 (Bad Request)} if the traitement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the traitement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/traitements/{id}")
    public ResponseEntity<Traitement> updateTraitement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Traitement traitement
    ) throws URISyntaxException {
        log.debug("REST request to update Traitement : {}, {}", id, traitement);
        if (traitement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, traitement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!traitementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Traitement result = traitementRepository.save(traitement);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, traitement.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /traitements/:id} : Partial updates given fields of an existing traitement, field will ignore if it is null
     *
     * @param id the id of the traitement to save.
     * @param traitement the traitement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traitement,
     * or with status {@code 400 (Bad Request)} if the traitement is not valid,
     * or with status {@code 404 (Not Found)} if the traitement is not found,
     * or with status {@code 500 (Internal Server Error)} if the traitement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/traitements/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Traitement> partialUpdateTraitement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Traitement traitement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Traitement partially : {}, {}", id, traitement);
        if (traitement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, traitement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!traitementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Traitement> result = traitementRepository
            .findById(traitement.getId())
            .map(
                existingTraitement -> {
                    if (traitement.getTraitement() != null) {
                        existingTraitement.setTraitement(traitement.getTraitement());
                    }
                    if (traitement.getDatetraitement() != null) {
                        existingTraitement.setDatetraitement(traitement.getDatetraitement());
                    }

                    return existingTraitement;
                }
            )
            .map(traitementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, traitement.getId().toString())
        );
    }

    /**
     * {@code GET  /traitements} : get all the traitements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of traitements in body.
     */
    @GetMapping("/traitements")
    public ResponseEntity<List<Traitement>> getAllTraitements(Pageable pageable) {
        log.debug("REST request to get a page of Traitements");
        Page<Traitement> page = traitementRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /traitements/:id} : get the "id" traitement.
     *
     * @param id the id of the traitement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the traitement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/traitements/{id}")
    public ResponseEntity<Traitement> getTraitement(@PathVariable Long id) {
        log.debug("REST request to get Traitement : {}", id);
        Optional<Traitement> traitement = traitementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(traitement);
    }

    /**
     * {@code DELETE  /traitements/:id} : delete the "id" traitement.
     *
     * @param id the id of the traitement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/traitements/{id}")
    public ResponseEntity<Void> deleteTraitement(@PathVariable Long id) {
        log.debug("REST request to delete Traitement : {}", id);
        traitementRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
