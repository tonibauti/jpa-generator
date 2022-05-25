package org.tonibauti.jpa.generator.utils;

import java.util.LinkedHashMap;
import java.util.Map;


public class DataMap
{
    private final Map<String,Object> map = new LinkedHashMap<>();


    public DataMap() {}


    @SuppressWarnings("unchecked")
    public <T> T get(String key)
    {
        return (T) map.get( key );
    }


    public void put(String key, Object value)
    {
        map.put(key, value);
    }


    public boolean isEmpty()
    {
        return map.isEmpty();
    }


    public void clear()
    {
        map.clear();
    }

}

