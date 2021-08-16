package br.jus.tre_pa.app.core;

import lombok.*;

/**
 * Classe que contém o resultado de uma operação de sumarização em determinado atributo.
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SummaryResult {

    /**
     * Nome do atributo que sofreu a operação de agragação.
     */
    private String dataField;

    /**
     * Resultado da operação de agregação no atributo.
     */
    private Object result;

    /**
     * Tipo de operação de agregação.
     */
    private SummaryRequest.Operation operation;

}