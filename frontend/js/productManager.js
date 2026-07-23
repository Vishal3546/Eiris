// Product Manager for handling products via Backend API

const API_BASE_URL = 'http://localhost:8080/api/admin/products';

function getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

const productManager = {
    getProducts: async function() {
        try {
            const response = await fetch('http://localhost:8080/api/public/products');
            if (!response.ok) throw new Error("Failed to fetch products");
            return await response.json();
        } catch (e) {
            console.error("Error fetching products", e);
            return [];
        }
    },

    uploadImage: async function(file) {
        try {
            const formData = new FormData();
            formData.append('image', file);
            
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`${API_BASE_URL}/upload-image`, {
                method: 'POST',
                headers: {
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                body: formData
            });
            if (!response.ok) throw new Error("Failed to upload image");
            const data = await response.json();
            return data.url;
        } catch (e) {
            console.error("Error uploading image", e);
            throw e;
        }
    },

    addProduct: async function(product) {
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(product)
            });
            if (!response.ok) throw new Error("Failed to add product");
            return await response.json();
        } catch (e) {
            console.error("Error adding product", e);
            throw e;
        }
    },

    updateProduct: async function(id, updatedProduct) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(updatedProduct)
            });
            if (!response.ok) throw new Error("Failed to update product");
            return await response.json();
        } catch (e) {
            console.error("Error updating product", e);
            throw e;
        }
    },

    deleteProduct: async function(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': localStorage.getItem('accessToken') ? `Bearer ${localStorage.getItem('accessToken')}` : ''
                }
            });
            if (!response.ok) throw new Error("Failed to delete product");
            return true;
        } catch (e) {
            console.error("Error deleting product", e);
            throw e;
        }
    }
};

window.productManager = productManager;

