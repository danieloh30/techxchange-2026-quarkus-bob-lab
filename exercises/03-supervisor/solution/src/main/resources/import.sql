DROP SEQUENCE IF EXISTS incident_info_id_seq;
CREATE SEQUENCE incident_info_id_seq;

DROP TABLE IF EXISTS incident_info;
CREATE TABLE incident_info (
    id INT PRIMARY KEY DEFAULT nextval('incident_info_id_seq'),
    system_name VARCHAR(255) NOT NULL,
    service VARCHAR(255) NOT NULL,
    priority VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL
);

INSERT INTO incident_info (id, system_name, service, priority, description, status) VALUES
    (nextval('incident_info_id_seq'), 'Payment Gateway', 'Checkout Service', 'P2', 'Intermittent timeout errors during peak hours', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Kubernetes Cluster', 'API Server', 'P3', 'Node scaling configured and tested', 'IN_PROGRESS'),
    (nextval('incident_info_id_seq'), 'Email Platform', 'SMTP Relay', 'P3', 'Recent security patches applied', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Legacy ERP', 'Inventory Module', 'P4', 'Slow query performance on reports', 'TRIAGING'),
    (nextval('incident_info_id_seq'), 'Core Database', 'Primary Cluster', 'P1', 'Replication lag causing data inconsistency', 'OPEN'),
    (nextval('incident_info_id_seq'), 'CDN', 'Edge Cache', 'P4', 'Operating normally after maintenance', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Authentication', 'SSO Provider', 'P3', 'Token refresh delays under load', 'OPEN'),
    (nextval('incident_info_id_seq'), 'Load Balancer', 'Traffic Manager', 'P2', 'Health check failures on backend pool', 'IN_PROGRESS');