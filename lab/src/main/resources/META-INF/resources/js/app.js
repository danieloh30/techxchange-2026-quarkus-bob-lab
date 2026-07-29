// Incident Management UI JavaScript

// Global variables for sorting and filtering
let currentSortColumn = 'id';
let currentSortDirection = 'asc';
let incidentsData = [];
let currentFilterText = '';
let currentFilterField = 'all';
let lastUpdatedIncidentId = null;

document.addEventListener('DOMContentLoaded', function() {
    loadAllIncidents();
    setupEventListeners();
    setupSorting();
});

function loadAllIncidents() {
    fetch('/incidents')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(incidents => {
            incidentsData = incidents;
            sortIncidents();
            populateIncidentTable(incidentsData);
        })
        .catch(error => {
            console.error('Error fetching incidents:', error);
            displayError('Failed to load incident data. Please try again later.');
        });
}

function setupSorting() {
    const sortableHeaders = document.querySelectorAll('.sortable');

    sortableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const column = this.getAttribute('data-sort');

            if (column === currentSortColumn) {
                currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                currentSortColumn = column;
                currentSortDirection = 'asc';
            }

            updateSortHeaders();
            sortIncidents();
            populateIncidentTable(incidentsData);
        });
    });
}

function updateSortHeaders() {
    document.querySelectorAll('.sortable').forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
    });

    const currentHeader = document.querySelector(`.sortable[data-sort="${currentSortColumn}"]`);
    if (currentHeader) {
        currentHeader.classList.add(currentSortDirection === 'asc' ? 'sort-asc' : 'sort-desc');
    }
}

function sortIncidents() {
    incidentsData.sort((a, b) => {
        let valueA, valueB;

        if (currentSortColumn === 'status') {
            valueA = getStatusDisplay(a.status);
            valueB = getStatusDisplay(b.status);
        } else {
            valueA = a[currentSortColumn];
            valueB = b[currentSortColumn];
        }

        if (currentSortColumn === 'id' || currentSortColumn === 'priority') {
            valueA = Number(valueA) || 0;
            valueB = Number(valueB) || 0;
        }

        if (valueA < valueB) {
            return currentSortDirection === 'asc' ? -1 : 1;
        }
        if (valueA > valueB) {
            return currentSortDirection === 'asc' ? 1 : -1;
        }
        return 0;
    });
}

function filterIncidents() {
    if (!currentFilterText) {
        return incidentsData;
    }

    return incidentsData.filter(incident => {
        const filterText = currentFilterText.toLowerCase();

        if (currentFilterField !== 'all') {
            let fieldValue = incident[currentFilterField];

            if (currentFilterField === 'status') {
                fieldValue = getStatusDisplay(fieldValue);
            }

            return String(fieldValue).toLowerCase().includes(filterText);
        }

        return (
            String(incident.id).toLowerCase().includes(filterText) ||
            incident.system.toLowerCase().includes(filterText) ||
            incident.service.toLowerCase().includes(filterText) ||
            String(incident.priority).toLowerCase().includes(filterText) ||
            (incident.description && incident.description.toLowerCase().includes(filterText)) ||
            getStatusDisplay(incident.status).toLowerCase().includes(filterText)
        );
    });
}

