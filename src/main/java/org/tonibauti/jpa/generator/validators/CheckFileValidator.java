package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.io.File;
import java.util.Collection;


public class CheckFileValidator implements ConstraintValidator<CheckFile, Object>
{
    private CheckFile checkFile;


    @Override
    public void initialize(CheckFile constraintAnnotation)
    {
        this.checkFile = constraintAnnotation;
    }

    private boolean existsFile(File file)
    {
        return (file.exists() && file.isFile());
    }

    private boolean check(File file, ConstraintValidatorContext constraintContext)
    {
        if (file == null)
            return true;

        if (existsFile(file))
            return true;

        HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext
            .addMessageParameter("file", file.getAbsolutePath())
            .buildConstraintViolationWithTemplate( checkFile.message() )
            .addConstraintViolation();

        return false;
    }


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintContext)
    {
        if (value == null)
            return true;

        boolean result = true;

        if (value instanceof Collection)
        {
            Collection<?> collection = (Collection<?>) value;

            if (!collection.isEmpty())
            {
                for (Object item : collection)
                {
                    if (item == null)
                        continue;

                    if (item instanceof File)
                    {
                        File file = (File) item;
                        if (!check(file, constraintContext))
                            result = false;
                    }
                    else
                    {
                        String fileName = item.toString().trim();
                        if (!check(new File(fileName), constraintContext))
                            result = false;
                    }
                }
            }
        }
        else
        if (value instanceof File)
        {
            File file = (File) value;
            result = check(file, constraintContext);
        }
        else
        {
            String fileName = value.toString().trim();
            result = check(new File(fileName), constraintContext);
        }

        return result;
    }

}

