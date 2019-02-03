package ru.nextbi.csv_util;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public interface IParserWrapper {
    enum ParserType {CSV, REGX}
    enum Method {REGEX, SPLIT}
    void startParse(InputStream ins );
    String[] parseNext() throws IOException;
    void setProperty(String key, String value );
    void setDelimiter( char val );
    void setEscape( char escape );
    void setNewLine( char newline );
    void setColumnsToSplit( Integer[] indexes );
    void setSplitChar(char val );
    void setStringBound( char symbol );
    Pattern getPatternForColumn(int index );
    void close();
}
