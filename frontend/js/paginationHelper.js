/**
 * Reusable Pagination Helper for Eiris Admin & Agency Tables
 * Automatically paginates data, renders rows, and inserts a responsive footer with page controls.
 * Default page size is 10 items per page.
 */
(function() {
    window.renderTableWithPagination = function({
        tableBodyId,
        data,
        currentPage = 1,
        pageSize = 10,
        renderRow,
        colspan = 6,
        emptyMessage = "No records found."
    }) {
        const tbody = document.getElementById(tableBodyId);
        if (!tbody) return 1;

        const totalItems = data.length;
        const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = Math.min(startIndex + pageSize, totalItems);
        const pageData = data.slice(startIndex, endIndex);

        if (pageData.length === 0) {
            tbody.innerHTML = `<tr><td colspan="${colspan}" class="text-center py-4 text-muted">${emptyMessage}</td></tr>`;
        } else {
            let html = '';
            pageData.forEach((item, idx) => {
                html += renderRow(item, startIndex + idx);
            });
            tbody.innerHTML = html;
        }

        const tableResponsive = tbody.closest('.table-responsive');
        if (!tableResponsive) return currentPage;

        let footer = tableResponsive.nextElementSibling;
        if (!footer || !footer.classList.contains('eiris-pagination-footer')) {
            footer = document.createElement('div');
            footer.className = 'd-flex flex-wrap justify-content-between align-items-center eiris-pagination-footer gap-3';
            tableResponsive.parentNode.insertBefore(footer, tableResponsive.nextSibling);
        }

        if (totalItems === 0) {
            footer.innerHTML = `<div class="eiris-pagination-info">Showing <strong>0</strong> to <strong>0</strong> of <strong>0</strong> entries</div>`;
            return currentPage;
        }

        let pagesHtml = '';
        pagesHtml += `<li>
            <button class="eiris-page-btn" data-page="${currentPage - 1}" ${currentPage === 1 ? 'disabled' : ''} title="Previous Page">
                <i class="bi bi-chevron-left"></i>
            </button>
        </li>`;

        for (let p = 1; p <= totalPages; p++) {
            if (p === 1 || p === totalPages || (p >= currentPage - 1 && p <= currentPage + 1)) {
                const isActive = p === currentPage;
                pagesHtml += `<li>
                    <button class="eiris-page-btn ${isActive ? 'active' : ''}" data-page="${p}">${p}</button>
                </li>`;
            } else if (p === currentPage - 2 || p === currentPage + 2) {
                pagesHtml += `<li class="eiris-page-dots">...</li>`;
            }
        }

        pagesHtml += `<li>
            <button class="eiris-page-btn" data-page="${currentPage + 1}" ${currentPage === totalPages ? 'disabled' : ''} title="Next Page">
                <i class="bi bi-chevron-right"></i>
            </button>
        </li>`;

        footer.innerHTML = `
            <div class="eiris-pagination-info">
                Showing <strong>${startIndex + 1}</strong> to <strong>${endIndex}</strong> of <strong>${totalItems}</strong> entries
            </div>
            <ul class="eiris-pagination-nav">${pagesHtml}</ul>
        `;

        const buttons = footer.querySelectorAll('button[data-page]');
        buttons.forEach(btn => {
            btn.onclick = (e) => {
                e.preventDefault();
                const targetPage = parseInt(btn.getAttribute('data-page'));
                if (!isNaN(targetPage) && targetPage !== currentPage && targetPage >= 1 && targetPage <= totalPages) {
                    tbody.dispatchEvent(new CustomEvent('pageChange', { detail: { page: targetPage } }));
                }
            };
        });

        return currentPage;
    };
})();
