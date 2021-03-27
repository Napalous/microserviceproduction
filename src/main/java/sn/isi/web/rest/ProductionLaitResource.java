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
import sn.isi.domain.ProductionLait;
import sn.isi.repository.ProductionLaitRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.ProductionLait}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductionLaitResource {

    private final Logger log = LoggerFactory.getLogger(ProductionLaitResource.class);

    private static final String ENTITY_NAME = "microserviceproductionProductionLait";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductionLaitRepository productionLaitRepository;

    public ProductionLaitResource(ProductionLaitRepository productionLaitRepository) {
        this.productionLaitRepository = productionLaitRepository;
    }

    /**
     * {@code POST  /production-laits} : Create a new productionLait.
     *
     * @param productionLait the productionLait to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productionLait, or with status {@code 400 (Bad Request)} if the productionLait has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/production-laits")
    public ResponseEntity<ProductionLait> createProductionLait(@Valid @RequestBody ProductionLait productionLait)
        throws URISyntaxException {
        log.debug("REST request to save ProductionLait : {}", productionLait);
        if (productionLait.getId() != null) {
            throw new BadRequestAlertException("A new productionLait cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductionLait result = productionLaitRepository.save(productionLait);
        return ResponseEntity
            .created(new URI("/api/production-laits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /production-laits/:id} : Updates an existing productionLait.
     *
     * @param id the id of the productionLait to save.
     * @param productionLait the productionLait to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productionLait,
     * or with status {@code 400 (Bad Request)} if the productionLait is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productionLait couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/production-laits/{id}")
    public ResponseEntity<ProductionLait> updateProductionLait(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductionLait productionLait
    ) throws URISyntaxException {
        log.debug("REST request to update ProductionLait : {}, {}", id, productionLait);
        if (productionLait.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productionLait.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productionLaitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductionLait result = productionLaitRepository.save(productionLait);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productionLait.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /production-laits/:id} : Partial updates given fields of an existing productionLait, field will ignore if it is null
     *
     * @param id the id of the productionLait to save.
     * @param productionLait the productionLait to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productionLait,
     * or with status {@code 400 (Bad Request)} if the productionLait is not valid,
     * or with status {@code 404 (Not Found)} if the productionLait is not found,
     * or with status {@code 500 (Internal Server Error)} if the productionLait couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/production-laits/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ProductionLait> partialUpdateProductionLait(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductionLait productionLait
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductionLait partially : {}, {}", id, productionLait);
        if (productionLait.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productionLait.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productionLaitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductionLait> result = productionLaitRepository
            .findById(productionLait.getId())
            .map(
                existingProductionLait -> {
                    if (productionLait.getQuantite() != null) {
                        existingProductionLait.setQuantite(productionLait.getQuantite());
                    }
                    if (productionLait.getDateproduction() != null) {
                        existingProductionLait.setDateproduction(productionLait.getDateproduction());
                    }

                    return existingProductionLait;
                }
            )
            .map(productionLaitRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productionLait.getId().toString())
        );
    }

    /**
     * {@code GET  /production-laits} : get all the productionLaits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productionLaits in body.
     */
    @GetMapping("/production-laits")
    public ResponseEntity<List<ProductionLait>> getAllProductionLaits(Pageable pageable) {
        log.debug("REST request to get a page of ProductionLaits");
        Page<ProductionLait> page = productionLaitRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /production-laits/:id} : get the "id" productionLait.
     *
     * @param id the id of the productionLait to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productionLait, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/production-laits/{id}")
    public ResponseEntity<ProductionLait> getProductionLait(@PathVariable Long id) {
        log.debug("REST request to get ProductionLait : {}", id);
        Optional<ProductionLait> productionLait = productionLaitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productionLait);
    }

    /**
     * {@code DELETE  /production-laits/:id} : delete the "id" productionLait.
     *
     * @param id the id of the productionLait to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/production-laits/{id}")
    public ResponseEntity<Void> deleteProductionLait(@PathVariable Long id) {
        log.debug("REST request to delete ProductionLait : {}", id);
        productionLaitRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
