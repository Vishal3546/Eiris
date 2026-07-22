import os

html_files = [
    'admin-dashboard.html',
    'admin-agencies.html',
    'admin-purchase.html',
    'admin-products.html',
    'agency-dashboard.html',
    'agency-clients.html',
    'agency-purchase.html',
    'agency-sales.html',
    'agency-stocks.html'
]

frontend_dir = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend'

modal_html = """
    <!-- Logout Confirm Modal -->
    <div class="modal fade" id="logoutConfirmModal" tabindex="-1" aria-labelledby="logoutConfirmModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content glass-modal-content">
                <div class="modal-body text-center p-5">
                    <div class="glass-icon-container">
                        <i class="bi bi-box-arrow-right"></i>
                    </div>
                    <h4 class="mb-3 fw-bold">Logout?</h4>
                    <p class="text-white-50 mb-4">Are you sure you want to log out of your account?</p>
                    <div class="d-flex justify-content-center gap-3">
                        <button type="button" class="btn btn-glass-cancel rounded-3 px-4 py-2 fw-semibold w-50" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-glass-delete rounded-3 px-4 py-2 fw-semibold w-50" onclick="performLogout()">Logout</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function performLogout() {
            if (typeof authService !== 'undefined') {
                authService.logout();
            } else {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('user');
            }
            window.location.href = 'index.html';
        }
    </script>
"""

for f in html_files:
    file_path = os.path.join(frontend_dir, f)
    if not os.path.exists(file_path):
        print(f'File not found: {file_path}')
        continue
    
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()
    
    # Replace the link
    old_link_1 = '<a href="index.html" class="btn btn-outline-light w-100 d-flex align-items-center justify-content-center border-opacity-25">'
    new_link = '<a href="#" data-bs-toggle="modal" data-bs-target="#logoutConfirmModal" class="btn btn-outline-light w-100 d-flex align-items-center justify-content-center border-opacity-25">'
    
    if old_link_1 in content:
        content = content.replace(old_link_1, new_link)
    else:
        print(f'Warning: logout link not found exactly in {f}')

    # Inject modal before bootstrap JS or </body>
    if 'id="logoutConfirmModal"' not in content:
        target_injection = '<!-- Bootstrap JS Bundle'
        if target_injection in content:
            content = content.replace(target_injection, modal_html + '\n    ' + target_injection)
        else:
            target_injection_2 = '</body>'
            if target_injection_2 in content:
                content = content.replace(target_injection_2, modal_html + '\n' + target_injection_2)
            else:
                print(f'Could not inject modal in {f}')

    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)
        
print('Replacement complete.')
