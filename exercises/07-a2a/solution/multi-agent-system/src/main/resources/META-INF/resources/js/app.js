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

    // Start polling for approvals (always active now with modal)
    startApprovalPolling();
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

            if (column === currentSortColumn) {
                currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                currentSortColumn = column;
                currentSortDirection = 'asc';
            }

            updateSortHeaders();
            sortIncidents();
            populateIncidentStatusTable(incidentsData);
        });
    });
}

// Function to update sort header classes
function updateSortHeaders() {
    document.querySelectorAll('.sortable').forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
    });

    const currentHeader = document.querySelector(`.sortable[data-sort="${currentSortColumn}"]`);
    if (currentHeader) {
        currentHeader.classList.add(currentSortDirection === 'asc' ? 'sort-asc' : 'sort-desc');
    }
}

// Function to sort incidents based on current sort settings
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

// Function to filter incidents based on current filter settings
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

// Function to populate the Incident Status table
function populateIncidentStatusTable(incidents) {
    const tableBody = document.getElementById('incident-status-table-body');
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
                        <input type="file" id="log-image-${incident.id}" accept="image/*">
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

    const imageInput = document.getElementById(`log-image-${incidentId}`);
    const formData = new FormData();
    formData.append('report', report);
    if (imageInput && imageInput.files.length > 0) {
        formData.append('logImage', imageInput.files[0]);
    }

    fetch(`/incident-management/process/${incidentId}`, {
        method: 'POST',
        body: formData
    })
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
        console.error(`Error processing incident from ${statusLabels[status]}:`, error);
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
    const refreshButton = document.getElementById('refresh-button');
    if (refreshButton) {
        refreshButton.addEventListener('click', loadAllIncidents);
    }

    const filterInput = document.getElementById('incident-filter');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            currentFilterText = this.value;
            populateIncidentStatusTable(incidentsData);
        });
    }

    const filterField = document.getElementById('filter-field');
    if (filterField) {
        filterField.addEventListener('change', function() {
            currentFilterField = this.value;
            populateIncidentStatusTable(incidentsData);
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

        setTimeout(() => {
            notificationDiv.style.display = 'none';
        }, 3000);
    }
}



// Poll for pending approvals every 2 seconds
let approvalPollingInterval = null;
let lastApprovalCount = 0;
let isModalOpen = false;

// ============================================================================
// HUMAN-IN-THE-LOOP APPROVAL FUNCTIONS
// ============================================================================

// Load and display pending approvals in modal
async function loadPendingApprovals() {
    try {
        const response = await fetch('/api/approvals/pending');
        const proposals = await response.json();

        const floatBtn = document.getElementById('approval-notification-btn');
        const countBadge = floatBtn.querySelector('.approval-count-badge');

        if (proposals.length > lastApprovalCount && lastApprovalCount >= 0) {
            if (proposals.length > 0) {
                showBrowserNotification('Approval Required',
                    `${proposals.length} incident escalation${proposals.length > 1 ? 's' : ''} awaiting your approval`);
            }
        }
        lastApprovalCount = proposals.length;

        if (proposals.length > 0) {
            floatBtn.style.display = 'flex';
            countBadge.textContent = proposals.length;
        } else {
            floatBtn.style.display = 'none';
            if (isModalOpen) {
                closeApprovalModal();
            }
        }

        if (!isModalOpen) {
            const modalBody = document.getElementById('approval-modal-body');
            if (!proposals || proposals.length === 0) {
                modalBody.innerHTML = '<p style="text-align: center; padding: 40px; color: #666;">No pending approvals at this time.</p>';
            } else {
                modalBody.innerHTML = '';
                proposals.forEach(proposal => {
                    const card = createApprovalCard(proposal);
                    modalBody.appendChild(card);
                });
            }
        }
    } catch (error) {
        console.error('Error loading pending approvals:', error);
    }
}

// Open approval modal
function openApprovalModal() {
    isModalOpen = true;
    const modal = document.getElementById('approval-modal');
    modal.style.display = 'flex';

    loadModalContent();
}

// Close approval modal
function closeApprovalModal() {
    isModalOpen = false;
    document.getElementById('approval-modal').style.display = 'none';
}

// Load modal content (called when opening modal)
async function loadModalContent() {
    try {
        const response = await fetch('/api/approvals/pending');
        const proposals = await response.json();
        const modalBody = document.getElementById('approval-modal-body');

        if (!proposals || proposals.length === 0) {
            modalBody.innerHTML = '<p style="text-align: center; padding: 40px; color: #666;">No pending approvals at this time.</p>';
        } else {
            modalBody.innerHTML = '';
            proposals.forEach(proposal => {
                const card = createApprovalCard(proposal);
                modalBody.appendChild(card);
            });
        }
    } catch (error) {
        console.error('Error loading modal content:', error);
    }
}

// Show browser notification (requires permission)
function showBrowserNotification(title, body) {
    if (!("Notification" in window)) {
        return;
    }

    if (Notification.permission === "granted") {
        new Notification(title, { body, icon: '/favicon.ico' });
    } else if (Notification.permission !== "denied") {
        Notification.requestPermission().then(permission => {
            if (permission === "granted") {
                new Notification(title, { body, icon: '/favicon.ico' });
            }
        });
    }
}

// Create an approval card UI element for a proposal
function createApprovalCard(proposal) {
    const card = document.createElement('div');
    card.className = 'approval-card';
    card.id = `approval-${proposal.id}`;

    card.innerHTML = `
        <div class="approval-card-header">
            <div class="vehicle-title">
                <span class="vehicle-icon">&#9888;&#65039;</span>
                <h3>${proposal.incidentPriority} - ${proposal.incidentSystem} / ${proposal.incidentService}</h3>
            </div>
            <div class="vehicle-value">${proposal.revenueImpact}</div>
        </div>

        <div class="approval-card-body">
            <div class="info-row">
                <span class="info-label">Incident #${proposal.incidentNumber}</span>
                <span class="info-label">Description: ${proposal.incidentDescription}</span>
            </div>

            <div class="damage-section">
                <div class="section-title">Incident Report</div>
                <div class="damage-text">${proposal.incidentReport || 'No report provided'}</div>
            </div>

            <div class="proposal-section">
                <div class="section-title">AI Recommendation</div>
                <div class="proposal-action">
                    <span class="action-badge">${proposal.proposedEscalation}</span>
                    <span class="action-reason">${proposal.escalationReason}</span>
                </div>
            </div>
        </div>

        <div class="approval-card-footer">
            ${getApprovalButtons(proposal)}
        </div>
    `;

    return card;
}

// Get approval buttons - Resolve vs Escalate
function getApprovalButtons(proposal) {
    return `
        <button class="btn-approve" onclick="handleProposalDecision(${proposal.id}, 'RESOLVE_INCIDENT')">
            Resolve & Monitor
        </button>
        <button class="btn-reject" onclick="handleProposalDecision(${proposal.id}, 'ESCALATE_INCIDENT')">
            Escalate
        </button>
    `;
}

// Handle approval/rejection decision for a proposal
async function handleProposalDecision(proposalId, decision) {
    try {
        const reasonInput = document.getElementById(`reason-${proposalId}`);
        const reason = reasonInput ? reasonInput.value.trim() : '';

        const response = await fetch(`/api/approvals/${proposalId}/decide`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                decision: decision,
                reason: reason || `${decision === 'RESOLVE_INCIDENT' ? 'Resolve and monitor' : 'Escalate'} decision by human reviewer`,
                approvedBy: 'Workshop User'
            })
        });

        if (response.ok) {
            const actionText = decision === 'RESOLVE_INCIDENT' ? 'RESOLVE & MONITOR' : 'ESCALATE';
            showNotification(`Decision: ${actionText} - Workflow will complete shortly`);

            const card = document.getElementById(`approval-${proposalId}`);
            if (card) {
                card.style.opacity = '0';
                card.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    card.remove();
                    loadPendingApprovals();
                }, 300);
            }
        } else {
            const error = await response.json();
            showNotification(`Error: ${error.error || 'Failed to record decision'}`);
        }
    } catch (error) {
        console.error('Error handling proposal decision:', error);
        showNotification('Error recording decision');
    }
}

// Start polling for pending approvals
function startApprovalPolling() {
    if ("Notification" in window && Notification.permission === "default") {
        Notification.requestPermission();
    }

    loadPendingApprovals();

    if (approvalPollingInterval) {
        clearInterval(approvalPollingInterval);
    }
    approvalPollingInterval = setInterval(loadPendingApprovals, 2000);
}

// Stop polling for pending approvals
function stopApprovalPolling() {
    if (approvalPollingInterval) {
        clearInterval(approvalPollingInterval);
        approvalPollingInterval = null;
    }
}
