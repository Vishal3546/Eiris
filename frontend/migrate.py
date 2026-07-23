import re

# Read admin-products.html
with open(r'c:\Users\user\Desktop\Eiris\frontend\admin-products.html', 'r', encoding='utf-8') as f:
    admin_content = f.read()

# Extract tabs and tab content
tabs_match = re.search(r'(<!-- Tabs -->\s*<ul class=\"nav nav-pills custom-tabs.*?</ul>\s*<!-- Tab Content -->\s*<div class=\"tab-content\" id=\"productTabsContent\">.*?</div>\s*</div>)', admin_content, re.DOTALL)
if tabs_match:
    tabs_html = tabs_match.group(1)
    
    # Replace the admin action buttons with agency action buttons
    pattern = re.compile(r'<div class=\"d-flex justify-content-between align-items-center mt-3 mt-auto\">.*?<span class=\"fw-bold fs-5 text-sand-tan-shadow\">(.*?)</span>.*?<button class=\"btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3 ms-auto\" data-bs-toggle=\"collapse\" data-bs-target=\"#(.*?)\">View Details</button>.*?<div class=\"ms-2 d-flex gap-1\">.*?<button.*?><i class=\"bi bi-pencil-fill\"></i></button>.*?<button.*?><i class=\"bi bi-trash-fill\"></i></button>.*?</div>.*?</div>', re.DOTALL)
    
    def replacement(m):
        price = m.group(1)
        if not price:
            price = '₹899' # default price for hardcoded agency
        target = m.group(2)
        return f'''<div class="d-flex justify-content-between align-items-center mt-3 mt-auto">
                                                <button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3" data-bs-toggle="collapse" data-bs-target="#{target}">View Details</button>
                                            </div>
                                            <div class="mt-3">
                                                <div class="d-flex justify-content-between align-items-center mb-3">
                                                    <span class="fw-bold fs-5 text-sand-tan-shadow">{price} / unit</span>
                                                    <span class="text-success small"><i class="bi bi-check-circle-fill"></i> In Stock</span>
                                                </div>
                                                <button class="btn btn-outline-primary btn-secondary-custom rounded-pill w-100"><i class="bi bi-cart-plus me-2"></i>Add to Order</button>
                                            </div>'''
    
    agency_tabs_html = pattern.sub(replacement, tabs_html)
    
    # Read agency-purchase.html
    with open(r'c:\Users\user\Desktop\Eiris\frontend\agency-purchase.html', 'r', encoding='utf-8') as f:
        agency_content = f.read()
        
    # Replace <div class="row g-4"> ... </div> (the hardcoded products) with agency_tabs_html
    agency_content = re.sub(r'<div class=\"row g-4\">.*?</div>\s*</div>\s*</div>', agency_tabs_html + '\n            </div>\n        </div>', agency_content, flags=re.DOTALL)
    
    css_tabs = '''    <style>
        /* Custom Tab Styles based on the image */
        .custom-tabs {
            gap: 15px;
            border: none;
        }
        .custom-tabs .nav-link {
            background-color: transparent;
            border: 1px solid var(--eiris-dark-green);
            color: #a0aab2;
            border-radius: 8px;
            padding: 10px 24px;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        .custom-tabs .nav-link:hover {
            color: var(--eiris-sage-green);
            border-color: var(--eiris-sage-green);
        }
        .custom-tabs .nav-link.active {
            background-color: rgba(156, 176, 128, 0.15);
            border: 1px solid var(--eiris-sage-green);
            color: var(--eiris-sage-green);
        }
    </style>'''
    if 'custom-tabs' not in agency_content:
        agency_content = agency_content.replace('</head>', css_tabs + '\n</head>')

    agency_script = '''
    <script src="js/productManager.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            function renderCustomProducts() {
                const products = window.productManager ? window.productManager.getProducts() : [];
                document.querySelectorAll('.custom-added-product').forEach(el => el.remove());

                products.forEach(p => {
                    let targetPaneId = '';
                    switch(p.category) {
                        case 'Car Accessories': targetPaneId = 'pane-car-acc'; break;
                        case 'LED Lights': targetPaneId = 'pane-led-lights'; break;
                        case 'Fan Regulator': targetPaneId = 'pane-fan-reg'; break;
                        case 'LED Bulbs': targetPaneId = 'pane-led-bulbs'; break;
                        case 'Emergency LED': targetPaneId = 'pane-led-emerg'; break;
                    }
                    const targetPane = document.getElementById(targetPaneId);
                    if (targetPane) {
                        const row = targetPane.querySelector('.row');
                        if (row) {
                            const detailsList = p.details ? p.details.split(',').map(d => `<li>${d.trim()}</li>`).join('') : '';
                            const newId = 'detailsProductPage_' + p.id;
                            const productCard = `
                            <div class="col-md-6 col-lg-4 custom-added-product" data-id="${p.id}">
                                <div class="h-100">
                                    <div class="card h-100 shadow-sm border-0 rounded-4 overflow-hidden product-card">
                                        <div style="height: 200px; background-color: #f0f0f0; position: relative;">
                                            <img src="${p.imageUrl}" alt="${p.name}" style="width: 100%; height: 100%; object-fit: cover;">
                                            <span class="badge bg-night-blue position-absolute top-0 end-0 m-3 p-2">${p.category}</span>
                                        </div>
                                        <div class="card-body d-flex flex-column p-4">
                                            <h5 class="card-title fw-bold mb-2 text-night-blue-shadow">${p.name}</h5>
                                            <div class="collapse" id="${newId}">
                                                <ul class="text-start small text-dark mb-0 ps-3 mt-2">
                                                    ${detailsList}
                                                </ul>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-center mt-3 mt-auto">
                                                <button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3" data-bs-toggle="collapse" data-bs-target="#${newId}">View Details</button>
                                            </div>
                                            <div class="mt-3">
                                                <div class="d-flex justify-content-between align-items-center mb-3">
                                                    <span class="fw-bold fs-5 text-sand-tan-shadow">₹${p.price || 0} / unit</span>
                                                    <span class="text-success small"><i class="bi bi-check-circle-fill"></i> In Stock</span>
                                                </div>
                                                <button class="btn btn-outline-primary btn-secondary-custom rounded-pill w-100"><i class="bi bi-cart-plus me-2"></i>Add to Order</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>`;
                            row.insertAdjacentHTML('afterbegin', productCard);
                        }
                    }
                });
            }
            renderCustomProducts();
        });
    </script>
'''
    agency_content = agency_content.replace('<!-- Custom JS -->', agency_script + '\n    <!-- Custom JS -->')
    
    with open(r'c:\Users\user\Desktop\Eiris\frontend\agency-purchase.html', 'w', encoding='utf-8') as f:
        f.write(agency_content)
    print("Successfully migrated products to agency-purchase.html")
else:
    print("Could not find tabs in admin-products.html")
