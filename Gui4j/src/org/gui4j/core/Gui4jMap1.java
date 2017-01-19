package org.gui4j.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class <code>Gui4jAccess</code> works with parameter maps. Since usually only one
 * parameter is used, this class provides a very simple implementation of the
 * <code>Map</code> interface for exaclty one entry. Note that this class is only to
 * improve performance.
 * @author Joachim Schmid
 */
public final class Gui4jMap1 implements Map, Map.Entry, Serializable
{
	private final Object mKey;
	private Object mValue;
	
	public Gui4jMap1(Object key, Object value)
	{
		mKey = key;
		mValue = value;
	}
	
	public Object getKey()
	{
		return mKey;
	}
	
	public Object getValue()
	{
		return mValue;
	}

	public Object setValue(Object value)
	{
		mValue = value;
		return value;
	}
	
	/**
	 * @see java.util.Map#size()
	 */
	public int size()
	{
		return 1;
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return false;
	}

	/**
	 * @see java.util.Map#containsKey(Object)
	 */
	public boolean containsKey(Object key)
	{
		if (mKey==null)
		{
			return key==null;
		}
		else
		{
			if (key==null)
			{
				return false;
			}
			return mKey.equals(key);
		}
	}

	/**
	 * @see java.util.Map#containsValue(Object)
	 */
	public boolean containsValue(Object value)
	{
		if (mValue==null)
		{
			return value==null;
		}
		else
		{
			if (value==null)
			{
				return false;
			}
			return mValue.equals(value);
		}
	}

	/**
	 * @see java.util.Map#get(Object)
	 */
	public Object get(Object key)
	{
		if (mKey==key)
		{
			return mValue;
		}
		if (mKey==null || key==null)
		{
			return null;
		}
		if (mKey.equals(key))
		{
			return mValue;
		}
		return null;
	}

	/**
	 * @see java.util.Map#put(Object, Object)
	 */
	public Object put(Object key, Object value)
	{
		throw new UnsupportedOperationException("unexpected put operation");
	}

	/**
	 * @see java.util.Map#remove(Object)
	 */
	public Object remove(Object arg0)
	{
		throw new UnsupportedOperationException("unexpected remove operation");
	}

	/**
	 * @see java.util.Map#putAll(Map)
	 */
	public void putAll(Map arg0)
	{
		throw new UnsupportedOperationException("unexpected putAll operation");
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clear()
	{
		throw new UnsupportedOperationException("unexpected clear operation");
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet()
	{
		HashSet set = new HashSet();
		set.add(mKey);
		return set;
	}

	/**
	 * @see java.util.Map#values()
	 */
	public Collection values()
	{
		ArrayList list = new ArrayList();
		list.add(mValue);
		return list;
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet()
	{
		Set set = new HashSet();
		set.add(this);
		return set;
	}

}
