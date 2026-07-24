const API_BASE_URL_ADMIN_ORDERS = 'http://localhost:8080/api/admin/orders';

function getAdminAuthHeaders() {
    const token = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

const adminPurchaseManager = {
    getOrders: async function() {
        try {
            const response = await fetch(API_BASE_URL_ADMIN_ORDERS, { headers: getAdminAuthHeaders() });
            if (!response.ok) throw new Error("Failed to fetch admin orders");
            return await response.json();
        } catch (e) {
            console.error("Error in getOrders:", e);
            return [];
        }
    },

    updateOrderStatus: async function(orderId, newStatus) {
        try {
            const response = await fetch(`${API_BASE_URL_ADMIN_ORDERS}/${orderId}/status?status=${newStatus}`, {
                method: 'PUT',
                headers: getAdminAuthHeaders()
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                return { success: false, message: errorData.message || "Failed to update order status" };
            }
            
            return { success: true, message: "Order status updated to " + newStatus };
        } catch (e) {
            console.error("Error in updateOrderStatus:", e);
            return { success: false, message: "Network error occurred." };
        }
    }
};

window.adminPurchaseManager = adminPurchaseManager;
