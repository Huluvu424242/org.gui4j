package org.gui4j.core.definition;


public class Attribute
{
    private final String mName;
    private final String mType;
    private final String mKind;
    private final boolean required;
    private final boolean writeAsCDATA;
    private final AttributeType mAttributeType;

    public Attribute(String name, AttributeType type, boolean required, boolean writeAsCDATA)
    {
        mName = name;
        mType = type.getDTDTypeDefinition();
        mKind = required ? DTDWriter.DTD_REQUIRED : DTDWriter.DTD_IMPLIED;
        this.required = required;
        this.writeAsCDATA = writeAsCDATA;
        mAttributeType = type;
    }

    public AttributeType getAttributeType()
    {
        return mAttributeType;
    }

    public String toString()
    {
        String lType = writeAsCDATA ? DTDWriter.CDATA : mType;
        return mName + " " + lType + " " + mKind;
    }

    public String getName()
    {
        return mName;
    }

    /**
     * For the attribute definition for the style-part in the DTD
     * @return String
     */
    public String getOptional()
    {
        return mName + " " + mType + " " + DTDWriter.DTD_IMPLIED;
    }

    public String getKind()
    {
        return mKind.substring(1);
    }

    public boolean isRequired()
    {
        return required;
    }
}