package br.jus.tre_pa.app.core;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Classe base para as classes Rest baseadas em entidades da aplicação.
 *
 * @param <T>  Entidade JPA
 * @param <ID> Tipo do atributo Id da entidade JPA
 * @param <S>  Classe de service da entidade JPA
 */
public class AbstractCrudRest<T, ID, S extends CrudService<T, ID>> {

    @Getter
    protected final S service;

    public AbstractCrudRest(S service) {
        this.service = service;
    }

    /**
     * Realiza uma consulta para busca de registros da entidade com suporte a filtragem e paginação.
     *
     * @param q        Query Param no formato RSQL
     * @param pageable Query Param de informações de paginação e ordenação
     * @return Lista de objetos {@link Page} com itens de entidade.
     */
    @GetMapping
    public ResponseEntity<Page<T>> findAll(RSQLParam q, Pageable pageable) {
        return ResponseEntity.ok(service.findAll(q.getSpecification(), pageable));
    }

    /**
     * Realiza uma consulta na base de dados para retorna a instância da entidade JPA definida pelo parametro id.
     *
     * @param id Id da entidade JPA.
     * @return Entidade JPA.
     */
    @GetMapping(path = "/{id}" )
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping(path = "/edit/{id}" )
    public ResponseEntity<T> findEditById(@PathVariable ID id) {
        return ResponseEntity.ok(service.findEditById(id));
    }

    /**
     * Realiza uma operação de insert da entidade passada no body do request.
     *
     * @param entity Entidade JPA a ser salva. Body do request.
     * @return Entidade JPA salva em banco.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<T> insert(@RequestBody @Valid T entity) {
        return ResponseEntity.ok(service.insert(entity));
    }

    /**
     * Realiza uma operação de atualização da entidade passada no body do request e com o Id correspondente no path
     *
     * @param id     Id da entidade a ser atualizada.
     * @param entity Entidade JPA a ser atualizada
     * @return Entidade JPA atualizada em banco
     */
    @PutMapping(path = "/{id}" )
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody @Valid T entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    /**
     * Realiza uma operação de exclusão da entidade.
     *
     * @param id Id da entidade a ser excluída.
     */
    @DeleteMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ID id) {
        service.delete(id);
    }
}
