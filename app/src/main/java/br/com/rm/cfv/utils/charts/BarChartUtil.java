package br.com.rm.cfv.utils.charts;

import android.app.Activity;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.rm.cfv.utils.charts.common.MyValueFormatter;
import br.com.rm.cfv.utils.charts.common.monthAxisValueFormatter;

public class BarChartUtil implements OnChartValueSelectedListener {

    protected Typeface tfRegular;
    protected Typeface tfLight;
    private Activity activity;
    private int resourceId;
    private BarChart chart;

    public BarChartUtil(Activity activity, int resourceId) {
        this.activity = activity;
        this.resourceId = resourceId;
    }

    public void build(){

        chart = activity.findViewById(resourceId);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new monthAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("R$");

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(10, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);


    }

    public void setData(String label, Map<Float, Float> dataEntries) {
        List<BarEntry> values = new ArrayList<>();

        for(Map.Entry<Float, Float> entity : dataEntries.entrySet()){
            BarEntry pieEntry = new BarEntry(entity.getKey(), entity.getValue());
            values.add(pieEntry);
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, label);

            set1.setDrawIcons(false);

            int startColor1 = ContextCompat.getColor(activity, android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(activity, android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(activity, android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(activity, android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(activity, android.R.color.holo_red_light);
            int endColor1  = ContextCompat.getColor(activity, android.R.color.holo_blue_dark);
            int endColor2  = ContextCompat.getColor(activity, android.R.color.holo_purple);
            int endColor3  = ContextCompat.getColor(activity, android.R.color.holo_green_dark);
            int endColor4  = ContextCompat.getColor(activity, android.R.color.holo_red_dark);
            int endColor5  = ContextCompat.getColor(activity, android.R.color.holo_orange_dark);

            set1.setColors(startColor1,
                    startColor2,
                    startColor3,
                    startColor4,
                    startColor5,
                    endColor1,
                    endColor2,
                    endColor3,
                    endColor4,
                    endColor5 );

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = new RectF();
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + chart.getLowestVisibleX() + ", high: "
                        + chart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
        Log.i("BarChart", "nothing selected");
    }
}
