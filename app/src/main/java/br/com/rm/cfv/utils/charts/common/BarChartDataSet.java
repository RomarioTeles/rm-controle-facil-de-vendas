package br.com.rm.cfv.utils.charts.common;

import java.util.Collections;
import java.util.Map;

public class BarChartDataSet {

    private String label;

    private Integer color;

    private Map<Float, Float> dataset;

    public BarChartDataSet(String label, Integer color, Map<Float, Float> dataset) {
        this.label = label;
        this.color = color;
        this.dataset = dataset;
    }

    public String getLabel() {
        return label;
    }

    public Integer getColor() {
        return color;
    }

    public Map<Float, Float> getDataset() {
        return dataset;
    }

    public Float getMinX(){
        return Collections.min(dataset.keySet());
    }

    public Float getMinY(){
        return Collections.min(dataset.values());
    }

    public Float getMaxX(){
        return Collections.max(dataset.keySet());
    }

    public Float getMaxY(){
        return Collections.max(dataset.values());
    }
}