function populateIncidentTable(incidents) {
    const tableBody = document.getElementById('incident-table-body');
    tableBody.innerHTML = '';

    const filteredIncidents = currentFilterText ? filterIncidents() : incidents;

    if (filteredIncidents.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7">No incidents match your filter criteria</td></tr>';
        return;
    }

    filteredIncidents.forEach(incident => {
        const row = document.createElement('tr');

        if (incident.id === lastUpdatedIncidentId) {
            row.classList.add('highlight-row');
            setTimeout(() => {
                lastUpdatedIncidentId = null;
            }, 3000);
        }

        const statusPillClass = getStatusPillClass(incident.status);
        const priorityLabel = 'P' + incident.priority;

        let actionCell = '';
        if (incident.status === 'OPEN' || incident.status === 'TRIAGING' || incident.status === 'IN_PROGRESS') {
            actionCell = `
                <td>
                    <form onsubmit="processReport(event, ${incident.id}, '${incident.status}')">
                        <input type="text" class="feedback-input" id="report-${incident.id}" placeholder="Enter report">
                        <button type="submit" class="return-button">Process</button>
                    </form>
                </td>`;
        } else {
            actionCell = `<td></td>`;
        }

        row.innerHTML = `
            <td>${incident.id}</td>
            <td>${incident.system}</td>
            <td>${incident.service}</td>
            <td><span class="priority-badge priority-${incident.priority}">${priorityLabel}</span></td>
            <td>${incident.description || 'N/A'}</td>
            <td><span class="status-pill ${statusPillClass}">${getStatusDisplay(incident.status)}</span></td>
            ${actionCell}
        `;

        tableBody.appendChild(row);
    });
}

function processReport(event, incidentId, status) {
    event.preventDefault();
    const report = document.getElementById(`report-${incidentId}`).value;
    const button = event.target.querySelector('button');

    button.disabled = true;
    button.classList.add('loading');
    const originalText = button.textContent;
    button.textContent = 'Processing...';

    const statusLabels = {
        'OPEN': 'open incident',
        'TRIAGING': 'triage',
        'IN_PROGRESS': 'investigation'
    };

    fetch(`/incident-management/process/${incidentId}?report=${encodeURIComponent(report)}`, { method: 'POST' })
    .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.text();
    })
    .then(data => {
        lastUpdatedIncidentId = incidentId;
        showNotification(`Incident #${incidentId} processed successfully from ${statusLabels[status]}`);
        loadAllIncidents();
    })
    .catch(error => {
        console.error(`Error processing ${statusLabels[status]}:`, error);
        displayError(`Failed to process ${statusLabels[status]}. Please try again.`);
        button.disabled = false;
        button.classList.remove('loading');
        button.textContent = originalText;
    });
}

function getStatusPillClass(status) {
    switch(status) {
        case 'OPEN':
            return 'status-pill-open';
        case 'TRIAGING':
            return 'status-pill-triaging';
        case 'IN_PROGRESS':
            return 'status-pill-in-progress';
        case 'RESOLVED':
            return 'status-pill-resolved';
        case 'ESCALATED':
            return 'status-pill-escalated';
        default:
            return '';
    }
}

function getStatusDisplay(status) {
    switch(status) {
        case 'OPEN':
            return 'Open';
        case 'TRIAGING':
            return 'Triaging';
        case 'IN_PROGRESS':
            return 'In Progress';
        case 'RESOLVED':
            return 'Resolved';
        case 'ESCALATED':
            return 'Escalated';
        default:
            return status;
    }
}

function setupEventListeners() {
    const refreshButton = document.getElementById('refresh-button');
    if (refreshButton) {
        refreshButton.addEventListener('click', loadAllIncidents);
    }

    const filterInput = document.getElementById('incident-filter');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            currentFilterText = this.value;
            populateIncidentTable(incidentsData);
        });
    }

    const filterField = document.getElementById('filter-field');
    if (filterField) {
        filterField.addEventListener('change', function() {
            currentFilterField = this.value;
            populateIncidentTable(incidentsData);
        });
    }

    const clearFilterButton = document.getElementById('clear-filter');
    if (clearFilterButton) {
        clearFilterButton.addEventListener('click', function() {
            const filterInput = document.getElementById('incident-filter');
            const filterField = document.getElementById('filter-field');

            currentFilterText = '';
            currentFilterField = 'all';

            if (filterInput) filterInput.value = '';
            if (filterField) filterField.value = 'all';

            populateIncidentTable(incidentsData);
        });
    }
}

function displayError(message) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';

        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    } else {
        alert(message);
    }
}

function showNotification(message) {
    const notificationDiv = document.getElementById('notification');
    if (notificationDiv) {
        notificationDiv.textContent = message;
        notificationDiv.style.display = 'block';

        setTimeout(() => {
            notificationDiv.style.display = 'none';
        }, 3000);
    }
}
