package br.jus.tre_pa.app.core;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public abstract class AbstractRepositoryEx<T> implements RepositoryEx<T> {

    @PersistenceContext
    protected EntityManager em;

    private Class<T> entityClass;

    protected AbstractRepositoryEx() {
        this.entityClass = (Class<T>) ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).getRawClass();
    }

    @Override
    public List<SummaryResult> summarize(Specification<T> specification, List<SummaryRequest> summariesRequest) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<T> root = cq.from(entityClass);
        Selection[] selections = summariesRequest
                .stream()
                .map(agg -> agg.getOperation().getSqlDef().apply(Triple.of(agg.getDataField(), root, cb)))
                .toArray(Selection[]::new);
        cq.multiselect(selections);
        applySpecification(specification, root, cq, cb);
        TypedQuery<Tuple> query = em.createQuery(cq);
        Tuple tuple = query.getSingleResult();
        List<SummaryResult> summariesResult = new ArrayList<>();
        for (int i = 0; i < tuple.getElements().size(); i++)
            summariesResult.add(
                    new SummaryResult(
                            summariesRequest.get(i).getDataField(),
                            tuple.get(i),
                            summariesRequest.get(i).getOperation()
                    ));
        return summariesResult;
    }

    @Override
    public List<SummaryResult> summarize(List<SummaryRequest> summariesRequest) {
        return summarize(null, summariesRequest);
    }

    protected <X> void applySpecification(Specification<X> specification, Root<X> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (Objects.nonNull(specification) && Objects.nonNull(specification.toPredicate(root, cq, cb))) {
            cq.where(specification.toPredicate(root, cq, cb));
        }
    }

    protected void applyPagination(TypedQuery<?> q, Pageable pageable) {
        if (Objects.nonNull(pageable)) {
            q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            q.setMaxResults(pageable.getPageSize());
        }
    }

    protected <X> void applySort(Root<X> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Pageable pageable) {
        if (Objects.nonNull(pageable) && Objects.nonNull(pageable.getSort())) {
            cq.orderBy(pageable.getSort()
                    .stream()
                    .map(o -> o.isAscending() ? cb.asc(root.get(o.getProperty())) : cb.desc(root.get(o.getProperty())))
                    .toArray(Order[]::new));
        }
    }

    protected <X> long count(Class<X> countEntityClass, Specification<X> specification) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<X> root = cq.from(countEntityClass);
        cq.select(cb.count(root));
        applySpecification(specification, root, cq, cb);
        TypedQuery<Number> query = em.createQuery(cq);
        return query.getSingleResult().longValue();
    }

}
