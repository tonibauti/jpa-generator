package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.LinkedHashMap;
import java.util.Map;


public class StringValuesValidator implements ConstraintValidator<StringValues, String>
{
    private StringValues stringValues;
    private Map<String,String> stringValuesMap;
    private boolean ignoreCase;


    public StringValuesValidator() { super(); }


    @Override
    public void initialize(StringValues constraintAnnotation)
    {
        this.stringValues = constraintAnnotation;

        this.stringValuesMap = new LinkedHashMap<>();
        this.ignoreCase = constraintAnnotation.ignoreCase();

        String[] values = constraintAnnotation.value().split( constraintAnnotation.separator() );

        if (ignoreCase)
        {
            for (String s : values)
            {
                String value = s.trim();

                if (!value.isEmpty())
                    stringValuesMap.put(value.toLowerCase(), value);
            }
        }
        else
        {
            for (String s : values)
            {
                String value = s.trim();

                if (!value.isEmpty())
                    stringValuesMap.put(value, value);
            }
        }
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext)
    {
        if (value == null)
            return true;

        if (stringValuesMap.containsKey( (ignoreCase) ? value.trim().toLowerCase() : value.trim() ))
            return true;

        HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext
                .addMessageParameter("values", stringValuesMap.values())
                .buildConstraintViolationWithTemplate( stringValues.message() )
                .addConstraintViolation();

        return false;
    }

}

