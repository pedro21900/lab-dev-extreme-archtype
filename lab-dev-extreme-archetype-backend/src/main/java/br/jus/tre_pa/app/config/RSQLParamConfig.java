package br.jus.tre_pa.app.config;

import br.jus.tre_pa.app.core.RSQLParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Configuration
public class RSQLParamConfig implements WebMvcConfigurer {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RSQLParamResolver(entityManager, false));
    }

    public class RSQLParamResolver extends RequestParamMethodArgumentResolver {

        private EntityManager entityManager;

        public RSQLParamResolver(EntityManager entityManager, boolean useDefaultResolution) {
            super(useDefaultResolution);
            this.entityManager = entityManager;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType() == RSQLParam.class;
        }

        @Override
        protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
            return new RSQLParam(entityManager, request.getParameter("q"));
        }

    }
}
