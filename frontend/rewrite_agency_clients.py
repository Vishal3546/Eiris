import os, re

file_path = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend\agency-clients.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Replace Top Clients container
top_clients_pattern = r'<div class="row g-4 mb-4">[\s\S]*?(?=<!-- Client List Table -->)'
top_clients_replacement = '''<div class="row g-4 mb-4" id="topClientsContainer">
                    <!-- Rendered by JS -->
                </div>
                
                '''
content = re.sub(top_clients_pattern, top_clients_replacement, content)

# 2. Replace Search Box
search_box_pattern = r'<div class="input-group" style="width: 250px;">[\s\S]*?</div>'
search_box_replacement = '''<div class="input-group shadow-sm border-0 rounded-pill overflow-hidden bg-white" style="width: 320px; border: 1px solid var(--eiris-sand-tan) !important;">
                            <span class="input-group-text bg-white border-0 text-primary pe-1"><i class="bi bi-search"></i></span>
                            <input type="text" id="clientSearch" class="form-control border-0 shadow-none bg-white" placeholder="Search name or farm/company...">
                        </div>'''
content = re.sub(search_box_pattern, search_box_replacement, content)

# 3. Replace Table Body
table_body_pattern = r'<tbody>[\s\S]*?</tbody>'
table_body_replacement = '''<tbody id="clientsTableBody">
                                    <!-- Rendered by JS -->
                                </tbody>'''
content = re.sub(table_body_pattern, table_body_replacement, content)

# 4. Add JS logic inside the script tag at the bottom
script_to_append = '''
            // Initial Mock Data
            let clientsList = [
                { name: "John Doe", added: "Jan 12, 2024", company: "Tech Solutions Inc.", email: "john@techsolutions.com", phone: "+91 98765 43210", place: "New York", sales: 45000, orders: 12, status: "Active" },
                { name: "Alice Smith", added: "Feb 05, 2024", company: "Global Logistics", email: "alice@globallogistics.com", phone: "+91 98765 43211", place: "London", sales: 38500, orders: 8, status: "Active" },
                { name: "Robert Johnson", added: "Mar 18, 2024", company: "Prime Enterprises", email: "robert@primeent.com", phone: "+91 98765 43212", place: "Sydney", sales: 29200, orders: 5, status: "Inactive" }
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
                    html = '<tr><td colspan="7" class="text-center py-4 text-muted">No clients found</td></tr>';
                } else {
                    data.forEach(client => {
                        let statusBadge = client.status === 'Active' 
                            ? '<span class="badge bg-success bg-opacity-10 text-success px-2 py-1 rounded-pill">Active</span>'
                            : '<span class="badge bg-warning bg-opacity-10 text-warning px-2 py-1 rounded-pill">Inactive</span>';
                            
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
                            <td>${statusBadge}</td>
                            <td class="pe-4">
                                <div class="d-flex justify-content-end gap-2">
                                    <button class="btn btn-sm btn-light rounded-circle text-primary" title="Edit"><i class="bi bi-pencil"></i></button>
                                    <button class="btn btn-sm btn-danger rounded-circle text-white" title="Delete"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                        </tr>`;
                    });
                }
                $('#clientsTableBody').html(html);
            }

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
                let contact = '+91 ' + $('#clientContact').val();
                let city = $('#clientCity').val();
                
                let newClient = {
                    name: name,
                    added: new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
                    company: company,
                    email: '-',
                    phone: contact,
                    place: city || '-',
                    sales: 0,
                    orders: 0,
                    status: "Active"
                };
                
                clientsList.unshift(newClient); 
                renderTopClients(); 
                
                $('#clientSearch').val('');
                renderClientsTable();
                
                $('#addClientModal').modal('hide');
                form.reset();
                $('#clientState').val(null).trigger('change');
            });
'''

old_trigger_code = "citySelect.trigger('change');\n            });"
if old_trigger_code in content:
    content = content.replace(old_trigger_code, old_trigger_code + '\n' + script_to_append)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Rewrite complete')
