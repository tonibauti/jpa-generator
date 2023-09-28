package org.tonibauti.jpa.generator.main;


public class Test
{
    private static String[] getArgs()
    {
        return new String[]
        {
                //"-h",
                "-f",

                //"C:/Temp/jpa-generator/boyaca-mysql.yml",

                "C:/Temp/jpa-generator/ss-mysql.yml",

                //"C:/Temp/jpa-generator/manadas-access.yml",
                //"C:/Temp/jpa-generator/manadas-mysql.yml",

                //"C:/Temp/jpa-generator/manadas-postgresql.yml",

                //"C:/Temp/jpa-generator/eid_emails-mysql.yml",

                //"C:/Temp/jpa-generator/abc-mysql.yml",

                //"-e",
                //"C:/Temp/jpa-generator/manadas-mysql.env",

                "-v",
        };
    }


    public static void main(String[] args)
    {
        Main.main( getArgs() );
    }

}

