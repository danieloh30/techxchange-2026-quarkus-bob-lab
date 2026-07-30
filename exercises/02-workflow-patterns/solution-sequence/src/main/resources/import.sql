DROP SEQUENCE IF EXISTS incident_info_id_seq;
CREATE SEQUENCE incident_info_id_seq;

DROP TABLE IF EXISTS incident_info;
CREATE TABLE incident_info (
    id INT PRIMARY KEY DEFAULT nextval('incident_info_id_seq'),
    system_name VARCHAR(255) NOT NULL,
    service VARCHAR(255) NOT NULL,
    priority INT NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL
);

INSERT INTO incident_info (id, system_name, service, priority, description, status) VALUES
    (nextval('incident_info_id_seq'), 'payment-gateway', 'checkout-api', 2, 'Intermittent timeout errors during peak hours', 'OPEN'),
    (nextval('incident_info_id_seq'), 'auth-service', 'user-login', 1, 'Authentication failures affecting all users', 'IN_PROGRESS'),
    (nextval('incident_info_id_seq'), 'inventory-db', 'stock-sync', 3, 'Stock levels not syncing between warehouses', 'OPEN'),
    (nextval('incident_info_id_seq'), 'cdn-edge', 'static-assets', 4, 'Slow asset loading in APAC region', 'TRIAGING'),
    (nextval('incident_info_id_seq'), 'email-service', 'notification-api', 2, 'Notification emails delayed by 30+ minutes', 'OPEN'),
    (nextval('incident_info_id_seq'), 'search-engine', 'product-search', 3, 'Search results returning stale data', 'OPEN'),
    (nextval('incident_info_id_seq'), 'monitoring', 'alerting-api', 2, 'False positive alerts flooding on-call team', 'OPEN'),
    (nextval('incident_info_id_seq'), 'api-gateway', 'rate-limiter', 1, 'Rate limiter misconfigured causing legitimate request drops', 'IN_PROGRESS');
