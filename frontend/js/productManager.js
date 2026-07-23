// Product Manager for handling custom added products in localStorage

const PRODUCT_STORAGE_KEY = 'eiris_custom_products';

const productManager = {
    getProducts: function() {
        const productsStr = localStorage.getItem(PRODUCT_STORAGE_KEY);
        if (productsStr) {
            try {
                return JSON.parse(productsStr);
            } catch (e) {
                console.error("Error parsing custom products", e);
                return [];
            }
        }
        return [];
    },

    addProduct: function(product) {
        const products = this.getProducts();
        product.id = 'prod_' + Date.now();
        products.push(product);
        localStorage.setItem(PRODUCT_STORAGE_KEY, JSON.stringify(products));
        return product;
    },

    updateProduct: function(id, updatedProduct) {
        const products = this.getProducts();
        const index = products.findIndex(p => p.id === id);
        if (index !== -1) {
            products[index] = { ...products[index], ...updatedProduct };
            localStorage.setItem(PRODUCT_STORAGE_KEY, JSON.stringify(products));
            return products[index];
        }
        return null;
    },

    deleteProduct: function(id) {
        let products = this.getProducts();
        products = products.filter(p => p.id !== id);
        localStorage.setItem(PRODUCT_STORAGE_KEY, JSON.stringify(products));
    }
};

window.productManager = productManager;
