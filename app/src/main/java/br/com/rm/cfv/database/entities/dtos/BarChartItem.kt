package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo

class BarChartItem {
    @ColumnInfo(name="axisX")
    var axisX: Long? = 0
    @ColumnInfo(name="axisY")
    var axisY: Float = 0.0F
}
