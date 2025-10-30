# Migration Template & Quick Examples

Use this document as a reference when creating new Flyway migrations.

---

## File Naming Convention

**Format:** `V<VERSION>__<DESCRIPTION>.sql`

```
V1__Initial_schema.sql
V2__Add_column_to_users.sql
V3__Create_notifications_table.sql
V4__Add_index_on_email.sql
V5__Rename_column_in_bookings.sql
```

**Location:** `backend/src/main/resources/db/migration/`

---

## Template: Basic Migration

```sql
-- V<N>__<Description>.sql
-- Brief description of what this migration does
-- Date created: YYYY-MM-DD
-- Author: Your Name (optional)

-- ============================================================================
-- Migration Description
-- ============================================================================

-- ADD YOUR SQL HERE

-- ============================================================================
-- Migration Complete
-- ============================================================================
```

---

## Common Migration Examples

### 1️⃣ Add Column to Existing Table

**File:** `V2__Add_city_to_users.sql`

```sql
-- Adds city column to users table for location-based features

ALTER TABLE users ADD COLUMN city VARCHAR(255);

-- Optional: Add index if frequently searched
CREATE INDEX idx_users_city ON users(city);
```

---

### 2️⃣ Create New Table

**File:** `V3__Create_notifications_table.sql`

```sql
-- Create notifications table for user notifications

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index for querying user notifications
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
```

---

### 3️⃣ Add Foreign Key

**File:** `V4__Add_category_to_services.sql`

```sql
-- Adds category relationship to services

-- First, create categories table if it doesn't exist
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Add foreign key column to services
ALTER TABLE services ADD COLUMN category_id BIGINT;

-- Add foreign key constraint
ALTER TABLE services ADD CONSTRAINT fk_services_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

-- Create index for performance
CREATE INDEX idx_services_category_id ON services(category_id);
```

---

### 4️⃣ Add Unique Constraint

**File:** `V5__Add_unique_constraints.sql`

```sql
-- Add unique constraints for data integrity

-- Unique email constraint (if not already present)
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Unique spa name constraint
ALTER TABLE spas ADD CONSTRAINT uk_spas_name UNIQUE (name);

-- Unique membership plan names
ALTER TABLE memberships ADD CONSTRAINT uk_memberships_name UNIQUE (name);
```

---

### 5️⃣ Rename Column

**File:** `V6__Rename_columns.sql`

```sql
-- Renames columns for clarity

-- Rename user_name to display_name
ALTER TABLE users RENAME COLUMN user_name TO display_name;

-- Rename booking_time to booked_at
ALTER TABLE bookings RENAME COLUMN booking_time TO booked_at;
```

---

### 6️⃣ Change Column Type

**File:** `V7__Change_column_types.sql`

```sql
-- Change column types for better data handling

-- Change description from VARCHAR to TEXT
ALTER TABLE services ALTER COLUMN description SET DATA TYPE TEXT;

-- Change rating from VARCHAR to INTEGER
ALTER TABLE reviews ALTER COLUMN rating SET DATA TYPE INTEGER;
```

---

### 7️⃣ Add Default Values

**File:** `V8__Add_defaults.sql`

```sql
-- Add default values to columns

-- Add created_at timestamp with default
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add updated_at timestamp with default
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add is_active boolean with default
ALTER TABLE services ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
```

---

### 8️⃣ Add Check Constraint

**File:** `V9__Add_check_constraints.sql`

```sql
-- Add check constraints for data validation

-- Ensure rating is between 1 and 5
ALTER TABLE reviews ADD CONSTRAINT ck_reviews_rating 
    CHECK (rating >= 1 AND rating <= 5);

-- Ensure price is positive
ALTER TABLE services ADD CONSTRAINT ck_services_price 
    CHECK (price > 0);

-- Ensure discount is between 0 and 100
ALTER TABLE memberships ADD CONSTRAINT ck_memberships_discount 
    CHECK (discount_percentage >= 0 AND discount_percentage <= 100);
```

