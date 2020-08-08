package br.com.rm.cfv.utils.charts.common;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class monthAxisValueFormatter extends ValueFormatter
{

    private final String[] mMonths = new String[]{
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    private final BarLineChartBase<?> chart;

    public monthAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            return mMonths[Float.valueOf(value).intValue()];
            //return String.valueOf(value);
        }catch (Exception e){
            return "";
        }
    }

}
