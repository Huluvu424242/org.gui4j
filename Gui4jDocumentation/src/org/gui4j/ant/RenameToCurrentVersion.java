package org.gui4j.ant;

import java.io.File;

import org.gui4j.constants.Const;

public class RenameToCurrentVersion
{

    public static void main(String[] args)
    {
        File toRename = new File(args[0]);
        if (toRename.isFile())
        {
            File dest = new File(toRename.getParent(),"gui4j-"+Const.GUI4J_VERSION+".zip");
            toRename.renameTo(dest);
        }
        else
        {
            File dest = new File(toRename.getParent(),"gui4j-"+Const.GUI4J_VERSION);
            toRename.renameTo(dest);
        }
    }
}
