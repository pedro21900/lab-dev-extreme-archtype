package br.jus.tre_pa.app.core;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Classe com as definições do repositorio extendido.
 *
 * @param <T> Entidade JPA
 */
public interface RepositoryEx<T> {

    /**
     * Realiza uma opereação de summarização com suporte a Specification.
     *
     * @param specification    Informções de Specification da consulta
     * @param summariesRequest Lista com a requisição de operações de sumarização.
     * @return Lista com o(s) resultado(s) da operações de sumarização.
     */
    List<SummaryResult> summarize(Specification<T> specification, List<SummaryRequest> summariesRequest);

    /**
     * Realiza uma opereação de summarização
     *
     * @param summariesRequest Lista com a requisição de operações de sumarização.
     * @return Lista com o(s) resultado(s) da operações de sumarização.
     */
    List<SummaryResult> summarize(List<SummaryRequest> summariesRequest);

}
