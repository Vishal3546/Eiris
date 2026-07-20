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

});
