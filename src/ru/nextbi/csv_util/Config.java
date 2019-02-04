package ru.nextbi.csv_util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    // default ,
    public static final String DELIMITER = "delimiter";
    // default \
    public static final String ESCAPE = "escape";
    // default \r
    public static final String LINEBREAK = "linebreak";
    // default csv
    public static final String PARSER = "parser";
    // default false
    public static final String HEADERS = "headers";
    // default false
    public static final String BOM = "bom";
    // no default value
    public static final String SPLITCOLUMS = "splitcolums";
    // default ,
    public static final String SPLITCHAR = "splitchar";
    // default split
    public static final String REGEXUSAGE = "regexusage";
    // default ",s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
    public static final String REGEX = "regex";
    // default regex for extracting strings between quates
    public static final String VALUEEXTRACTOR = "valueextractor";

    public static final String defaultSplitRegexp = "%s(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

    public static final String HEADERSPLITCHAR = "headersplitchar";

    File file;
    Properties props;

    public Config( File file )
    {
        this.file = file;
        props = new Properties();
    }

    public Config(  )
    {
        props = new Properties();
    }

    public void setProperty(String key, String value )
    {
        props.setProperty( key, value );
    }

    public void getProperty( String key )
    {
        props.getProperty( key );
    }

    public void read() throws IOException {

        try( FileInputStream fis = new FileInputStream( file ) ) {
            props.load( fis );
        }
    }

    public char getDelimiter()
    {
        return props.getProperty( DELIMITER, "," ).charAt( 0 );
    }

    public char getEscape()
    {
        return props.getProperty( ESCAPE, "\\" ).charAt( 0 );
    }

    public char getLinebreak()
    {
        return props.getProperty( LINEBREAK, "\r" ).charAt( 0 );
    }

    public IParserWrapper.ParserType getParserType()
    {
        if( props.getProperty( PARSER, "csv" ).equals( "csv" ) )
            return IParserWrapper.ParserType.CSV;
        else
            return IParserWrapper.ParserType.REGX;
    }

    public IParserWrapper.Method getParseMethod()
    {
        if( props.getProperty( REGEXUSAGE, "split" ).equals( "split" ) )
            return IParserWrapper.Method.SPLIT;
        else
            return IParserWrapper.Method.REGEX;
    }

    public boolean hasHeaders()
    {
        return new Boolean( props.getProperty( HEADERS, "false" ) );
    }

    public String getRegex()
    {
        return props.getProperty( REGEX, defaultSplitRegexp );
    }

    public String getSplitChar()
    {
        return props.getProperty( SPLITCHAR, "," );
    }

    public Integer[] getSplitColumns()
    {
        String buff = props.getProperty( SPLITCOLUMS );
        Integer[] cols = null;
        if( buff != null ) {
            String[] items = buff.split( getSplitChar());
            cols = new Integer[items.length];
            for (int i = 0; i < items.length; i++)
                cols[i] = Integer.parseInt(items[i]);
        }
        return cols;
    }

    String getRegexpForColumn( int index )
    {
        String key = "col" + index;
        return props.getProperty( key );
    }

    public String getDefaultColumnRegexp()
    {
        return props.getProperty( VALUEEXTRACTOR, "[\\\"]*\\\"(.*)\\\"" );
    }

    public String getDefaultSplitRegexp()
    {
        return defaultSplitRegexp;
    }

    public String getHeaderSplitChar()
    {
        return props.getProperty( HEADERSPLITCHAR, "," );
    }
}
