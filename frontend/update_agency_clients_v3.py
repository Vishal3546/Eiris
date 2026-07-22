import os, re

file_path = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend\agency-clients.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove Status from Header
content = content.replace('<th>Status</th>', '')

# 2. Add Email field to addClientForm
add_email_html = '''<div class="mb-3">
                            <label class="form-label text-muted fw-bold">Email Address</label>
                            <input type="email" id="clientEmail" class="form-control" placeholder="Enter email address" required>
                        </div>
                        <div class="mb-3">'''
content = content.replace('<div class="mb-3">\n                            <label class="form-label text-muted fw-bold">Farm/Company Name</label>', add_email_html.replace('<div class="mb-3">', '<div class="mb-3">\n                            <label class="form-label text-muted fw-bold">Farm/Company Name</label>', 1))
# Actually, let's just use string replace on Farm/Company Name field
add_email_html = '''<div class="mb-3">
                            <label class="form-label text-muted fw-bold">Email Address</label>
                            <input type="email" id="clientEmail" class="form-control" placeholder="Enter email address" required>
                        </div>
                        <div class="row mb-3">'''
content = content.replace('<div class="row mb-3">', add_email_html)


# 3. Add Edit Client Modal and Confirm Modal HTML
modals_html = '''
    <!-- Edit Client Modal -->
    <div class="modal fade" id="editClientModal" tabindex="-1" aria-labelledby="editClientModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content rounded-4 shadow-lg" style="border: 3px solid var(--eiris-night-blue) !important;">
                <div class="modal-header bg-night-blue text-white rounded-top-4 border-0">
                    <h5 class="modal-title" id="editClientModalLabel">Edit Client</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                        aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <form id="editClientForm">
                        <input type="hidden" id="editClientId">
                        <div id="editFormErrorMsg" class="alert alert-danger d-none small"></div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Farm/Company Name</label>
                            <input type="text" id="editClientCompany" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Client Name</label>
                            <input type="text" id="editClientName" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Email Address</label>
                            <input type="email" id="editClientEmail" class="form-control" required>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label text-muted fw-bold">State</label>
                                <select id="editClientState" class="form-select select2" required>
                                    <option value="" disabled selected>Select State</option>
                                </select>
                            </div>
                            <div class="col-md-6 mt-3 mt-md-0">
                                <label class="form-label text-muted fw-bold">City</label>
                                <select id="editClientCity" class="form-select select2" required>
                                    <option value="" disabled selected>Select City</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Contact Number</label>
                            <div class="input-group">
                                <span class="input-group-text">+91</span>
                                <input type="tel" id="editClientContact" class="form-control" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" id="updateClientBtn" class="btn btn-secondary-custom rounded-pill px-4">Update Client</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Custom Glassmorphism Confirm Modal -->
    <div class="modal fade" id="glassConfirmModal" tabindex="-1" aria-labelledby="glassConfirmModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content glass-modal-content">
                <div class="modal-body text-center p-5">
                    <div class="glass-icon-container">
                        <i class="bi bi-trash-fill"></i>
                    </div>
                    <h4 class="mb-3 fw-bold" id="glassConfirmTitle">Delete Client?</h4>
                    <p class="text-white-50 mb-4" id="glassConfirmMessage">Are you sure you want to delete this client?</p>
                    <div class="d-flex justify-content-center gap-3">
                        <button type="button" class="btn btn-glass-cancel rounded-3 px-4 py-2 fw-semibold w-50" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-glass-delete rounded-3 px-4 py-2 fw-semibold w-50" id="glassConfirmBtn">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
'''
if 'id="editClientModal"' not in content:
    content = content.replace('    <!-- Logout Confirm Modal -->', modals_html + '\n    <!-- Logout Confirm Modal -->')

