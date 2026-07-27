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
            footer.className = 'd-flex flex-wrap justify-content-between align-items-center p-3 border-top bg-white eiris-pagination-footer gap-2';
            tableResponsive.parentNode.insertBefore(footer, tableResponsive.nextSibling);
        }

        if (totalItems === 0) {
            footer.innerHTML = `<div class="text-muted small">Showing <strong>0</strong> to <strong>0</strong> of <strong>0</strong> entries</div>`;
            return currentPage;
        }

        let pagesHtml = '';
        pagesHtml += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <button class="page-link shadow-sm border" data-page="${currentPage - 1}" ${currentPage === 1 ? 'disabled' : ''}>Prev</button>
        </li>`;

        for (let p = 1; p <= totalPages; p++) {
            if (p === 1 || p === totalPages || (p >= currentPage - 1 && p <= currentPage + 1)) {
                const isActive = p === currentPage;
                pagesHtml += `<li class="page-item ${isActive ? 'active' : ''}">
                    <button class="page-link shadow-sm border ${isActive ? 'bg-night-blue border-night-blue text-white fw-bold' : 'text-night-blue'}" data-page="${p}">${p}</button>
                </li>`;
            } else if (p === currentPage - 2 || p === currentPage + 2) {
                pagesHtml += `<li class="page-item disabled"><span class="page-link border text-muted">...</span></li>`;
            }
        }

        pagesHtml += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <button class="page-link shadow-sm border" data-page="${currentPage + 1}" ${currentPage === totalPages ? 'disabled' : ''}>Next</button>
        </li>`;

        footer.innerHTML = `
            <div class="text-muted small">Showing <strong>${startIndex + 1}</strong> to <strong>${endIndex}</strong> of <strong>${totalItems}</strong> entries</div>
            <ul class="pagination pagination-sm mb-0 gap-1">${pagesHtml}</ul>
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
