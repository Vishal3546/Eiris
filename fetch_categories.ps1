$urls = @{
    "Car Accessories" = "https://eiris.co.in/car-accessories/"
    "LED Lights" = "https://eiris.co.in/led-lights/"
    "Fan Regulator" = "https://eiris.co.in/fan-regulator/"
    "LED Bulbs" = "https://eiris.co.in/led-blubs/"
    "LED Emergency Bulbs" = "https://eiris.co.in/led-emergency-blubs/"
}

$results = @{}

foreach ($key in $urls.Keys) {
    $url = $urls[$key]
    Write-Host "Fetching $key..."
    $response = Invoke-WebRequest -Uri $url -UserAgent "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36" -UseBasicParsing -ErrorAction SilentlyContinue
    if ($response) {
        $html = $response.Content
        # Match WooCommerce product blocks or img tags
        $images = [regex]::Matches($html, '(?i)<img[^>]+src="([^">]+)"') | ForEach-Object { $_.Groups[1].Value } | Where-Object { $_ -match "wp-content/uploads" } | Select-Object -Unique | Select-Object -First 3
        $titles = [regex]::Matches($html, '(?i)<h[1-4][^>]*>(.*?)</h[1-4]>') | ForEach-Object { $_.Groups[1].Value.Trim() } | Where-Object { $_ -match "\w" -and $_ -notmatch "Off|Products|Features" } | Select-Object -Unique | Select-Object -First 3
        
        $categoryProducts = @()
        $max = [math]::Min($images.Count, $titles.Count)
        if ($max -eq 0) { $max = $images.Count } # If no titles, just use images
        
        for ($i=0; $i -lt $max; $i++) {
            $t = if ($i -lt $titles.Count) { $titles[$i] } else { "$key Product $($i+1)" }
            $categoryProducts += @{ title=$t; img=$images[$i] }
        }
        $results[$key] = $categoryProducts
    }
}

$json = $results | ConvertTo-Json -Depth 5
Set-Content -Path 'scraped_data.json' -Value $json -Encoding UTF8
Write-Host "Done"
