package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.regex.Pattern;


public class CheckVarNameValidator implements ConstraintValidator<CheckVarName, String>
{
    private static final Pattern VAR_NAME_PATTERN = Pattern.compile("[a-zA-z]+[_a-zA-Z0-9]*");

    private CheckVarName checkVarName;


    @Override
    public void initialize(CheckVarName constraintAnnotation)
    {
        this.checkVarName = constraintAnnotation;
    }


    private boolean isVarNamePattern(String value)
    {
        return VAR_NAME_PATTERN.matcher(value).matches();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext)
    {
        if (value == null)
            return true;

        if (isVarNamePattern(value))
            return true;

        HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext
            .addMessageParameter("value", value)
            .addMessageParameter("pattern", VAR_NAME_PATTERN)
            .buildConstraintViolationWithTemplate( checkVarName.message() )
            .addConstraintViolation();

        return false;
    }

}

