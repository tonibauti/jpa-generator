package org.tonibauti.jpa.generator.utils;

import java.util.Objects;


public class Triple<L,M,R>
{
    public static final Triple<Object, Object, Object> EMPTY = Triple.of(null, null, null);

    private L left;
    private M middle;
    private R right;


    public Triple() {}


    public static <L,M,R> Triple<L,M,R> of(L left, M middle, R right)
    {
        Triple<L,M,R> triple = new Triple<>();
        triple.setLeft( left );
        triple.setMiddle( middle );
        triple.setRight( right );
        return triple;
    }


    public Triple(L left, M middle, R right)
    {
        setLeft( left );
        setMiddle( middle );
        setRight( right );
    }


    public L getLeft()
    {
        return left;
    }


    public Triple<L,M,R> setLeft(L left)
    {
        this.left = left;
        return this;
    }


    public M getMiddle()
    {
        return middle;
    }


    public Triple<L,M,R> setMiddle(M middle)
    {
        this.middle = middle;
        return this;
    }


    public R getRight()
    {
        return right;
    }


    public Triple<L,M,R> setRight(R right)
    {
        this.right = right;
        return this;
    }


    public boolean isEmpty()
    {
        return (right == null && middle == null && left == null );
    }


    public Triple<L,M,R> populate(Triple<L,M,R> triple)
    {
        if (triple != null)
        {
            setLeft( triple.getLeft() );
            setMiddle( triple.getMiddle() );
            setRight( triple.getRight() );
        }
        return this;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else
        if (!(obj instanceof Triple))
        {
            return false;
        }
        else
        {
            Triple<?,?,?> other = (Triple<?,?,?>)obj;

            return (Objects.equals(this.left, other.getLeft())
                    &&
                    Objects.equals(this.middle, other.getMiddle())
                    &&
                    Objects.equals(this.right, other.getRight()));
        }
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(this.left, this.middle, this.right);
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{");
        builder.append("left=").append(this.left);
        builder.append(",middle=").append(this.middle);
        builder.append(",right=").append(this.right);
        builder.append("}");
        return builder.toString();
    }

}
