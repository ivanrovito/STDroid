package jp.gr.kmtn.stdroid.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//-----------------------------
/**
 * this calss is used for parse the xml file from the server;
 */
//------------------------------
public class CrowdroidUpdateHandler extends DefaultHandler
{

    String                   message      = null;

    String                   str          = null;

    int                      currentstate = 0;

    boolean                  updated      = false;

    private static final int ZERO         = 0;

    private static final int NECESITY     = 1;

    private static final int APKURL       = 2;

    public CrowdroidUpdateHandler()
    {
        super();
    }

    @Override
    public void startDocument() throws SAXException
    {}

    @Override
    public void endDocument() throws SAXException
    {}

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException
    {
        if (localName.equals("update"))
        {
            this.currentstate = ZERO;
            return;
        }

        if (localName.equals("necesity"))
        {
            this.currentstate = NECESITY;
            return;
        }

        if (localName.equals("apk-url"))
        {
            this.currentstate = APKURL;
            return;
        }

        this.currentstate = ZERO;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException
    {
        if (localName.equals("update"))
        {
            if (this.updated == false)
            {
                this.message = "no";
            }
            else
            {
                this.message = this.str;
            }
            return;
        }
    }

    @Override
    public void characters(char ch[], int start, int length)
    {
        String theString = new String(ch, start, length);

        switch (this.currentstate)
        {
        case NECESITY:
            if (theString.equals("YES"))
            {
                this.updated = true;
            }
            else
            {
                this.updated = false;
            }
            this.currentstate = 0;
            break;

        case APKURL:
            if (this.updated)
            {
                this.str = theString;
            }
            this.currentstate = 0;
            break;

        default:
            break;
        }
    }

    public String getCrowdroid()
    {

        return this.message;
    }

}
