-- =============================================================================
-- FlyerBoard local-dev seed data
--
-- Apply with:  ./scripts/flyerboard_seed_test_data.sh  (from repo root)
-- This runs `supabase db reset` which wipes the DB and re-applies all
-- migrations + this file from scratch.
--
-- Seeded users have no password. Login via OTP / magic link.
-- Check InBucket at http://localhost:54324 for the OTP code after entering an email.
--
-- UUID key:
--   auth/app users : 00000000-0000-0000-0001-xxxxxxxxxxxx
--   flyers         : 00000000-0000-0000-0002-xxxxxxxxxxxx
-- =============================================================================

-- =============================================================================
-- SECTION 1: AUTH USERS (7 total: 2 admins, 5 regular users)
-- Column layout matches a confirmed-working locally-created user.
-- Login via OTP / magic-link — encrypted_password is NULL (no password set).
-- Token columns use '' (empty string), not NULL, to match GoTrue's own writes.
-- confirmed_at is a GENERATED column; do NOT include it here.
-- email_verified in identity_data is false (set by GoTrue for OTP users);
--   email_verified in raw_user_meta_data is true (confirms email is valid).
-- =============================================================================

INSERT INTO auth.users (
    instance_id, id, aud, role, email,
    encrypted_password, email_confirmed_at,
    invited_at,
    confirmation_token, confirmation_sent_at,
    recovery_token, recovery_sent_at,
    email_change_token_new, email_change, email_change_sent_at,
    last_sign_in_at,
    raw_app_meta_data, raw_user_meta_data,
    created_at, updated_at,
    phone, phone_confirmed_at,
    phone_change, phone_change_token, phone_change_sent_at,
    email_change_token_current, email_change_confirm_status,
    banned_until,
    reauthentication_token, reauthentication_sent_at,
    is_sso_user, deleted_at, is_anonymous
) VALUES
-- Admins (2)
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000001','authenticated','authenticated','admin1@dev.local', NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000002','authenticated','authenticated','admin2@dev.local', NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
-- Regular users (5)
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000003','authenticated','authenticated','user1@dev.local',  NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000004','authenticated','authenticated','user2@dev.local',  NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000005','authenticated','authenticated','user3@dev.local',  NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000006','authenticated','authenticated','user4@dev.local',  NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false),
('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0001-000000000007','authenticated','authenticated','user5@dev.local',  NULL,NOW(), NULL, '',NULL, '',NULL, '',  '',NULL, NULL, '{"provider":"email","providers":["email"]}','{"email_verified":true}', NOW(),NOW(), NULL,NULL, '',  '',NULL, '',0, NULL, '',NULL, false,NULL,false)
ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- SECTION 1b: AUTH IDENTITIES
-- Required for GoTrue to resolve email logins.
-- =============================================================================

