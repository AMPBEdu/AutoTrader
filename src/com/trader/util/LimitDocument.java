package com.trader.util;

import javax.swing.text.*;
public class LimitDocument extends PlainDocument
{
    private int limit;
    public LimitDocument(int limit)
    {
        super();
        setLimit(limit);  // store the limit
    }
    public final int getLimit()
    {
        return limit;
    }
    public void insertString(int offset, String s, AttributeSet attributeSet)
            throws BadLocationException
    {
        if(offset < limit) // if we haven't reached the limit, insert the str
        {
            super.insertString(offset,s,attributeSet);
        } // otherwise, just lose the string
    }
    public final void setLimit(int newValue)
    {
        this.limit = newValue;
    }
}