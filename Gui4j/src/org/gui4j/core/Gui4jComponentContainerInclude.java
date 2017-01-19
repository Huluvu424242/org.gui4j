package org.gui4j.core;

import java.io.Serializable;
import java.util.Map;

import org.gui4j.core.util.Extract;


public final class Gui4jComponentContainerInclude implements Serializable
{
    private final Gui4jComponentContainer mGui4jComponentContainer;
    private final String mAliasName;
    private final Gui4jComponentContainer mGui4jComponentContainerParent;
    private Gui4jCall mGui4jBaseController;
    private Gui4jCall[] mRefresh;
    private Map mParamMap;  // ParamId -> Id

    public Gui4jComponentContainerInclude(
        Gui4jComponentContainer gui4jComponentContainerParent,
        Gui4jComponentContainer gui4jComponentContainer,
        Map paramMap,
        String name)
    {
        this.mGui4jComponentContainerParent = gui4jComponentContainerParent;
        this.mGui4jComponentContainer = gui4jComponentContainer;
        this.mParamMap = paramMap;
        this.mAliasName = name;
    }
    
    public Map getParamMap()
    {
        return mParamMap;
    }

    public void setGui4jBaseController(Gui4jCall gui4jCall)
    {
        mGui4jBaseController = gui4jCall;
    }

    public Gui4jCall getGui4jBaseController()
    {
        return mGui4jBaseController;
    }

    public Gui4jComponentContainer getGui4jComponentContainer()
    {
        return mGui4jComponentContainer;
    }

    public Gui4jComponentContainer getGui4jComponentContainerParent()
    {
        return mGui4jComponentContainerParent;
    }
    
    public String getAliasName()
    {
        return  mAliasName;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Gui4jComponentContainerInclude)
        {
            Gui4jComponentContainerInclude gui4jComponentContainerInclude = (Gui4jComponentContainerInclude) obj;
            return mAliasName.equals(gui4jComponentContainerInclude.getAliasName());
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
        return mAliasName.hashCode();
    }

    /**
     * Returns the refresh.
     * @return Gui4jCall[]
     */
    public Gui4jCall[] getRefresh()
    {
        return mRefresh;
    }

    /**
     * Sets the refresh.
     * @param refresh The refresh to set
     */
    public void setRefresh(Gui4jCall[] refresh)
    {
        this.mRefresh = refresh;
    }
    
    

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return Extract.getClassname(getClass())+"["+mAliasName +"="+mGui4jComponentContainer+"]";
    }

}
