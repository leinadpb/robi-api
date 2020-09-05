-- Users Tables
CREATE TABLE APP_USERS (
    ID BIGSERIAL PRIMARY KEY,
    USERNAME VARCHAR(19) NOT NULL,
    FULL_NAME VARCHAR(255),
    EMAIL VARCHAR(120),
    PASSWORD VARCHAR(800) NOT NULL,
    LOCKED BOOLEAN,
    ENABLED BOOLEAN,
    REQUIRE_LOGIN BOOLEAN DEFAULT TRUE,
    LOGIN_COUNT BIGINT DEFAULT 0,
    PASSWORD_RESET_COUNT INT DEFAULT 0,
    PASSWORD_RESET_MAX_COUNT INT DEFAULT 3,
    CREATED_AT TIMESTAMP DEFAULT NOW(),
    UPDATED_AT TIMESTAMP,
    STATUS CHAR(1),
    robot_endpoint varchar(800),

    UNIQUE (USERNAME),
    UNIQUE (email)
);

-- AppUser Table Indexes
CREATE INDEX users_lower_username_idx ON APP_USERS ( lower(USERNAME) ); -- Allows efficient searches using lower(USERNAME)
CREATE INDEX users_lower_email_idx ON APP_USERS ( lower(EMAIL) ); -- Allows efficient searches using lower(EMAIL)
CREATE INDEX users_lower_status_idx ON APP_USERS ( STATUS ); -- Allows efficient searches filtering by STATUS

