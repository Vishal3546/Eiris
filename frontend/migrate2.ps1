$ErrorActionPreference = 'Stop'

$adminContent = Get-Content -Path .\admin-products.html -Raw
$start = $adminContent.IndexOf("<!-- Tabs -->")
$endStr = "            </div>`n        </div>`n    </div>"
$end = $adminContent.IndexOf($endStr, $start)
if ($end -eq -1) {
    # Try alternate line endings
    $endStr = "            </div>`r`n        </div>`r`n    </div>"
    $end = $adminContent.IndexOf($endStr, $start)
}

if ($start -ne -1 -and $end -ne -1) {
    $tabsHtml = $adminContent.Substring($start, $end - $start)
    
    # Replace buttons
    $pattern = '(?s)<div class="d-flex justify-content-between align-items-center mt-3 mt-auto">.*?<span class="fw-bold fs-5 text-sand-tan-shadow"[^>]*>.*?</span>.*?<button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3 ms-auto" data-bs-toggle="collapse" data-bs-target="#(.*?)">View Details</button>.*?<div class="ms-2 d-flex gap-1">.*?</div>.*?</div>'
    
    $replacement = '<div class="d-flex justify-content-between align-items-center mt-3 mt-auto"><button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3" data-bs-toggle="collapse" data-bs-target="#$1">View Details</button></div><div class="mt-3"><div class="d-flex justify-content-between align-items-center mb-3"><span class="fw-bold fs-5 text-sand-tan-shadow">₹899 / unit</span><span class="text-success small"><i class="bi bi-check-circle-fill"></i> In Stock</span></div><button class="btn btn-outline-primary btn-secondary-custom rounded-pill w-100"><i class="bi bi-cart-plus me-2"></i>Add to Order</button></div>'
    
    $agencyTabsHtml = [regex]::Replace($tabsHtml, $pattern, $replacement, 'Singleline,IgnoreCase')
    
    $agencyContent = Get-Content -Path .\agency-purchase.html -Raw
    
    $startAg = $agencyContent.IndexOf('<div class="row g-4">')
    $endAg = $agencyContent.IndexOf('</div>', $agencyContent.IndexOf('<!-- Product 2 -->'))
    # Skip a few closing divs
    $endAg = $agencyContent.IndexOf('</div>', $endAg + 1)
    $endAg = $agencyContent.IndexOf('</div>', $endAg + 1)
    $endAg = $agencyContent.IndexOf('</div>', $endAg + 1) + 6
    
    $agencyContent = $agencyContent.Substring(0, $startAg) + $agencyTabsHtml + $agencyContent.Substring($endAg)
    
    $cssTabs = @"
    <style>
        .custom-tabs { gap: 15px; border: none; }
        .custom-tabs .nav-link { background-color: transparent; border: 1px solid var(--eiris-dark-green); color: #a0aab2; border-radius: 8px; padding: 10px 24px; font-weight: 500; transition: all 0.3s ease; }
        .custom-tabs .nav-link:hover { color: var(--eiris-sage-green); border-color: var(--eiris-sage-green); }
        .custom-tabs .nav-link.active { background-color: rgba(156, 176, 128, 0.15); border: 1px solid var(--eiris-sage-green); color: var(--eiris-sage-green); }
    </style>
</head>
"@
    if (-not $agencyContent.Contains('custom-tabs')) {
        $agencyContent = $agencyContent.Replace('</head>', $cssTabs)
    }
    
    $agencyScript = @"
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
                            const detailsList = p.details ? p.details.split(',').map(d => '<li>'+d.trim()+'</li>').join('') : '';
                            const newId = 'detailsProductPage_' + p.id;
                            const productCard = \`
                            <div class="col-md-6 col-lg-4 custom-added-product" data-id="\${p.id}">
                                <div class="h-100">
                                    <div class="card h-100 shadow-sm border-0 rounded-4 overflow-hidden product-card">
                                        <div style="height: 200px; background-color: #f0f0f0; position: relative;">
                                            <img src="\${p.imageUrl}" alt="\${p.name}" style="width: 100%; height: 100%; object-fit: cover;">
                                            <span class="badge bg-night-blue position-absolute top-0 end-0 m-3 p-2">\${p.category}</span>
                                        </div>
                                        <div class="card-body d-flex flex-column p-4">
                                            <h5 class="card-title fw-bold mb-2 text-night-blue-shadow">\${p.name}</h5>
                                            <div class="collapse" id="\${newId}">
                                                <ul class="text-start small text-dark mb-0 ps-3 mt-2">
                                                    \${detailsList}
                                                </ul>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-center mt-3 mt-auto">
                                                <button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3" data-bs-toggle="collapse" data-bs-target="#\${newId}">View Details</button>
                                            </div>
                                            <div class="mt-3">
                                                <div class="d-flex justify-content-between align-items-center mb-3">
                                                    <span class="fw-bold fs-5 text-sand-tan-shadow">₹\${p.price || 0} / unit</span>
                                                    <span class="text-success small"><i class="bi bi-check-circle-fill"></i> In Stock</span>
                                                </div>
                                                <button class="btn btn-outline-primary btn-secondary-custom rounded-pill w-100"><i class="bi bi-cart-plus me-2"></i>Add to Order</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>\`;
                            row.insertAdjacentHTML('afterbegin', productCard);
                        }
                    }
                });
            }
            renderCustomProducts();
        });
    </script>
    <!-- Custom JS -->
"@
    
    $agencyContent = $agencyContent.Replace('<!-- Custom JS -->', $agencyScript)
    
    Set-Content -Path .\agency-purchase.html -Value $agencyContent -Encoding UTF8
    Write-Host "Success"
} else {
    Write-Host "Could not find start or end block"
}
