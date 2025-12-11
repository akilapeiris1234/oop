package medicare.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Stores doctor details like specialty, working days, and slots. */
public class Doctor extends Person {
    private String specialty;
    private Set<String> workingDays; // e.g., MONDAY, TUESDAY
    private List<LocalTime> timeSlots;
    private int maxAppointmentsPerDay;

    public Doctor(String fullName, String specialty, String contactNumber, String address, Set<String> workingDays,
            List<LocalTime> timeSlots, int maxAppointmentsPerDay) {
        super(fullName, contactNumber, address);
        this.specialty = specialty;
        this.workingDays = new HashSet<>(workingDays);
        this.timeSlots = new ArrayList<>(timeSlots);
        this.maxAppointmentsPerDay = maxAppointmentsPerDay;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Set<String> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Set<String> workingDays) {
        this.workingDays = new HashSet<>(workingDays);
    }

    public List<LocalTime> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<LocalTime> timeSlots) {
        this.timeSlots = new ArrayList<>(timeSlots);
    }

    public int getMaxAppointmentsPerDay() {
        return maxAppointmentsPerDay;
    }

    public void setMaxAppointmentsPerDay(int maxAppointmentsPerDay) {
        this.maxAppointmentsPerDay = maxAppointmentsPerDay;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + specialty + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Doctor doctor = (Doctor) obj;
        return Objects.equals(getId(), doctor.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

