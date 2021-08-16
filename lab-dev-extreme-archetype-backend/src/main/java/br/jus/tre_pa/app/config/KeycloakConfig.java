package br.jus.tre_pa.app.config;

import br.jus.tre_pa.app.core.security.ResourceSecurityConfig;
import br.jus.tre_pa.app.core.security.annotations.CompositeRoleDef;
import br.jus.tre_pa.app.core.security.annotations.RoleDef;
import br.jus.tre_pa.app.core.security.annotations.RoleDefs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.UserInfo;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe de configuração com as definições de segurança da aplicação.
 */
@Slf4j
@AllArgsConstructor
@KeycloakConfiguration
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Injeção com todas as classes que implementam {@link ResourceSecurityConfig}.
     */
    private final Set<ResourceSecurityConfig> resourceSecurityConfigs;

    private final Environment env;

    public final KeycloakClientRequestFactory keycloakClientRequestFactory;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        resourceSecurityConfigs.forEach(resSec -> resSec.configure(http));
        http.authorizeRequests().antMatchers("/actuator/**").permitAll().anyRequest().denyAll();
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        SimpleAuthorityMapper simpleAuthorityMapper = new SimpleAuthorityMapper();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(simpleAuthorityMapper);
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public KeycloakSecurityContext accessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserInfo userInfo() {
        ResponseEntity<UserInfo> response = keycloakRestTemplate().getForEntity(
            env.getRequiredProperty("keycloak.auth-server-url").concat("/realms/").concat(env.getRequiredProperty("keycloak.realm")).concat("/protocol/openid-connect/userinfo"), UserInfo.class);
        return response.getBody();
    }

    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder
            .builder()
            .serverUrl(env.getRequiredProperty("keycloak.auth-server-url"))
            .realm("master")
            .grantType(OAuth2Constants.PASSWORD)
            .clientId("admin-cli")
            .username(env.getProperty("keycloak.admin-user", "admin"))
            .password(env.getProperty("keycloak.admin-pass", "admin"))
            .resteasyClient(new ResteasyClientBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .connectionPoolSize(5).build())
            .build();
    }

    /**
     * Método chamado logo após a inicialização do sistema e utilizado para iniciar o registro da aplicação no Keycloak.
     *
     * @param event
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Profiles: {}", env.getActiveProfiles());
        String authServerUrl = env.getRequiredProperty("keycloak.auth-server-url");
        String realm = env.getRequiredProperty("keycloak.realm");
        log.info("");
        log.info("Iniciando configuração da aplicação no Keycloak {} (REALM={})", authServerUrl, realm);

        /*
         * Definições de registro do client backend.
         */
        ClientRepresentation backend = new ClientRepresentation();
        backend.setName(env.getRequiredProperty("keycloak.config.backend-id"));
        backend.setClientId(env.getRequiredProperty("keycloak.config.backend-id"));
        backend.setBearerOnly(true);
        backend.setSecret(env.getRequiredProperty("keycloak.credentials.secret"));
        getKeycloak().realm(realm).clients().create(backend).close();
        log.info("Client backend {} registrado com sucesso.", backend.getName());
        /*
         * Registro das roles do backend.
         */
        ClientResource backendResource = backendResource();
        registerRoles(backendResource);
        registerCompositeRoles(backendResource);
        /*
         * Definições de registro do client frontend.
         */
        ClientRepresentation frontend = new ClientRepresentation();
        frontend.setName(env.getRequiredProperty("keycloak.config.frontend-id"));
        frontend.setClientId(env.getRequiredProperty("keycloak.config.frontend-id"));
        frontend.setPublicClient(true);
        frontend.setDirectAccessGrantsEnabled(false);
        frontend.setWebOrigins(Arrays.asList("*"));
        frontend.setRedirectUris(
            Arrays.asList(
                env.getProperty("keycloak.config.valid-redirect-url").split(",")
            )
        );
        frontend.setBaseUrl(env.getProperty("keycloak.config.base-url"));
        frontend.setFullScopeAllowed(false);
        getKeycloak().realm(realm).clients().create(frontend).close();
        log.info("Client frontend {} registrado com sucesso.", frontend.getName());
        ClientResource frontendResource = frontendResource();

        configScopeMappings(backendResource, frontendResource);
        createRootUser();
    }

    private void registerRoles(ClientResource backendResource) {
        log.info("Add roles...");
        for (ResourceSecurityConfig resSec : resourceSecurityConfigs) {
            if (resSec.getClass().getSuperclass().isAnnotationPresent(RoleDefs.class)) {
                RoleDef[] roles = resSec.getClass().getSuperclass().getAnnotation(RoleDefs.class).roles();
                Map<String, RoleRepresentation> rolesInKeycloak = rolesInKeycloak();
                log.debug("rolesInKeycloak: {}", rolesInKeycloak.keySet());
                Stream.of(roles)
                    .filter(role -> !rolesInKeycloak.containsKey(role.name()))
                    .map(roleDef -> new RoleRepresentation(roleDef.name(), roleDef.description(), false))
                    .forEach(role -> backendResource.roles().create(role));
//                log.info("...({})", Stream.of(roles).map(RoleDef::name).collect(Collectors.joining(", ")));
            }
        }
    }

    private void registerCompositeRoles(ClientResource backendResource) {
        for (ResourceSecurityConfig resSec : resourceSecurityConfigs) {
            if (resSec.getClass().getSuperclass().isAnnotationPresent(RoleDefs.class)) {
                CompositeRoleDef[] roles = resSec.getClass().getSuperclass().getAnnotation(RoleDefs.class).compositeRoles();
                Map<String, RoleRepresentation> rolesInKeycloak = rolesInKeycloak();
                log.debug("rolesInKeycloak: {}", rolesInKeycloak.keySet());
                Stream.of(roles)
                    .forEach(compositeRoleDef -> backendResource.roles().get(compositeRoleDef.name())
                        .addComposites(
                            Stream.of(compositeRoleDef.roles())
                                .map(r -> rolesInKeycloak.get(r))
                                .collect(Collectors.toList()))
                    );
//                log.info("Composite Role ({}) [{}]", Stream.of(roles).map(CompositeRoleDef::name));
            }
        }
    }

    private void configScopeMappings(ClientResource backendResource, ClientResource frontendResource) {
        frontendResource.getScopeMappings().clientLevel(backendResource.toRepresentation().getId())
            .add(resourceSecurityConfigs.stream()
                .flatMap(r -> Stream.of(r.getClass().getSuperclass().getAnnotation(RoleDefs.class).roles()))
                .map(roleDef -> new RoleRepresentation(roleDef.name(), roleDef.description(), false))
                .collect(Collectors.toList())
            );

        frontendResource.getScopeMappings().clientLevel(accountResource().toRepresentation().getId())
            .add(Arrays.asList(
                new RoleRepresentation("view-profile", "", false),
                new RoleRepresentation("manage-account", "", false)
            ));
    }

    private void createRootUser() {
        if (Arrays.asList(env.getActiveProfiles()).isEmpty()) {
            String realm = env.getRequiredProperty("keycloak.realm");
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername("root");
            userRepresentation.setEmail("root@tre-pa.jus.br");
            userRepresentation.setFirstName("Root");
            userRepresentation.setEnabled(true);
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setValue("root");
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            userRepresentation.setCredentials(Arrays.asList(credentialRepresentation));

            getKeycloak().realm(realm).users().create(userRepresentation).close();
            rootResource().roles().clientLevel(backendResource().toRepresentation().getId())
                .add(rolesInKeycloak().values().stream().collect(Collectors.toList()));
            log.info("User root criado.");
        }
    }

    /*
     * Retorna o objeto ClientResource do backend.
     */
    private ClientResource backendResource() {
        String realm = env.getRequiredProperty("keycloak.realm");
        String backendId = env.getRequiredProperty("keycloak.config.backend-id");
        ClientResource backendResource = getKeycloak().realm(realm).clients().findByClientId(backendId).stream()
            .map(client -> getKeycloak().realm(realm).clients().get(client.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Erro ao encontrar client do backend '%s'", backendId)));
        return backendResource;
    }

    private ClientResource frontendResource() {
        String realm = env.getRequiredProperty("keycloak.realm");
        String frontendId = env.getRequiredProperty("keycloak.config.frontend-id");
        ClientResource frontendResource = getKeycloak().realm(realm).clients().findByClientId(frontendId).stream()
            .map(client -> getKeycloak().realm(realm).clients().get(client.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Erro ao encontrar client do frontend '%s'", frontendId)));
        return frontendResource;
    }

    private ClientResource accountResource() {
        String realm = env.getRequiredProperty("keycloak.realm");
        ClientResource accountResource = getKeycloak().realm(realm).clients().findByClientId("account").stream()
            .map(client -> getKeycloak().realm(realm).clients().get(client.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Erro ao encontrar client do frontend '%s'", "account")));
        return accountResource;
    }

    private UserResource rootResource() {
        String realm = env.getRequiredProperty("keycloak.realm");
        UserResource rootResource = getKeycloak().realm(realm).users().list().stream()
            .map(user -> getKeycloak().realm(realm).users().get(user.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Erro ao encontrar usuário root")));
        return rootResource;
    }

    /*
     * Retorna as roles já registradas no Keycloak.
     */
    private Map<String, RoleRepresentation> rolesInKeycloak() {
        Map<String, RoleRepresentation> rolesInKeycloak = backendResource().roles().list()
            .stream()
            .collect(Collectors.toMap(RoleRepresentation::getName, Function.identity()));
        return rolesInKeycloak;
    }

}
