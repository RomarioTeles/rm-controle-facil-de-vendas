package br.com.rm.cfv.utils.charts.common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import br.com.rm.numberUtils.DecimalFormatUtils;

public class MyValueFormatter extends ValueFormatter
{

    private final String suffix;

    public MyValueFormatter(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getFormattedValue(float value) {
        return DecimalFormatUtils.decimalFormatPtBR(value, 0, 0);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (axis instanceof XAxis) {
            return DecimalFormatUtils.decimalFormatPtBR(value, 0, 0);
        } else {
            return suffix+ " " + DecimalFormatUtils.decimalFormatPtBR(value, 0, 0);
        }
    }
}
