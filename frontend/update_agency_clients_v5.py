import os, re

file_path = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend\agency-clients.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

new_js = '''// Initial Mock Data and Backend Integration
            const API_BASE_URL = 'http://localhost:8080/api/agency/clients';
            
            function getAuthHeaders() {
                const token = localStorage.getItem('accessToken');
                return {
                    'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : ''
                };
            }

            let clientsList = [];

            async function fetchClients() {
                try {
                    const response = await fetch(API_BASE_URL, { headers: getAuthHeaders() });
                    if (!response.ok) throw new Error("Failed to fetch clients");
                    const data = await response.json();
                    
                    // Map backend properties to frontend properties format if necessary
                    clientsList = data.map(c => ({
                        id: c.id,
                        name: c.clientName,
                        company: c.clientCompany || '-',
                        email: c.clientEmail,
                        phone: c.clientContact,
                        state: c.clientState || '-',
                        place: c.clientCity || '-',
                        sales: c.sales || 0,
                        orders: c.orders || 0,
                        added: new Date(c.createdAt).toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' })
                    }));
                    renderTopClients();
                    renderClientsTable();
                } catch (e) {
                    console.error("Error fetching clients", e);
                }
            }

            function getInitials(name) {
                if(!name) return 'C';
                let parts = name.trim().split(' ');
                if (parts.length >= 2) {
                    return (parts[0][0] + parts[1][0]).toUpperCase();
                } else if (parts.length === 1 && parts[0].length >= 2) {
                    return parts[0].substring(0, 2).toUpperCase();
                }
                return name.substring(0,1).toUpperCase();
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
                            <td>
                                <div class="mb-1">${client.place}</div>
                                <div class="text-muted small">${client.state}</div>
                            </td>
                            <td class="fw-bold text-success">₹${client.sales.toLocaleString('en-IN')}</td>
                            <td class="pe-4 text-end">
                                <div class="d-flex justify-content-end gap-2 flex-nowrap">
                                    <button class="btn btn-sm btn-outline-primary rounded-circle" title="Edit" onclick="openEditClient('${client.id}')"><i class="bi bi-pencil-fill"></i></button>
                                    <button class="btn btn-sm btn-outline-danger rounded-circle" title="Delete" onclick="confirmDeleteClient('${client.id}')"><i class="bi bi-trash-fill"></i></button>
                                </div>
                            </td>
                        </tr>`;
                    });
                }
                $('#clientsTableBody').html(html);
            }

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
                    
                    let num = client.phone ? client.phone.replace('+91 ', '') : '';
                    $('#editClientContact').val(num);
                    
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
                        
                        if(client.state) {
                            $('#editClientState').val(client.state).trigger('change');
                        }
                        
                        if(client.place) {
                            setTimeout(() => {
                                $('#editClientCity').val(client.place).trigger('change');
                            }, 50);
                        }
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
                
                let id = $('#editClientId').val();
                let clientIndex = clientsList.findIndex(c => c.id == id);
                if(clientIndex !== -1) {
                    clientsList[clientIndex].name = $('#editClientName').val();
                    clientsList[clientIndex].company = $('#editClientCompany').val();
                    clientsList[clientIndex].email = $('#editClientEmail').val();
                    clientsList[clientIndex].phone = '+91 ' + $('#editClientContact').val();
                    clientsList[clientIndex].state = $('#editClientState').val() || '-';
                    clientsList[clientIndex].place = $('#editClientCity').val() || '-';
                    
                    renderTopClients();
                    renderClientsTable();
                    $('#editClientModal').modal('hide');
                }
            });

            window.confirmDeleteClient = function(id) {
                showCustomConfirm().then(confirmed => {
                    if(confirmed) {
                        clientsList = clientsList.filter(c => c.id != id);
                        renderTopClients();
                        renderClientsTable();
                    }
                });
            };

            fetchClients();
            
            $('#clientSearch').on('input', function() {
                let val = $(this).val().toLowerCase();
                let filtered = clientsList.filter(c => 
                    c.name.toLowerCase().includes(val) || 
                    c.company.toLowerCase().includes(val)
                );
                renderClientsTable(filtered);
            });
            
            $('#saveClientBtn').on('click', async function() {
                const form = document.getElementById('addClientForm');
                let errorMsg = $('#formErrorMsg');
                
                if (!form.checkValidity()) {
                    errorMsg.removeClass('d-none').text('Please fill all required fields before submitting.');
                    return;
                }
                errorMsg.addClass('d-none');
                
                let btn = $(this);
                let originalText = btn.html();
                btn.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...');
                btn.prop('disabled', true);
                
                const clientData = {
                    clientName: $('#clientName').val(),
                    clientCompany: $('#clientCompany').val(),
                    clientEmail: $('#clientEmail').val(),
                    clientContact: '+91 ' + $('#clientContact').val(),
                    clientState: $('#clientState').val(),
                    clientCity: $('#clientCity').val()
                };
                
                try {
                    const response = await fetch(API_BASE_URL, {
                        method: 'POST',
                        headers: getAuthHeaders(),
                        body: JSON.stringify(clientData)
                    });
                    
                    if (!response.ok) throw new Error("Failed to save client");
                    
                    await fetchClients();
                    
                    $('#clientSearch').val('');
                    $('#addClientModal').modal('hide');
                    form.reset();
                    $('#clientState').val(null).trigger('change');
                } catch (e) {
                    console.error("Error saving client", e);
                    errorMsg.removeClass('d-none').text('Error saving client. Please try again.');
                } finally {
                    btn.html(originalText);
                    btn.prop('disabled', false);
                }
            });

            $('#addClientModal').on('hidden.bs.modal', function () {
                document.getElementById('addClientForm').reset();
                $('#formErrorMsg').addClass('d-none');
                $('#clientState').val(null).trigger('change');
            });

            $('#editClientModal').on('hidden.bs.modal', function () {
                document.getElementById('editClientForm').reset();
                $('#editFormErrorMsg').addClass('d-none');
                $('#editClientState').val(null).trigger('change');
            });
'''

# Use regex to replace everything from // Initial Mock Data to the end of the script, before </body>
content = re.sub(r'// Initial Mock Data[\s\S]*?(?=\s*</script>\s*</body>)', new_js, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("HTML updated successfully.")
