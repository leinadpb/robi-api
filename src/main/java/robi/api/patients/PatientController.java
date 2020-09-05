package robi.api.patients;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/v1/patient")
@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public ResponseEntity<Patient> create(HttpServletRequest req, @RequestBody Patient patient) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        patient.setOwnerId(userId);
        return new ResponseEntity<>(patientRepository.createPatient(patient), HttpStatus.CREATED);
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<Patient> update(HttpServletRequest req, @RequestBody Patient patient, @PathVariable Long patientId) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        patient.setOwnerId(userId);
        patient.setId(patientId);
        return new ResponseEntity<>(patientRepository.updatePatient(patient), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getPatients(HttpServletRequest req) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return new ResponseEntity<>(patientRepository.getByOwner(userId), HttpStatus.OK);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatient(HttpServletRequest req, @PathVariable Long patientId) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return new ResponseEntity<>(patientRepository.getById(patientId), HttpStatus.OK);
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Boolean> delete(HttpServletRequest req, @PathVariable Long patientId) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return new ResponseEntity<>(patientRepository.deletePatient(patientId, userId), HttpStatus.OK);
    }

    @GetMapping("/progress/visual/{patientId}/{amount}")
    public ResponseEntity<Boolean> updateVisual(HttpServletRequest req, @PathVariable Long patientId,
                                                      @PathVariable Double amount) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        PatientProgress progress = new PatientProgress();
        progress.setOwnerId(userId);
        progress.setPatientId(patientId);
        progress.setProgress(amount);
        return new ResponseEntity<>(patientRepository.updateVisual(progress), HttpStatus.OK);
    }

    @GetMapping("/progress/touch/{patientId}/{amount}")
    public ResponseEntity<Boolean> updateTouch(HttpServletRequest req, @PathVariable Long patientId,
                                                @PathVariable Double amount) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        PatientProgress progress = new PatientProgress();
        progress.setOwnerId(userId);
        progress.setPatientId(patientId);
        progress.setProgress(amount);
        return new ResponseEntity<>(patientRepository.updateTouch(progress), HttpStatus.OK);
    }

    @GetMapping("/progress/vestibular/{patientId}/{amount}")
    public ResponseEntity<Boolean> updateVestibular(HttpServletRequest req, @PathVariable Long patientId,
                                               @PathVariable Double amount) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        PatientProgress progress = new PatientProgress();
        progress.setOwnerId(userId);
        progress.setPatientId(patientId);
        progress.setProgress(amount);
        return new ResponseEntity<>(patientRepository.updateVestibular(progress), HttpStatus.OK);
    }

    @GetMapping("/progress/auditive/{patientId}/{amount}")
    public ResponseEntity<Boolean> updateAuditive(HttpServletRequest req, @PathVariable Long patientId,
                                                    @PathVariable Double amount) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        PatientProgress progress = new PatientProgress();
        progress.setOwnerId(userId);
        progress.setPatientId(patientId);
        progress.setProgress(amount);
        return new ResponseEntity<>(patientRepository.updateAuditive(progress), HttpStatus.OK);
    }
}
