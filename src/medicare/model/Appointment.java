package medicare.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** Connects a patient and doctor on a date/time with a status. */
public class Appointment implements Serializable {
    private final String id;
    private final String patientId;
    private final String doctorId;
    private LocalDate date;
    private LocalTime time;
    private AppointmentStatus status;

    public Appointment(String patientId, String doctorId, LocalDate date, LocalTime time) {
        this.id = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}