-- LOGIN Logs Table
CREATE TABLE LOGIN_LOG (
    ID BIGSERIAL PRIMARY KEY,
    USER_ID BIGSERIAL,
    USERNAME VARCHAR(80),
    IP_ADDRESS VARCHAR(32),
    USER_AGENT VARCHAR(255),
    STATUS CHAR(1), -- S "Success" E "Error"
    ERROR_MSG VARCHAR(255),
    ERROR_DETAILS VARCHAR(255),
    CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- ENV Variables
CREATE TABLE ENV_VARIABLES (
    env_key VARCHAR(255) PRIMARY KEY,
    env_value VARCHAR(4000)
);

-- LOGIN Log table Indexes
CREATE INDEX loginLog_status_idx ON LOGIN_LOG (STATUS); -- Allows efficient filtering by STATUS.
CREATE INDEX loginLog_userId_idx ON LOGIN_LOG (USER_ID); -- Allows efficient filtering by STATUS.

-- Function: GET AppUser By ID
-- Required: userId
CREATE OR REPLACE FUNCTION get_user_by_id (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_id BIGINT;
BEGIN
    p_id = in_json::jsonb ->> 'userId';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'robotEndpoint', U.robot_endpoint,
                             'requireLogin', U.require_login                         )
    FROM APP_USERS U WHERE ID = p_id;
END;
$$ LANGUAGE plpgsql;

-- Function: GET owner's user
-- Required: ownerId
CREATE OR REPLACE FUNCTION get_owner_users (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_owner_id BIGINT;
BEGIN
    p_owner_id = in_json::jsonb ->> 'ownerId';

    RETURN QUERY SELECT
                     json_agg( json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'requireLogin', U.require_login
                         ))
                 FROM APP_USERS U INNER JOIN APP_USERS_USERS AUU on U.ID = AUU.CUSTOM_USER_ID
                 WHERE AUU.OWNER_USER_ID = p_owner_id;
END;
$$ LANGUAGE plpgsql;

-- Function: GET AppUser By ID
-- Required: userId
CREATE OR REPLACE FUNCTION get_user_with_owner (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    info jsonb;
    p_owner_id BIGINT;
    p_custom_user_id BIGINT;
BEGIN
    info = in_json::jsonb;
    p_owner_id = info ->> 'ownerId';
    p_custom_user_id = info ->> 'objectId';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'requireLogin', U.require_login
                         )
                 FROM APP_USERS U
                    INNER JOIN APP_USERS_USERS AUU on U.ID = AUU.CUSTOM_USER_ID
                 WHERE U.ID = p_custom_user_id AND AUU.OWNER_USER_ID = p_owner_id;
END;
$$ LANGUAGE plpgsql;

-- Function: GET AppUser By email
-- Required: email
CREATE OR REPLACE FUNCTION get_user_by_email (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_email VARCHAR;
BEGIN
    p_email = in_json::jsonb ->> 'email';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'requireLogin', U.require_login
                         )
    FROM APP_USERS U WHERE EMAIL = trim(p_email);
END;
$$ LANGUAGE plpgsql;

-- Function: GET AppUser By username
-- Required: username
CREATE OR REPLACE FUNCTION get_user_by_username(in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_username VARCHAR;
BEGIN
    p_username = in_json::jsonb ->> 'username';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'requireLogin', U.require_login
                         )
                 FROM APP_USERS U WHERE lower(USERNAME) = trim(lower(p_username))
                 LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- Function: Register app_user
-- Initialize app_user STATUS with 'A' => ACTIVE
-- Required: username, email, password
CREATE OR REPLACE FUNCTION ins_user (in_json IN TEXT)
    RETURNS TABLE (document BIGINT)
AS $$
DECLARE
    info jsonb;
    p_username VARCHAR;
    p_name VARCHAR;
    p_email VARCHAR;
    p_password VARCHAR;
    added_id BIGINT;
BEGIN
    info = in_json::jsonb;
    p_username = info ->> 'username';
    p_name = info ->> 'fullName';
    p_email = info ->> 'email';
    p_password = info ->> 'password';

    INSERT INTO APP_USERS (USERNAME, FULL_NAME, EMAIL, PASSWORD, LOCKED, ENABLED, REQUIRE_LOGIN, LOGIN_COUNT, PASSWORD_RESET_COUNT, PASSWORD_RESET_MAX_COUNT, STATUS)
    VALUES (p_username, p_name, p_email, p_password, FALSE, TRUE, TRUE, 0, 0, 3, 'A')
           RETURNING ID into added_id;

    RETURN QUERY SELECT added_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Delete app_user by ID
-- Do not actually remove it, just change status to 'D' => DELETED
-- Required: userId
CREATE OR REPLACE FUNCTION rmv_user (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    p_id BIGINT;
BEGIN
    p_id = in_json::jsonb ->> 'userId';

    UPDATE APP_USERS SET STATUS = 'D', ENABLED = FALSE, LOCKED = TRUE, UPDATED_AT = NOW()
    WHERE ID = p_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Delete custom_user by ID
-- Do not actually remove it, just change status to 'D' => DELETED
-- Required: userId
CREATE OR REPLACE FUNCTION rmv_custom_user (in_json IN TEXT)
    RETURNS TABLE (document BIGINT)
AS $$
DECLARE
    info json;
    p_owner_id BIGINT;
    p_user_id BIGINT;
    deleted_id BIGINT;
BEGIN
    info = in_json::jsonb;
    p_owner_id = info ->> 'ownerId';
    p_user_id = info ->> 'objectId';

    DELETE FROM APP_USERS U
    WHERE U.ID = p_user_id
    RETURNING U.ID INTO deleted_id;

    RETURN QUERY SELECT deleted_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Update app_user (update grants and user defined roles)
-- Required: userId
CREATE OR REPLACE FUNCTION upd_user (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_user_name VARCHAR;
    p_email VARCHAR;
    p_name VARCHAR;
    p_grant jsonb;
    p_role jsonb;
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'id';
    p_user_name = info ->> 'username';
    p_email = info ->> 'email';
    p_name = info ->> 'fullName';


    UPDATE APP_USERS SET
                         USERNAME = trim(lower(p_user_name)),
                         FULL_NAME = trim(p_name),
                         EMAIL = trim(p_email),
                         UPDATED_AT = NOW()
    WHERE ID = p_id;

    -- Remove all previous grants
    DELETE FROM USER_GRANT
    WHERE user_id = p_id and grant_id <> 'custom-user';

    -- Add new desired grants
    FOR p_grant IN
        SELECT jsonb_array_elements as grantId FROM jsonb_array_elements(info -> 'grants')
    LOOP
        INSERT INTO USER_GRANT (user_id, grant_id) VALUES (p_id, p_grant ->> 'grantId');
    END loop;

    -- Return updated user
    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'email', U.email,
                             'fullName', U.full_name,
                             'locked', U.locked,
                             'requireLogin', U.require_login
                         )
                 FROM APP_USERS U WHERE ID = p_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Update app_user roleName
-- Required: userId, username
CREATE OR REPLACE FUNCTION upd_user_name (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_user_name VARCHAR;
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'userId';
    p_user_name = info ->> 'username';

    UPDATE APP_USERS SET USERNAME = trim(lower(p_user_name)), UPDATED_AT = NOW()
    WHERE ID = p_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update app_user email
-- Required: userId, email
CREATE OR REPLACE FUNCTION upd_user_email (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_user_email VARCHAR;
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'userId';
    p_user_email = info ->> 'email';

    UPDATE APP_USERS SET EMAIL = trim(lower(p_user_email)), UPDATED_AT = NOW()
    WHERE ID = p_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update app_user full roleName
-- Required: userId, fullName
CREATE OR REPLACE FUNCTION upd_user_full_name (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_fullName VARCHAR;
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'userId';
    p_fullName = info ->> 'fullName';

    UPDATE APP_USERS SET FULL_NAME = trim(p_fullName), UPDATED_AT = NOW()
    WHERE ID = p_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Insert login log
CREATE OR REPLACE FUNCTION ins_login_log (in_json IN TEXT)
    RETURNS TABLE (document BIGINT)
AS $$
DECLARE
    info json;
    p_user_id BIGINT;
    p_username VARCHAR;
    p_ip_address VARCHAR;
    p_user_agent VARCHAR;
    p_error_msg VARCHAR;
    p_error_detail VARCHAR;
    p_status VARCHAR;
    added_id BIGINT;
    current_login_count BIGINT;
BEGIN
    info = in_json::jsonb;
    p_user_id = info ->> 'userId';
    p_username = info ->> 'username';
    p_ip_address = info ->> 'ipAddress';
    p_user_agent = info ->> 'userAgent';
    p_error_msg = info ->> 'errorMessage';
    p_error_detail = info ->> 'errorDetail';
    p_status = info ->> 'status';

    INSERT INTO LOGIN_LOG (IP_ADDRESS, USER_AGENT, USER_ID, USERNAME, STATUS, ERROR_MSG, ERROR_DETAILS)
        VALUES (p_ip_address, p_user_agent, p_user_id, p_username, p_status, p_error_msg, p_error_detail)
    RETURNING ID into added_id;

    IF p_status = 'C' THEN
        SELECT LOGIN_COUNT INTO current_login_count FROM APP_USERS U WHERE U.ID = p_user_id;
        UPDATE APP_USERS U SET LOGIN_COUNT = current_login_count + 1 WHERE U.ID = p_user_id;
    ELSE
        -- TODO: Lock user account after N (look value in ENV_VARIABLES) bad login tries
    END IF;

    RETURN QUERY SELECT added_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Checks if username is available
-- Required: username
CREATE OR REPLACE FUNCTION check_username_available (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    p_username VARCHAR;
    p_count INT;
BEGIN
    p_username = in_json::jsonb ->> 'username';

    SELECT count(*) into p_count
    FROM APP_USERS WHERE lower(USERNAME) = trim(lower(p_username));

    IF p_count > 0 THEN
        RETURN QUERY SELECT FALSE;
    ELSE
        RETURN QUERY SELECT TRUE;
    END IF;

END;
$$ LANGUAGE plpgsql;

-- Function: Checks if email is available
-- Required: email
CREATE OR REPLACE FUNCTION check_email_available (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    p_email VARCHAR;
    p_count INT;
BEGIN
    p_email = in_json::jsonb ->> 'email';

    SELECT count(*) into p_count
    FROM APP_USERS WHERE lower(EMAIL) = trim(lower(p_email));

    IF p_count > 0 THEN
        RETURN QUERY SELECT FALSE;
    ELSE
        RETURN QUERY SELECT TRUE;
    END IF;

END;
$$ LANGUAGE plpgsql;
