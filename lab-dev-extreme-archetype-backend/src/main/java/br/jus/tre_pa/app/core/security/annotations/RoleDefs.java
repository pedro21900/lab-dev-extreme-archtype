package br.jus.tre_pa.app.core.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleDefs {
    RoleDef[] roles();

    CompositeRoleDef[] compositeRoles() default {};
}
