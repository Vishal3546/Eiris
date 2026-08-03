const AGENCY_DASHBOARD_API = 'https://eiris.onrender.com/api/agency/dashboard';

window.agencyDashboardManager = {
    recentSalesData: [],
    currentPage: 1,
    itemsPerPage: 5,

    init: async function() {
        await this.loadDashboardData();
    },

    loadDashboardData: async function() {
        try {
            let data;
            if (typeof apiService !== 'undefined') {
                const res = await apiService.get('/agency/dashboard');
                data = res.data || res;
            } else {
                const token = localStorage.getItem('agency_accessToken');
                if (!token) return;

                const response = await fetch(AGENCY_DASHBOARD_API, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    data = await response.json();
                } else {
                    console.error("Failed to fetch agency dashboard data");
                }
            }

            if (data) {
                this.updateMetrics(data);
                this.renderChart(data.monthlySales);
                this.recentSalesData = data.recentSales || [];
                this.renderRecentSales();
            }
        } catch (error) {
            console.error("Error loading agency dashboard data:", error);
        }
    },

    updateMetrics: function(data) {
        document.getElementById('totalClients').textContent = data.totalClients || 0;
        document.getElementById('lowStockCount').textContent = data.lowStockCount || 0;
        
        const formatter = new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        });
        
        document.getElementById('totalSalesRevenue').textContent = formatter.format(data.totalSalesRevenue || 0);
        document.getElementById('totalInventoryValue').textContent = formatter.format(data.totalInventoryValue || 0);
    },

    renderChart: function(monthlySales) {
        if (!monthlySales || Object.keys(monthlySales).length === 0) {
            monthlySales = { "No Data": 0 };
        }

        const categories = Object.keys(monthlySales);
        const seriesData = Object.values(monthlySales);

        const options = {
            series: [{
                name: 'Sales Revenue',
                data: seriesData
            }],
            chart: {
                type: 'bar',
                height: 350,
                fontFamily: 'inherit',
                toolbar: { show: false },
                zoom: { enabled: false }
            },
            colors: ['#2d545e'], // eiris-sand-tan shadow color or similar
            plotOptions: {
                bar: {
                    borderRadius: 4,
                    columnWidth: categories.length <= 2 ? '15%' : '40%',
                }
            },
            dataLabels: { enabled: false },
            xaxis: {
                categories: categories,
                axisBorder: { show: false },
                axisTicks: { show: false }
            },
            yaxis: {
                labels: {
                    formatter: (value) => {
                        return new Intl.NumberFormat('en-IN', {
                            style: 'currency',
                            currency: 'INR',
                            maximumFractionDigits: 0
                        }).format(value);
                    }
                }
            },
            tooltip: {
                theme: 'light',
                y: {
                    formatter: function (val) {
                        return "₹" + val;
                    }
                }
            }
        };

        const chartElement = document.querySelector("#salesChart");
        if (chartElement) {
            chartElement.innerHTML = '';
            const chart = new ApexCharts(chartElement, options);
            chart.render();
        }
    },

    renderRecentSales: function() {
        const tbody = document.getElementById('recentSalesTableBody');
        if (!tbody) return;

        if (!tbody.hasAttribute('data-paginated')) {
            tbody.setAttribute('data-paginated', 'true');
            tbody.addEventListener('pageChange', (e) => {
                this.currentPage = e.detail.page;
                this.renderRecentSales();
            });
        }

        const formatter = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

        this.currentPage = window.renderTableWithPagination({
            tableBodyId: 'recentSalesTableBody',
            data: this.recentSalesData,
            currentPage: this.currentPage,
            pageSize: 5,
            colspan: 5,
            emptyMessage: "No recent sales found.",
            renderRow: (sale, index) => {
                const dateStr = sale.date ? new Date(sale.date).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit' }) : 'Unknown';

                return `
                <tr>
                    <td>${dateStr}</td>
                    <td class="fw-semibold text-night-blue">${sale.customerName || 'Walk-in'}</td>
                    <td>
                        <div class="d-flex align-items-center">
                            <div class="bg-light rounded-circle p-2 me-2 text-success">
                                <i class="bi bi-box-seam"></i>
                            </div>
                            <div>
                                <p class="mb-0 fw-semibold">${sale.productName || 'Unknown Product'}</p>
                                <small class="text-muted">${sale.category || 'Category'}</small>
                            </div>
                        </div>
                    </td>
                    <td>${sale.quantity} <span class="text-muted small">units</span></td>
                    <td class="fw-bold text-success">${formatter.format(sale.totalPrice || 0)}</td>
                </tr>
                `;
            }
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    if(window.location.pathname.includes('agency-dashboard') || document.getElementById('totalClients')) {
        window.agencyDashboardManager.init();
        // Silent background sync (modern SWR auto-polling pattern without full page refresh)
        setInterval(() => {
            if (document.querySelector('.modal.show, .offcanvas.show')) return;
            window.agencyDashboardManager.loadDashboardData();
        }, 15000);
    }
});