---

### 9️⃣ Create Composite Index

**File:** `V10__Add_composite_indexes.sql`

```sql
-- Create composite indexes for complex queries

-- Index for finding user bookings by status and date
CREATE INDEX idx_bookings_user_status_time 
    ON bookings(customer_user_id, status, booking_time);

-- Index for finding services by spa and status
CREATE INDEX idx_services_spa_status 
    ON services(spa_id, service_status);

-- Index for finding reviews by spa and rating
CREATE INDEX idx_reviews_spa_rating 
    ON reviews(spa_id, rating);
```

---

### 🔟 Drop Column (Use with Caution)

**File:** `V11__Remove_obsolete_columns.sql`

```sql
-- Remove obsolete columns (careful - cannot be undone easily!)

-- First, ensure nothing depends on this column
ALTER TABLE users DROP COLUMN old_phone_field;

-- Drop related indexes
DROP INDEX idx_users_old_phone_field IF EXISTS;
```

---

### 1️⃣1️⃣ Data Migration (Update Existing Data)

**File:** `V12__Migrate_data.sql`

```sql
-- Example: Convert all roles to uppercase
UPDATE users SET role = UPPER(role) WHERE role IS NOT NULL;

-- Example: Set membership for all users
UPDATE users SET membership_status = 'INACTIVE' 
    WHERE membership_id IS NULL AND membership_status IS NULL;

-- Example: Fill in missing timestamps
UPDATE bookings SET booking_time = CURRENT_TIMESTAMP 
    WHERE booking_time IS NULL;
```

---

### 1️⃣2️⃣ Multi-Step Complex Migration

**File:** `V13__Refactor_service_pricing.sql`

```sql
-- Complex refactoring: Add new pricing structure

-- Step 1: Create new pricing table
CREATE TABLE service_pricing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL UNIQUE,
    base_price DOUBLE NOT NULL,
    seasonal_multiplier DOUBLE DEFAULT 1.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- Step 2: Migrate existing prices to new table
INSERT INTO service_pricing (service_id, base_price)
SELECT id, price FROM services;

-- Step 3: Add index
CREATE INDEX idx_service_pricing_service_id ON service_pricing(service_id);

-- Step 4: Optional - drop old price column after verification period
-- ALTER TABLE services DROP COLUMN price;
```

---

## Migration Checklist

Before committing a migration, verify:

- [ ] File name follows `V<N>__<Description>.sql` format
- [ ] Version number is sequential (V1, V2, V3...)
- [ ] SQL syntax is valid for H2 database
- [ ] Migration tested locally (delete db and restart)
- [ ] All foreign keys have ON DELETE rules
- [ ] Indexes created for frequently queried columns
- [ ] Comments explain the purpose of changes
- [ ] No sensitive data hardcoded
- [ ] Backward compatible (no breaking changes)
- [ ] Matches entity changes in Java code

---

## Testing Your Migration

### Local Testing

```bash
# 1. Delete the database to start fresh
rm ./data/spabooking.h2.db

# 2. Restart application with dev profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# 3. Check migration ran (look for log: "Migrating to v<N>")

# 4. Verify in H2 Console
# - New column/table exists
# - SELECT * FROM flyway_schema_history; shows success=true
```

### Verification Queries

```sql
-- Check migration history
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
ORDER BY version DESC;

-- Verify schema changes
SHOW COLUMNS FROM users;

-- Check indexes
SELECT * FROM information_schema.indexes WHERE table_name = 'users';

-- Verify constraints
SELECT * FROM information_schema.constraints;
```

---

## SQL Syntax for H2

### Common H2-Specific Syntax

