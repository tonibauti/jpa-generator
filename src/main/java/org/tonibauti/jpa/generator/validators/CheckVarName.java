package org.tonibauti.jpa.generator.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckVarNameValidator.class)
@Documented
public @interface CheckVarName
{
    String message() default "{validation.constraints.VarName.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

