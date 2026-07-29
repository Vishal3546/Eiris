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
    const currentPath = window.location.pathname.split('/').pop();
    
    if (currentPath) {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            const linkHref = link.getAttribute('href');
            if (linkHref === currentPath) {
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

});

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
