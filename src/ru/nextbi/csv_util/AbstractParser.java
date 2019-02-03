package ru.nextbi.csv_util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractParser implements IParserWrapper {

    Map<Integer, Pattern> patterns;
    Config config;

    public AbstractParser( Config config )
    {
        this.config = config;
        patterns = new HashMap<>();
    }

    @Override
    public void setProperty(String key, String value) {

    }

    @Override
    public void close() {

    }

    public Pattern getPatternForColumn(int index )
    {
        if( !patterns.containsKey( index ) )
        {
            String regexp = config.getRegexpForColumn( index );
            if( regexp == null )
                regexp = config.getDefaultColumnRegexp();

            Pattern pattern = Pattern.compile( regexp );
            patterns.put( index, pattern );
            return pattern;
        }
        else
            return patterns.get( index );
    }
}