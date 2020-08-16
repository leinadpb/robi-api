CREATE TABLE TEST_ROBI (
    NAME VARCHAR(255)
);

INSERT INTO TEST_ROBI (NAME) VALUES ('working hard!');
INSERT INTO TEST_ROBI (NAME) VALUES ('Hhahahaha');
INSERT INTO TEST_ROBI (NAME) VALUES ('The Pirate Bay');
INSERT INTO TEST_ROBI (NAME) VALUES ('Algo');

-- Function: GET all from TEST_PBA
-- Receives: "roleName" String
CREATE OR REPLACE FUNCTION get_all_from_test_robi_like (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_name VARCHAR;
BEGIN
    p_name = in_json::jsonb ->> 'name';

    RETURN QUERY SELECT json_agg(TEST_ROBI) FROM TEST_ROBI WHERE lower(NAME) like '%' || lower(p_name) || '%';
END;
$$ LANGUAGE plpgsql;

-- Function: GET all from TEST_PBA
CREATE OR REPLACE FUNCTION get_all_from_test_robi ()
    RETURNS TABLE (document json)
AS $$
    BEGIN
        RETURN QUERY SELECT json_agg(TESTS) FROM (SELECT * FROM TEST_ROBI) TESTS;
    END;
$$ LANGUAGE plpgsql;