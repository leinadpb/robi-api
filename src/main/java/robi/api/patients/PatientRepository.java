package robi.api.patients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;
import robi.api.common.ObjectWithOwner;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

@Repository
public class PatientRepository extends BaseRepository<Patient> {
    public PatientRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }

    public List<Patient> getByOwner(Long ownerId) {
        return all("get_owner_patients", ownerId, "ownerId");
    }

    public Patient getById(Long patientId) {
        return (Patient) execute(Patient.class,"get_patient_by_id", patientId, "patientId");
    }

    public Patient createPatient(Patient patient) {
        return (Patient) execute(Patient.class,"ins_patient", patient);
    }

    public Patient updatePatient(Patient patient) {
        return (Patient) execute(Patient.class,"upd_patient", patient);
    }

    public Boolean deletePatient(Long patientId, Long ownerId) {
        return (Boolean) execute("rmv_patient", new ObjectWithOwner(patientId, ownerId), Types.BOOLEAN);
    }

    public Boolean updateAuditive(PatientProgress progress) {
        return (Boolean) execute("upd_patient_auditive_progress", progress, Types.BOOLEAN);
    }

    public Boolean updateTouch(PatientProgress progress) {
        return (Boolean) execute("upd_patient_touch_progress", progress, Types.BOOLEAN);
    }

    public Boolean updateVisual(PatientProgress progress) {
        return (Boolean) execute("upd_patient_visual_progress", progress, Types.BOOLEAN);
    }

    public Boolean updateVestibular(PatientProgress progress) {
        return (Boolean) execute("upd_patient_vestibular_progress", progress, Types.BOOLEAN);
    }
}
