package ru.nextbi.csv_util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractParser implements IParserWrapper {

    char delimiter;
    char escape;
    char newLine;
    Map<Integer, Pattern> patterns;
    Config config;

    public AbstractParser( Config config )
    {
        this.config = config;
        patterns = new HashMap<>();
    }

    public char getStringBound() {
        return stringBound;
    }

    char stringBound;

    public char getSplitChar() {
        return splitChar;
    }

    char splitChar;

    protected Integer[] indexes;

    public char getDelimiter() {
        return delimiter;
    }

    public char getEscape() {
        return escape;
    }

    public char getNewLine() {
        return newLine;
    }

    @Override
    public void setProperty(String key, String value) {

    }

    @Override
    public void setDelimiter(char val) {
        delimiter = val;
    }

    @Override
    public void setEscape(char escape) {
        this.escape = escape;
    }

    @Override
    public void setNewLine(char newline) {
        this.newLine = newline;
    }

    @Override
    public void setColumnsToSplit(Integer[] indexes) {
        this.indexes = indexes.clone();
    }

    @Override
    public void setSplitChar(char val) {
        this.splitChar = splitChar;
    }

    @Override
    public void close() {

    }

    @Override
    public void setStringBound( char symbol )
    {
        stringBound = symbol;
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