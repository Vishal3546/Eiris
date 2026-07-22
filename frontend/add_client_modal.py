import os

file_path = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend\agency-clients.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add Select2 CSS and styles
head_replacement = '''    <!-- Select2 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .select2-container--default .select2-selection--single {
            height: 38px;
            border: 1px solid #dee2e6;
            border-radius: 0.375rem;
            display: flex;
            align-items: center;
        }
        .select2-container--default .select2-selection--single .select2-selection__arrow {
            height: 36px;
        }
        .select2-container--default .select2-selection--single .select2-selection__rendered {
            color: #495057;
            padding-left: 12px;
        }
    </style>
</head>'''

if 'select2.min.css' not in content:
    content = content.replace('</head>', head_replacement)

# 2. Add data-bs-toggle to the "Add New Client" button
btn_old = '''<button class="btn btn-primary-custom rounded-pill px-4">
                        <i class="bi bi-plus-lg me-2"></i> Add New Client
                    </button>'''
btn_new = '''<button class="btn btn-primary-custom rounded-pill px-4" data-bs-toggle="modal" data-bs-target="#addClientModal">
                        <i class="bi bi-plus-lg me-2"></i> Add New Client
                    </button>'''

if btn_old in content:
    content = content.replace(btn_old, btn_new)

# 3. Add Add Client Modal
modal_html = '''
    <!-- Add Client Modal -->
    <div class="modal fade" id="addClientModal" tabindex="-1" aria-labelledby="addClientModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content rounded-4 shadow-lg" style="border: 3px solid var(--eiris-night-blue) !important;">
                <div class="modal-header bg-night-blue text-white rounded-top-4 border-0">
                    <h5 class="modal-title" id="addClientModalLabel">Add New Client</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                        aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <form id="addClientForm">
                        <div id="formErrorMsg" class="alert alert-danger d-none small"></div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Farm/Company Name</label>
                            <input type="text" id="clientCompany" class="form-control" placeholder="Enter farm or company name" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Client Name</label>
                            <input type="text" id="clientName" class="form-control" placeholder="Enter client name" required>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label text-muted fw-bold">State</label>
                                <select id="clientState" class="form-select select2" required>
                                    <option value="" disabled selected>Select State</option>
                                </select>
                            </div>
                            <div class="col-md-6 mt-3 mt-md-0">
                                <label class="form-label text-muted fw-bold">City</label>
                                <select id="clientCity" class="form-select select2" required>
                                    <option value="" disabled selected>Select City</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted fw-bold">Contact Number</label>
                            <div class="input-group">
                                <span class="input-group-text">+91</span>
                                <input type="tel" id="clientContact" class="form-control" placeholder="XXXX XXXXX" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" id="saveClientBtn" class="btn btn-secondary-custom rounded-pill px-4">Save Client</button>
                </div>
            </div>
        </div>
    </div>
'''

if 'id="addClientModal"' not in content:
    content = content.replace('    <!-- Logout Confirm Modal -->', modal_html + '\n    <!-- Logout Confirm Modal -->')

# 4. Add JS logic
js_html = '''
    <!-- jQuery and Select2 JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script src="js/locations.js"></script>
    
    <script>
        $(document).ready(function() {
            // Initialize Select2
            $('#clientState, #clientCity').select2({
                theme: 'classic',
                width: '100%',
                dropdownParent: $('#addClientModal')
            });

            // Populate States
            if (typeof indiaLocations !== 'undefined') {
                const states = Object.keys(indiaLocations).sort();
                states.forEach(state => {
                    $('#clientState').append(new Option(state, state));
                });
            }

            // Populate Cities on State change
            $('#clientState').on('change', function() {
                const state = $(this).val();
                const citySelect = $('#clientCity');
                citySelect.empty().append(new Option('Select City', '', true, true));
                
                if(state && typeof indiaLocations !== 'undefined' && indiaLocations[state]) {
                    const cities = indiaLocations[state].sort();
                    cities.forEach(city => {
                        citySelect.append(new Option(city, city));
                    });
                }
                citySelect.trigger('change');
            });
        });
    </script>
'''

if 'select2.min.js' not in content:
    content = content.replace('</body>', js_html + '\n</body>')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Update successful.')
