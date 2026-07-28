package com.eiris.backend.dto.response;

import java.util.List;
import java.util.Map;

public class AdminDashboardResponse {
    private long totalAgencies;
    private long totalProducts;
    private double totalRevenue;
    private long pendingOrdersCount;
    private Map<String, Double> monthlyRevenue; // Month Name -> Revenue
    private List<AgencyOrderResponse> recentOrders;

    public long getTotalAgencies() { return totalAgencies; }
    public void setTotalAgencies(long totalAgencies) { this.totalAgencies = totalAgencies; }
    
    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
    
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public long getPendingOrdersCount() { return pendingOrdersCount; }
    public void setPendingOrdersCount(long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; }
    
    public Map<String, Double> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(Map<String, Double> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    
    public List<AgencyOrderResponse> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<AgencyOrderResponse> recentOrders) { this.recentOrders = recentOrders; }
}
