const INVENTORY_STORAGE_KEY = 'eiris_agency_inventory';
const SALES_STORAGE_KEY = 'eiris_agency_sales';

const agencyInventoryManager = {
    getInventory: function() {
        const invStr = localStorage.getItem(INVENTORY_STORAGE_KEY);
        return invStr ? JSON.parse(invStr) : [];
    },

    saveInventory: function(inventory) {
        localStorage.setItem(INVENTORY_STORAGE_KEY, JSON.stringify(inventory));
        this.notifyInventoryChanged();
    },

    getSales: function() {
        const salesStr = localStorage.getItem(SALES_STORAGE_KEY);
        return salesStr ? JSON.parse(salesStr) : [];
    },

    saveSales: function(sales) {
        localStorage.setItem(SALES_STORAGE_KEY, JSON.stringify(sales));
    },

    addItemsToInventory: function(cartItems) {
        let inventory = this.getInventory();
        
        cartItems.forEach(cartItem => {
            const existingItemIndex = inventory.findIndex(i => i.id === cartItem.id);
            if (existingItemIndex !== -1) {
                inventory[existingItemIndex].quantity += cartItem.quantity;
                // Update price/details just in case they changed
                inventory[existingItemIndex].price = cartItem.price;
                inventory[existingItemIndex].name = cartItem.name;
            } else {
                inventory.push({
                    id: cartItem.id,
                    name: cartItem.name,
                    category: cartItem.category,
                    price: cartItem.price,
                    imageUrl: cartItem.imageUrl || cartItem.image,
                    quantity: cartItem.quantity
                });
            }
        });
        
        this.saveInventory(inventory);
    },

    recordSale: function(productId, quantitySold, customerName = "Walk-in Customer") {
        let inventory = this.getInventory();
        const itemIndex = inventory.findIndex(i => i.id === productId);
        
        if (itemIndex === -1) {
            return { success: false, message: "Item not found in inventory." };
        }
        
        const item = inventory[itemIndex];
        const qtyToSell = parseInt(quantitySold, 10);
        
        if (item.quantity < qtyToSell) {
            return { success: false, message: "Not enough stock available." };
        }
        
        // Deduct from inventory
        item.quantity -= qtyToSell;
        this.saveInventory(inventory);
        
        // Log the sale
        let sales = this.getSales();
        sales.push({
            id: Date.now().toString(),
            productId: item.id,
            productName: item.name,
            category: item.category,
            quantity: qtyToSell,
            unitPrice: item.price,
            totalPrice: item.price * qtyToSell,
            customerName: customerName,
            date: new Date().toISOString()
        });
        this.saveSales(sales);
        
        return { success: true, message: "Sale recorded successfully!" };
    },
    
    getMetrics: function() {
        const inventory = this.getInventory();
        const totalItems = inventory.reduce((sum, item) => sum + item.quantity, 0);
        const lowStockCount = inventory.filter(item => item.quantity > 0 && item.quantity <= 20).length;
        const outOfStockCount = inventory.filter(item => item.quantity <= 0).length;
        const totalValue = inventory.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        
        return {
            totalItems,
            lowStockCount,
            outOfStockCount,
            totalValue
        };
    },

    notifyInventoryChanged: function() {
        window.dispatchEvent(new CustomEvent('inventoryChanged'));
    }
};

window.agencyInventoryManager = agencyInventoryManager;
