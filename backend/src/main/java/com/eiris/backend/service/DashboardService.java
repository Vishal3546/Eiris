package com.eiris.backend.service;

import com.eiris.backend.dto.response.AdminDashboardResponse;
import com.eiris.backend.dto.response.AgencyDashboardResponse;
import com.eiris.backend.dto.response.AgencyOrderResponse;
import com.eiris.backend.dto.response.AgencySaleResponse;
import com.eiris.backend.entity.*;
import com.eiris.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AgencyRepository agencyRepository;
    private final ProductRepository productRepository;
    private final AgencyOrderRepository orderRepository;
    private final AgencyClientRepository clientRepository;
    private final AgencySaleRepository saleRepository;
    private final AgencyInventoryRepository inventoryRepository;

    public DashboardService(AgencyRepository agencyRepository,
                            ProductRepository productRepository,
                            AgencyOrderRepository orderRepository,
                            AgencyClientRepository clientRepository,
                            AgencySaleRepository saleRepository,
                            AgencyInventoryRepository inventoryRepository) {
        this.agencyRepository = agencyRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.saleRepository = saleRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public AdminDashboardResponse getAdminDashboardMetrics() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalAgencies(agencyRepository.count());
        response.setTotalProducts(productRepository.count());

        List<AgencyOrder> allOrders = orderRepository.findAll();
        
        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();
        response.setPendingOrdersCount(pendingOrders);

        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.APPROVED || o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.SHIPPED)
                .mapToDouble(AgencyOrder::getTotalPrice)
                .sum();
        response.setTotalRevenue(totalRevenue);

        // Group revenue by Month-Year for the last 6 months (simple grouping)
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.APPROVED || o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.SHIPPED)
                .sorted(Comparator.comparing(AgencyOrder::getCreatedAt))
                .forEach(o -> {
                    String month = o.getCreatedAt().format(formatter);
                    monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + o.getTotalPrice());
                });
        response.setMonthlyRevenue(monthlyRevenue);

        // Pre-fetch agencies to avoid N+1 queries
        List<Agency> allAgenciesList = agencyRepository.findAll();
        Map<UUID, String> agencyMap = allAgenciesList.stream()
                .filter(a -> a.getUser() != null)
                .collect(Collectors.toMap(
                        a -> a.getUser().getId(),
                        Agency::getAgencyName,
                        (name1, name2) -> name1
                ));

        // Top 50 recent orders
        List<AgencyOrderResponse> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(AgencyOrder::getCreatedAt).reversed())
                .limit(50)
                .map(order -> mapToOrderResponse(order, agencyMap))
                .collect(Collectors.toList());
        response.setRecentOrders(recentOrders);

        return response;
    }

    public AgencyDashboardResponse getAgencyDashboardMetrics(User agencyUser) {
        AgencyDashboardResponse response = new AgencyDashboardResponse();

        // Let's use a workaround for clients by counting distinct customers from sales
        
        List<AgencySale> agencySales = saleRepository.findByAgencyUserOrderByCreatedAtDesc(agencyUser);
        
        double totalSalesRevenue = agencySales.stream().mapToDouble(AgencySale::getTotalPrice).sum();
        response.setTotalSalesRevenue(totalSalesRevenue);

        // Group sales by Month-Year
        Map<String, Double> monthlySales = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        agencySales.stream()
                .sorted(Comparator.comparing(AgencySale::getCreatedAt))
                .forEach(s -> {
                    if(s.getCreatedAt() != null) {
                        String month = s.getCreatedAt().format(formatter);
                        monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + s.getTotalPrice());
                    }
                });
        response.setMonthlySales(monthlySales);

        // Recent 50 sales
        List<AgencySaleResponse> recentSales = agencySales.stream()
                .limit(50)
                .map(this::mapToSaleResponse)
                .collect(Collectors.toList());
        response.setRecentSales(recentSales);

        // Inventory metrics
        List<AgencyInventory> inventory = inventoryRepository.findByAgencyUser(agencyUser);
        long lowStockCount = inventory.stream().filter(i -> i.getAvailableQuantity() < 5).count();
        double inventoryValue = inventory.stream().mapToDouble(i -> i.getAvailableQuantity() * i.getProduct().getPrice()).sum();
        
        response.setLowStockCount(lowStockCount);
        response.setTotalInventoryValue(inventoryValue);

        // To get total clients without custom repo method, we can extract distinct clients from sales, or we assume it exists.
        // Actually let's just count distinct client names from sales to be safe if repo method is missing
        long uniqueClients = agencySales.stream().map(AgencySale::getCustomerName).filter(Objects::nonNull).distinct().count();
        response.setTotalClients(uniqueClients);

        return response;
    }

    private AgencyOrderResponse mapToOrderResponse(AgencyOrder order, Map<UUID, String> agencyMap) {
        AgencyOrderResponse resp = new AgencyOrderResponse();
        resp.setId(order.getId());
        
        String agencyName = "Unknown Agency";
        if (order.getAgencyUser() != null && agencyMap.containsKey(order.getAgencyUser().getId())) {
            agencyName = agencyMap.get(order.getAgencyUser().getId());
        }
        resp.setAgencyName(agencyName);
        
        resp.setStatus(order.getStatus());
        resp.setTotalPrice(order.getTotalPrice());
        if (order.getCreatedAt() != null) {
            resp.setDate(order.getCreatedAt());
        }
        if (order.getProduct() != null) {
            resp.setProductName(order.getProduct().getName());
            resp.setQuantity(order.getQuantity());
            resp.setUnitPrice(order.getUnitPrice());
        }
        return resp;
    }

    private AgencySaleResponse mapToSaleResponse(AgencySale sale) {
        AgencySaleResponse resp = new AgencySaleResponse();
        resp.setId(sale.getId());
        resp.setCustomerName(sale.getCustomerName());
        resp.setQuantity(sale.getQuantity());
        resp.setUnitPrice(sale.getUnitPrice());
        resp.setTotalPrice(sale.getTotalPrice());
        if (sale.getProduct() != null) {
            resp.setProductName(sale.getProduct().getName());
            resp.setCategory(sale.getProduct().getCategory());
        }
        if (sale.getCreatedAt() != null) {
            resp.setDate(sale.getCreatedAt());
        }
        return resp;
    }
}
