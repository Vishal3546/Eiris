// 0. Global Auth Check and Fetch Interceptor
(function() {
    const currentPath = (window.location.pathname.split('/').pop() || '').replace(/\.html$/, '');
    
    // Auth Check
    if (currentPath && currentPath.startsWith('agency-') && currentPath !== 'agency-login') {
        if (!localStorage.getItem('agency_accessToken')) {
            window.location.replace('agency-login.html');
            return;
        }
    } else if (currentPath && currentPath.startsWith('admin-') && currentPath !== 'admin-login' && currentPath !== 'admin-reset-password') {
        if (!localStorage.getItem('admin_accessToken')) {
            window.location.replace('admin-login.html');
            return;
        }
    }

    // Prevent mobile back-button BFCache from showing protected page after logout
    window.addEventListener('pageshow', function(event) {
        if (currentPath && currentPath.startsWith('agency-') && currentPath !== 'agency-login') {
            if (!localStorage.getItem('agency_accessToken')) {
                window.location.replace('agency-login.html');
            }
        } else if (currentPath && currentPath.startsWith('admin-') && currentPath !== 'admin-login' && currentPath !== 'admin-reset-password') {
            if (!localStorage.getItem('admin_accessToken')) {
                window.location.replace('admin-login.html');
            }
        }
    });

    // Global fetch interceptor to catch 401/403
    const originalFetch = window.fetch;
    window.fetch = async function(...args) {
        const response = await originalFetch.apply(this, args);
        if (response.status === 401 || response.status === 403) {
            if (currentPath && (currentPath.startsWith('agency-') || currentPath.startsWith('admin-')) 
                && currentPath !== 'agency-login' && currentPath !== 'admin-login') {
                console.error("Authentication failed. Redirecting to login...");
                if (currentPath.startsWith('agency-')) {
                    localStorage.removeItem('agency_accessToken');
                    localStorage.removeItem('agency_refreshToken');
                    localStorage.removeItem('agency_user');
                    window.location.replace('agency-login.html');
                } else {
                    localStorage.removeItem('admin_accessToken');
                    localStorage.removeItem('admin_refreshToken');
                    localStorage.removeItem('admin_user');
                    window.location.replace('admin-login.html');
                }
            }
        }
        return response;
    };
})();

document.addEventListener('DOMContentLoaded', function() {
    
    // 1. Setup Intersection Observer for scroll animations
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animationPlayState = 'running';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const animatedElements = document.querySelectorAll('.animate-fade-in-up, .animate-slide-down, .animate-slide-right, .animate-expand-width, .animate-pop-in-rotate');
    animatedElements.forEach(el => {
        // Only pause fade-in-up for scroll trigger; others play immediately on load
        if (el.classList.contains('animate-fade-in-up')) {
            el.style.animationPlayState = 'paused';
            observer.observe(el);
        }
    });

    // 2. Active Link Handling for Sidebars
    // This highlights the current page in the sidebar based on the URL
    const rawPath = window.location.pathname.split('/').pop();
    const currentPath = (rawPath || '').replace(/\.html$/, '');
    
    if (currentPath) {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            const linkHref = (link.getAttribute('href') || '').replace(/\.html$/, '');
            if (linkHref && linkHref === currentPath) {
                // Add active styles (Bootstrap primary/custom colors)
                link.classList.remove('opacity-75', 'custom-hover');
                
                if (currentPath.startsWith('admin-')) {
                    link.classList.add('bg-night-blue-shadow', 'text-sand-tan', 'border-start', 'border-4', 'border-sand-tan');
                } else if (currentPath.startsWith('agency-')) {
                    link.classList.add('bg-night-blue', 'text-sand-tan', 'border-start', 'border-4', 'border-sand-tan');
                }
            } else {
                // Remove active styles from other links
                if (currentPath.startsWith('admin-')) {
                    link.classList.remove('bg-night-blue-shadow', 'text-sand-tan', 'border-start', 'border-4', 'border-sand-tan');
                } else if (currentPath.startsWith('agency-')) {
                    link.classList.remove('bg-night-blue', 'text-sand-tan', 'border-start', 'border-4', 'border-sand-tan');
                }
                link.classList.add('opacity-75', 'custom-hover');
            }
        });
    }

    // 3. Counter Animation for Statistics
    const counterElements = document.querySelectorAll('.counter');
    const counterObserver = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const target = entry.target;
                const targetValue = parseInt(target.getAttribute('data-target'));
                const duration = 2000; // 2 seconds
                const increment = targetValue / (duration / 16);
                let current = 0;
                
                const updateCounter = () => {
                    current += increment;
                    if (current < targetValue) {
                        target.innerText = Math.ceil(current);
                        requestAnimationFrame(updateCounter);
                    } else {
                        target.innerText = targetValue;
                    }
                };
                
                updateCounter();
                obs.unobserve(target);
            }
        });
    }, observerOptions);
    
    counterElements.forEach(el => counterObserver.observe(el));
    // 4. Responsive Sidebar Toggle Logic
    const sidebar = document.getElementById('sidebar');
    const sidebarToggleBtn = document.getElementById('sidebarToggle');
    
    if (sidebar && sidebarToggleBtn) {
        // Create overlay dynamically
        const overlay = document.createElement('div');
        overlay.className = 'sidebar-overlay';
        document.body.appendChild(overlay);

        // Toggle sidebar on button click
        sidebarToggleBtn.addEventListener('click', function(e) {
            e.stopPropagation(); // Prevent immediate closing if body has listener
            sidebar.classList.toggle('show-sidebar');
            overlay.classList.toggle('show-sidebar-overlay');
        });

        // Close sidebar on overlay click
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('show-sidebar');
            overlay.classList.remove('show-sidebar-overlay');
        });
    }

    // 5. Update Agency Name dynamically in Header
    if (window.location.pathname.includes('agency-')) {
        const userStr = localStorage.getItem('agency_user');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                const nameDisplay = user.name || user.email || 'Agency Partner';
                const initials = nameDisplay.substring(0, 2).toUpperCase();

                const agencyNameEl = document.querySelector('span.text-muted strong.text-night-blue');
                const agencyInitialsEl = document.querySelector('.text-white.fw-bold.rounded-circle');

                if (agencyNameEl) {
                    agencyNameEl.textContent = nameDisplay;
                }
                if (agencyInitialsEl) {
                    agencyInitialsEl.textContent = initials;
                }
            } catch (e) {
                console.error('Error parsing agency user data', e);
            }
        }
    }

    // 6. Fix for Bootstrap 5 aria-hidden accessibility warning on modal close
    document.addEventListener('hide.bs.modal', function(event) {
        if (document.activeElement && document.activeElement.closest('.modal')) {
            document.activeElement.blur();
        }
    });

    // 7. Global Admin Purchase Requests Pending Counter Badge in Sidebar
    if (typeof window.updateAdminPurchaseRequestBadge === 'function') {
        window.updateAdminPurchaseRequestBadge();
        setInterval(window.updateAdminPurchaseRequestBadge, 30000);
    }

});

