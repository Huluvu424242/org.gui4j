package org.gui4j.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Gui4jComponentPath implements Serializable
{
    private final Gui4jComponentPath mSon;
    private final Gui4jComponentContainerInclude mInclude;

    public static Gui4jComponentPath getInstance(List includes)
    {
        Gui4jComponentPath p = new Gui4jComponentPath();
        for (int i = includes.size() - 1; i >= 0; i--)
        {
            p = new Gui4jComponentPath((Gui4jComponentContainerInclude) includes.get(i), p);
        }
        return p;
    }

    public Gui4jComponentPath()
    {
        mSon = null;
        mInclude = null;
    }

    public Gui4jComponentPath(Gui4jComponentContainerInclude include)
    {
        assert include != null;
        mSon = null;
        mInclude = include;
    }
    
    public Gui4jComponentPath getSon()
    {
        return mSon;
    }
    
    public Gui4jComponentContainerInclude getInclude()
    {
        return mInclude;
    }

    public Gui4jComponentPath(Gui4jComponentContainerInclude include, Gui4jComponentPath son)
    {
        assert son != null && include != null;
        mSon = son.mInclude == null ? null : son;
        mInclude = include;
    }
    

    public Gui4jComponentPath extend(Gui4jComponentPath son)
    {
        assert son != null;
        List l = new ArrayList();
        collectInclude(l);
        son.collectInclude(l);
        return getInstance(l);
    }

    public String getId(String id)
    {
        if (id == null)
        {
            return null;
        }
        if (mInclude == null)
        {
            return id;
        }
        else
        {
            if (mSon != null)
            {
                return mInclude.getAliasName() + "/" + mSon.getId(id);
            }
            else
            {
                return mInclude.getAliasName() + "/" + id;
            }
        }
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Gui4jComponentPath)
        {
            Gui4jComponentPath path = (Gui4jComponentPath) obj;
            if (mInclude == null && path.mInclude == null)
            {
                return true;
            }
            if (mInclude == null && path.mInclude != null)
            {
                return false;
            }
            if (mInclude != null && path.mInclude == null)
            {
                return false;
            }
            if (mInclude.getAliasName().equals(path.mInclude.getAliasName()))
            {
                return mSon == null ? (path.mSon == null) : mSon.equals(path.mSon);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (mInclude == null)
        {
            return 0;
        }
        return mInclude.getAliasName().hashCode() + (mSon != null ? mSon.hashCode() : 0);
    }

    public void collectInclude(List l)
    {
        if (mInclude != null)
        {
            l.add(mInclude);
            if (mSon != null)
            {
                mSon.collectInclude(l);
            }
        }
    }

}
