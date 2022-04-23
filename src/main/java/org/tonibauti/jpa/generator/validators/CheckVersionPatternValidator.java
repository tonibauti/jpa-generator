package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.List;
import java.util.regex.Pattern;


// https://ihateregex.io/expr/semver/#


public class CheckVersionPatternValidator extends AbstractComponent implements ConstraintValidator<CheckVersionPattern, String>
{
    private static final Pattern VERSION_PATTERN  = Pattern.compile("(0|[1-9]+)(.(0|[1-9]+)){0,2}");
    private static final String SUPPORTED_VERSION = Console.SUPPORTED_VERSION;

    private CheckVersionPattern checkVersionPattern;


    @Override
    public void initialize(CheckVersionPattern constraintAnnotation)
    {
        this.checkVersionPattern = constraintAnnotation;
    }


    private boolean matchPattern(String value)
    {
        return VERSION_PATTERN.matcher(value).matches();
    }


    private boolean isSupportedVersion(String version)
    {
        List<String> auxVersion = super.multiSplit(version, ".");

        if (auxVersion.isEmpty())
            auxVersion.add( version );

        String[] supportedVersion = SUPPORTED_VERSION.split("\\.");

        boolean valid = true;

        for (int i=0; i<supportedVersion.length; i++)
            if (auxVersion.size() > i)
                valid &= (super.getInt(auxVersion.get(i),0) >= super.getInt(supportedVersion[i],0));

        return valid;
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext)
    {
        if (value == null)
            return true;

        if (!matchPattern(value))
        {
            HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("value", value)
                    .addMessageParameter("pattern", VERSION_PATTERN)
                    .buildConstraintViolationWithTemplate( checkVersionPattern.message() )
                    .addConstraintViolation();

            return false;
        }

        if (!isSupportedVersion(value))
        {
            HibernateConstraintValidatorContext hibernateContext = constraintContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("value", value)
                    .addMessageParameter("version", SUPPORTED_VERSION)
                    .buildConstraintViolationWithTemplate( "{validation.constraints.UnsupportedVersion.message}" )
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

}

