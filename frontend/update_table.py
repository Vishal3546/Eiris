import os

file_path = r'c:\Users\DELL\Desktop\Eiris New\Eiris\frontend\agency-clients.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace Headers
content = content.replace('<th>Email</th>\n                                        <th>Phone</th>', '<th>Contact Info</th>\n                                        <th>Place</th>')

# Replace Row 1
content = content.replace('<td>john@techsolutions.com</td>\n                                        <td>+91 98765 43210</td>', 
'''<td>
                                            <div class="mb-1">john@techsolutions.com</div>
                                            <div class="text-muted small"><i class="bi bi-telephone-fill me-1"></i>+91 98765 43210</div>
                                        </td>
                                        <td>New York</td>''')

# Replace Row 2
content = content.replace('<td>alice@globallogistics.com</td>\n                                        <td>+91 98765 43211</td>',
'''<td>
                                            <div class="mb-1">alice@globallogistics.com</div>
                                            <div class="text-muted small"><i class="bi bi-telephone-fill me-1"></i>+91 98765 43211</div>
                                        </td>
                                        <td>London</td>''')

# Replace Row 3
content = content.replace('<td>robert@primeent.com</td>\n                                        <td>+91 98765 43212</td>',
'''<td>
                                            <div class="mb-1">robert@primeent.com</div>
                                            <div class="text-muted small"><i class="bi bi-telephone-fill me-1"></i>+91 98765 43212</div>
                                        </td>
                                        <td>Sydney</td>''')

# Replace Buttons for all rows
old_buttons = '''<td class="text-end pe-4">
                                            <button class="btn btn-sm btn-light rounded-circle me-1"><i class="bi bi-eye"></i></button>
                                            <button class="btn btn-sm btn-light rounded-circle"><i class="bi bi-pencil"></i></button>
                                        </td>'''

new_buttons = '''<td class="pe-4">
                                            <div class="d-flex justify-content-end gap-2">
                                                <button class="btn btn-sm btn-light rounded-circle"><i class="bi bi-eye"></i></button>
                                                <button class="btn btn-sm btn-light rounded-circle"><i class="bi bi-pencil"></i></button>
                                            </div>
                                        </td>'''

content = content.replace(old_buttons, new_buttons)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Update successful.')
