create table patients (
   ID BIGSERIAL PRIMARY KEY,
   APP_USER_ID BIGINT,
   FULL_NAME VARCHAR(255),
   DESCRIPTION VARCHAR(255),
   BIRTH_DATE VARCHAR(255),
   progress_visual NUMERIC(10, 2) DEFAULT 0.00,
   progress_auditivo NUMERIC(10, 2) DEFAULT 0.00,
   progress_tactil NUMERIC(10, 2) DEFAULT 0.00,
   progress_vestibular NUMERIC(10, 2) DEFAULT 0.00,

   FOREIGN KEY (APP_USER_ID) REFERENCES APP_USERS(ID)
);


-- Function: GET Patient by id
-- Required: patientId
CREATE OR REPLACE FUNCTION get_patient_by_id (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_id BIGINT;
BEGIN
    p_id = in_json::jsonb ->> 'patientId';

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'fullName', U.full_name,
                             'ownerId', U.APP_USER_ID,
                             'fullName', U.FULL_NAME,
                             'description', U.DESCRIPTION,
                             'birthDate', U.BIRTH_DATE,
                             'visualProgress', U.progress_visual,
                             'auditiveProgress', U.progress_auditivo,
                             'touchProgress', U.progress_tactil,
                             'vestibularProgress', U.progress_vestibular
                         )
                 FROM patients U WHERE ID = p_id;
END;
$$ LANGUAGE plpgsql;

-- Function: GET owner's patients
-- Required: ownerId
CREATE OR REPLACE FUNCTION get_owner_patients (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    p_owner_id BIGINT;
BEGIN
    p_owner_id = in_json::jsonb ->> 'ownerId';

    RETURN QUERY SELECT
                     json_agg(
                             json_build_object(
                                     'id', U.ID,
                                     'fullName', U.full_name,
                                     'ownerId', U.APP_USER_ID,
                                     'fullName', U.FULL_NAME,
                                     'description', U.DESCRIPTION,
                                     'birthDate', U.BIRTH_DATE,
                                     'visualProgress', U.progress_visual,
                                     'auditiveProgress', U.progress_auditivo,
                                     'touchProgress', U.progress_tactil,
                                     'vestibularProgress', U.progress_vestibular
                                 )
                         )
                 FROM patients U WHERE U.APP_USER_ID = p_owner_id;
END;
$$ LANGUAGE plpgsql;


-- Function: Create new patient
CREATE OR REPLACE FUNCTION ins_patient (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    info jsonb;
    p_full_name VARCHAR(255);
    p_owner_id BIGINT;
    p_description VARCHAR(255);
    p_birth_date DATE;
    p_visual_progress NUMERIC(10,2);
    p_auditive_progress NUMERIC(10,2);
    p_touch_progress NUMERIC(10,2);
    p_vestibular_progress NUMERIC(10,2);
    added_id BIGINT;
BEGIN
    info = in_json::jsonb;
    p_full_name = info ->> 'fullName';
    p_owner_id = info ->> 'ownerId';
    p_description = info ->> 'description';
    p_birth_date = info ->> 'birthDate';
    p_visual_progress = info ->> 'visualProgress';
    p_auditive_progress = info ->> 'auditiveProgress';
    p_touch_progress = info ->> 'touchProgress';
    p_vestibular_progress = info ->> 'vestibularProgress';

    INSERT INTO patients (APP_USER_ID, FULL_NAME, DESCRIPTION, BIRTH_DATE, progress_visual, progress_auditivo, progress_tactil, progress_vestibular)
    VALUES (p_owner_id, p_full_name, p_description, p_birth_date, p_visual_progress, p_auditive_progress, p_touch_progress, p_vestibular_progress)
           RETURNING ID into added_id;

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'fullName', U.full_name,
                             'ownerId', U.APP_USER_ID,
                             'fullName', U.FULL_NAME,
                             'description', U.DESCRIPTION,
                             'birthDate', U.BIRTH_DATE,
                             'visualProgress', U.progress_visual,
                             'auditiveProgress', U.progress_auditivo,
                             'touchProgress', U.progress_tactil,
                             'vestibularProgress', U.progress_vestibular
                         )
                 FROM patients U WHERE ID = added_id;
END;
$$ LANGUAGE plpgsql;

-- Function: Delete patient with patientId and ownerId
CREATE OR REPLACE FUNCTION rmv_patient (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    p_id BIGINT;
    p_owner_id BIGINT;
BEGIN
    p_id = in_json::jsonb ->> 'objectId';
    p_owner_id = in_json::jsonb ->> 'ownerId';

    DELETE FROM patients
        where ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update patient visual progress
CREATE OR REPLACE FUNCTION upd_patient_visual_progress (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_owner_id BIGINT;
    p_progress NUMERIC(10,2);
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'patientId';
    p_progress = info ->> 'progress';
    p_owner_id = info ->> 'ownerId';

    UPDATE patients SET progress_visual = p_progress
    WHERE ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update patient touch progress
CREATE OR REPLACE FUNCTION upd_patient_touch_progress (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_owner_id BIGINT;
    p_progress NUMERIC(10,2);
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'patientId';
    p_progress = info ->> 'progress';
    p_owner_id = info ->> 'ownerId';

    UPDATE patients SET progress_tactil = p_progress
    WHERE ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update patient vestibular progress
CREATE OR REPLACE FUNCTION upd_patient_vestibular_progress (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_owner_id BIGINT;
    p_progress NUMERIC(10,2);
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'patientId';
    p_progress = info ->> 'progress';
    p_owner_id = info ->> 'ownerId';

    UPDATE patients SET progress_vestibular = p_progress
    WHERE ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update patient auditive progress
CREATE OR REPLACE FUNCTION upd_patient_auditive_progress (in_json IN TEXT)
    RETURNS TABLE (document BOOLEAN)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_owner_id BIGINT;
    p_progress NUMERIC(10,2);
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'patientId';
    p_progress = info ->> 'progress';
    p_owner_id = info ->> 'ownerId';

    UPDATE patients SET progress_auditivo = p_progress
    WHERE ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function: Update patient visual progress
CREATE OR REPLACE FUNCTION upd_patient (in_json IN TEXT)
    RETURNS TABLE (document json)
AS $$
DECLARE
    info jsonb;
    p_id BIGINT;
    p_owner_id BIGINT;
    p_name VARCHAR(255);
    p_description VARCHAR(255);
BEGIN
    info = in_json::jsonb;
    p_id = info ->> 'id';
    p_owner_id = info ->> 'ownerId';
    p_name = info ->> 'fullName';
    p_description = info ->> 'description';

    UPDATE patients SET FULL_NAME = p_name, DESCRIPTION = p_description
    WHERE ID = p_id AND APP_USER_ID = p_owner_id;

    RETURN QUERY SELECT
                     json_build_object(
                             'id', U.ID,
                             'fullName', U.full_name,
                             'ownerId', U.APP_USER_ID,
                             'fullName', U.FULL_NAME,
                             'description', U.DESCRIPTION,
                             'birthDate', U.BIRTH_DATE,
                             'visualProgress', U.progress_visual,
                             'auditiveProgress', U.progress_auditivo,
                             'touchProgress', U.progress_tactil,
                             'vestibularProgress', U.progress_vestibular
                         )
                 FROM patients U WHERE ID = p_id;
END;
$$ LANGUAGE plpgsql;