package org.tonibauti.jpa.generator.utils;

import java.util.ArrayList;
import java.util.List;


public class Strings
{
    public static final String EMPTY = "";


    private Strings() {}


    public static String getNotNull(String str)
    {
        return (str != null) ? str : EMPTY;
    }


    public static String getTrimNotNull(String str)
    {
        return (str != null) ? str.trim() : EMPTY;
    }


    public static boolean isNotEmpty(String str)
    {
        return (str != null && !str.trim().isEmpty());
    }


    public static boolean isNullOrEmpty(String str)
    {
        return (str == null || str.trim().isEmpty());
    }


    public static boolean isValidIdentifier(String str, List<String> keyWords)
    {
        char[] chars = str.toCharArray();

        for (char c : chars)
            if (!Character.isLetterOrDigit(c) && c != '_')
                return false;

        if (keyWords != null && !keyWords.isEmpty())
            return !keyWords.contains(str);
        else
            return true;
    }


    public static String capitalizeFirstChar(String str)
    {
        String aux = getTrimNotNull(str);

        if (aux.length() == 0)
            return str;

        if (aux.length() == 1)
            return aux.toUpperCase();

        return aux.substring(0,1).toUpperCase() + aux.substring(1);
    }


    public static String capitalize(String str)
    {
        String aux = getTrimNotNull(str);

        if (aux.length() == 0)
            return str;

        if (aux.length() == 1)
            return aux.toUpperCase();

        return aux.substring(0,1).toUpperCase() + aux.substring(1).toLowerCase();
    }


    private static String toValidVarName(String str, boolean toUpperCase)
    {
        String aux = getTrimNotNull(str);

        if (aux.length() == 0)
            return str;

        if (toUpperCase)
            aux = aux.toUpperCase();

        char[] chars = aux.toCharArray();
        StringBuilder result = new StringBuilder();

        int i=0;
        while(!Character.isLetterOrDigit(chars[i++]));

        for (i=i-1; i<chars.length; i++)
        {
            if (Character.isLetterOrDigit(chars[i]))
            {
                result.append( chars[i] );
            }
            else
            {
                try
                {
                    while(!Character.isLetterOrDigit(chars[++i]));
                    result.append( "_" ).append( chars[i] );
                }
                catch (Exception e) { /*ignored */ }
            }
        }

        return result.toString();
    }


    public static String toValidVarName(String str)
    {
        return toValidVarName(str, false);
    }


    public static String toColumnName(String str)
    {
        return toValidVarName(str, true);
    }


    public static String toPropertyName(String str)
    {
        String aux = getTrimNotNull(str);

        if (aux.length() == 0)
            return str;

        aux = aux.toLowerCase();

        char[] chars = aux.toCharArray();
        StringBuilder result = new StringBuilder();

        int i=0;
        while(!Character.isLetterOrDigit(chars[i++]));

        for (i=i-1; i<chars.length; i++)
        {
            if (Character.isLetterOrDigit(chars[i]))
            {
                result.append( chars[i] );
            }
            else
            {
                try
                {
                    while(!Character.isLetterOrDigit(chars[++i]));
                    result.append( Character.toUpperCase(chars[i]) );
                }
                catch (Exception e) { /* igonore */ }
            }
        }

        return result.toString();
    }


    public static String toClassName(String str)
    {
        return capitalizeFirstChar( toPropertyName(str) );
    }


    public static List<String> toStringList(String str, String separator)
    {
        List<String> list = new ArrayList<>();

        if (isNotEmpty(str))
        {
            try
            {
                int pos;
                int act = 0;

                String value;

                while ( (pos=str.indexOf(separator,act)) >= 0 )
                {
                    value = str.substring(act,pos);
                    value = value.trim();

                    if (!value.isEmpty())
                        list.add( value );

                    act = pos + separator.length();
                }

                // resto despues del ultimo separador
                value = str.substring(act);
                value = value.trim();

                if (!value.isEmpty())
                    list.add( value );
            }
            catch(Exception e) { /* ignored */}

            // no se encontro el separador
            if (list.isEmpty())
                list.add( str );
        }

        return list;
    }


    private static String[] split(String str, String separator, boolean last)
    {
        if (isNullOrEmpty(str))
            return new String[]{ EMPTY, EMPTY };

        if (str.equals(separator))
            return new String[]{ EMPTY, EMPTY };

        int pos = (last) ? str.lastIndexOf(separator) : str.indexOf(separator);

        if (pos < 0)
            return new String[]{ str, EMPTY };

        if (pos == 0)
            return new String[]{ EMPTY, str.substring(1)} ;

        return new String[]{ str.substring(0, pos), str.substring(pos + separator.length()) };
    }


    public static String[] split(String str, String separator)
    {
        return split( str, separator, false );
    }


    public static String[] splitLast(String str, String separator)
    {
        return split( str, separator, true );
    }


    public static boolean wildcard(String str, String mask)
    {
        if (str == null || mask == null)
            return false;

        final String WILDCARD = "*";

        // "*"
        if (WILDCARD.equals(mask))
            return true;

        if (!mask.contains(WILDCARD))
        {
            // "str"
            return str.equals(mask);
        }

        //String[] s = split(mask, WILDCARD);
        String[] s = splitLast(mask, WILDCARD);

        if (isNotEmpty(s[0]))
        {
            // "str*"
            return str.startsWith(s[0]);
        }
        else
        if (isNotEmpty(s[1]))
        {
            // "*str"
            return str.endsWith(s[1]);
        }

        return false;
    }


    public static boolean checkWildcard(String name, String mask, boolean ignoreCase)
    {
        return (ignoreCase) ? wildcard(name.toLowerCase(), mask.toLowerCase()) : wildcard(name, mask);
    }


    public static boolean checkWildcard(String name, List<String> maskList, boolean ignoreCase)
    {
        if (name != null && maskList != null)
            for (String mask : maskList)
                if ( checkWildcard(name, mask, ignoreCase) )
                    return true;

        return false;
    }


    public static String toSpaceSize(int size)
    {
        float spaceSize = (float) size;

        if (spaceSize < 1024)
            //return (Math.round(spaceSize) + "b");
            return (Math.round(spaceSize) + "");

        if (spaceSize < 1024*1024)
            return (Math.round(spaceSize/1024) + "k");

        if (spaceSize < 1024*1024*1024)
            return (Math.round(spaceSize/(1024*1024)) + "m");

        return (Math.round(spaceSize/(1024*1024*1024)) + "g");
    }


    public static void replace(StringBuilder builder, String from, String to)
    {
        int index = builder.indexOf(from);

        while (index != -1)
        {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }


    public static String blanksFill(String str, int maxLength)
    {
        StringBuilder result = new StringBuilder( getTrimNotNull(str) );

        int n = maxLength - result.length();

        for (int i=0; i<n; i++)
            result.append(" ");

        return result.toString();
    }

}