INSERT INTO auth.identities (provider_id, user_id, identity_data, provider, last_sign_in_at, created_at, updated_at) VALUES
('00000000-0000-0000-0001-000000000001','00000000-0000-0000-0001-000000000001','{"sub":"00000000-0000-0000-0001-000000000001","email":"admin1@dev.local","email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000002','00000000-0000-0000-0001-000000000002','{"sub":"00000000-0000-0000-0001-000000000002","email":"admin2@dev.local","email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000003','00000000-0000-0000-0001-000000000003','{"sub":"00000000-0000-0000-0001-000000000003","email":"user1@dev.local", "email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000004','00000000-0000-0000-0001-000000000004','{"sub":"00000000-0000-0000-0001-000000000004","email":"user2@dev.local", "email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000005','00000000-0000-0000-0001-000000000005','{"sub":"00000000-0000-0000-0001-000000000005","email":"user3@dev.local", "email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000006','00000000-0000-0000-0001-000000000006','{"sub":"00000000-0000-0000-0001-000000000006","email":"user4@dev.local", "email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW()),
('00000000-0000-0000-0001-000000000007','00000000-0000-0000-0001-000000000007','{"sub":"00000000-0000-0000-0001-000000000007","email":"user5@dev.local", "email_verified":false,"phone_verified":false}', 'email',NOW(),NOW(),NOW())
ON CONFLICT (provider, provider_id) DO NOTHING;

-- =============================================================================
-- SECTION 2: APP USERS (public.users — display name, same UUIDs as auth.users)
-- =============================================================================

INSERT INTO public.users (id, first_name, last_name) VALUES
('00000000-0000-0000-0001-000000000001','Ava',   'Administrator'),
('00000000-0000-0000-0001-000000000002','Marcus','Moderator'),
('00000000-0000-0000-0001-000000000003','Uma',   'Userton'),
('00000000-0000-0000-0001-000000000004','Leo',   'Lister'),
('00000000-0000-0000-0001-000000000005','Nina',  'Newman'),
('00000000-0000-0000-0001-000000000006','Owen',  'Oliver'),
('00000000-0000-0000-0001-000000000007','Priya', 'Patel')
ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- SECTION 3: USER_PROFILES (role — used for authorization)
-- =============================================================================

INSERT INTO public.user_profiles (id, role) VALUES
('00000000-0000-0000-0001-000000000001','admin'),
('00000000-0000-0000-0001-000000000002','admin'),
('00000000-0000-0000-0001-000000000003','user'),
('00000000-0000-0000-0001-000000000004','user'),
('00000000-0000-0000-0001-000000000005','user'),
('00000000-0000-0000-0001-000000000006','user'),
('00000000-0000-0000-0001-000000000007','user')
ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- SECTION 4: FLYERS (14 total, covering every status)
-- file_path matches the flyer's own id — this is the storage object key
-- convention used by FlyerService (see SupabaseFlyerDatastore). No object is
-- seeded in Storage itself, so signed URLs for these rows will 404; that's
-- expected for local-dev DB seed data (same approach edifikana uses for
-- image_url — no Storage dependency).
-- =============================================================================

INSERT INTO public.flyers (id, title, description, file_path, status, expires_at, uploader_id, created_at, updated_at) VALUES
-- Approved (6) — visible to anonymous/public readers
('00000000-0000-0000-0002-000000000001','Community Yard Sale',            'Multi-family yard sale, Saturday 8am-2pm. Furniture, tools, kids clothes.', '00000000-0000-0000-0002-000000000001','approved', NOW() + INTERVAL '14 days', '00000000-0000-0000-0001-000000000003', NOW() - INTERVAL '5 days',  NOW() - INTERVAL '4 days'),
('00000000-0000-0000-0002-000000000002','Lost Cat - Whiskers',            'Orange tabby, last seen near Elm St. Please call if found, reward offered.', '00000000-0000-0000-0002-000000000002','approved', NOW() + INTERVAL '30 days', '00000000-0000-0000-0001-000000000004', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days'),
('00000000-0000-0000-0002-000000000003','Local Farmers Market Every Saturday', 'Fresh produce, baked goods, and local crafts every Saturday 9am-1pm at the town square.', '00000000-0000-0000-0002-000000000003','approved', NOW() + INTERVAL '60 days', '00000000-0000-0000-0001-000000000005', NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days'),
('00000000-0000-0000-0002-000000000004','Guitar Lessons for Beginners',   'Patient instructor, all ages welcome. First lesson free.', '00000000-0000-0000-0002-000000000004','approved', NULL,                       '00000000-0000-0000-0001-000000000003', NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days'),
('00000000-0000-0000-0002-000000000012','Piano Recital - Spring Concert', 'Student recital open to the public, Sunday 4pm at the community center.', '00000000-0000-0000-0002-000000000012','approved', NOW() + INTERVAL '7 days',  '00000000-0000-0000-0001-000000000007', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '1 days'),
('00000000-0000-0000-0002-000000000014','Neighborhood Watch Meeting',     'Monthly meeting, first Tuesday of every month at the library.', '00000000-0000-0000-0002-000000000014','approved', NULL,                       '00000000-0000-0000-0001-000000000004', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '5 days'),
-- Pending (4) — awaiting admin review
('00000000-0000-0000-0002-000000000005','Room for Rent - Downtown',       'Furnished room, shared kitchen/bath, walking distance to transit.', '00000000-0000-0000-0002-000000000005','pending',  NOW() + INTERVAL '21 days', '00000000-0000-0000-0001-000000000006', NOW() - INTERVAL '1 days',  NOW() - INTERVAL '1 days'),
('00000000-0000-0000-0002-000000000006','Garage Band Seeking Drummer',    'Looking for a drummer for weekend jam sessions, all skill levels fine.', '00000000-0000-0000-0002-000000000006','pending',  NOW() + INTERVAL '45 days', '00000000-0000-0000-0001-000000000007', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours'),
('00000000-0000-0000-0002-000000000007','Free Furniture - Moving Sale',   'Couch, dining table, bookshelf, all free, must pick up by Friday.', '00000000-0000-0000-0002-000000000007','pending',  NULL,                       '00000000-0000-0000-0001-000000000004', NOW() - INTERVAL '2 hours',  NOW() - INTERVAL '2 hours'),
('00000000-0000-0000-0002-000000000013','Handyman Services Available',    'Odd jobs, minor repairs, reasonable rates, references available.', '00000000-0000-0000-0002-000000000013','pending',  NOW() + INTERVAL '30 days', '00000000-0000-0000-0001-000000000002', NOW() - INTERVAL '4 hours',  NOW() - INTERVAL '4 hours'),
-- Rejected (2) — moderation rejected
('00000000-0000-0000-0002-000000000008','Miracle Weight Loss Pills - Buy Now!!', 'Lose 30 lbs in 30 days guaranteed! Limited time offer, click now!', '00000000-0000-0000-0002-000000000008','rejected', NOW() + INTERVAL '10 days', '00000000-0000-0000-0001-000000000005', NOW() - INTERVAL '8 days',  NOW() - INTERVAL '7 days'),
('00000000-0000-0000-0002-000000000009','Unlicensed Dog Walking Service', 'Cheap dog walking, no licensing or insurance needed to inquire.', '00000000-0000-0000-0002-000000000009','rejected', NULL,                       '00000000-0000-0000-0001-000000000006', NOW() - INTERVAL '9 days',  NOW() - INTERVAL '8 days'),
-- Archived (2) — past events, no longer active
('00000000-0000-0000-0002-000000000010','Summer Block Party 2025',       'Annual block party with food trucks and live music.', '00000000-0000-0000-0002-000000000010','archived', NOW() - INTERVAL '30 days', '00000000-0000-0000-0001-000000000003', NOW() - INTERVAL '90 days', NOW() - INTERVAL '30 days'),
('00000000-0000-0000-0002-000000000011','Winter Coat Drive',             'Donate gently-used winter coats, drop-off bins at the community center.', '00000000-0000-0000-0002-000000000011','archived', NOW() - INTERVAL '60 days', '00000000-0000-0000-0001-000000000001', NOW() - INTERVAL '120 days', NOW() - INTERVAL '60 days')
ON CONFLICT (id) DO NOTHING;
