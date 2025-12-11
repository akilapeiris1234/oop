package medicare.service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import medicare.model.Patient;

/** Handles patient add/update/delete and checks for duplicates. */
public class PatientService {
    private final FileRepository<Patient> repository;

    public PatientService() {
        this.repository = new FileRepository<>(Paths.get("data"), "patients.dat");
    }

    public List<Patient> list() {
        return repository.loadAll();
    }

    public void add(Patient patient) {
        repository.update(list -> {
            boolean duplicate = list.stream().anyMatch(existing -> existing.getNicOrPassport()
                    .equalsIgnoreCase(patient.getNicOrPassport())
                    || existing.getContactNumber().equalsIgnoreCase(patient.getContactNumber()));
            if (duplicate) {
                throw new IllegalArgumentException("Duplicate NIC or contact");
            }
            list.add(patient);
        });
    }

    public void update(Patient updated) {
        repository.update(list -> {
            for (int i = 0; i < list.size(); i++) {
                Patient p = list.get(i);
                if (p.getId().equals(updated.getId())) {
                    list.set(i, updated);
                    return;
                }
            }
            throw new IllegalArgumentException("Patient not found");
        });
    }

    public void remove(String id) {
        repository.update(list -> list.removeIf(p -> p.getId().equals(id)));
    }

    public Optional<Patient> findById(String id) {
        return list().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Patient> search(String query) {
        String normalized = query.toLowerCase();
        return list().stream().filter(p -> p.getFullName().toLowerCase().contains(normalized)
                || p.getNicOrPassport().toLowerCase().contains(normalized)
                || p.getContactNumber().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }
}

