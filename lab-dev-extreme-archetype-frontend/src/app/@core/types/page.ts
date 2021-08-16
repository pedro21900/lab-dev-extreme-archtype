/**
 * Classe com estrutura de retorna de uma consulta paginada ao backend. Estrutura aderente a paginação Spring Boot.
 * @see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
 */
export class Page<T> {
    last?: boolean;
    totalElements?: number;
    totalPages?: number;
    sort?: string;
    first?: boolean;
    numberOfElements?: number;
    size?: number;
    number?: number;
    content: T[] = [];
}
