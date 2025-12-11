package medicare.service;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import medicare.model.Appointment;
import medicare.model.AppointmentStatus;
import medicare.model.Doctor;
import medicare.model.Patient;

/** Books appointments, prevents double booking, and updates status. */
public class AppointmentService {
    private final FileRepository<Appointment> repository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final NotificationService notificationService;

    public AppointmentService(PatientService patientService, DoctorService doctorService,
            NotificationService notificationService) {
        this.repository = new FileRepository<>(Paths.get("data"), "appointments.dat");
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.notificationService = notificationService;
    }

    public List<Appointment> list() {
        return repository.loadAll();
    }

    public void schedule(Appointment appointment) {
        repository.update(list -> {
            boolean slotTaken = list.stream().anyMatch(existing -> existing.getDoctorId().equals(appointment.getDoctorId())
                    && existing.getDate().equals(appointment.getDate())
                    && existing.getTime().equals(appointment.getTime()));
            if (slotTaken) {
                throw new IllegalArgumentException("Time slot already booked");
            }
            list.add(appointment);
            notifyScheduling(appointment);
        });
    }

    public void updateStatus(String appointmentId, AppointmentStatus status) {
        repository.update(list -> {
            for (Appointment appt : list) {
                if (appt.getId().equals(appointmentId)) {
                    appt.setStatus(status);
                    notificationService.push("Appointment " + status.name(), appt.getPatientId());
                    return;
                }
            }
            throw new IllegalArgumentException("Appointment not found");
        });
    }

    public List<Appointment> byDate(LocalDate date) {
        return list().stream().filter(a -> a.getDate().equals(date)).collect(Collectors.toList());
    }

    public Optional<Appointment> findById(String id) {
        return list().stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public Doctor autoAssign(String specialty, LocalDate date, LocalTime time) {
        List<Doctor> candidates = doctorService.bySpecialty(specialty).stream()
                .filter(d -> d.getWorkingDays().contains(date.getDayOfWeek().name())
                        && d.getTimeSlots().contains(time))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            return null;
        }
        // choose doctor with fewest appointments that day
        return candidates.stream().min(Comparator.comparingInt(d -> (int) list().stream()
                .filter(a -> a.getDoctorId().equals(d.getId()) && a.getDate().equals(date)).count())).orElse(null);
    }

    private void notifyScheduling(Appointment appointment) {
        Patient patient = patientService.findById(appointment.getPatientId()).orElse(null);
        Doctor doctor = doctorService.findById(appointment.getDoctorId()).orElse(null);
        if (patient != null) {
            notificationService.push("Appointment booked for " + appointment.getDate() + " at " + appointment.getTime(),
                    patient.getId());
        }
        if (doctor != null) {
            notificationService.push("New appointment with " + (patient != null ? patient.getFullName() : "patient"),
                    doctor.getId());
        }
    }
}

