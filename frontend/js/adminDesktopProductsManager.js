const AdminDesktopProductsManager = {
    products: [],
    currentPage: 1,
    pageSize: 10,
    sortBy: 'old-to-new',
    searchQuery: '',
    currentEditId: null,
    currentDeleteId: null,
    
    init() {
        if (typeof authService !== 'undefined') {
            const user = authService.getCurrentUser();
            if (!user || user.role !== 'ADMIN') {
                window.location.replace('admin-login.html');
                return;
            }
        }
        this.bindEvents();
        this.loadProducts();
    },

    bindEvents() {
        const addForm = document.getElementById('addProductForm');
        if (addForm) {
            const saveBtn = document.getElementById('saveProductBtn');
            if (saveBtn) {
                saveBtn.addEventListener('click', () => this.handleAddProduct());
            }
        }

        const editForm = document.getElementById('editProductForm');
        if (editForm) {
            const updateBtn = document.getElementById('updateProductBtn');
            if (updateBtn) {
                updateBtn.addEventListener('click', () => this.handleUpdateProduct());
            }
        }

        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        if (confirmDeleteBtn) {
            confirmDeleteBtn.addEventListener('click', () => this.executeDelete());
        }

        const searchInput = document.getElementById('productSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchQuery = e.target.value.toLowerCase().trim();
                this.currentPage = 1;
                this.renderTable();
            });
        }

        const sortItems = document.querySelectorAll('#productSortDropdownList .dropdown-item');
        sortItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const sortVal = item.getAttribute('data-sort');
                const sortLabel = item.getAttribute('data-label');
                const sortIcon = item.getAttribute('data-icon');

                sortItems.forEach(el => {
                    el.classList.remove('active');
                    const icon = el.querySelector('i.bi-check-circle-fill');
                    if (icon) icon.classList.add('d-none');
                });

                item.classList.add('active');
                const icon = item.querySelector('i.bi-check-circle-fill');
                if (icon) icon.classList.remove('d-none');

                const labelEl = document.getElementById('currentProdSortLabel');
                if (labelEl) labelEl.textContent = sortLabel || 'Oldest First';
                const iconEl = document.getElementById('currentProdSortIcon');
                if (iconEl) iconEl.className = `bi ${sortIcon || 'bi-sort-down'}`;

                this.sortBy = sortVal;
                this.currentPage = 1;
                this.renderTable();
            });
        });

        const tbody = document.getElementById('productsTableBody');
        if (tbody) {
            tbody.addEventListener('pageChange', (e) => {
                this.currentPage = e.detail.page;
                this.renderTable();
            });
        }
    },

    async loadProducts() {
        try {
            const response = await apiService.get('/admin/index-products');
            this.products = response.data || [];
            this.renderTable();
        } catch (error) {
            console.error('Error loading products:', error);
            if (typeof customAlert === 'function') {
                customAlert('Failed to load products');
            }
        }
    },

    renderTable() {
        const tbody = document.getElementById('productsTableBody');
        if (!tbody) return;

        let filtered = (this.products || []).filter(p => {
            if (!this.searchQuery) return true;
            return (p.name || '').toLowerCase().includes(this.searchQuery) ||
                   (p.category || '').toLowerCase().includes(this.searchQuery);
        });

        filtered = [...filtered].sort((a, b) => {
            if (this.sortBy === 'old-to-new') {
                const tA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
                const tB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
                return tA - tB;
            } else if (this.sortBy === 'new-to-old') {
                const tA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
                const tB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
                return tB - tA;
            } else if (this.sortBy === 'name-a-z') {
                return (a.name || '').localeCompare(b.name || '');
            } else if (this.sortBy === 'name-z-a') {
                return (b.name || '').localeCompare(a.name || '');
            }
            return 0;
        });

        if (typeof window.renderTableWithPagination === 'function') {
            this.currentPage = window.renderTableWithPagination({
                tableBodyId: 'productsTableBody',
                data: filtered,
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                colspan: 6,
                emptyMessage: "No products found",
                renderRow: (p, index) => {
                    return `
                        <tr>
                            <td class="ps-4 fw-bold text-muted">${index + 1}</td>
                            <td>
                                <img src="${p.imageUrl || 'images/placeholder.jpg'}" alt="${p.name || 'Product'}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;">
                            </td>
                            <td class="fw-semibold text-night-blue">${p.name || '-'}</td>
                            <td><span class="badge bg-light text-night-blue border px-2 py-1 rounded-pill">${p.category || 'General'}</span></td>
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
                }
            });
        } else {
            if (filtered.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No products found</td></tr>`;
                return;
            }
            let html = '';
            filtered.forEach((p, index) => {
                html += `
                    <tr>
                        <td class="ps-4 fw-bold text-muted">${index + 1}</td>
                        <td>
                            <img src="${p.imageUrl || 'images/placeholder.jpg'}" alt="${p.name || 'Product'}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;">
                        </td>
                        <td class="fw-semibold text-night-blue">${p.name || '-'}</td>
                        <td><span class="badge bg-light text-night-blue border px-2 py-1 rounded-pill">${p.category || 'General'}</span></td>
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
        }
    },

    async uploadImage(file) {
        const formData = new FormData();
        formData.append('image', file);
        
        try {
            const token = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
            const response = await fetch('https://eiris.onrender.com/api/admin/index-products/upload-image', {
                method: 'POST',
                headers: {
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                body: formData
            });
            if (!response.ok) {
                throw new Error(`Request failed with status ${response.status}`);
            }
            const data = await response.json();
            return data.url;
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
            let imageUrl = null;
            const imageFile = document.getElementById('productImage')?.files[0];
            if (imageFile) {
                imageUrl = await this.uploadImage(imageFile);
            }

            const payload = {
                name: document.getElementById('productName').value,
                category: document.getElementById('productCategory').value,

                imageUrl: imageUrl || '',
                details: document.getElementById('productDetails')?.value || ''
            };

            await apiService.post('/admin/index-products', payload);
            
            const modalEl = document.getElementById('addProductModal');
            if (modalEl) {
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            }
            form.reset();
            if (window.$ && $('#productCategory').hasClass('select2-hidden-accessible')) {
                $('#productCategory').val('').trigger('change');
            }
            if (typeof customAlert === 'function') {
                customAlert('Product added successfully', 'success');
            }
            this.loadProducts();
        } catch (error) {
            console.error('Error adding product:', error);
            if (typeof customAlert === 'function') {
                customAlert('Failed to add product');
            }
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    },

    openEditModal(id) {
        const product = (this.products || []).find(p => p.id === id);
        if (!product) return;

        this.currentEditId = id;
        document.getElementById('editProductName').value = product.name || '';
        document.getElementById('editProductCategory').value = product.category || '';
        if (window.$ && $('#editProductCategory').hasClass('select2-hidden-accessible')) {
            $('#editProductCategory').val(product.category || '').trigger('change');
        }
        if (document.getElementById('editProductDetails')) {
            document.getElementById('editProductDetails').value = product.details || '';
        }
        
        const preview = document.getElementById('editImagePreview');
        if (preview) {
            preview.classList.remove('d-none');
            const img = preview.querySelector('img');
            if (img) img.src = product.imageUrl || 'images/placeholder.jpg';
        }

        const editModalEl = document.getElementById('editProductModal');
        if (editModalEl) {
            new bootstrap.Modal(editModalEl).show();
        }
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
            const imageFile = document.getElementById('editProductImage')?.files[0];
            if (imageFile) {
                imageUrl = await this.uploadImage(imageFile);
            }

            const payload = {
                name: document.getElementById('editProductName').value,
                category: document.getElementById('editProductCategory').value,

                details: document.getElementById('editProductDetails')?.value || ''
            };

            if (imageUrl) {
                payload.imageUrl = imageUrl;
            }

            await apiService.put(`/admin/index-products/${this.currentEditId}`, payload);
            
            const editModalEl = document.getElementById('editProductModal');
            if (editModalEl) {
                const editModal = bootstrap.Modal.getInstance(editModalEl);
                if (editModal) editModal.hide();
            }
            form.reset();
            if (window.$ && $('#editProductCategory').hasClass('select2-hidden-accessible')) {
                $('#editProductCategory').val('').trigger('change');
            }
            const preview = document.getElementById('editImagePreview');
            if (preview) preview.classList.add('d-none');
            
            if (typeof customAlert === 'function') {
                customAlert('Product updated successfully', 'success');
            }
            this.loadProducts();
        } catch (error) {
            console.error('Error updating product:', error);
            if (typeof customAlert === 'function') {
                customAlert('Failed to update product');
            }
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    },

    openDeleteModal(id) {
        this.currentDeleteId = id;
        const deleteModalEl = document.getElementById('deleteConfirmModal');
        if (deleteModalEl) {
            new bootstrap.Modal(deleteModalEl).show();
        }
    },

    async executeDelete() {
        if (!this.currentDeleteId) return;

        try {
            await apiService.delete(`/admin/index-products/${this.currentDeleteId}`);
            const deleteModalEl = document.getElementById('deleteConfirmModal');
            if (deleteModalEl) {
                const deleteModal = bootstrap.Modal.getInstance(deleteModalEl);
                if (deleteModal) deleteModal.hide();
            }
            if (typeof customAlert === 'function') {
                customAlert('Product deleted successfully', 'success');
            }
            this.loadProducts();
        } catch (error) {
            console.error('Error deleting product:', error);
            if (typeof customAlert === 'function') {
                customAlert('Failed to delete product');
            }
        }
    },

    formatDetailsAsBullets(details) {
        if (!details) return '<span class="text-muted">No details provided</span>';
        let lines = details.split(/\r?\n/).map(s => s.trim()).filter(Boolean);
        if (lines.length === 1 && lines[0].includes(',')) {
            lines = lines[0].split(',').map(s => s.trim()).filter(Boolean);
        }
        return lines.map(line => {
            const cleaned = line.replace(/^[•*\-\s]+/, '');
            return `<div class="d-flex align-items-start mb-2">
                <span class="me-2 fw-bold" style="color: #ea580c; font-size: 1.2rem; line-height: 1;">•</span>
                <span class="text-dark" style="font-size: 0.95rem;">${cleaned}</span>
            </div>`;
        }).join('');
    },

    viewDetails(id) {
        const product = (this.products || []).find(p => p.id === id);
        if (!product) return;

        const content = document.getElementById('viewDetailsContent');
        if (content) {
            content.innerHTML = this.formatDetailsAsBullets(product.details);
        }
        
        const modalEl = document.getElementById('viewDetailsModal');
        if (modalEl) {
            new bootstrap.Modal(modalEl).show();
        }
    }
};

window.AdminDesktopProductsManager = AdminDesktopProductsManager;

document.addEventListener('DOMContentLoaded', () => {
    AdminDesktopProductsManager.init();
    // Silent background sync (modern SWR auto-polling pattern without full page refresh)
    setInterval(() => {
        if (document.querySelector('.modal.show, .offcanvas.show')) return;
        AdminDesktopProductsManager.loadProducts();
    }, 15000);
});
