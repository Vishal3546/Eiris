// apiService.js

// Ensure axios is loaded in your HTML before this script: 
// <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>

const API_BASE_URL = 'http://localhost:8080/api';

const apiService = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to attach access token
apiService.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle token refresh on 401
apiService.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // Exclude auth endpoints from the 401 refresh logic
        const isAuthEndpoint = originalRequest.url.includes('/auth/login') || originalRequest.url.includes('/auth/refresh');

        // If error is 401 and we haven't retried yet, and it's not a login/refresh request
        if (error.response && error.response.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
            console.warn("Got 401 Unauthorized, attempting to refresh token...", error.config.url);
            originalRequest._retry = true;

            const refreshToken = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken');
            if (refreshToken) {
                try {
                    // Attempt to refresh token
                    const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
                        refreshToken: refreshToken
                    });

                    // Store new tokens
                    const { accessToken, refreshToken: newRefreshToken, user } = response.data;
                    localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken', accessToken);
                    localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken', newRefreshToken);
                    localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user', JSON.stringify(user));

                    console.log("Token refreshed successfully.");
                    // Update header and retry original request
                    originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
                    return apiService(originalRequest);
                } catch (refreshError) {
                    console.error("Token refresh failed. Clearing localStorage.", refreshError);
                    // Refresh failed, user needs to login again
                    localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
                    localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken');
                    localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user');
                    // Optional: Redirect to login page
                    // window.location.href = '/login.html';
                    return Promise.reject(refreshError);
                }
            } else {
                console.warn("No refresh token available. Clearing localStorage.");
                // No refresh token available, clear storage just in case
                localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
                localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user');
                // window.location.href = '/login.html';
            }
        }

        return Promise.reject(error);
    }
);

// Auth helper functions
const authService = {
    async login(email, password) {
        const response = await apiService.post('/auth/login', { email, password });
        if (response.data.accessToken) {
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken', response.data.accessToken);
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken', response.data.refreshToken);
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user', JSON.stringify(response.data.user));
        }
        return response.data;
    },

    async register(email, password) {
        const response = await apiService.post('/auth/register', { email, password });
        if (response.data.accessToken) {
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken', response.data.accessToken);
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken', response.data.refreshToken);
            localStorage.setItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user', JSON.stringify(response.data.user));
        }
        return response.data;
    },

    logout() {
        localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_accessToken' : 'agency_accessToken');
        localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_refreshToken' : 'agency_refreshToken');
        localStorage.removeItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user');
        // window.location.href = '/login.html';
    },
    
    getCurrentUser() {
        const userStr = localStorage.getItem(window.location.pathname.includes('admin-') ? 'admin_user' : 'agency_user');
        if (userStr) return JSON.parse(userStr);
        return null;
    },
    
    async forgotPassword(email) {
        const response = await apiService.post('/auth/forgot-password', { email });
        return response.data;
    }
};

const adminAgencyService = {
    async getAllAgencies() {
        const response = await apiService.get('/admin/agencies');
        return response.data;
    },
    async createAgency(agencyData) {
        const response = await apiService.post('/admin/agencies', agencyData);
        return response.data;
    },
    async updateAgency(id, agencyData) {
        const response = await apiService.put(`/admin/agencies/${id}`, agencyData);
        return response.data;
    },
    async deleteAgency(id) {
        await apiService.delete(`/admin/agencies/${id}`);
    }
};

window.apiService = apiService;
window.authService = authService;
window.adminAgencyService = adminAgencyService;
