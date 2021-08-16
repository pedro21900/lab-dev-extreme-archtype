import {KeycloakService} from "keycloak-angular";
import {environment} from "../../environments/environment";

//Sem authorization header
const bearerExcludedUrls = ['/assets', '/clients/public'];

/**
 * Função chamada na inicialização da aplicação para inicialização da segurança da aplicação.
 *
 * @param keycloakService Service do Keycloak para realizar a autenticação do usuário.
 */
export function securityInitFn(keycloakService: KeycloakService) {
  return async () => {
    console.log("Inicializando a segurança da aplicação...");
    const keycloak = await keycloakService.init({
      config: {
        url: `${environment.keycloak.authServerUrl}`,
        realm: `${environment.keycloak.realm}`,
        clientId: `${environment.keycloak.resource}`,

      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false,
      },
      bearerExcludedUrls: bearerExcludedUrls,
    });
    console.log("Segurança inicializada com sucesso!");
    return keycloak;
  }
}
