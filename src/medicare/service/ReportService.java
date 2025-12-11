package medicare.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import medicare.model.Appointment;
import medicare.model.AppointmentStatus;
import medicare.model.Doctor;
import medicare.model.Patient;

/** Builds monthly stats and dashboard counts from appointments. */
public class ReportService {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public ReportService(PatientService patientService, DoctorService doctorService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    public ReportSummary monthly(YearMonth month) {
        List<Appointment> all = appointmentService.list();
        List<Appointment> monthAppointments = all.stream()
                .filter(a -> YearMonth.from(a.getDate()).equals(month)).collect(Collectors.toList());

        long completed = monthAppointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long canceled = monthAppointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELED).count();

        Map<String, Long> byDoctor = monthAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDoctorId, Collectors.counting()));

        Map<String, Long> byPatient = monthAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getPatientId, Collectors.counting()));

        return new ReportSummary(monthAppointments.size(), completed, canceled, byDoctor, byPatient);
    }

    public static class ReportSummary {
        public final long totalAppointments;
        public final long completedAppointments;
        public final long canceledAppointments;
        public final Map<String, Long> appointmentsByDoctor;
        public final Map<String, Long> appointmentsByPatient;

        public ReportSummary(long totalAppointments, long completedAppointments, long canceledAppointments,
                Map<String, Long> appointmentsByDoctor, Map<String, Long> appointmentsByPatient) {
            this.totalAppointments = totalAppointments;
            this.completedAppointments = completedAppointments;
            this.canceledAppointments = canceledAppointments;
            this.appointmentsByDoctor = appointmentsByDoctor;
            this.appointmentsByPatient = appointmentsByPatient;
        }
    }

    public int totalPatients() {
        return patientService.list().size();
    }

    public int totalDoctors() {
        return doctorService.list().size();
    }

    public int appointmentsToday() {
        LocalDate today = LocalDate.now();
        return (int) appointmentService.list().stream().filter(a -> a.getDate().equals(today)).count();
    }
}

