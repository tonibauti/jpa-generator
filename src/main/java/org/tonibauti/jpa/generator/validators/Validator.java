package org.tonibauti.jpa.generator.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.*;


public class Validator extends AbstractComponent
{
    private static final Validator instance = new Validator();

    private final jakarta.validation.Validator validator;


    private Validator()
    {
        // disable logs before creating hibernate-validator instance
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.OFF);

        Locale.setDefault( Locale.US );

        List<String> bundleNames = new ArrayList<>();
        bundleNames.add( "validation_errors" );

        ResourceBundleLocator resourceBundleLocator = new AggregateResourceBundleLocator( bundleNames );
        MessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator( resourceBundleLocator );

        validator = Validation.byProvider( HibernateValidator.class )
                    .configure()
                    .messageInterpolator( messageInterpolator )
                    .buildValidatorFactory()
                    .getValidator();
    }


    public static Validator getInstance()
    {
        return instance;
    }


    @Override
    public Set<ConstraintViolation<?>> validate(Object obj)
    {
        return new LinkedHashSet<>( validator.validate(obj) );
    }


    public void validate(Config config)
    {
        if (isNullOrEmpty(config))
            return;

        Set<ConstraintViolation<?>> constraintViolations = config.validate();

        if (isNotEmpty(constraintViolations))
        {
            StringBuilder validations = new StringBuilder();

            validations.append("\n");

            for (ConstraintViolation<?> violation : constraintViolations)
            {
                validations.append("\t");

                validations
                    .append("'")
                    .append(violation.getRootBeanClass().getSimpleName())
                    .append(".")
                    .append(violation.getPropertyPath())
                    .append("'")
                    .append(" - ")
                    .append(violation.getMessage())
                    ;

                validations.append("\n");
            }

            Console.throwValidationException( validations.toString() );
        }
    }

}