// Global function to update Admin Purchase Requests Badge in sidebar
window.updateAdminPurchaseRequestBadge = function() {
    if (window.location.pathname.includes('admin-') && !window.location.pathname.includes('admin-login')) {
        const adminToken = localStorage.getItem('admin_accessToken');
        if (adminToken) {
            fetch('https://eiris.onrender.com/api/admin/orders', {
                headers: { 'Authorization': `Bearer ${adminToken}` }
            })
            .then(res => res.ok ? res.json() : [])
            .then(orders => {
                if (Array.isArray(orders)) {
                    const pendingCount = orders.filter(o => o.status === 'PENDING').length;
                    const purchaseLinks = document.querySelectorAll('a[href*="admin-purchase"]');
                    purchaseLinks.forEach(purchaseLink => {
                        purchaseLink.classList.add('d-flex', 'align-items-center');
                        let badge = purchaseLink.querySelector('#sidebarPurchaseRequestBadge');
                        if (!badge) {
                            badge = document.createElement('span');
                            badge.id = 'sidebarPurchaseRequestBadge';
                            badge.className = 'eiris-sidebar-badge d-none';
                            purchaseLink.appendChild(badge);
                        }
                        if (pendingCount > 0) {
                            badge.textContent = pendingCount > 99 ? '99+' : pendingCount;
                            badge.classList.remove('d-none');
                            badge.classList.add('d-inline-flex');
                        } else {
                            badge.classList.add('d-none');
                            badge.classList.remove('d-inline-flex');
                        }
                    });
                }
            })
            .catch(err => console.error("Error fetching purchase requests count for sidebar badge:", err));
        }
    }
};

// 7. Global Custom Alert to replace default browser alert()
window.customAlert = function(message) {
    let modalEl = document.getElementById('globalCustomAlertModal');
    if (!modalEl) {
        const modalHtml = `
            <div class="modal fade" id="globalCustomAlertModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-sm" style="max-width: 350px;">
                    <div class="modal-content glass-modal-content text-center p-4">
                        <div class="glass-icon-container mx-auto mb-3" style="border-color: var(--eiris-sand-tan); width: 60px; height: 60px;">
                            <i class="bi bi-info-circle" style="color: var(--eiris-sand-tan); font-size: 30px;"></i>
                        </div>
                        <h5 class="mb-3 fw-bold text-white">Notification</h5>
                        <p class="text-white-50 mb-4" id="globalCustomAlertMessage" style="font-size: 0.95rem; line-height: 1.5;"></p>
                        <button type="button" class="btn btn-secondary-custom rounded-pill px-4 w-100 fw-bold shadow-sm" data-bs-dismiss="modal">OK</button>
                    </div>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        modalEl = document.getElementById('globalCustomAlertModal');
    }
    
    document.getElementById('globalCustomAlertMessage').textContent = message;
    
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
};
