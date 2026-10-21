-- Initialize default roles if they do not exist
INSERT INTO roles (name, description) 
SELECT 'ROLE_CUSTOMER', 'Standard E-commerce Customer' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_CUSTOMER');

INSERT INTO roles (name, description) 
SELECT 'ROLE_ADMIN', 'Store Administrator with full access' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (name, description) 
SELECT 'ROLE_MODERATOR', 'Store Moderator with restricted management access' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_MODERATOR');
