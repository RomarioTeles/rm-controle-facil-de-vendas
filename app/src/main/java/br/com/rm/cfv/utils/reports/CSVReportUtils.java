package br.com.rm.cfv.utils.reports;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

public class CSVReportUtils {

    private CSVReportUtils(){}

    public static <T> List<T> parseByPropertyNames(Reader csvReader, Class<T> beanClass) throws IOException {
        CSVReader reader = new CSVReaderBuilder(csvReader).withCSVParser(new
                CSVParserBuilder().build()).build();
        CsvToBean<T> bean = new CsvToBean<T>();
        HeaderColumnNameMappingStrategy<T> mappingStrategy = new HeaderColumnNameMappingStrategy<T>();
        mappingStrategy.setType(beanClass);
        bean.setMappingStrategy(mappingStrategy);
        bean.setCsvReader(reader);
        return bean.parse();
    }

    public static <T> void writeCsvFromBean(final Path path, final List<T> list) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        Writer writer = new FileWriter(path.toString());
        StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();
        sbc.write(list);
        writer.close();
    }
}
