/*
 * June 2006: This is the original file except of this paragraph and 
 * for consistency, the original package name info.clearthought
 * was renamed.  
 */
package org.gui4j.core.swing;
/*
 * ====================================================================
 *
 * The Clearthought Software License, Version 1.0
 *
 * Copyright (c) 2001 Daniel Barbalace.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The original software may not be altered.  However, the classes
 *    provided may be subclasses as long as the subclasses are not
 *    packaged in the info.clearthought package or any subpackage of
 *    info.clearthought.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR, AFFILATED BUSINESSES,
 * OR ANYONE ELSE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

/**
 * TableLayoutConstants define the constants used by all the TableLayout
 * classes.
 *
 * @version 1.1 4/4/02
 * @author  Daniel E. Barbalace
 */

public interface TableLayoutConstants
{

    /** Indicates that the component is left justified in its cell */
    public static final int LEFT = 0;

    /** Indicates that the component is top justified in its cell */
    public static final int TOP = 0;

    /** Indicates that the component is centered in its cell */
    public static final int CENTER = 1;

    /** Indicates that the component is full justified in its cell */
    public static final int FULL = 2;

    /** Indicates that the component is bottom justified in its cell */
    public static final int BOTTOM = 3;

    /** Indicates that the component is right justified in its cell */
    public static final int RIGHT = 3;

    /** Indicates that the row/column should fill the available space */
    public static final double FILL = -1.0;

    /** Indicates that the row/column should be allocated just enough space to
        accomidate the preferred size of all components contained completely within
        this row/column. */
    public static final double PREFERRED = -2.0;

    /** Indicates that the row/column should be allocated just enough space to
        accomidate the minimum size of all components contained completely within
        this row/column. */
    public static final double MINIMUM = -3.0;

    /** Minimum value for an alignment */
    public static final int MIN_ALIGN = 0;

    /** Maximum value for an alignment */
    public static final int MAX_ALIGN = 3;

}
