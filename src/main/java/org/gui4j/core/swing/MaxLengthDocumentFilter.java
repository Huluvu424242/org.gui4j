/*
 * Created on 26.02.2004
 */
package org.gui4j.core.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author MarcusT
 */
public class MaxLengthDocumentFilter extends DocumentFilter {
	private final int max;

	public MaxLengthDocumentFilter(int max)
	{
		super();
		this.max = max;
	}

	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
		throws BadLocationException
	{
		int overflow = fb.getDocument().getLength() + string.length() - max;
		if (overflow <= 0)
		{
			super.insertString(fb, offset, string, attr);
		}
		else
		{
			super.insertString(fb, offset, string.substring(0, string.length() - overflow), attr);
		}
	}

	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
		throws BadLocationException
	{
        	        	
		int textlength = text == null ? 0 : text.length();
		int overflow = fb.getDocument().getLength() - length + textlength - max;
		if (overflow <= 0)
		{
			super.replace(fb, offset, length, text, attrs);
		}
		else
		{
			super.replace(fb, offset, length, text.substring(0, textlength - overflow), attrs);
		}

	}

}
