-- Users Robots table
CREATE TABLE APP_ROBOTS (
   ID BIGSERIAL PRIMARY KEY,
   NAME VARCHAR(255),
   CONNECTION VARCHAR(4000),
   PASSWORD VARCHAR(800),
   USERNAME VARCHAR(800),
   ENABLED BOOLEAN,
   CREATED_AT TIMESTAMP DEFAULT NOW(),
   UPDATED_AT TIMESTAMP
);

CREATE TABLE USER_ROBOTS (
    USER_ID BIGINT,
    ROBOT_ID BIGINT,

    FOREIGN KEY (USER_ID) REFERENCES APP_USERS (ID),
    FOREIGN KEY (ROBOT_ID) REFERENCES APP_ROBOTS (ID),

    UNIQUE (USER_ID, ROBOT_ID),
    UNIQUE (ROBOT_ID)
);

-- Function: GET all app available robots
-- Required: userId
CREATE OR REPLACE FUNCTION get_all_robots ()
    RETURNS TABLE (document json)
AS $$
BEGIN
    RETURN QUERY SELECT
                    json_agg(
                            json_build_object(
                                    'id', U.ID,
                                    'username', U.username,
                                    'password', U.password,
                                    'connection', U.CONNECTION,
                                    'enabled', U.ENABLED,
                                    'createdAt', U.CREATED_AT,
                                    'updatedAt', U.UPDATED_AT
                            )
                    )
                 FROM APP_ROBOTS U;
END;
$$ LANGUAGE plpgsql;

-- Function: GET all app available robots
-- Required: userId
CREATE OR REPLACE FUNCTION get_user_robots (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_id BIGINT;
BEGIN
    p_id = in_json::jsonb ->> 'userId';

    RETURN QUERY SELECT
                    json_agg(
                        json_build_object(
                            'id', U.ID,
                            'username', U.username,
                            'password', U.password,
                            'connection', U.CONNECTION,
                            'enabled', U.ENABLED,
                            'createdAt', U.CREATED_AT,
                            'updatedAt', U.UPDATED_AT
                        ))
                 FROM APP_ROBOTS U INNER JOIN USER_ROBOTS UR on U.ID = UR.ROBOT_ID
                    WHERE UR.USER_ID = p_id;
END;
$$ LANGUAGE plpgsql;

-- Function: GET all app available robots
-- Required: userId
CREATE OR REPLACE FUNCTION get_user_robot_by_connection_and_user (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    userId BIGINT;
    robotId BIGINT;
BEGIN
    userId = in_json::jsonb ->> 'ownerId';
    robotId = in_json::jsonb ->> 'objectId';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'connection', U.CONNECTION,
                             'enabled', U.ENABLED,
                             'createdAt', U.CREATED_AT,
                             'updatedAt', U.UPDATED_AT                         )
                 FROM APP_ROBOTS U INNER JOIN USER_ROBOTS UR on U.ID = UR.ROBOT_ID
                 WHERE UR.USER_ID = userId AND UR.ROBOT_ID = robotId;
END;
$$ LANGUAGE plpgsql;

-- Function: Create a new robot in the app
CREATE OR REPLACE FUNCTION create_app_robot (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_name VARCHAR;
    p_connection VARCHAR;
    p_username VARCHAR;
    p_password VARCHAR;
    added_id BIGINT;
BEGIN
    p_name = in_json::jsonb ->> 'name';
    p_connection = in_json::jsonb ->> 'connection';
    p_username = in_json::jsonb ->> 'username';
    p_password = in_json::jsonb ->> 'password';

    INSERT INTO APP_ROBOTS (NAME, CONNECTION, PASSWORD, USERNAME, ENABLED, CREATED_AT, UPDATED_AT)
    VALUES (p_name, p_connection, p_password, p_username, true, now(), null)
    RETURNING ID INTO added_id;


    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'connection', U.CONNECTION,
                             'enabled', U.ENABLED,
                             'createdAt', U.CREATED_AT,
                             'updatedAt', U.UPDATED_AT                         )
                 FROM APP_ROBOTS U WHERE U.ID = added_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Adds a robot to a user
CREATE OR REPLACE FUNCTION add_robot_to_user (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
   p_user_id BIGINT;
    p_robot_id BIGINT;
BEGIN
    p_user_id = in_json::jsonb ->> 'ownerId';
    p_robot_id = in_json::jsonb ->> 'objectId';

    INSERT INTO USER_ROBOTS (USER_ID, ROBOT_ID) VALUES (p_user_id, p_robot_id);


    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'username', U.username,
                             'password', U.password,
                             'connection', U.CONNECTION,
                             'enabled', U.ENABLED,
                             'createdAt', U.CREATED_AT,
                             'updatedAt', U.UPDATED_AT                         )
                 FROM APP_ROBOTS U INNER JOIN USER_ROBOTS UR on U.ID = UR.ROBOT_ID
                    WHERE U.ID = p_robot_id and UR.USER_ID = p_user_id;

END;
$$ LANGUAGE plpgsql;