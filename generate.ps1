$header = @"
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Eiris - Premium Products</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
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
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg bg-night-blue navbar-dark sticky-top shadow-sm py-2 border-bottom animate-slide-down" style="border-color: var(--eiris-night-blue-shadow) !important;">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center fw-bold fs-4 text-sand-tan" href="index.html">
                <div class="hover-rotate me-2">
                    <i class="bi bi-car-front-fill" style="color: var(--eiris-sand-tan); font-size: 1.5rem;"></i>
                </div>
                <span style="letter-spacing: 1px;">Eiris</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#basic-navbar-nav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="basic-navbar-nav">
                <ul class="navbar-nav me-auto ms-4">
                    <li class="nav-item"><a class="nav-link text-white position-relative hover-underline" href="index.html">Home</a></li>
                    <li class="nav-item"><a class="nav-link text-white position-relative hover-underline active" href="products.html">Products</a></li>
                    <li class="nav-item"><a class="nav-link text-white position-relative hover-underline" href="agency-login.html">Agency Login</a></li>
                    <li class="nav-item"><a class="nav-link text-white position-relative hover-underline" href="contact-us.html">Contact Us</a></li>
                </ul>
            </div>
        </div>
    </nav>
"@

$footer = @"
    <!-- Footer -->
    <footer class="bg-night-blue-shadow text-white pt-5 pb-3" style="background-color: var(--eiris-night-blue-shadow);">
        <div class="container">
            <div class="row mb-4">
                <div class="col-md-4 mb-4 mb-md-0">
                    <h4 class="text-sand-tan fw-bold mb-3">Eiris</h4>
                    <p class="text-light opacity-75">
                        Premium automotive and lighting solutions.
                        Bringing you the best quality car accessories, LED lights, and fan regulators.
                    </p>
                </div>
                <div class="col-md-4 mb-4 mb-md-0">
                    <h5 class="mb-3 text-sand-tan">Quick Links</h5>
                    <ul class="list-unstyled">
                        <li class="mb-2"><a href="index.html" class="text-white text-decoration-none opacity-75 custom-hover">Home</a></li>
                        <li class="mb-2"><a href="products.html" class="text-white text-decoration-none opacity-75 custom-hover">Products</a></li>
                        <li class="mb-2"><a href="agency-login.html" class="text-white text-decoration-none opacity-75 custom-hover">Agency Portal</a></li>
                        <li class="mb-2"><a href="contact-us.html" class="text-white text-decoration-none opacity-75 custom-hover">Contact Us</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5 class="mb-3 text-sand-tan">Contact Us</h5>
                    <ul class="list-unstyled">
                        <li class="mb-3 d-flex align-items-start">
                            <i class="bi bi-geo-alt-fill me-2 text-sand-tan flex-shrink-0" style="font-size: 20px;"></i>
                            <span class="opacity-75">123 Eiris Avenue, Business Park, Tech City 10001</span>
                        </li>
                        <li class="mb-3 d-flex align-items-center">
                            <i class="bi bi-telephone-fill me-2 text-sand-tan flex-shrink-0" style="font-size: 20px;"></i>
                            <span class="opacity-75">+1 (800) 123-4567</span>
                        </li>
                        <li class="mb-3 d-flex align-items-center">
                            <i class="bi bi-envelope-fill me-2 text-sand-tan flex-shrink-0" style="font-size: 20px;"></i>
                            <span class="opacity-75">support@eiris.co.in</span>
                        </li>
                    </ul>
                </div>
            </div>
            <hr class="border-light opacity-25" />
            <div class="row">
                <div class="col text-center opacity-75">
                    <small>&copy; 2026 Eiris. All Rights Reserved.</small>
                </div>
            </div>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/script.js"></script>
</body>
</html>
"@

$categories = @{
    "pane-car-acc" = @(
        @{ title="FMBT Player Radio"; img="https://eiris.co.in/wp-content/uploads/2025/06/FMBT-Player-1.jpg"; badge="Car Accessories"; specs=@("High Quality Audio", "Bluetooth 5.0", "FM Radio Support") },
        @{ title="Double Knob Player"; img="https://eiris.co.in/wp-content/uploads/2025/06/Double-Knob-player-1.jpg"; badge="Car Accessories"; specs=@("Double Knob Control", "USB & SD Card Support", "LCD Display") },
        @{ title="Rear View Cameras"; img="https://eiris.co.in/wp-content/uploads/2025/06/cameras.jpg"; badge="Car Accessories"; specs=@("Night Vision", "Wide Angle Lens", "Waterproof Design") }
    )
    "pane-led-lights" = @(
        @{ title="K Light"; img="https://eiris.co.in/wp-content/uploads/2025/07/K-LIGHT-PC.jpg"; badge="LED Lights"; specs=@("High Lumens", "Energy Efficient", "Long Lifespan") },
        @{ title="Eyelit"; img="https://eiris.co.in/wp-content/uploads/2025/07/EYELIT-PC.jpg"; badge="LED Lights"; specs=@("Premium Design", "Super Bright", "Easy Installation") },
        @{ title="Ball PC"; img="https://eiris.co.in/wp-content/uploads/2025/07/Ball-PC.jpg"; badge="LED Lights"; specs=@("Durable Casing", "Flicker-Free", "Warm White") }
    )
    "pane-fan-reg" = @(
        @{ title="Step Fan Regulator"; img="https://eiris.co.in/wp-content/uploads/2025/06/regulator.jpg"; badge="Fan Regulator"; specs=@("4-Step Control", "Hum-Free", "Polycarbonate Body") },
        @{ title="Mini Fan Regulator"; img="https://eiris.co.in/wp-content/uploads/2025/06/regulator-1.jpg"; badge="Fan Regulator"; specs=@("Compact Design", "Smooth Operation", "ISI Certified") },
        @{ title="Premium Fan Regulator"; img="https://eiris.co.in/wp-content/uploads/2025/06/regulator-2.jpg"; badge="Fan Regulator"; specs=@("High Load Capacity", "Flame Retardant", "Modern Aesthetics") }
    )
    "pane-led-bulbs" = @(
        @{ title="LED Bulb 9W"; img="https://eiris.co.in/wp-content/uploads/2025/07/EYELIT-PC.jpg"; badge="LED Bulbs"; specs=@("9 Watt Power", "B22 Base", "Cool Daylight") },
        @{ title="LED Bulb 12W"; img="https://eiris.co.in/wp-content/uploads/2025/07/Ball-PC.jpg"; badge="LED Bulbs"; specs=@("12 Watt Power", "Energy Saving", "Wide Beam Angle") }
    )
    "pane-led-emerg" = @(
        @{ title="Emergency LED 9W"; img="https://eiris.co.in/wp-content/uploads/2025/07/K-LIGHT-PC.jpg"; badge="Emergency"; specs=@("Battery Backup", "Automatic ON", "Rechargeable") }
    )
}

