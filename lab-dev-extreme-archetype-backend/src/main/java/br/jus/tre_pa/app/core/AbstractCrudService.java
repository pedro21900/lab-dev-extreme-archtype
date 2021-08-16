package br.jus.tre_pa.app.core;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class AbstractCrudService<T, ID, R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID> & RepositoryEx<T>> implements CrudService<T, ID> {

    @Getter
    protected final R repository;

    public AbstractCrudService(R repository) {
        this.repository = repository;
    }

    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    public List<SummaryResult> summarize(Specification<T> specification, List<SummaryRequest> summariesRequest) {
        return repository.summarize(specification, summariesRequest);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    @Override
    public T findEditById(ID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    @Override
    @Transactional
    public T insert(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        if (!repository.existsById(id)) throw new EntityNotFoundException(id.toString());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        repository.deleteById(id);
    }
}
