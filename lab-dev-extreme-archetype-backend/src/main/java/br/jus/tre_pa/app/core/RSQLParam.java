package br.jus.tre_pa.app.core;

import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;
import com.github.tennaito.rsql.misc.ArgumentFormatException;
import com.github.tennaito.rsql.misc.ArgumentParser;
import com.github.tennaito.rsql.misc.DefaultArgumentParser;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class RSQLParam {
    private final EntityManager entityManager;
    @Getter
    private final String q;


    public <T> Specification<T> getSpecification() {
        if (Objects.isNull(q)) return null;
        return (root, cq, cb) -> {
            JpaPredicateVisitor<T> predicateVisitor = new JpaPredicateVisitor<>();
            predicateVisitor.getBuilderTools().setArgumentParser(new LocalDateAndLocalDateTimeArgumentParser());
            RSQLVisitor<Predicate, EntityManager> visitor = predicateVisitor.defineRoot(root);
            Node rootNode = new RSQLParser().parse(q);
            return rootNode.accept(visitor, entityManager);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSQLParam rsqlParam = (RSQLParam) o;
        return q.equals(rsqlParam.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(q);
    }

    static class LocalDateAndLocalDateTimeArgumentParser extends DefaultArgumentParser {

        @SuppressWarnings("unchecked")
        @Override
        public <T> T parse(String argument, Class<T> type) throws ArgumentFormatException, IllegalArgumentException {
            if (type.equals(LocalDate.class)) {
                return (T) LocalDate.parse(argument);
            }
            if (type.equals(LocalDateTime.class)) {
                return (T) LocalDateTime.parse(argument);
            }
            return super.parse(argument, type);
        }

    }

}
