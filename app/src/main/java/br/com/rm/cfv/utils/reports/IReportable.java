package br.com.rm.cfv.utils.reports;

import java.util.ArrayList;
import java.util.List;

public interface IReportable {

    default String getReportFileName() {
        return "report.csv";
    }

    default List<Object> getDataSet() {
        return new ArrayList<>();
    }
}
