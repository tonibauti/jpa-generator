package org.tonibauti.jpa.generator.main;

import jakarta.validation.ConstraintViolation;
import org.tonibauti.jpa.generator.utils.Triple;
import org.tonibauti.jpa.generator.validators.Validator;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@SuppressWarnings("unchecked")
public abstract class AbstractComponent
{

    protected AbstractComponent() {}


    protected Set<ConstraintViolation<?>> validate(Object obj)
    {
        return Validator.getInstance().validate( obj );
    }


    protected int getProcessorsNumber()
    {
        return Runtime.getRuntime().availableProcessors();
    }


    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException th)
        {
            Thread.currentThread().interrupt();
        }
    }


    protected <T> T safeEval(Supplier<T> supplier, T defaultResult)
    {
        try
        {
            return supplier.get();
        }
        catch (Exception e)
        {
            return defaultResult;
        }
    }


    protected <T> T safeEval(Supplier<T> supplier)
    {
        return safeEval(supplier, null);
    }


    protected <T> boolean isSafeEval(Supplier<T> supplier)
    {
        return (safeEval(supplier) != null);
    }


    protected <T> T trim(T value)
    {
        if (value != null)
            if (value instanceof String)
                return (T) ((String)value).trim();

        return value;
    }


    protected boolean bool(Boolean value)
    {
        return bool(value, false);
    }


    protected boolean bool(Boolean value, boolean defaultValue)
    {
        return (value != null) ? value : defaultValue;
    }


    protected int integer(Integer value)
    {
        return integer(value, 0);
    }


    protected int integer(Integer value, int defaultValue)
    {
        return (value != null) ? value : defaultValue;
    }


    protected Integer getInt(String value, Integer defaultValue)
    {
        try { return Integer.parseInt(value); } catch (Exception e) { return defaultValue; }
    }


    protected boolean isNullOrEmpty(Object value)
    {
        if (value == null)
            return true;

        if (value.getClass().isArray() && (Array.getLength(value) == 0))
            return true;

        if (value instanceof Collection && ((Collection<?>)value).isEmpty())
            return true;

        if (value instanceof Map && ((Map<?,?>)value).isEmpty())
            return true;

        if (value instanceof Triple && ((Triple<?,?,?>)value).isEmpty())
            return true;

        if (value instanceof String)
            return ((String)value).trim().isEmpty();

        return false;
    }


    protected boolean isAnyNullOrEmpty(Object... values)
    {
        if (values == null)
            return true;

        for (Object value : values)
            if (isNullOrEmpty(value))
                return true;

        return false;
    }


    protected boolean isNotEmpty(Object value)
    {
        return !isNullOrEmpty( value );
    }


    protected boolean isAllNotEmpty(Object... values)
    {
        return !isAnyNullOrEmpty( values );
    }


    protected <T> void addToList(T item, List<T> list, boolean allowDuplicates)
    {
        if (item == null || list == null)
            return;

        if (list.contains(item) && !allowDuplicates)
            return;

        list.add(item);
    }


    protected <T> boolean listContainsSameElements(Collection<T> collection1, Collection<T> collection2, boolean ignoreCase)
    {
        if (collection1 != null && collection2 != null)
        {
            // remove nulls
            collection1.removeIf( Objects::isNull );
            collection2.removeIf( Objects::isNull );

            if (collection1.isEmpty() && collection2.isEmpty())
                return true;

            List<T> list1;
            List<T> list2;

            if (ignoreCase)
            {
                // to lower case
                list1 = collection1
                        .stream()
                        .map(item -> (T) ((item instanceof String) ? ((String)item).toLowerCase() : item))
                        .collect(Collectors.toList());

                // to lower case
                list2 = collection1
                        .stream()
                        .map(item -> (T) ((item instanceof String) ? ((String)item).toLowerCase() : item))
                        .collect(Collectors.toList());

                // not duplicates and sorted
                list1 = (new HashSet<>(list1)).stream().sorted().collect(Collectors.toList());
                list2 = (new HashSet<>(list2)).stream().sorted().collect(Collectors.toList());
            }
            else
            {
                // not duplicates and sorted
                list1 = (new HashSet<>(collection1)).stream().sorted().collect(Collectors.toList());
                list2 = (new HashSet<>(collection2)).stream().sorted().collect(Collectors.toList());
            }

            return list1.equals(list2);
        }
        else
        {
            return (collection1 == null && collection2 == null);
        }
    }


    protected <T> int getSize(Collection <T> collection)
    {
        return (collection != null) ? collection.size() : 0;
    }


    protected <T> T getFirst(List<T> list)
    {
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }


    protected <T> T getLast(List<T> list)
    {
        return (list != null && !list.isEmpty()) ? list.get(list.size()-1) : null;
    }


    protected <K,V> void removeNulls(Map<K,V> map)
    {
        if (map != null)
            map.values().removeIf( Objects::isNull );
    }


    protected <T> void removeNulls(Collection<T> collection)
    {
        if (collection != null)
            collection.removeIf( Objects::isNull );
    }


    protected void removeNullsAndBlanks(Collection<String> collection)
    {
        if (collection != null)
            collection.removeIf( (str) -> (str == null || str.trim().isEmpty()) );
    }


    protected <T> List<T> removeDuplicates(Collection<T> collection)
    {
        if (isNullOrEmpty(collection))
            return new ArrayList<>();

        collection.removeIf( Objects::isNull );

        return new ArrayList<>( new LinkedHashSet<>(collection) );
    }


    protected <T> Map<String, T> toMap(Collection<T> entities, Function<T, String> keyFunction)
    {
        Map<String, T> result = new LinkedHashMap<>();

        if (entities != null)
        {
            for (T entity : entities)
            {
                if (entity == null)
                    continue;

                String key = keyFunction.apply( entity );

                if (key == null)
                    continue;

                result.put(key, entity);
            }
        }

        return result;
    }


    protected <T,R> List<R> toList(Collection<T> entities, Function<T,R> valueFunction, boolean allowDuplicates)
    {
        List<R> result = new ArrayList<>();

        if (entities != null)
        {
            for (T entity : entities)
            {
                if (entity == null)
                    continue;

                R value = valueFunction.apply( entity );

                if (value == null)
                    continue;

                if (result.contains(value) && !allowDuplicates)
                    continue;

                result.add( value );
            }
        }

        return result;
    }


    protected List<String> multiSplit(String str, String separator)
    {
        List<String> list = new ArrayList<>();

        if (str != null && !str.trim().isEmpty())
        {
            try
            {
                int pos;
                int index = 0;

                String value;

                while ( (pos=str.indexOf(separator,index)) >= 0 )
                {
                    value = str.substring(index,pos);
                    value = value.trim();

                    if (!value.isEmpty())
                        list.add( value );

                    index = pos + separator.length();
                }

                // remainder after the last separator
                value = str.substring(index);
                value = value.trim();

                if (!value.isEmpty())
                    list.add( value );
            }
            catch (Exception ignored) { /**/ }

            // separator not found
            if (list.isEmpty())
                list.add( str );
        }

        return list;
    }


    protected String getFirstSplit(String str, String separator)
    {
        return safeEval(() -> getFirst(multiSplit(str, separator)), "");
    }


    protected String getLastSplit(String str, String separator)
    {
        return safeEval(() -> getLast(multiSplit(str, separator)), "");
    }


    protected String replacePattern(String str, String begin, String end, Map<Object,Object> map)
    {
        if (str != null && !str.trim().isEmpty())
        {
            Map<String, String> auxMap = new HashMap<>();

            String[] separators = new String[]{begin,end};

            while (str.contains(begin) && str.contains(end))
            {
                int pos;
                int index = 0;

                String value;

                int sep = 0;
                while ( (pos=str.indexOf(separators[sep],index)) >= 0 )
                {
                    if (separators[sep].equals(end))
                    {
                        value = str.substring(index,pos);

                        String prop = (String) map.get( value.trim() );

                        if (prop == null)
                            throw new RuntimeException("property '" + (begin + value + end) + "' not found");

                        auxMap.put((begin + value + end), prop.trim());
                    }

                    // next
                    index = pos + separators[sep].length();
                    sep   = (sep+1) % separators.length;
                }

                for (Map.Entry<String,String> entry : auxMap.entrySet())
                    str = str.replace(entry.getKey(), entry.getValue());

                auxMap.clear();
            }
        }

        return str;
    }

}

