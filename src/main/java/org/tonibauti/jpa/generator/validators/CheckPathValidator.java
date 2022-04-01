package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.io.File;


public class CheckPathValidator implements ConstraintValidator<CheckPath, String>
{
    private CheckPath checkPath;


    @Override
    public void initialize(CheckPath constraintAnnotation)
    {
        this.checkPath = constraintAnnotation;
    }

    @Override
    public boolean isValid(String pathName, ConstraintValidatorContext constraintContext)
    {
        if (pathName == null)
            return true;

        File file = new File( pathName.trim() );

        if (file.exists() && file.isDirectory())
            return true;

        if (checkPath.createIfNoExists())
            if (file.mkdirs())
                return true;

        HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext
            .addMessageParameter("path", file.getAbsolutePath())
            .buildConstraintViolationWithTemplate( checkPath.message() )
            .addConstraintViolation();

        return false;
    }

}

