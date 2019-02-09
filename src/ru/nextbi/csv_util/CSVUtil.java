package ru.nextbi.csv_util;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CSVUtil{
    Config config;

    File inFile;
    File outFile;
    List<String[]> current = new ArrayList<>();
    List<String[]> subResult = new ArrayList<>();
    long rowCount;

    public void setInFile(File inFile){
        this.inFile = inFile;
    }

    public void setOutFile(File outFile){
        this.outFile = outFile;
    }

    public void setConfig(Config config){
        this.config = config;
    }

    public static void main(String[] args){
        Options ops = new Options();

        ops.addOption("csv", "csv", false, "use univelocity CSV parser");
        ops.addOption("regx", "regx-method", true, "use regexp for parsing. The argument set methos can be regx or split");
        ops.addOption("p", "pattern", true, "regex pattern");
        ops.addOption("f", "config", true, "config file");
        ops.addRequiredOption("i", "input", true, "input file");
        ops.addRequiredOption("o", "output", true, "output file");
        ops.addOption("d", "delim", true, "columns delimiter");
        ops.addOption("h", "headers", false, "input file has headers");
        ops.addOption("e", "escape", true, "escape symbol");
        ops.addOption("b", "BOM", false, "input file is UTF-8 with BOM");
        ops.addOption("c", "cols", true, "list of colums to split");
        ops.addOption("s", "split", true, "split char");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(ops, args);
        } catch( ParseException e ) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", ops);
            System.exit(1);
            return;
        }

        CSVUtil util = new CSVUtil();
        util.setInFile(new File(cmd.getOptionValue("input")));
        util.setOutFile(new File(cmd.getOptionValue("output")));
        Config cfg;
        try {
            if( cmd.hasOption("config") ) {
                cfg = new Config(new File(cmd.getOptionValue("config")));
                cfg.read();
            } else
                cfg = new Config();

            if( cmd.hasOption("headers") )
                cfg.setProperty(Config.HEADERS, "true");
            if( cmd.hasOption("delim") )
                cfg.setProperty(Config.DELIMITER, cmd.getOptionValue("delim"));
            if( cmd.hasOption("escape") )
                cfg.setProperty(Config.ESCAPE, cmd.getOptionValue("escape"));
            if( cmd.hasOption("BOM") )
                cfg.setProperty(Config.BOM, "true");
            if( cmd.hasOption("cols") )
                cfg.setProperty(Config.SPLITCOLUMS, cmd.getOptionValue("cols"));
            if( cmd.hasOption("split") )
                cfg.setProperty(Config.SPLITCHAR, cmd.getOptionValue("split"));
            if( cmd.hasOption("regx-method") ) {
                cfg.setProperty(Config.PARSER, cmd.getOptionValue("regx-method"));
                cfg.setProperty(Config.REGEXUSAGE, cmd.getOptionValue("regx-method"));
                cfg.setProperty(Config.REGEX, cmd.getOptionValue("pattern"));
            }
            util.setConfig(cfg);

        } catch( IOException e1 ) {
            e1.printStackTrace();
            return;
        }

        try {
            util.parse();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void parse() throws IOException{
        int headersCount = 0;
        IParserWrapper parser;
        if( config.getParserType().equals(IParserWrapper.ParserType.CSV) )
            parser = new CSVParserWrapper(config);
        else
            parser = new RegxParser(config);

        CsvWriterSettings wst = new CsvWriterSettings();
        CsvWriter writer = new CsvWriter(outFile, wst);

        FileInputStream fis = new FileInputStream(inFile);
        rowCount = 0;
        parser.startParse(fis);
        String[] row;
        List<String[]> rows = new ArrayList<>();
        if( config.hasHeaders() ) {
            String[] headers = parser.parseNext();

            if( config.getDefaultReplaceRegex() != null ) {
                List<String[]> list = new ArrayList<>();
                list.add( headers );
                sanitize(list);
                for( int i = 0; i < headers.length; i++ )
                   headers[i] = headers[i].trim();
            }

            headersCount = headers.length;
            writer.writeHeaders( headers );
        }

        Integer[] cols = config.getSplitColumns();
        while( (row = parser.parseNext()) != null ) {
            rowCount++;

            if( headersCount > 0 && row.length > headersCount )
            {
                System.out.println( "Too many values - "  + row.length + " must be " + headersCount );
                for( String val : row ) {
                    System.out.print( "[" );
                    System.out.print(val);
                    System.out.print( "]" );
                }
                System.out.println( "" );
            }

            if( cols != null && cols.length != 0 )
                split(cols, rows, row);
            else
                rows.add(row);

            if( config.getDefaultReplaceRegex() != null ) {
                sanitize(rows);
            }

            write(writer, rows);

            rows.clear();
        }

        parser.close();
        writer.close();
    }

    private void sanitize(List<String[]> row){
        String replaceRegex = config.getDefaultReplaceRegex();

        for( String[] fields : row ) {
            for( int i = 0; i < fields.length; i++ ) {
                if( fields[i] != null )
                    fields[i] = fields[i].replaceAll(replaceRegex, " ");
            }
        }
    }

    private void split(Integer[] cols, List<String[]> rows, String[] row){
        current.add(row);

        for( Integer index : cols ) {
            for( String[] r : current )
                subResult.addAll(splitCol(index, r));
            current.clear();
            current.addAll(subResult);
            subResult.clear();
        }

        rows.addAll(current);
        current.clear();
    }

    private Collection<? extends String[]> splitCol(Integer index, String[] row){
        List<String[]> rows = new ArrayList<>();

        if( index >= row.length ) {
            System.out.println("Can't split row. " + rowCount + "Colums index " + index + ". Row " + row.toString());
        } else {
            String rawValue = row[index];
            if( rawValue == null )
                rawValue = "";
            String[] values = rawValue.split(config.getSplitChar());

            for( String value : values ) {
                String[] rowCopy = row.clone();
                rowCopy[index] = value;
                rows.add(rowCopy);
            }
        }
        return rows;
    }

    private void write(CsvWriter writer, List<String[]> rows){
        for( String[] row : rows )
            writer.writeRow(row);
    }

}