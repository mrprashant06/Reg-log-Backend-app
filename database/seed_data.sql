USE ecommerce_auth_db;

-- Insert Default Roles
INSERT INTO roles (name, description) VALUES 
('ROLE_CUSTOMER', 'Standard E-commerce Customer'),
('ROLE_ADMIN', 'Store Administrator with full access'),
('ROLE_MODERATOR', 'Store Moderator with restricted management access')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- Default Admin User:
-- Email: admin@ecommerce.com
-- Password: AdminPassword123! (BCrypt hashed)
INSERT INTO users (email, password_hash, first_name, last_name, phone_number, status)
VALUES ('admin@ecommerce.com', '$2a$12$ZqC4R8Jz/UvWkX6mP1y9v.1s3QYpZl2hFk9mP4w8o2sE6kG4eY7t2', 'Store', 'Administrator', '+10000000000', 'ACTIVE')
ON DUPLICATE KEY UPDATE first_name=VALUES(first_name);

-- Assign ROLE_ADMIN and ROLE_CUSTOMER to default admin user
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'admin@ecommerce.com' AND r.name IN ('ROLE_ADMIN', 'ROLE_CUSTOMER');
