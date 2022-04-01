package org.tonibauti.jpa.generator.templates.base;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.tonibauti.jpa.generator.utils.Strings;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;


public class FreemarkerTemplates
{
    private static final FreemarkerTemplates instance = new FreemarkerTemplates();


    public FreemarkerTemplates() {}


    public static FreemarkerTemplates getInstance()
    {
        return instance;
    }


    private static class ConvertMethod implements TemplateMethodModelEx
    {
        private String convertMethod = "";

        public ConvertMethod(String convertMethod)
        {
            this.convertMethod = convertMethod;
        }

        private String getArgument(List arguments) throws TemplateModelException
        {
            if (arguments == null || arguments.isEmpty() || arguments.get(0) == null)
                throw new TemplateModelException("Wrong arguments!");

            return arguments.get(0).toString();
        }

        public Object exec(List arguments) throws TemplateModelException
        {
            switch (convertMethod)
            {
                case "LineSeparator" : return System.lineSeparator();
                case "ClassName"     : return Strings.toClassName( getArgument(arguments) );
                case "PropertyName"  : return Strings.toPropertyName( getArgument(arguments) );
                case "ColumnName"    : return Strings.toColumnName( getArgument(arguments) );
                case "ValidVarName"  : return Strings.toValidVarName( getArgument(arguments) );
                //case "Camel"         : return Strings.camel( getArgument(arguments) );
            }

            throw new TemplateModelException("Invalid convert method: " + convertMethod);
        }
    }


    public void process(String templateName, StringBuilder template, Map<String, Object> mapping) throws Exception
    {
        String templateContent = template.toString();
        template.delete(0, template.length());

        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(templateName, templateContent);

        Configuration config = new Configuration( Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS );
        config.setTemplateLoader( stringLoader );

        Template templateConf = config.getTemplate(templateName, "UTF-8");
        StringWriter writer = new StringWriter();

        mapping.put("lineSeparator", new ConvertMethod("LineSeparator"));
        mapping.put("asClass",       new ConvertMethod("ClassName"));
        mapping.put("asProperty",    new ConvertMethod("PropertyName"));
        mapping.put("asColumn",      new ConvertMethod("ColumnName"));
        mapping.put("asVar",         new ConvertMethod("ValidVarName"));
        //mapping.put("asCamel",       new ConvertMethod("Camel"));

        templateConf.process(mapping, writer);

        template.append( writer );
    }

}

