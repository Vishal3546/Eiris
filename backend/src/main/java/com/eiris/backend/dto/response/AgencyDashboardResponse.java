package com.eiris.backend.dto.response;

import java.util.List;
import java.util.Map;

public class AgencyDashboardResponse {
    private long totalClients;
    private double totalSalesRevenue;
    private long lowStockCount;
    private double totalInventoryValue;
    private Map<String, Double> monthlySales; // Month Name -> Sales Revenue
    private List<AgencySaleResponse> recentSales;

    public long getTotalClients() { return totalClients; }
    public void setTotalClients(long totalClients) { this.totalClients = totalClients; }
    
    public double getTotalSalesRevenue() { return totalSalesRevenue; }
    public void setTotalSalesRevenue(double totalSalesRevenue) { this.totalSalesRevenue = totalSalesRevenue; }
    
    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }
    
    public double getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(double totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    
    public Map<String, Double> getMonthlySales() { return monthlySales; }
    public void setMonthlySales(Map<String, Double> monthlySales) { this.monthlySales = monthlySales; }
    
    public List<AgencySaleResponse> getRecentSales() { return recentSales; }
    public void setRecentSales(List<AgencySaleResponse> recentSales) { this.recentSales = recentSales; }
}