$middle = @"
    <!-- Page Header -->
    <section class="py-5" style="background: linear-gradient(135deg, var(--eiris-night-blue) 0%, var(--eiris-night-blue-shadow) 100%);">
        <div class="container text-center py-4">
            <h1 class="display-4 fw-bold text-white mb-3">Our Complete Range</h1>
            <p class="text-white-50 fs-5 mb-0">Explore our premium quality products categorized for your convenience.</p>
        </div>
    </section>

    <!-- Products Tabs Area -->
    <section class="py-5" style="background-color: var(--eiris-night-blue);">
        <div class="container py-4">
            <!-- Tabs -->
            <ul class="nav nav-pills custom-tabs mb-5 justify-content-center" id="productTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="tab-car-acc" data-bs-toggle="pill" data-bs-target="#pane-car-acc" type="button" role="tab" aria-selected="true">Car Accessories</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-led-lights" data-bs-toggle="pill" data-bs-target="#pane-led-lights" type="button" role="tab" aria-selected="false">LED Lights</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-fan-reg" data-bs-toggle="pill" data-bs-target="#pane-fan-reg" type="button" role="tab" aria-selected="false">Fan Regulator</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-led-bulbs" data-bs-toggle="pill" data-bs-target="#pane-led-bulbs" type="button" role="tab" aria-selected="false">LED Bulbs</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-led-emerg" data-bs-toggle="pill" data-bs-target="#pane-led-emerg" type="button" role="tab" aria-selected="false">LED Emergency Bulbs</button>
                </li>
            </ul>

            <!-- Tab Content -->
            <div class="tab-content" id="productTabsContent">
"@

$i = 1
foreach ($pane in @("pane-car-acc", "pane-led-lights", "pane-fan-reg", "pane-led-bulbs", "pane-led-emerg")) {
    $activeClass = if ($pane -eq "pane-car-acc") { "show active" } else { "" }
    
    $middle += @"
                <!-- Pane: $pane -->
                <div class="tab-pane fade $activeClass" id="$pane" role="tabpanel">
                    <div class="row g-4">
"@
    foreach ($p in $categories[$pane]) {
        $t = $p.title
        $im = $p.img
        $b = $p.badge
        $specsHtml = ""
        foreach ($spec in $p.specs) {
            $specsHtml += "<li>$spec</li>`n"
        }
        
        $middle += @"
                        <!-- Product $i -->
                        <div class="col-md-6 col-lg-4">
                            <div class="h-100">
                                <div class="card h-100 shadow-sm border-0 rounded-4 overflow-hidden product-card">
                                    <div style="height: 200px; background-color: #f0f0f0; position: relative;">
                                        <img src="$im" alt="$t" style="width: 100%; height: 100%; object-fit: cover;">
                                        <span class="badge bg-night-blue position-absolute top-0 end-0 m-3 p-2">$b</span>
                                    </div>
                                    <div class="card-body d-flex flex-column p-4">
                                        <h5 class="card-title fw-bold mb-2 text-night-blue-shadow">$t</h5>
                                        <div class="collapse" id="detailsProductPage$i">
                                            <ul class="text-start small text-dark mb-0 ps-3 mt-2">
                                                $specsHtml
                                            </ul>
                                        </div>
                                        <div class="d-flex justify-content-between align-items-center mt-3 mt-auto">
                                            <span class="fw-bold fs-5 text-sand-tan-shadow"></span>
                                            <button class="btn btn-outline-primary btn-sm btn-secondary-custom rounded-pill px-3 ms-auto" data-bs-toggle="collapse" data-bs-target="#detailsProductPage$i">View Details</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
"@
        $i++
    }
    
    $middle += @"
                    </div>
                </div>
"@
}

$middle += @"
            </div>
        </div>
    </section>
"@

$fullHtml = $header + $middle + $footer
Set-Content -Path 'products.html' -Value $fullHtml -Encoding utf8
