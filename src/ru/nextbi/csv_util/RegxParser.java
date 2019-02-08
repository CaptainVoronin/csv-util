package ru.nextbi.csv_util;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ,(?=(?:[^\"]*\"[^\"]*\")*[^\"']*$)
 */
public class RegxParser extends AbstractParser {
    // ,(?=(?:[^\{]*\{[^\}]*\})*[^\}]*$)
    //static final String stdPattern = "%s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    String regex;
    Pattern pattern;
    String replaceRegex;

    InputStream stream;
    boolean useSplit;
    boolean EOF;
    boolean hasHeaders;
    boolean headersParsed;

    public RegxParser( Config config ) {
        super( config );
        useSplit = config.getParseMethod().equals( Method.SPLIT );
        regex = config.getRegex();
        hasHeaders = config.hasHeaders();
        headersParsed = false;
        replaceRegex = config.getDefaultReplaceRegex();
    }

    @Override
    public void setProperty(String key, String value) {
        if (key.equals("regex"))
            regex = value;
        else if (key.equals("method"))
            useSplit = value.equals("split");
    }

    @Override
    public void startParse(InputStream ins) {
        EOF = false;
        if (!useSplit) {
            pattern = Pattern.compile(regex);
        }

        stream = ins;
    }

    @Override
    public String[] parseNext() throws IOException {
        String[] arr = null;
        String line;
        while( true ) {
            line = readLine();
            if( line == null )
                return null;

            line = line.trim();
            if( line.length() != 0 )
                break;
        }

        if( hasHeaders && !headersParsed )
        {
            headersParsed = true;
            return parseHeaders(line);
        }

        if (useSplit) {
            arr = line.split(regex);
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
                Pattern pattern = getPatternForColumn( i );
                Matcher m = pattern.matcher( arr[i] );
                if( m.find() )
                    arr[i] = m.group( 1 ).trim();
                arr[i] = arr[i].trim();
            }
        } else {
            //throw new NotImplementedException( );

            Matcher m = pattern.matcher(line);
            List<String> list = new ArrayList<>();
            while( m.find() )
                for( int i = 1; i <= m.groupCount(); i++ )
                    list.add( m.group( i ) );
            arr = new String[ list.size() ];
            list.toArray( arr );
        }
        return arr;
    }

    private String[] parseHeaders(String line)
    {
        String regexp = String.format( config.getDefaultSplitRegexp(), config.getHeaderSplitChar() );

        String[] arr = line.split( regexp );
        for( int i = 0; i < arr.length; i++  )
            arr[i] = arr[i].trim();
        return arr;
    }

    String readLine() throws IOException {
        int b;

        if (EOF)
            return null;

        StringBuffer sb = new StringBuffer();
        while ((b = stream.read()) != config.getLinebreak()) {
            if (b == -1) {
                EOF = true;
                break;
            }
            sb.append((char) b);
        }
        return sb.toString().trim();
    }

    @Override
    public void close() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
