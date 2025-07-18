-- Database indexes for performance optimization
-- Author: Rihua Development Team
-- Version: 2.0.0

-- =====================================================
-- USERS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_updated_at ON users(updated_at);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_last_login_at ON users(last_login_at);
CREATE INDEX idx_users_email_verified_at ON users(email_verified_at);
-- Composite indexes for common queries
CREATE INDEX idx_users_status_role ON users(status, role);
CREATE INDEX idx_users_email_deleted ON users(email, deleted_at);

-- =====================================================
-- EVENTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_events_organizer_id ON events(organizer_id);
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_start_time ON events(start_time);
CREATE INDEX idx_events_end_time ON events(end_time);
CREATE INDEX idx_events_location ON events(location);
CREATE INDEX idx_events_price ON events(price);
CREATE INDEX idx_events_capacity ON events(capacity);
CREATE INDEX idx_events_sold_tickets ON events(sold_tickets);
CREATE INDEX idx_events_created_at ON events(created_at);
CREATE INDEX idx_events_updated_at ON events(updated_at);
CREATE INDEX idx_events_deleted_at ON events(deleted_at);
CREATE INDEX idx_events_view_count ON events(view_count);
-- Composite indexes for common queries
CREATE INDEX idx_events_status_start_time ON events(status, start_time);
CREATE INDEX idx_events_category_status ON events(category, status);
CREATE INDEX idx_events_organizer_status ON events(organizer_id, status);
CREATE INDEX idx_events_price_range ON events(price, status) WHERE status = 'PUBLISHED';

-- =====================================================
-- HOUSING TABLE INDEXES
-- =====================================================
CREATE INDEX idx_housing_poster_id ON housing(poster_id);
CREATE INDEX idx_housing_type ON housing(type);
CREATE INDEX idx_housing_status ON housing(status);
CREATE INDEX idx_housing_prefecture ON housing(prefecture);
CREATE INDEX idx_housing_city ON housing(city);
CREATE INDEX idx_housing_price ON housing(price);
CREATE INDEX idx_housing_rooms ON housing(rooms);
CREATE INDEX idx_housing_foreigner_friendly ON housing(foreigner_friendly);
CREATE INDEX idx_housing_pet_allowed ON housing(pet_allowed);
CREATE INDEX idx_housing_created_at ON housing(created_at);
CREATE INDEX idx_housing_updated_at ON housing(updated_at);
CREATE INDEX idx_housing_deleted_at ON housing(deleted_at);
CREATE INDEX idx_housing_available_until ON housing(available_until);
CREATE INDEX idx_housing_view_count ON housing(view_count);
-- Composite indexes for common queries
CREATE INDEX idx_housing_type_status ON housing(type, status);
CREATE INDEX idx_housing_location_price ON housing(prefecture, city, price);
CREATE INDEX idx_housing_status_price ON housing(status, price);
CREATE INDEX idx_housing_features ON housing(foreigner_friendly, pet_allowed, status);

-- =====================================================
-- JOBS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_jobs_poster_id ON jobs(poster_id);
CREATE INDEX idx_jobs_type ON jobs(type);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_prefecture ON jobs(prefecture);
CREATE INDEX idx_jobs_city ON jobs(city);
CREATE INDEX idx_jobs_salary_min ON jobs(salary_min);
CREATE INDEX idx_jobs_salary_max ON jobs(salary_max);
CREATE INDEX idx_jobs_experience_required ON jobs(experience_required);
CREATE INDEX idx_jobs_visa_support ON jobs(visa_support);
CREATE INDEX idx_jobs_remote_work ON jobs(remote_work);
CREATE INDEX idx_jobs_created_at ON jobs(created_at);
CREATE INDEX idx_jobs_updated_at ON jobs(updated_at);
CREATE INDEX idx_jobs_deleted_at ON jobs(deleted_at);
CREATE INDEX idx_jobs_application_deadline ON jobs(application_deadline);
CREATE INDEX idx_jobs_view_count ON jobs(view_count);
-- Composite indexes for common queries
CREATE INDEX idx_jobs_type_status ON jobs(type, status);
CREATE INDEX idx_jobs_location_salary ON jobs(prefecture, city, salary_min, salary_max);
CREATE INDEX idx_jobs_status_salary ON jobs(status, salary_min, salary_max);
CREATE INDEX idx_jobs_features ON jobs(visa_support, remote_work, status);