# 4. Replace JS logic block entirely to fix the rendering and state
js_to_replace = r'// Initial Mock Data[\s\S]*?}\);\s*</script>'
new_js = '''// Initial Mock Data
            let clientsList = [
                { id: 1, name: "John Doe", added: "Jan 12, 2024", company: "Tech Solutions Inc.", email: "john@techsolutions.com", phone: "+91 98765 43210", place: "New York", sales: 45000, orders: 12 },
                { id: 2, name: "Alice Smith", added: "Feb 05, 2024", company: "Global Logistics", email: "alice@globallogistics.com", phone: "+91 98765 43211", place: "London", sales: 38500, orders: 8 },
                { id: 3, name: "Robert Johnson", added: "Mar 18, 2024", company: "Prime Enterprises", email: "robert@primeent.com", phone: "+91 98765 43212", place: "Sydney", sales: 29200, orders: 5 }
            ];

            function getInitials(name) {
                let parts = name.trim().split(' ');
                if (parts.length >= 2) {
                    return (parts[0][0] + parts[1][0]).toUpperCase();
                } else if (parts.length === 1 && parts[0].length >= 2) {
                    return parts[0].substring(0, 2).toUpperCase();
                }
                return 'C';
            }

            function getAvatarClass(index) {
                const classes = [
                    { bg: 'bg-info bg-opacity-10', text: 'text-info' },
                    { bg: 'bg-success bg-opacity-10', text: 'text-mint' },
                    { bg: 'bg-warning bg-opacity-10', text: 'text-warning' }
                ];
                return classes[index % classes.length];
            }

            function renderTopClients() {
                let sortedClients = [...clientsList].sort((a, b) => b.sales - a.sales).slice(0, 3);
                let html = '';
                sortedClients.forEach((client, index) => {
                    let avatar = getAvatarClass(index);
                    let initials = getInitials(client.name);
                    html += `
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm rounded-4 h-100 glass-agency-panel animate-fade-in-up" style="animation-delay: ${index * 0.1}s">
                            <div class="card-body p-4 text-center">
                                <div class="${avatar.bg} rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                                    <span class="fs-4 fw-bold ${avatar.text}">${initials}</span>
                                </div>
                                <h5 class="fw-bold mb-1">${client.name}</h5>
                                <p class="text-muted small mb-3">${client.company}</p>
                                <div class="d-flex justify-content-between text-start border-top pt-3">
                                    <div>
                                        <p class="text-muted small mb-0">Total Sales</p>
                                        <h6 class="fw-bold mb-0">₹${client.sales.toLocaleString('en-IN')}</h6>
                                    </div>
                                    <div class="text-end">
                                        <p class="text-muted small mb-0">Orders</p>
                                        <h6 class="fw-bold mb-0">${client.orders}</h6>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>`;
                });
                $('#topClientsContainer').html(html);
            }

            function renderClientsTable(data = clientsList) {
                let html = '';
                if(data.length === 0) {
                    html = '<tr><td colspan="6" class="text-center py-4 text-muted">No clients found</td></tr>';
                } else {
                    data.forEach(client => {
                        html += `
                        <tr>
                            <td class="ps-4">
                                <div>
                                    <h6 class="mb-0 fw-bold">${client.name}</h6>
                                    <small class="text-muted">Added: ${client.added}</small>
                                </div>
                            </td>
                            <td>${client.company}</td>
                            <td>
                                <div class="mb-1">${client.email}</div>
                                <div class="text-muted small"><i class="bi bi-telephone-fill me-1"></i>${client.phone}</div>
                            </td>
                            <td>${client.place}</td>
                            <td class="fw-bold text-success">₹${client.sales.toLocaleString('en-IN')}</td>
                            <td class="pe-4 text-end">
                                <div class="d-flex justify-content-end gap-2 flex-nowrap">
                                    <button class="btn btn-sm btn-outline-primary rounded-circle" title="Edit" onclick="openEditClient(${client.id})"><i class="bi bi-pencil-fill"></i></button>
                                    <button class="btn btn-sm btn-outline-danger rounded-circle" title="Delete" onclick="confirmDeleteClient(${client.id})"><i class="bi bi-trash-fill"></i></button>
                                </div>
                            </td>
                        </tr>`;
                    });
                }
                $('#clientsTableBody').html(html);
            }

            // Custom Confirm Dialog Promise
            function showCustomConfirm() {
                return new Promise((resolve) => {
                    const modalEl = document.getElementById('glassConfirmModal');
                    const modal = new bootstrap.Modal(modalEl);
                    const confirmBtn = document.getElementById('glassConfirmBtn');
                    
                    const handleConfirm = () => {
                        modal.hide();
                        confirmBtn.removeEventListener('click', handleConfirm);
                        resolve(true);
                    };
                    
                    const handleCancel = () => {
                        confirmBtn.removeEventListener('click', handleConfirm);
                        resolve(false);
                    };

                    confirmBtn.addEventListener('click', handleConfirm);
                    modalEl.addEventListener('hidden.bs.modal', handleCancel, { once: true });
                    
                    modal.show();
                });
            }

            window.openEditClient = function(id) {
                let client = clientsList.find(c => c.id === id);
                if(client) {
                    $('#editClientId').val(client.id);
                    $('#editClientName').val(client.name);
                    $('#editClientCompany').val(client.company);
                    $('#editClientEmail').val(client.email);
                    
                    // Contact
                    let num = client.phone.replace('+91 ', '');
                    $('#editClientContact').val(num);
                    
                    // State and City (Mock logic, since we don't store state separately, we'll try to guess or leave blank)
                    // We will initialize select2
                    $('#editClientState, #editClientCity').select2({
                        theme: 'classic',
                        width: '100%',
                        dropdownParent: $('#editClientModal')
                    });
                    
                    if (typeof indiaLocations !== 'undefined') {
                        $('#editClientState').empty().append(new Option('Select State', '', true, true));
                        Object.keys(indiaLocations).sort().forEach(state => {
                            $('#editClientState').append(new Option(state, state));
                        });
                        
                        // We don't have State, only Place (city) in our mock array, so we'll leave State blank unless we can find it
                        // For a real app, state is saved in DB. Here we just let user reselect.
                    }
                    
                    let modal = new bootstrap.Modal(document.getElementById('editClientModal'));
                    modal.show();
                }
            };

            $('#editClientState').on('change', function() {
                const state = $(this).val();
                const citySelect = $('#editClientCity');
                citySelect.empty().append(new Option('Select City', '', true, true));
                
                if(state && typeof indiaLocations !== 'undefined' && indiaLocations[state]) {
                    const cities = indiaLocations[state].sort();
                    cities.forEach(city => {
                        citySelect.append(new Option(city, city));
                    });
                }
                citySelect.trigger('change');
            });

            $('#updateClientBtn').on('click', function() {
                const form = document.getElementById('editClientForm');
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }
                
                let id = parseInt($('#editClientId').val());
                let clientIndex = clientsList.findIndex(c => c.id === id);
                if(clientIndex !== -1) {
                    clientsList[clientIndex].name = $('#editClientName').val();
                    clientsList[clientIndex].company = $('#editClientCompany').val();
                    clientsList[clientIndex].email = $('#editClientEmail').val();
                    clientsList[clientIndex].phone = '+91 ' + $('#editClientContact').val();
                    let city = $('#editClientCity').val();
                    if(city) {
                        clientsList[clientIndex].place = city;
                    }
                    
                    renderTopClients();
                    renderClientsTable();
                    $('#editClientModal').modal('hide');
                }
            });

            window.confirmDeleteClient = function(id) {
                showCustomConfirm().then(confirmed => {
                    if(confirmed) {
                        clientsList = clientsList.filter(c => c.id !== id);
                        renderTopClients();
                        renderClientsTable();
                    }
                });
            };

            renderTopClients();
            renderClientsTable();
            
            $('#clientSearch').on('input', function() {
                let val = $(this).val().toLowerCase();
                let filtered = clientsList.filter(c => 
                    c.name.toLowerCase().includes(val) || 
                    c.company.toLowerCase().includes(val)
                );
                renderClientsTable(filtered);
            });
            
            $('#saveClientBtn').on('click', function() {
                const form = document.getElementById('addClientForm');
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }
                
                let name = $('#clientName').val();
                let company = $('#clientCompany').val();
                let email = $('#clientEmail').val();
                let contact = '+91 ' + $('#clientContact').val();
                let city = $('#clientCity').val();
                
                let newId = clientsList.length > 0 ? Math.max(...clientsList.map(c => c.id)) + 1 : 1;
                
                let newClient = {
                    id: newId,
                    name: name,
                    added: new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
                    company: company,
                    email: email,
                    phone: contact,
                    place: city || 'Unknown',
                    sales: 0,
                    orders: 0
                };
                
                clientsList.unshift(newClient); 
                renderTopClients(); 
                
                $('#clientSearch').val('');
                renderClientsTable();
                
                $('#addClientModal').modal('hide');
                form.reset();
                $('#clientState').val(null).trigger('change');
            });

        });
    </script>'''

content = re.sub(r'// Initial Mock Data[\s\S]*?\}\);\s*</script>', new_js, content)


with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Rewrite complete')
