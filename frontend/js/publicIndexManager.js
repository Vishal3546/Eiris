const PublicIndexManager = {
    init() {
        this.loadLatestProducts();
    },

    async loadLatestProducts() {
        const container = document.getElementById('indexProductsContainer');
        if (!container) return;

        try {
            const baseUrl = typeof API_BASE_URL !== 'undefined' ? API_BASE_URL.replace(/\/api$/, '') : 'https://eiris.onrender.com';
            const response = await fetch(`${baseUrl}/api/public/index-products/latest-per-category`);
            if (response.ok) {
                const products = await response.json();
                this.renderProducts(products, container);
            } else {
                container.innerHTML = '<div class="col-12 text-center py-5 text-danger">Failed to load products.</div>';
            }
        } catch (error) {
            console.error('Error loading index products:', error);
            container.innerHTML = '<div class="col-12 text-center py-5 text-danger">Error loading products.</div>';
        }
    },

    renderProducts(products, container) {
        if (!products || products.length === 0) {
            container.innerHTML = '<div class="col-12 text-center py-5 text-muted">No products available.</div>';
            return;
        }

        let html = '';
        products.forEach((product, idx) => {
            const collapseId = `detailsIndexProduct_${product.id || idx}`;
            
            html += `
                <div class="col-md-6 col-lg-4">
                    <div class="h-100 animate-fade-in-up" style="animation-delay: ${idx * 100}ms;">
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

        container.innerHTML = html;
    }
};

document.addEventListener('DOMContentLoaded', () => {
    PublicIndexManager.init();
});