-- =====================================================
-- FORUM POSTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_forum_posts_author_id ON forum_posts(author_id);
CREATE INDEX idx_forum_posts_category ON forum_posts(category);
CREATE INDEX idx_forum_posts_status ON forum_posts(status);
CREATE INDEX idx_forum_posts_created_at ON forum_posts(created_at);
CREATE INDEX idx_forum_posts_updated_at ON forum_posts(updated_at);
CREATE INDEX idx_forum_posts_deleted_at ON forum_posts(deleted_at);
CREATE INDEX idx_forum_posts_like_count ON forum_posts(like_count);
CREATE INDEX idx_forum_posts_comment_count ON forum_posts(comment_count);
CREATE INDEX idx_forum_posts_view_count ON forum_posts(view_count);
CREATE INDEX idx_forum_posts_pinned ON forum_posts(pinned);
CREATE INDEX idx_forum_posts_report_count ON forum_posts(report_count);
-- Composite indexes for common queries
CREATE INDEX idx_forum_posts_category_status ON forum_posts(category, status);
CREATE INDEX idx_forum_posts_status_created ON forum_posts(status, created_at);
CREATE INDEX idx_forum_posts_author_status ON forum_posts(author_id, status);
CREATE INDEX idx_forum_posts_popularity ON forum_posts(like_count, comment_count, status);

-- =====================================================
-- COMMENTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_comments_forum_post_id ON comments(forum_post_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_status ON comments(status);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_comments_updated_at ON comments(updated_at);
CREATE INDEX idx_comments_deleted_at ON comments(deleted_at);
CREATE INDEX idx_comments_like_count ON comments(like_count);
CREATE INDEX idx_comments_report_count ON comments(report_count);
-- Composite indexes for common queries
CREATE INDEX idx_comments_post_status ON comments(forum_post_id, status);
CREATE INDEX idx_comments_post_created ON comments(forum_post_id, created_at);
CREATE INDEX idx_comments_parent_status ON comments(parent_comment_id, status);
CREATE INDEX idx_comments_author_status ON comments(author_id, status);

-- =====================================================
-- TICKETS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_tickets_event_id ON tickets(event_id);
CREATE INDEX idx_tickets_purchaser_id ON tickets(purchaser_id);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_qr_code ON tickets(qr_code);
CREATE INDEX idx_tickets_price ON tickets(price);
CREATE INDEX idx_tickets_created_at ON tickets(created_at);
CREATE INDEX idx_tickets_updated_at ON tickets(updated_at);
CREATE INDEX idx_tickets_deleted_at ON tickets(deleted_at);
CREATE INDEX idx_tickets_used_at ON tickets(used_at);
-- Composite indexes for common queries
CREATE INDEX idx_tickets_event_status ON tickets(event_id, status);
CREATE INDEX idx_tickets_purchaser_status ON tickets(purchaser_id, status);
CREATE INDEX idx_tickets_event_purchaser ON tickets(event_id, purchaser_id);

-- =====================================================
-- NOTIFICATIONS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_priority ON notifications(priority);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_updated_at ON notifications(updated_at);
CREATE INDEX idx_notifications_deleted_at ON notifications(deleted_at);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);
CREATE INDEX idx_notifications_retry_count ON notifications(retry_count);
-- Composite indexes for common queries
CREATE INDEX idx_notifications_user_status ON notifications(user_id, status);
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at);
CREATE INDEX idx_notifications_type_status ON notifications(type, status);
CREATE INDEX idx_notifications_priority_status ON notifications(priority, status);

-- =====================================================
-- FILE METADATA TABLE INDEXES
-- =====================================================
CREATE INDEX idx_file_metadata_uploader_id ON file_metadata(uploader_id);
CREATE INDEX idx_file_metadata_file_type ON file_metadata(file_type);
CREATE INDEX idx_file_metadata_file_size ON file_metadata(file_size);
CREATE INDEX idx_file_metadata_mime_type ON file_metadata(mime_type);
CREATE INDEX idx_file_metadata_created_at ON file_metadata(created_at);
CREATE INDEX idx_file_metadata_updated_at ON file_metadata(updated_at);
CREATE INDEX idx_file_metadata_deleted_at ON file_metadata(deleted_at);
CREATE INDEX idx_file_metadata_reference_count ON file_metadata(reference_count);
-- Composite indexes for common queries
CREATE INDEX idx_file_metadata_uploader_type ON file_metadata(uploader_id, file_type);
CREATE INDEX idx_file_metadata_type_size ON file_metadata(file_type, file_size);

-- =====================================================
-- LIKES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_likes_likeable_type ON likes(likeable_type);
CREATE INDEX idx_likes_likeable_id ON likes(likeable_id);
CREATE INDEX idx_likes_created_at ON likes(created_at);
CREATE INDEX idx_likes_deleted_at ON likes(deleted_at);
-- Composite indexes for common queries
CREATE INDEX idx_likes_user_target ON likes(user_id, likeable_type, likeable_id);
CREATE INDEX idx_likes_target_created ON likes(likeable_type, likeable_id, created_at);
CREATE INDEX idx_likes_user_type ON likes(user_id, likeable_type);

