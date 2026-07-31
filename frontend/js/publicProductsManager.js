const PublicProductsManager = {
    products: [],
    currentCategory: 'Control Products',
    itemsPerPage: 6,
    currentPage: 1,
    
    // Mapping tab IDs to category names
    categoryMap: {
        'pane-quality-control': 'Control Products',
        'pane-car-acc': 'Car Accessories',
        'pane-led-lights': 'LED Lights',
        'pane-fan-reg': 'Fan Regulator',
        'pane-led-bulbs': 'LED Bulbs',
        'pane-led-emerg': 'Emergency LED'
    },

    init() {
        this.bindEvents();
        this.loadProducts();
    },

    bindEvents() {
        const tabs = document.querySelectorAll('#productTabs button[data-bs-toggle="pill"]');
        tabs.forEach(tab => {
            tab.addEventListener('shown.bs.tab', (event) => {
                const targetId = event.target.getAttribute('data-bs-target').replace('#', '');
                this.currentCategory = this.categoryMap[targetId];
                this.currentPage = 1;
                this.renderProducts();
            });
        });
    },

    async loadProducts() {
        try {
            // We use the public endpoint to get all products
            const baseUrl = typeof API_BASE_URL !== 'undefined' ? API_BASE_URL.replace(/\/api$/, '') : 'https://eiris.onrender.com';
            const response = await fetch(`${baseUrl}/api/public/index-products`);
            if (response.ok) {
                this.products = await response.json();
                this.renderProducts();
            } else {
                console.error("Failed to load products");
                document.getElementById('productTabsContent').innerHTML = '<div class="text-center text-white py-5">Failed to load products.</div>';
            }
        } catch (error) {
            console.error('Error loading products:', error);
            document.getElementById('productTabsContent').innerHTML = '<div class="text-center text-white py-5">Error loading products.</div>';
        }
    },

    renderProducts() {
        const container = document.getElementById('productTabsContent');
        if (!container) return;

        // Filter products by current category
        const filteredProducts = this.products.filter(p => p.category === this.currentCategory);
        
        // Paginate
        const totalItems = filteredProducts.length;
        const totalPages = Math.ceil(totalItems / this.itemsPerPage) || 1;
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const currentProducts = filteredProducts.slice(startIndex, startIndex + this.itemsPerPage);

        // Build HTML for the active tab pane
        let html = `
            <div class="tab-pane fade show active" role="tabpanel">
                <div class="row g-4">
        `;

        if (currentProducts.length === 0) {
            html += `<div class="col-12 text-center py-5 text-white"><h4 class="opacity-75">No products available in this category.</h4></div>`;
        } else {
            currentProducts.forEach((product, idx) => {
                // Ensure unique ID for collapse
                const collapseId = `details_${product.id || idx}`;
                
                html += `
                    <div class="col-md-6 col-lg-4">
                        <div class="h-100">
                            <div class="card h-100 shadow-sm border-0 rounded-4 overflow-hidden product-card">
                                <div style="height: 200px; background-color: #f0f0f0; position: relative;">
                                    <img src="${product.imageUrl || 'images/placeholder.jpg'}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                                    <span class="badge bg-night-blue position-absolute top-0 end-0 m-3 p-2">${product.category}</span>
                                </div>
                                <div class="card-body d-flex flex-column p-4">
                                    <h5 class="card-title fw-bold mb-2 text-night-blue-shadow">${product.name}</h5>
                                    <div class="collapse" id="${collapseId}">
                                        <p class="text-start small text-dark mb-0 mt-2" style="white-space: pre-wrap;">${product.details}</p>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mt-3 mt-auto">
                                        <span class="fw-bold fs-5 text-sand-tan-shadow"></span>
                                        <button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3 ms-auto" data-bs-toggle="collapse" data-bs-target="#${collapseId}">View Details</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
        }

        html += `
                </div>
            </div>
        `;

        container.innerHTML = html;

        // Render Pagination
        this.renderPagination(totalPages);
    },

    renderPagination(totalPages) {
        const paginationContainer = document.getElementById('productsPaginationContainer');
        if (!paginationContainer) return;

        if (totalPages <= 1) {
            paginationContainer.innerHTML = '';
            return;
        }

        let html = `
            <nav aria-label="Products page navigation">
                <ul class="pagination pagination-sm custom-pagination mb-0">
                    <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                        <a class="page-link shadow-sm rounded-start-pill" href="#" data-page="${this.currentPage - 1}">Previous</a>
                    </li>
        `;

        for (let i = 1; i <= totalPages; i++) {
            html += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link shadow-sm" href="#" data-page="${i}">${i}</a>
                </li>
            `;
        }

        html += `
                    <li class="page-item ${this.currentPage === totalPages ? 'disabled' : ''}">
                        <a class="page-link shadow-sm rounded-end-pill" href="#" data-page="${this.currentPage + 1}">Next</a>
                    </li>
                </ul>
            </nav>
        `;

        paginationContainer.innerHTML = html;

        // Bind pagination clicks
        const links = paginationContainer.querySelectorAll('.page-link');
        links.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = parseInt(e.target.getAttribute('data-page'));
                if (page && page !== this.currentPage && page >= 1 && page <= totalPages) {
                    this.currentPage = page;
                    this.renderProducts();
                    // Scroll to top of products smoothly
                    document.getElementById('productTabs').scrollIntoView({ behavior: 'smooth' });
                }
            });
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    PublicProductsManager.init();
});
