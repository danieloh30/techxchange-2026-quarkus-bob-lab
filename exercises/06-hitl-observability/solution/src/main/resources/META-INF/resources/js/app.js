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

        let actionCell = '';
        if (incident.status === 'OPEN' || incident.status === 'TRIAGING' || incident.status === 'IN_PROGRESS') {
            actionCell = `
                <td>
                    <form onsubmit="processIncidentReport(event, ${incident.id}, '${incident.status}')">
                        <input type="text" class="feedback-input" id="feedback-${incident.id}" placeholder="Enter report details">
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
            <td><span class="priority-badge priority-${incident.priority ? incident.priority.toLowerCase() : ''}">${incident.priority || 'N/A'}</span></td>
            <td>${incident.description || 'N/A'}</td>
            <td><span class="status-pill ${statusPillClass}">${getStatusDisplay(incident.status)}</span></td>
            ${actionCell}
        `;

        tableBody.appendChild(row);
    });
}

// Function to process an incident report
function processIncidentReport(event, incidentId, status) {
    event.preventDefault();
    const feedback = document.getElementById(`feedback-${incidentId}`).value;
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

    fetch(`/incident-management/process/${incidentId}?feedback=${encodeURIComponent(feedback)}`, { method: 'POST' })
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

        // Show browser notification if new approvals arrived
        if (proposals.length > lastApprovalCount && lastApprovalCount >= 0) {
            if (proposals.length > 0) {
                showBrowserNotification('Approval Required',
                    `${proposals.length} incident escalation${proposals.length > 1 ? 's' : ''} awaiting your approval`);
            }
        }
        lastApprovalCount = proposals.length;

        // Update floating button
        if (proposals.length > 0) {
            floatBtn.style.display = 'flex';
            countBadge.textContent = proposals.length;
        } else {
            floatBtn.style.display = 'none';
            // Close modal if no more approvals
            if (isModalOpen) {
                closeApprovalModal();
            }
        }

        // Only update modal content if modal is NOT open (prevents flashing)
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

    // Load content when opening
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
                <span class="vehicle-icon">&#9888;</span>
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

// Get approval buttons - simplified to always show Keep vs Escalate
function getApprovalButtons(proposal) {
    return `
        <button class="btn-approve" onclick="handleProposalDecision(${proposal.id}, 'KEEP_AT_TEAM')">
            Keep at Team Level
        </button>
        <button class="btn-reject" onclick="handleProposalDecision(${proposal.id}, 'ESCALATE_INCIDENT')">
            Escalate to Management
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
                decision: decision, // KEEP_AT_TEAM or ESCALATE_INCIDENT
                reason: reason || `${decision === 'KEEP_AT_TEAM' ? 'Keep at team level' : 'Escalate to management'} decision by human reviewer`,
                approvedBy: 'Workshop User'
            })
        });

        if (response.ok) {
            const actionText = decision === 'KEEP_AT_TEAM' ? 'KEEP AT TEAM' : 'ESCALATE';
            showNotification(`Decision: ${actionText} - Workflow will complete shortly`);

            // Remove the approval card with animation
            const card = document.getElementById(`approval-${proposalId}`);
            if (card) {
                card.style.opacity = '0';
                card.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    card.remove();
                    // Reload approvals to update the display
                    loadPendingApprovals();
                    // Don't reload incidents immediately - let the next automatic refresh handle it
                    // This prevents the UI from flickering between states
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
    // Request notification permission on first load
    if ("Notification" in window && Notification.permission === "default") {
        Notification.requestPermission();
    }

    // Load immediately
    loadPendingApprovals();

    // Then poll every 2 seconds
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
