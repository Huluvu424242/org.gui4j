package org.gui4j.core;

import java.io.Serializable;

import org.gui4j.core.util.Extract;


public class Gui4jQualifiedComponent implements Serializable
{
    private final Gui4jComponentPath mPath;
    private final Gui4jComponent mGui4jComponent;
    
    public Gui4jQualifiedComponent(Gui4jComponentPath path, Gui4jQualifiedComponent gui4jComponent)
    {
        assert path != null && gui4jComponent != null;
        this.mPath = path.extend(gui4jComponent.getGui4jComponentPath());
        this.mGui4jComponent = gui4jComponent.getGui4jComponent();
    }

    public Gui4jQualifiedComponent(Gui4jComponentContainerInclude include, Gui4jComponent gui4jComponent)
    {
        this(new Gui4jComponentPath(include),gui4jComponent);
    }

    public Gui4jQualifiedComponent(Gui4jComponentContainerInclude include, Gui4jQualifiedComponent path)
    {
        this(new Gui4jComponentPath(include),path);
    }


    public Gui4jQualifiedComponent(Gui4jComponentPath gui4jComponentPath, Gui4jComponent gui4jComponent)
    {
        assert gui4jComponentPath != null && gui4jComponent != null;
        this.mPath = gui4jComponentPath;
        this.mGui4jComponent = gui4jComponent;
    }
    
    public boolean hasId()
    {
        return mGui4jComponent.getId() != null;
    }

    public String getQualifiedId()
    {
        return mPath.getId(mGui4jComponent.getId());
    }
    
    public Gui4jComponentPath getGui4jComponentPath()
    {
        return mPath;
    }

    public Gui4jComponent getGui4jComponent()
    {
        return mGui4jComponent;
    }

    public String toString()
    {
        return Extract.getClassname(getClass())+"["+getGui4jComponentPath()+","+mGui4jComponent+"]";
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Gui4jQualifiedComponent)
        {
            Gui4jQualifiedComponent path = (Gui4jQualifiedComponent)obj;
            return mGui4jComponent.equals(path.mGui4jComponent) && mPath.equals(path.mPath);
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
        return mPath.hashCode()+mGui4jComponent.hashCode();
    }

}
