package br.com.rm.cfv.utils.charts.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BarChartDataSet {

    private final String label;

    private final Integer color;

    private final Map<Float, Float> dataset;

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

    public Float getMinX(Float def){
        try {
            return Collections.min(dataset.keySet());
        }catch (Exception e){
            return def;
        }
    }

    public Float getMinY(Float def){
        try {
            return Collections.min(dataset.values());
        }catch (Exception e){
            return def;
        }
    }

    public Float getMaxX(Float def){
        try{
        return Collections.max(dataset.keySet());
        }catch (Exception e){
            return def;
        }
    }


    public Float getMaxY(Float def){
        try {
            return Collections.max(dataset.values());
        }catch (Exception e){
            return def;
        }
    }

    public static float findSmallest(List<Float> arr) {
        float smallest = Float.MAX_VALUE;
        for(int i=0; i<arr.size(); i++) {
            if(arr.get(0) > 0 && arr.get(0)<smallest) {
                smallest = arr.get(0);
            }
        }
        return smallest;
    }
}
