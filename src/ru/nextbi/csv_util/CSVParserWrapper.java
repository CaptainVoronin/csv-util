package ru.nextbi.csv_util;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.InputStream;

public class CSVParserWrapper extends AbstractParser{
    CsvParser parser;
    CsvParserSettings settings;

    public CSVParserWrapper(Config config){
        super(config);
        settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(',');
        settings.getFormat().setQuoteEscape('\\');
        settings.getFormat().setLineSeparator("\n");
        settings.setMaxCharsPerColumn(20000);

    }

    @Override
    public void startParse(InputStream ins){
        parser = new CsvParser(settings);
        parser.beginParsing(ins);
    }

    @Override
    public String[] parseNext(){
        return parser.parseNext();
    }

}
