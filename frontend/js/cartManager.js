const CART_STORAGE_KEY = 'eiris_agency_cart';

const cartManager = {
    getCart: function() {
        const cartStr = localStorage.getItem(CART_STORAGE_KEY);
        if (cartStr) {
            try {
                return JSON.parse(cartStr);
            } catch (e) {
                console.error("Error parsing cart", e);
                return [];
            }
        }
        return [];
    },

    saveCart: function(cart) {
        localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
        this.notifyCartChanged();
    },

    addToCart: function(product, quantity) {
        let cart = this.getCart();
        const index = cart.findIndex(item => item.id === product.id);
        
        if (index !== -1) {
            cart[index].quantity += parseInt(quantity, 10);
        } else {
            cart.push({
                ...product,
                quantity: parseInt(quantity, 10)
            });
        }
        
        this.saveCart(cart);
    },

    updateQuantity: function(productId, quantity) {
        let cart = this.getCart();
        const index = cart.findIndex(item => item.id === productId);
        
        if (index !== -1) {
            if (quantity <= 0) {
                cart.splice(index, 1);
            } else {
                cart[index].quantity = parseInt(quantity, 10);
            }
            this.saveCart(cart);
        }
    },

    removeFromCart: function(productId) {
        let cart = this.getCart();
        cart = cart.filter(item => item.id !== productId);
        this.saveCart(cart);
    },

    clearCart: function() {
        this.saveCart([]);
    },

    getTotalPrice: function() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    },
    
    getTotalItems: function() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + item.quantity, 0);
    },

    notifyCartChanged: function() {
        const event = new CustomEvent('cartChanged');
        window.dispatchEvent(event);
    }
};

window.cartManager = cartManager;
