CREATE SEQUENCE IF NOT EXISTS incident_info_id_seq;

CREATE TABLE IF NOT EXISTS incident_info (
    id INT PRIMARY KEY DEFAULT nextval('incident_info_id_seq'),
    system_name VARCHAR(255) NOT NULL,
    service VARCHAR(255) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL
);

INSERT INTO incident_info (id, system_name, service, priority, description, status) VALUES
    (nextval('incident_info_id_seq'), 'Payment Processing', 'Checkout API', 'P1', 'Transaction failures reported by multiple customers', 'OPEN'),
    (nextval('incident_info_id_seq'), 'E-Commerce Platform', 'Product Catalog', 'P2', 'Catalog search returning stale results', 'IN_PROGRESS'),
    (nextval('incident_info_id_seq'), 'Trading System', 'Order Execution', 'P1', 'Order execution latency exceeding SLA thresholds', 'OPEN'),
    (nextval('incident_info_id_seq'), 'HR System', 'Employee Portal', 'P3', 'Portal login intermittently failing', 'TRIAGING'),
    (nextval('incident_info_id_seq'), 'Internal Tools', 'Wiki Platform', 'P4', 'Search indexing delayed by 2 hours', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Customer Portal', 'Dashboard API', 'P3', 'Dashboard loading slowly during peak hours', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Mobile App', 'Push Notifications', 'P2', 'Push notifications delayed by 15 minutes', 'OPEN'),
    (nextval('incident_info_id_seq'), 'API Gateway', 'Rate Limiter', 'P3', 'Rate limiter miscounting requests from specific IP ranges', 'IN_PROGRESS');
