package medicare.service;

import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import medicare.model.Doctor;

/** Handles doctor add/update/delete and finds available doctors. */
public class DoctorService {
    private final FileRepository<Doctor> repository;

    public DoctorService() {
        this.repository = new FileRepository<>(Paths.get("data"), "doctors.dat");
    }

    public List<Doctor> list() {
        return repository.loadAll();
    }

    public void add(Doctor doctor) {
        repository.update(list -> list.add(doctor));
    }

    public void update(Doctor updated) {
        repository.update(list -> {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(updated.getId())) {
                    list.set(i, updated);
                    return;
                }
            }
            throw new IllegalArgumentException("Doctor not found");
        });
    }

    public void remove(String id) {
        repository.update(list -> list.removeIf(d -> d.getId().equals(id)));
    }

    public Optional<Doctor> findById(String id) {
        return list().stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    public List<Doctor> availableFor(String dayName, LocalTime time) {
        return list().stream().filter(d -> d.getWorkingDays().contains(dayName.toUpperCase())
                && d.getTimeSlots().contains(time)).sorted(Comparator.comparing(Doctor::getFullName)).collect(Collectors.toList());
    }

    public List<Doctor> bySpecialty(String specialty) {
        return list().stream().filter(d -> d.getSpecialty().equalsIgnoreCase(specialty)).collect(Collectors.toList());
    }

    public Set<String> specialties() {
        return list().stream().map(Doctor::getSpecialty).collect(Collectors.toSet());
    }
}

