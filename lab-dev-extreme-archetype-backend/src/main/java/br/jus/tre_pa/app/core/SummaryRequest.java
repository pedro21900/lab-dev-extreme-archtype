package br.jus.tre_pa.app.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Triple;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.Function;

/**
 * Classe com informações de requisição de uma operação de sumarização.
 */
@Setter
@Getter
@EqualsAndHashCode(of = {"dataField", "operation"})
@ToString(of = {"dataField", "operation"})
public class SummaryRequest {

    /**
     * Nome do atributo que sofrerá a operação de sumarização.
     */
    @JsonAlias("selector")
    private String dataField;

    /**
     * Tipo de sumarização a ser realizada.
     *
     * @see Operation
     */
    @JsonAlias("summaryType")
    private Operation operation;

    public enum Operation {
        @JsonProperty("sum") SUM(t -> t.getRight().sum(t.getMiddle().get(t.getLeft()))),
        @JsonProperty("max") MAX(t -> t.getRight().max(t.getMiddle().get(t.getLeft()))),
        @JsonProperty("min") MIN(t -> t.getRight().min(t.getMiddle().get(t.getLeft()))),
        @JsonProperty("count") COUNT(t -> t.getRight().count(t.getMiddle().get(t.getLeft()))),
        @JsonProperty("dcount") DCOUNT(t -> t.getRight().countDistinct(t.getMiddle().get(t.getLeft()))),
        @JsonProperty("avg") AVG(t -> t.getRight().avg(t.getMiddle().get(t.getLeft())));

        @Getter
        Function<Triple<String, Root<?>, CriteriaBuilder>, Expression<? extends Number>> sqlDef;

        Operation(Function<Triple<String, Root<?>, CriteriaBuilder>, Expression<? extends Number>> sqlDef) {
            this.sqlDef = sqlDef;
        }
    }

}