package com.eiris.backend.dto.response;



public class AgencyMetricsResponse {
    private Integer totalItems;
    private Integer lowStockCount;
    private Integer outOfStockCount;
    private Double totalValue;

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public Integer getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Integer lowStockCount) { this.lowStockCount = lowStockCount; }
    public Integer getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(Integer outOfStockCount) { this.outOfStockCount = outOfStockCount; }
    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }
}
