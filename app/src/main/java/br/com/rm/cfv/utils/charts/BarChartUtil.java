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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.com.rm.cfv.utils.charts.common.BarChartDataSet;
import br.com.rm.cfv.utils.charts.common.XYMarkerView;

public class BarChartUtil implements OnChartValueSelectedListener {

    protected Typeface tfRegular;
    protected Typeface tfLight;
    private Activity activity;
    private int resourceId;
    private BarChart chart;

    private ValueFormatter xAxisFormatter;
    private ValueFormatter yAxisFormatter;

    public BarChartUtil(Activity activity, int resourceId) {
        this.activity = activity;
        this.resourceId = resourceId;
    }

    public void build(ValueFormatter xAxisFormatter, ValueFormatter yAxisFormatter) {

        this.xAxisFormatter = xAxisFormatter;
        this.yAxisFormatter = yAxisFormatter;

        chart = activity.findViewById(resourceId);
        chart.animateXY(2000, 2000);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(12);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        chart.setHorizontalScrollBarEnabled(true);
        // chart.setDrawYLabels(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(10, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(10f);
        l.setTextSize(12f);
        l.setXEntrySpace(5f);

        XYMarkerView mv = new XYMarkerView(activity, xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
    }

    public void setData(List<BarChartDataSet> datasets) {

        float groupSpace = 0.00f;
        float barSpace = 0.01f;
        float barWidth = 0.49f;
        List<Float> mins = new ArrayList<>();
        List<Float> maxs = new ArrayList<>();
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            int idx = 0;
            for (BarChartDataSet bardataset : datasets) {

                List<BarEntry> values = new ArrayList<>();
                for (Map.Entry<Float, Float> entity : bardataset.getDataset().entrySet()) {
                    BarEntry pieEntry = new BarEntry(entity.getKey(), entity.getValue());
                    values.add(pieEntry);
                }
                BarDataSet set1 = (BarDataSet) chart.getData().getDataSetByIndex(idx++);
                set1.setColor(ContextCompat.getColor(activity, bardataset.getColor()));
                set1.setValues(values);
            }

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        }else {

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();

            for (BarChartDataSet bardataset : datasets) {
                mins.add(bardataset.getMinX(0f));
                maxs.add(bardataset.getMaxX(12f));

                List<BarEntry> values = new ArrayList<>();
                for (Map.Entry<Float, Float> entity : bardataset.getDataset().entrySet()) {
                    BarEntry pieEntry = new BarEntry(entity.getKey(), entity.getValue());
                    values.add(pieEntry);
                }

                BarDataSet set1 = new BarDataSet(values, bardataset.getLabel());
                set1.setColor(ContextCompat.getColor(activity, bardataset.getColor()));
                set1.setDrawIcons(false);
                dataSets.add(set1);
            }

            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f);
            data.setValueTypeface(tfLight);
            chart.setData(data);
            data.setValueFormatter(yAxisFormatter);

            // specify the width each bar should have
            chart.getBarData().setBarWidth(barWidth);
            // restrict the x-axis range
            Float min = Collections.min(mins);
            Float max = Collections.max(maxs);

            chart.setVisibleXRangeMaximum(4);
            chart.moveViewToX(min);
            chart.enableScroll();

            chart.getXAxis().setAxisMinimum(min);
            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
            chart.getXAxis().setAxisMaximum(max + 2);
            chart.groupBars(min, groupSpace, barSpace);
            chart.invalidate();
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

    public BarChart getChart() {
        return chart;
    }
}
