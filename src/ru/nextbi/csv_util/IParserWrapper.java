package ru.nextbi.csv_util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public interface IParserWrapper {
    enum ParserType {CSV, REGX}
    enum Method {REGEX, SPLIT}
    void startParse(InputStream ins ) throws UnsupportedEncodingException;
    String[] parseNext() throws IOException;
    void setProperty(String key, String value );
    Pattern getPatternForColumn(int index );
    void close();
}
