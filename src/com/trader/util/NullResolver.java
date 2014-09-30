package com.trader.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

class NullResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
            IOException {
        return new InputSource(new StringReader(""));
    }
}