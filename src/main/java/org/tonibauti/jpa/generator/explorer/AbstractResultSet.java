package org.tonibauti.jpa.generator.explorer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;


public interface AbstractResultSet
{

    default Boolean getBoolean(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getBoolean(columnName); } catch (Exception e) { return null; }
    }

    default Boolean getBoolean(ResultSet resultSet, int position)
    {
        try { return resultSet.getBoolean(position); } catch (Exception e) { return null; }
    }


    default Byte getByte(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getByte(columnName); } catch (Exception e) { return null; }
    }

    default Byte getByte(ResultSet resultSet, int position)
    {
        try { return resultSet.getByte(position); } catch (Exception e) { return null; }
    }


    default Short getShort(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getShort(columnName); } catch (Exception e) { return null; }
    }

    default Short getShort(ResultSet resultSet, int position)
    {
        try { return resultSet.getShort(position); } catch (Exception e) { return null; }
    }


    default Integer getInt(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getInt(columnName); } catch (Exception e) { return null; }
    }

    default Integer getInt(ResultSet resultSet, int position)
    {
        try { return resultSet.getInt(position); } catch (Exception e) { return null; }
    }


    default Long getLong(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getLong(columnName); } catch (Exception e) { return null; }
    }

    default Long getLong(ResultSet resultSet, int position)
    {
        try { return resultSet.getLong(position); } catch (Exception e) { return null; }
    }


    default Float getFloat(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getFloat(columnName); } catch (Exception e) { return null; }
    }

    default Float getFloat(ResultSet resultSet, int position)
    {
        try { return resultSet.getFloat(position); } catch (Exception e) { return null; }
    }


    default Double getDouble(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getDouble(columnName); } catch (Exception e) { return null; }
    }

    default Double getDouble(ResultSet resultSet, int position)
    {
        try { return resultSet.getDouble(position); } catch (Exception e) { return null; }
    }


    default BigDecimal getBigDecimal(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getBigDecimal(columnName); } catch (Exception e) { return null; }
    }

    default BigDecimal getBigDecimal(ResultSet resultSet, int position)
    {
        try { return resultSet.getBigDecimal(position); } catch (Exception e) { return null; }
    }


    default String getString(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getString(columnName); } catch (Exception e) { return null; }
    }

    default String getString(ResultSet resultSet, int position)
    {
        try { return resultSet.getString(position); } catch (Exception e) { return null; }
    }


    default String getNString(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getNString(columnName); } catch (Exception e) { return null; }
    }

    default String getNString(ResultSet resultSet, int position)
    {
        try { return resultSet.getNString(position); } catch (Exception e) { return null; }
    }


    default Date getDate(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getDate(columnName); } catch (Exception e) { return null; }
    }

    default Date getDate(ResultSet resultSet, int position)
    {
        try { return resultSet.getDate(position); } catch (Exception e) { return null; }
    }


    default Time getTime(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getTime(columnName); } catch (Exception e) { return null; }
    }

    default Time getTime(ResultSet resultSet, int position)
    {
        try { return resultSet.getTime(position); } catch (Exception e) { return null; }
    }


    default Timestamp getTimestamp(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getTimestamp(columnName); } catch (Exception e) { return null; }
    }

    default Timestamp getTimestamp(ResultSet resultSet, int position)
    {
        try { return resultSet.getTimestamp(position); } catch (Exception e) { return null; }
    }


    default Object getObject(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getObject(columnName); } catch (Exception e) { return null; }
    }

    default Object getObject(ResultSet resultSet, int position)
    {
        try { return resultSet.getObject(position); } catch (Exception e) { return null; }
    }


    default byte[] getBytes(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getBytes(columnName); } catch (Exception e) { return null; }
    }

    default byte[] getBytes(ResultSet resultSet, int position)
    {
        try { return resultSet.getBytes(position); } catch (Exception e) { return null; }
    }


    default Blob getBlob(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getBlob(columnName); } catch (Exception e) { return null; }
    }

    default Blob getBlob(ResultSet resultSet, int position)
    {
        try { return resultSet.getBlob(position); } catch (Exception e) { return null; }
    }


    default Clob getClob(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getClob(columnName); } catch (Exception e) { return null; }
    }

    default Clob getClob(ResultSet resultSet, int position)
    {
        try { return resultSet.getClob(position); } catch (Exception e) { return null; }
    }


    default NClob getNClob(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getNClob(columnName); } catch (Exception e) { return null; }
    }

    default NClob getNClob(ResultSet resultSet, int position)
    {
        try { return resultSet.getNClob(position); } catch (Exception e) { return null; }
    }


    default SQLXML getSqlXml(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getSQLXML(columnName); } catch (Exception e) { return null; }
    }

    default SQLXML getSqlXml(ResultSet resultSet, int position)
    {
        try { return resultSet.getSQLXML(position); } catch (Exception e) { return null; }
    }


    default RowId getRowId(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getRowId(columnName); } catch (Exception e) { return null; }
    }

    default RowId getRowId(ResultSet resultSet, int position)
    {
        try { return resultSet.getRowId(position); } catch (Exception e) { return null; }
    }


    default Ref getRef(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getRef(columnName); } catch (Exception e) { return null; }
    }

    default Ref getRef(ResultSet resultSet, int position)
    {
        try { return resultSet.getRef(position); } catch (Exception e) { return null; }
    }


    default Array getArray(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getArray(columnName); } catch (Exception e) { return null; }
    }

    default Array getArray(ResultSet resultSet, int position)
    {
        try { return resultSet.getArray(position); } catch (Exception e) { return null; }
    }


    default InputStream getAsciiStream(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getAsciiStream(columnName); } catch (Exception e) { return null; }
    }

    default InputStream getAsciiStream(ResultSet resultSet, int position)
    {
        try { return resultSet.getAsciiStream(position); } catch (Exception e) { return null; }
    }


    default InputStream getBinaryStream(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getBinaryStream(columnName); } catch (Exception e) { return null; }
    }

    default InputStream getBinaryStream(ResultSet resultSet, int position)
    {
        try { return resultSet.getBinaryStream(position); } catch (Exception e) { return null; }
    }


    default Reader getCharacterStream(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getCharacterStream(columnName); } catch (Exception e) { return null; }
    }

    default Reader getCharacterStream(ResultSet resultSet, int position)
    {
        try { return resultSet.getCharacterStream(position); } catch (Exception e) { return null; }
    }


    default Reader getNCharacterStream(ResultSet resultSet, String columnName)
    {
        try { return resultSet.getNCharacterStream(columnName); } catch (Exception e) { return null; }
    }

    default Reader getNCharacterStream(ResultSet resultSet, int position)
    {
        try { return resultSet.getNCharacterStream(position); } catch (Exception e) { return null; }
    }

}

