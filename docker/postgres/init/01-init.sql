-- Initialize Rihua Database
-- This script runs when PostgreSQL container starts for the first time

-- Create additional databases if needed
-- CREATE DATABASE rihua_test;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Set timezone
SET timezone = 'Asia/Tokyo';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE rihua_dev TO rihua_user;

-- Create schemas if needed
-- CREATE SCHEMA IF NOT EXISTS audit;
-- GRANT USAGE ON SCHEMA audit TO rihua_user;
-- GRANT CREATE ON SCHEMA audit TO rihua_user;