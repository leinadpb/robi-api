alter table app_users add robot_endpoint varchar(800);


update app_users set robot_endpoint = 'https://occurrent-mayfly-6192.dataplicity.io'
where id = 1;


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
                             'requireLogin', U.require_login
                         )
                 FROM APP_USERS U WHERE ID = p_id;
END;
$$ LANGUAGE plpgsql;