-- =====================================================
-- REPORTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_reports_reporter_id ON reports(reporter_id);
CREATE INDEX idx_reports_reportable_type ON reports(reportable_type);
CREATE INDEX idx_reports_reportable_id ON reports(reportable_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_reason ON reports(reason);
CREATE INDEX idx_reports_priority ON reports(priority);
CREATE INDEX idx_reports_processor_id ON reports(processor_id);
CREATE INDEX idx_reports_created_at ON reports(created_at);
CREATE INDEX idx_reports_updated_at ON reports(updated_at);
CREATE INDEX idx_reports_deleted_at ON reports(deleted_at);
-- Composite indexes for common queries
CREATE INDEX idx_reports_target_status ON reports(reportable_type, reportable_id, status);
CREATE INDEX idx_reports_status_created ON reports(status, created_at);
CREATE INDEX idx_reports_reporter_target ON reports(reporter_id, reportable_type, reportable_id);
CREATE INDEX idx_reports_processor_status ON reports(processor_id, status);

-- =====================================================
-- AUDIT LOGS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_ip_address ON audit_logs(ip_address);
CREATE INDEX idx_audit_logs_success ON audit_logs(success);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
-- Composite indexes for common queries
CREATE INDEX idx_audit_logs_entity_action ON audit_logs(entity_type, action);
CREATE INDEX idx_audit_logs_user_action ON audit_logs(user_id, action);
CREATE INDEX idx_audit_logs_entity_target ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user_created ON audit_logs(user_id, created_at);

-- =====================================================
-- PARTIAL INDEXES FOR SOFT DELETE PATTERNS
-- =====================================================
-- These indexes exclude deleted records for better performance
CREATE INDEX idx_users_active ON users(id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_events_published ON events(id, status, start_time) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;
CREATE INDEX idx_housing_available ON housing(id, status, type, prefecture, city) WHERE status = 'AVAILABLE' AND deleted_at IS NULL;
CREATE INDEX idx_jobs_active ON jobs(id, status, type, prefecture, city) WHERE status = 'ACTIVE' AND deleted_at IS NULL;
CREATE INDEX idx_forum_posts_published ON forum_posts(id, status, category, created_at) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;
CREATE INDEX idx_comments_published ON comments(id, forum_post_id, status, created_at) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;
CREATE INDEX idx_tickets_valid ON tickets(id, event_id, status) WHERE status IN ('VALID', 'USED') AND deleted_at IS NULL;
CREATE INDEX idx_notifications_unread ON notifications(id, user_id, created_at) WHERE status = 'UNREAD' AND deleted_at IS NULL;

-- =====================================================
-- FULL-TEXT SEARCH INDEXES (PostgreSQL specific)
-- =====================================================
-- These indexes support full-text search functionality
CREATE INDEX idx_events_search ON events USING gin(to_tsvector('japanese', title || ' ' || description));
CREATE INDEX idx_housing_search ON housing USING gin(to_tsvector('japanese', title || ' ' || description));
CREATE INDEX idx_jobs_search ON jobs USING gin(to_tsvector('japanese', title || ' ' || description || ' ' || requirements));
CREATE INDEX idx_forum_posts_search ON forum_posts USING gin(to_tsvector('japanese', title || ' ' || content));
CREATE INDEX idx_comments_search ON comments USING gin(to_tsvector('japanese', content));

-- =====================================================
-- PERFORMANCE OPTIMIZATION INDEXES
-- =====================================================
-- Indexes for common sorting and filtering patterns
CREATE INDEX idx_events_popularity ON events(view_count DESC, sold_tickets DESC) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;
CREATE INDEX idx_housing_popularity ON housing(view_count DESC, created_at DESC) WHERE status = 'AVAILABLE' AND deleted_at IS NULL;
CREATE INDEX idx_jobs_popularity ON jobs(view_count DESC, created_at DESC) WHERE status = 'ACTIVE' AND deleted_at IS NULL;
CREATE INDEX idx_forum_posts_popularity ON forum_posts(like_count DESC, comment_count DESC, view_count DESC) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;

-- Indexes for date range queries
CREATE INDEX idx_events_date_range ON events(start_time, end_time) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;
CREATE INDEX idx_tickets_purchase_date ON tickets(created_at) WHERE status IN ('VALID', 'USED') AND deleted_at IS NULL;
CREATE INDEX idx_notifications_recent ON notifications(created_at DESC) WHERE deleted_at IS NULL;

-- =====================================================
-- FOREIGN KEY CONSTRAINT INDEXES
-- =====================================================
-- These indexes support foreign key constraints and joins
CREATE INDEX idx_events_organizer_fk ON events(organizer_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_housing_poster_fk ON housing(poster_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_jobs_poster_fk ON jobs(poster_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_forum_posts_author_fk ON forum_posts(author_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_comments_author_fk ON comments(author_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_comments_post_fk ON comments(forum_post_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_event_fk ON tickets(event_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_purchaser_fk ON tickets(purchaser_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_user_fk ON notifications(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_file_metadata_uploader_fk ON file_metadata(uploader_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_likes_user_fk ON likes(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_reports_reporter_fk ON reports(reporter_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_audit_logs_user_fk ON audit_logs(user_id);