// Incident Management UI JavaScript

// Global variables for sorting and filtering
let currentSortColumn = 'id';
let currentSortDirection = 'asc';
let incidentsData = []; // Store the incidents data globally for sorting
let currentFilterText = '';
let currentFilterField = 'all';
let lastUpdatedIncidentId = null; // Track the last updated incident for highlighting

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Load all incidents and populate the tables
    loadAllIncidents();

    // Add event listeners for form submissions
    setupEventListeners();

    // Set up sorting functionality
    setupSorting();
});

// Function to load all incidents from the API
function loadAllIncidents() {
    fetch('/incidents')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(incidents => {
            // Store incidents data globally for sorting
            incidentsData = incidents;

            // Sort the data if a sort is active
            sortIncidents();

            // Process the incidents data
            populateIncidentStatusTable(incidentsData);
        })
        .catch(error => {
            console.error('Error fetching incidents:', error);
            displayError('Failed to load incident data. Please try again later.');
        });
}

// Function to set up sorting functionality
function setupSorting() {
    const sortableHeaders = document.querySelectorAll('.sortable');

    sortableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const column = this.getAttribute('data-sort');

            // If clicking the same column, toggle direction
            if (column === currentSortColumn) {
                currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                // New column, default to ascending
                currentSortColumn = column;
                currentSortDirection = 'asc';
            }

            // Update header classes for visual indication
            updateSortHeaders();

            // Sort and redisplay data
            sortIncidents();
            populateIncidentStatusTable(incidentsData);
        });
    });
}

// Function to update sort header classes
function updateSortHeaders() {
    // Remove all sort classes
    document.querySelectorAll('.sortable').forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
    });

    // Add class to current sort column
    const currentHeader = document.querySelector(`.sortable[data-sort="${currentSortColumn}"]`);
    if (currentHeader) {
        currentHeader.classList.add(currentSortDirection === 'asc' ? 'sort-asc' : 'sort-desc');
    }
}

// Function to sort incidents based on current sort settings
function sortIncidents() {
    incidentsData.sort((a, b) => {
        let valueA, valueB;

        // Handle special case for status which needs to be displayed text
        if (currentSortColumn === 'status') {
            valueA = getStatusDisplay(a.status);
            valueB = getStatusDisplay(b.status);
        } else {
            valueA = a[currentSortColumn];
            valueB = b[currentSortColumn];
        }

        // Handle numeric values
        if (currentSortColumn === 'id') {
            valueA = Number(valueA) || 0;
            valueB = Number(valueB) || 0;
        }

        // Compare values based on direction
        if (valueA < valueB) {
            return currentSortDirection === 'asc' ? -1 : 1;
        }
        if (valueA > valueB) {
            return currentSortDirection === 'asc' ? 1 : -1;
        }
        return 0;
    });
}

// Function to filter incidents based on current filter settings
function filterIncidents() {
    if (!currentFilterText) {
        return incidentsData; // Return all incidents if no filter text
    }

    return incidentsData.filter(incident => {
        // Convert filter text to lowercase for case-insensitive comparison
        const filterText = currentFilterText.toLowerCase();

        // If filtering on a specific field
        if (currentFilterField !== 'all') {
            let fieldValue = incident[currentFilterField];

            // Handle special case for status which needs to be displayed text
            if (currentFilterField === 'status') {
                fieldValue = getStatusDisplay(fieldValue);
            }

            // Convert to string and check if it contains the filter text
            return String(fieldValue).toLowerCase().includes(filterText);
        }

        // If filtering across all fields
        return (
            String(incident.id).toLowerCase().includes(filterText) ||
            incident.system.toLowerCase().includes(filterText) ||
            incident.service.toLowerCase().includes(filterText) ||
            (incident.priority && incident.priority.toLowerCase().includes(filterText)) ||
            (incident.description && incident.description.toLowerCase().includes(filterText)) ||
            getStatusDisplay(incident.status).toLowerCase().includes(filterText)
        );
    });
}

// Function to populate the Incident Status table
function populateIncidentStatusTable(incidents) {
    const tableBody = document.getElementById('incident-status-table-body');
    tableBody.innerHTML = ''; // Clear existing rows

    // Apply filter if there's filter text
    const filteredIncidents = currentFilterText ? filterIncidents() : incidents;

    if (filteredIncidents.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7">No incidents match your filter criteria</td></tr>';
        return;
    }

    filteredIncidents.forEach(incident => {
        const row = document.createElement('tr');

        // Highlight the row if it was just updated
        if (incident.id === lastUpdatedIncidentId) {
            row.classList.add('highlight-row');
            // Clear the highlight after animation completes
            setTimeout(() => {
                lastUpdatedIncidentId = null;
            }, 3000);
        }

        // Get status pill class based on incident status
        const statusPillClass = getStatusPillClass(incident.status);

        // Build action cell based on status
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
            <td>${incident.priority || 'N/A'}</td>
            <td>${incident.description || 'N/A'}</td>
            <td><span class="status-pill ${statusPillClass}">${getStatusDisplay(incident.status)}</span></td>
            ${actionCell}
        `;

        tableBody.appendChild(row);
    });
}

// Function to process report and update an incident
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
        showNotification(`Incident successfully processed from ${statusLabels[status]}`);
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

// Helper function to get CSS class based on incident status
function getStatusClass(status) {
    switch(status) {
        case 'OPEN':
            return 'status-open';
        case 'TRIAGING':
            return 'status-triaging';
        case 'IN_PROGRESS':
            return 'status-in-progress';
        case 'RESOLVED':
            return 'status-resolved';
        case 'ESCALATED':
            return 'status-escalated';
        default:
            return '';
    }
}

// Helper function to get status pill class based on incident status
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

// Helper function to get display text for incident status
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

// Function to set up event listeners
function setupEventListeners() {
    // Add refresh button event listener
    const refreshButton = document.getElementById('refresh-button');
    if (refreshButton) {
        refreshButton.addEventListener('click', loadAllIncidents);
    }

    // Add filter input event listener
    const filterInput = document.getElementById('incident-filter');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            currentFilterText = this.value;
            populateIncidentStatusTable(incidentsData);
        });
    }

    // Add filter field select event listener
    const filterField = document.getElementById('filter-field');
    if (filterField) {
        filterField.addEventListener('change', function() {
            currentFilterField = this.value;
            populateIncidentStatusTable(incidentsData);
        });
    }

    // Add clear filter button event listener
    const clearFilterButton = document.getElementById('clear-filter');
    if (clearFilterButton) {
        clearFilterButton.addEventListener('click', function() {
            const filterInput = document.getElementById('incident-filter');
            const filterField = document.getElementById('filter-field');

            // Reset filter values
            currentFilterText = '';
            currentFilterField = 'all';

            // Reset UI elements
            if (filterInput) filterInput.value = '';
            if (filterField) filterField.value = 'all';

            // Refresh table
            populateIncidentStatusTable(incidentsData);
        });
    }
}

// Function to display error messages
function displayError(message) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';

        // Hide after 5 seconds
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    } else {
        alert(message);
    }
}

// Function to show notification messages
function showNotification(message) {
    const notificationDiv = document.getElementById('notification');
    if (notificationDiv) {
        notificationDiv.textContent = message;
        notificationDiv.style.display = 'block';

        // Hide after 3 seconds
        setTimeout(() => {
            notificationDiv.style.display = 'none';
        }, 3000);
    }
}
