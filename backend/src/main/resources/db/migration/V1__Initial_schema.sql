-- ============================================================================
-- V1__Initial_schema.sql
-- Initial database schema for Spa Booking System
--
-- This migration creates the complete schema with all tables and relationships
-- Executed automatically by Flyway on application startup
-- ============================================================================

-- Create MEMBERSHIPS table (foundational - no dependencies)
CREATE TABLE memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    price_per_month DOUBLE NOT NULL,
    discount_percentage DOUBLE NOT NULL
);

-- Create USERS table (central hub entity)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    phone VARCHAR(255),
    role VARCHAR(50) NOT NULL, -- Enum: USER, CLIENT, ADMIN
    membership_id BIGINT,
    membership_status VARCHAR(50), -- Enum: ACTIVE, INACTIVE, CANCELLED

    FOREIGN KEY (membership_id) REFERENCES memberships(id) ON DELETE SET NULL
);

-- Create SPAS table (business locations owned by CLIENT users)
CREATE TABLE spas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    description TEXT,
    approval_status VARCHAR(50), -- Enum: PENDING, APPROVED, REJECTED
    owner_user_id BIGINT NOT NULL,

    FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create SERVICES table (services offered by spas)
CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    duration_in_minutes INTEGER,
    approval_status VARCHAR(50), -- Enum: PENDING, APPROVED, REJECTED
    service_status VARCHAR(50), -- Enum: AVAILABLE, UNAVAILABLE
    spa_id BIGINT NOT NULL,

    FOREIGN KEY (spa_id) REFERENCES spas(id) ON DELETE CASCADE
);

-- Create BOOKINGS table (customer bookings - core transactions)
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL, -- Enum: PENDING, CONFIRMED, CANCELLED_BY_USER, DECLINED_BY_CLIENT
    final_price DOUBLE NOT NULL,
    customer_user_id BIGINT NOT NULL,
    spa_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,

    FOREIGN KEY (customer_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (spa_id) REFERENCES spas(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- Create REVIEWS table (customer feedback)
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT,
    review_date TIMESTAMP,
    user_id BIGINT NOT NULL,
    spa_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (spa_id) REFERENCES spas(id) ON DELETE CASCADE
);

-- Create USER_WISHLIST_SERVICES junction table (many-to-many relationship)
CREATE TABLE user_wishlist_services (
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, service_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- Spring Session tables are automatically created by Spring Boot
-- when spring.session.store-type=jdbc is configured
-- No need to manually create them in migrations

-- Create Indexes for Performance Optimization
-- Improve query performance on frequently searched columns

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_membership_id ON users(membership_id);

-- Spas table indexes
CREATE INDEX idx_spas_owner_user_id ON spas(owner_user_id);
CREATE INDEX idx_spas_approval_status ON spas(approval_status);

-- Services table indexes
CREATE INDEX idx_services_spa_id ON services(spa_id);
CREATE INDEX idx_services_approval_status ON services(approval_status);
CREATE INDEX idx_services_service_status ON services(service_status);

-- Bookings table indexes
CREATE INDEX idx_bookings_customer_user_id ON bookings(customer_user_id);
CREATE INDEX idx_bookings_spa_id ON bookings(spa_id);
CREATE INDEX idx_bookings_service_id ON bookings(service_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_booking_time ON bookings(booking_time);

-- Reviews table indexes
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_spa_id ON reviews(spa_id);

-- ============================================================================
-- Schema Migration Complete
--
-- Tables created:
-- 1. memberships - Subscription plans
-- 2. users - All user accounts (admin, client, customer)
-- 3. spas - Spa business locations
-- 4. services - Services offered by spas
-- 5. bookings - Customer bookings
-- 6. reviews - Customer reviews
-- 7. user_wishlist_services - User wishlist relationships
-- 8. spring_session - Session storage
-- 9. spring_session_attributes - Session attributes
-- 10. spring_session_sessions - Session list
--
-- Relationships:
-- - users has many memberships
-- - users owns spas
-- - spas have many services
-- - users make many bookings
-- - bookings reference services and spas
-- - users write many reviews
-- - users wishlist many services (M:M)
--
-- Next: Run DataInitializer to seed test data
-- ============================================================================
