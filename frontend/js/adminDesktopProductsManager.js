const AdminDesktopProductsManager = {
    products: [],
    
    init() {
        this.bindEvents();
        this.loadProducts();
    },

    bindEvents() {
        const addForm = document.getElementById('addProductForm');
        if (addForm) {
            document.getElementById('saveProductBtn').addEventListener('click', () => this.handleAddProduct());
        }

        const editForm = document.getElementById('editProductForm');
        if (editForm) {
            document.getElementById('updateProductBtn').addEventListener('click', () => this.handleUpdateProduct());
        }

        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        if (confirmDeleteBtn) {
            confirmDeleteBtn.addEventListener('click', () => this.executeDelete());
        }
    },

    async loadProducts() {
        try {
            const response = await apiService.get('/api/admin/index-products');
            this.products = response.data;
            this.renderTable();
        } catch (error) {
            console.error('Error loading products:', error);
            customAlert('Failed to load products');
        }
    },

    renderTable() {
        const tbody = document.getElementById('productsTableBody');
        if (!tbody) return;

        if (this.products.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No products found</td></tr>`;
            return;
        }

        let html = '';
        this.products.forEach((p, index) => {
            html += `
                <tr>
                    <td class="ps-4 fw-bold text-muted">${index + 1}</td>
                    <td class="fw-semibold text-night-blue">${p.name}</td>
                    <td><span class="badge bg-light text-night-blue border px-2 py-1 rounded-pill">${p.category}</span></td>
                    <td class="fw-bold text-success">₹${p.price.toLocaleString('en-IN')}</td>
                    <td>
                        <span class="badge ${p.stock <= 10 ? 'bg-danger' : 'bg-primary'} bg-opacity-10 ${p.stock <= 10 ? 'text-danger' : 'text-primary'} border ${p.stock <= 10 ? 'border-danger' : 'border-primary'} px-2 py-1 rounded-pill">
                            ${p.stock}
                        </span>
                    </td>
                    <td>
                        <img src="${p.imageUrl}" alt="${p.name}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;">
                    </td>
                    <td>
                        <button class="btn btn-sm btn-outline-info rounded-pill px-3" onclick="AdminDesktopProductsManager.viewDetails('${p.id}')">View</button>
                    </td>
                    <td class="pe-4 text-end">
                        <div class="d-flex justify-content-end gap-2">
                            <button class="btn btn-sm btn-outline-primary rounded-circle" style="width: 32px; height: 32px; padding: 0;" onclick="AdminDesktopProductsManager.openEditModal('${p.id}')" title="Edit">
                                <i class="bi bi-pencil-fill"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger rounded-circle" style="width: 32px; height: 32px; padding: 0;" onclick="AdminDesktopProductsManager.openDeleteModal('${p.id}')" title="Delete">
                                <i class="bi bi-trash-fill"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });
        tbody.innerHTML = html;
    },

    async uploadImage(file) {
        const formData = new FormData();
        formData.append('image', file);
        
        try {
            const response = await axios.post(`${API_BASE_URL}/api/admin/index-products/upload-image`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    ...authService.getAuthHeader()
                }
            });
            return response.data.url;
        } catch (error) {
            console.error('Error uploading image:', error);
            throw new Error('Image upload failed');
        }
    },

    async handleAddProduct() {
        const form = document.getElementById('addProductForm');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const btn = document.getElementById('saveProductBtn');
        const originalText = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';
        btn.disabled = true;

        try {
            const imageFile = document.getElementById('productImage').files[0];
            const imageUrl = await this.uploadImage(imageFile);

            const payload = {
                name: document.getElementById('productName').value,
                category: document.getElementById('productCategory').value,
                price: parseFloat(document.getElementById('productPrice').value),
                stock: parseInt(document.getElementById('productStock').value, 10),
                imageUrl: imageUrl,
                details: document.getElementById('productDetails').value
            };

            await apiService.post('/api/admin/index-products', payload);
            
            bootstrap.Modal.getInstance(document.getElementById('addProductModal')).hide();
            form.reset();
            customAlert('Product added successfully', 'success');
            this.loadProducts();

        } catch (error) {
            customAlert('Failed to add product');
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    },

    openEditModal(id) {
        const product = this.products.find(p => p.id === id);
        if (!product) return;

        this.currentEditId = id;
        document.getElementById('editProductName').value = product.name;
        document.getElementById('editProductCategory').value = product.category;
        document.getElementById('editProductPrice').value = product.price;
        document.getElementById('editProductStock').value = product.stock;
        document.getElementById('editProductDetails').value = product.details;
        
        const preview = document.getElementById('editImagePreview');
        preview.classList.remove('d-none');
        preview.querySelector('img').src = product.imageUrl;

        new bootstrap.Modal(document.getElementById('editProductModal')).show();
    },

    async handleUpdateProduct() {
        const form = document.getElementById('editProductForm');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const btn = document.getElementById('updateProductBtn');
        const originalText = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Updating...';
        btn.disabled = true;

        try {
            let imageUrl = null;
            const imageFile = document.getElementById('editProductImage').files[0];
            
            if (imageFile) {
                imageUrl = await this.uploadImage(imageFile);
            }

            const payload = {
                name: document.getElementById('editProductName').value,
                category: document.getElementById('editProductCategory').value,
                price: parseFloat(document.getElementById('editProductPrice').value),
                stock: parseInt(document.getElementById('editProductStock').value, 10),
                details: document.getElementById('editProductDetails').value
            };

            if (imageUrl) {
                payload.imageUrl = imageUrl;
            }

            await apiService.put(`/api/admin/index-products/${this.currentEditId}`, payload);
            
            bootstrap.Modal.getInstance(document.getElementById('editProductModal')).hide();
            form.reset();
            document.getElementById('editImagePreview').classList.add('d-none');
            
            customAlert('Product updated successfully', 'success');
            this.loadProducts();

        } catch (error) {
            customAlert('Failed to update product');
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    },

    openDeleteModal(id) {
        this.currentDeleteId = id;
        new bootstrap.Modal(document.getElementById('deleteConfirmModal')).show();
    },

    async executeDelete() {
        if (!this.currentDeleteId) return;

        try {
            await apiService.delete(`/api/admin/index-products/${this.currentDeleteId}`);
            bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal')).hide();
            customAlert('Product deleted successfully', 'success');
            this.loadProducts();
        } catch (error) {
            customAlert('Failed to delete product');
        }
    },

    viewDetails(id) {
        const product = this.products.find(p => p.id === id);
        if (!product) return;

        const content = document.getElementById('viewDetailsContent');
        content.innerHTML = `<p style="white-space: pre-wrap;">${product.details}</p>`;
        
        new bootstrap.Modal(document.getElementById('viewDetailsModal')).show();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    AdminDesktopProductsManager.init();
});
