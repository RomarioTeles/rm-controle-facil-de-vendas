package br.com.rm.cfv.utils.charts.common;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;

/**
 * Created by philipp on 02/06/16.
 */
public class MonthAxisValueFormatter extends ValueFormatter {

    private final String[] mMonths = new String[]{
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez",
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    private final BarLineChartBase<?> chart;

    public MonthAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            Calendar cal = Calendar.getInstance();
            if(value > 11.0) {
                cal.add(Calendar.YEAR, 1);
                return mMonths[Float.valueOf(value).intValue()] + " " +cal.get(Calendar.YEAR);
            }else{
                return mMonths[Float.valueOf(value).intValue()] + " " + cal.get(Calendar.YEAR);
            }
        } catch (Exception e) {
            return "";
        }
    }

}
