const ADMIN_DASHBOARD_API = 'https://eiris.onrender.com/api/admin/dashboard';

window.adminDashboardManager = {
    recentOrdersData: [],
    currentPage: 1,
    itemsPerPage: 5,

    init: async function() {
        await this.loadDashboardData();
    },

    loadDashboardData: async function() {
        try {
            let data;
            if (typeof apiService !== 'undefined') {
                const res = await apiService.get('/admin/dashboard');
                data = res.data || res;
            } else {
                const token = localStorage.getItem('admin_accessToken');
                if (!token) return;

                const response = await fetch(ADMIN_DASHBOARD_API, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    data = await response.json();
                } else {
                    console.error("Failed to fetch dashboard data");
                }
            }

            if (data) {
                this.updateMetrics(data);
                this.renderChart(data.monthlyRevenue);
                this.recentOrdersData = data.recentOrders || [];
                this.renderRecentOrders();
            }
        } catch (error) {
            console.error("Error loading dashboard data:", error);
        }
    },

    updateMetrics: function(data) {
        document.getElementById('totalAgencies').textContent = data.totalAgencies || 0;
        document.getElementById('totalProducts').textContent = data.totalProducts || 0;
        document.getElementById('pendingOrders').textContent = data.pendingOrdersCount || 0;
        
        // Format revenue to Indian format
        const formattedRevenue = new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(data.totalRevenue || 0);
        document.getElementById('totalRevenue').textContent = formattedRevenue;
    },

    renderChart: function(monthlyRevenue) {
        if (!monthlyRevenue || Object.keys(monthlyRevenue).length === 0) {
            monthlyRevenue = { "No Data": 0 };
        }

        const categories = Object.keys(monthlyRevenue);
        const seriesData = Object.values(monthlyRevenue);

        const options = {
            series: [{
                name: 'Revenue',
                data: seriesData
            }],
            chart: {
                type: 'area',
                height: 350,
                fontFamily: 'inherit',
                toolbar: { show: false },
                zoom: { enabled: false }
            },
            colors: ['#0b2b31'], // eiris-night-blue
            dataLabels: { enabled: false },
            stroke: { curve: 'smooth', width: 3 },
            fill: {
                type: 'gradient',
                gradient: {
                    shadeIntensity: 1,
                    opacityFrom: 0.4,
                    opacityTo: 0.05,
                    stops: [0, 90, 100]
                }
            },
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
            plotOptions: {
                bar: {
                    borderRadius: 4,
                    columnWidth: categories.length <= 2 ? '15%' : '40%',
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

        const chartElement = document.querySelector("#revenueChart");
        if (chartElement) {
            chartElement.innerHTML = '';
            const chart = new ApexCharts(chartElement, options);
            chart.render();
        }
    },

    renderRecentOrders: function() {
        const tbody = document.getElementById('recentOrdersTableBody');
        if (!tbody) return;

        if (!tbody.hasAttribute('data-paginated')) {
            tbody.setAttribute('data-paginated', 'true');
            tbody.addEventListener('pageChange', (e) => {
                this.currentPage = e.detail.page;
                this.renderRecentOrders();
            });
        }

        this.currentPage = window.renderTableWithPagination({
            tableBodyId: 'recentOrdersTableBody',
            data: this.recentOrdersData,
            currentPage: this.currentPage,
            pageSize: 5,
            colspan: 4,
            emptyMessage: "No recent orders found.",
            renderRow: (order, index) => {
                let badgeClass = 'bg-secondary';
                if (order.status === 'PENDING') badgeClass = 'bg-warning text-dark';
                else if (order.status === 'APPROVED' || order.status === 'SHIPPED' || order.status === 'COMPLETED' || order.status === 'DELIVERED') badgeClass = 'bg-success';
                else if (order.status === 'CANCELLED') badgeClass = 'bg-danger';

                const dateStr = order.date ? new Date(order.date).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit' }) : 'Unknown';

                return `
                <tr>
                    <td class="ps-4 fw-semibold text-night-blue">${order.agencyName || 'Unknown Agency'}</td>
                    <td>
                        <div class="d-flex align-items-center">
                            <div class="bg-light rounded-circle p-2 me-2 text-primary">
                                <i class="bi bi-box-seam"></i>
                            </div>
                            <div>
                                <p class="mb-0 fw-semibold">${order.productName || 'Unknown Product'}</p>
                                <small class="text-muted">Qty: ${order.quantity}</small>
                            </div>
                        </div>
                    </td>
                    <td class="text-muted">${dateStr}</td>
                    <td><span class="badge ${badgeClass} rounded-pill px-3 py-2">${order.status}</span></td>
                </tr>
                `;
            }
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    if(window.location.pathname.includes('admin-dashboard') || document.getElementById('totalAgencies')) {
        window.adminDashboardManager.init();
        // Silent background sync (modern SWR auto-polling pattern without full page refresh)
        setInterval(() => {
            if (document.querySelector('.modal.show, .offcanvas.show')) return;
            window.adminDashboardManager.loadDashboardData();
        }, 15000);
    }
});
