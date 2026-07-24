const AGENCY_API_BASE_URL = 'http://localhost:8080/api/agency';

function getAuthHeaders() {
    const token = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

const agencyInventoryManager = {
    getInventory: async function() {
        try {
            const response = await fetch(`${AGENCY_API_BASE_URL}/inventory`, { headers: getAuthHeaders() });
            if (!response.ok) throw new Error("Failed to fetch inventory");
            return await response.json();
        } catch (e) {
            console.error("Error in getInventory:", e);
            return [];
        }
    },

    getOrders: async function() {
        try {
            const response = await fetch(`${AGENCY_API_BASE_URL}/orders`, { headers: getAuthHeaders() });
            if (!response.ok) throw new Error("Failed to fetch orders");
            return await response.json();
        } catch (e) {
            console.error("Error in getOrders:", e);
            return [];
        }
    },

    receiveOrder: async function(orderId) {
        try {
            const response = await fetch(`${AGENCY_API_BASE_URL}/orders/${orderId}/receive`, {
                method: 'PUT',
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                return { success: false, message: errorData.message || "Failed to receive order" };
            }
            this.notifyInventoryChanged();
            return { success: true, message: "Order received successfully!" };
        } catch (e) {
            console.error("Error in receiveOrder:", e);
            return { success: false, message: "Network error occurred." };
        }
    },

    getSales: async function() {
        try {
            const response = await fetch(`${AGENCY_API_BASE_URL}/sales`, { headers: getAuthHeaders() });
            if (!response.ok) throw new Error("Failed to fetch sales");
            return await response.json();
        } catch (e) {
            console.error("Error in getSales:", e);
            return [];
        }
    },

    recordSale: async function(productId, quantitySold, customerName = "Walk-in Customer") {
        try {
            const payload = {
                productId: productId,
                quantity: parseInt(quantitySold, 10),
                customerName: customerName
            };
            const response = await fetch(`${AGENCY_API_BASE_URL}/sales`, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(payload)
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                return { success: false, message: errorData.message || "Failed to record sale" };
            }
            
            this.notifyInventoryChanged();
            return { success: true, message: "Sale recorded successfully!" };
        } catch (e) {
            console.error("Error in recordSale:", e);
            return { success: false, message: "Network error occurred." };
        }
    },
    
    getMetrics: async function() {
        try {
            const response = await fetch(`${AGENCY_API_BASE_URL}/inventory/metrics`, { headers: getAuthHeaders() });
            if (!response.ok) throw new Error("Failed to fetch metrics");
            return await response.json();
        } catch (e) {
            console.error("Error in getMetrics:", e);
            return { totalItems: 0, lowStockCount: 0, outOfStockCount: 0, totalValue: 0 };
        }
    },

    notifyInventoryChanged: function() {
        window.dispatchEvent(new CustomEvent('inventoryChanged'));
    }
};

window.agencyInventoryManager = agencyInventoryManager;
