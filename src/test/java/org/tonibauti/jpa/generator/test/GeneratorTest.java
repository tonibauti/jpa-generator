package org.tonibauti.jpa.generator.test;

import org.junit.Test;
import org.tonibauti.jpa.generator.main.Main;


public class GeneratorTest
{

    private String[] getArgs()
    {
        return new String[]
        {
            //"-h",
            "-f",

            //"C:/Temp/jpa-generator/manadas-access.yml",
            "C:/Temp/jpa-generator/manadas-mysql.yml",

            //"C:/Temp/jpa-generator/manadas-postgresql.yml",

            //"./test-postgresql.yml",

            "-v",
        };
    }


    @Test
    public void generateTest()
    {
        Main.main( getArgs() );
    }

}

