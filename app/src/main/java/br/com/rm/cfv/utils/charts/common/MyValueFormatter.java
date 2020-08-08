package br.com.rm.cfv.utils.charts.common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

import br.com.rm.numberUtils.DecimalFormatUtils;

public class MyValueFormatter extends ValueFormatter
{

    private String suffix;

    public MyValueFormatter(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getFormattedValue(float value) {
        return suffix + DecimalFormatUtils.decimalFormatPtBR(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (axis instanceof XAxis) {
            return DecimalFormatUtils.decimalFormatPtBR(value);
        } else if (value > 0) {
            return DecimalFormatUtils.decimalFormatPtBR(value);
        } else {
            return DecimalFormatUtils.decimalFormatPtBR(value);
        }
    }
}
