package br.jus.tre_pa.app.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CrudService<T, ID> {
    Page<T> findAll(Specification<T> specification, Pageable pageable);

    List<SummaryResult> summarize(Specification<T> specification, List<SummaryRequest> summariesRequest);

    T findById(ID id);

    T findEditById(ID id);

    T insert(T entity);

    T update(ID id, T entity);

    void delete(ID id);
}