```sql
-- Add column with default
ALTER TABLE table_name ADD COLUMN column_name TYPE DEFAULT value;

-- Rename column
ALTER TABLE table_name RENAME COLUMN old_name TO new_name;

-- Change column type
ALTER TABLE table_name ALTER COLUMN column_name SET DATA TYPE new_type;

-- Drop column
ALTER TABLE table_name DROP COLUMN column_name;

-- Drop index
DROP INDEX index_name;

-- Create sequence
CREATE SEQUENCE seq_name START WITH 1 INCREMENT BY 1;
```

---

## Do's and Don'ts

### ✅ DO

```sql
-- Do: Use descriptive file names
V2__Add_verified_flag_to_users.sql

-- Do: Include comments
-- Adds verified flag for email verification status
ALTER TABLE users ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Do: Add foreign key rules
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

-- Do: Create indexes for performance
CREATE INDEX idx_users_email ON users(email);

-- Do: Handle null values
ALTER TABLE services ADD COLUMN description TEXT DEFAULT '';
```

### ❌ DON'T

```sql
-- Don't: Edit existing migrations!
-- If you realize a mistake, create a new migration V3, V4, etc.

-- Don't: Use vague names
V2__update.sql  -- BAD
V2__Add_verified_flag_to_users.sql  -- GOOD

-- Don't: Mix multiple unrelated changes
-- Create separate migrations for each concern

-- Don't: Assume column order
SELECT id, email, name FROM users;  -- Might break

-- Don't: Run migrations manually
-- Let Flyway handle it automatically
```

---

## Git Workflow for Migrations

### Committing Migrations

```bash
# Feature branch: Add new feature with migration
git checkout -b feature/add-email-verification

# 1. Update entity
vim backend/src/main/java/.../User.java

# 2. Create migration
vim backend/src/main/resources/db/migration/V2__Add_verified_flag_to_users.sql

# 3. Commit both together
git add backend/src/main/java/.../User.java
git add backend/src/main/resources/db/migration/V2__Add_verified_flag_to_users.sql
git commit -m "Feature: Add email verification support"

# 4. Push and create PR
git push origin feature/add-email-verification
```

### Reviewing Migrations in PR

```
Code Review Checklist:
- [ ] Migration file follows naming convention
- [ ] SQL syntax is correct
- [ ] Comments explain the change
- [ ] Matches entity changes
- [ ] No data loss (if removing columns)
- [ ] Indexes added for performance
- [ ] Foreign keys have ON DELETE rules
```

---

## Migration Error Recovery

### Migration Failed - How to Fix

```bash
# 1. Check what went wrong
# Look at application logs for SQL error

# 2. Find the failed migration in history
# H2 Console: SELECT * FROM flyway_schema_history;

# 3. Development Only: Reset database
rm ./data/spabooking.h2.db

# 4. Fix the SQL file
# Edit the migration file with correct SQL

# 5. Test again
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Migration Checksum Error

**Error:** "Validate failed: Migration checksum mismatch"

**Cause:** You edited an already-executed migration file

**Solution:**
```sql
-- Don't edit V1, V2, etc. after they're executed!
-- Instead, create a new migration V3, V4, etc.

-- IF you must fix a critical issue:
-- 1. Revert the change to original file
-- 2. Create new migration with the fix
-- 3. Never edit executed migrations
```

---

## Summary

| Task | File Pattern |
|------|-------------|
| Add column | `V<N>__Add_<column>_to_<table>.sql` |
| Create table | `V<N>__Create_<table>_table.sql` |
| Add constraint | `V<N>__Add_<constraint>_<table>.sql` |
| Rename column | `V<N>__Rename_<old>_to_<new>.sql` |
| Add index | `V<N>__Add_index_<table>_<column>.sql` |
| Migrate data | `V<N>__Migrate_<description>.sql` |

**Key Rules:**
1. ✅ Always create NEW migration files
2. ✅ Never edit executed migrations
3. ✅ Follow naming convention strictly
4. ✅ Test locally before committing
5. ✅ Include comments in SQL
6. ✅ Keep migrations focused and small

You're ready to manage database changes safely! 🚀
