package medicare.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import medicare.model.Appointment;
import medicare.model.AppointmentStatus;
import medicare.model.Doctor;
import medicare.model.Patient;
import medicare.service.AppointmentService;
import medicare.service.DoctorService;
import medicare.service.PatientService;
import medicare.ui.ColorPalette;
import medicare.ui.components.RoundedButton;
import medicare.ui.components.RoundedPanel;
import medicare.ui.components.RoundedTextField;

/** Appointment screen with booking, auto-assign, and status updates. */
public class AppointmentPanel extends JPanel {
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DefaultTableModel model;
    private final JTable table;

    public AppointmentPanel(PatientService patientService, DoctorService doctorService,
            AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;

        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        model = new DefaultTableModel(new Object[] { "ID", "Patient", "Doctor", "Date", "Time", "Status" }, 0);
        table = new JTable(model);
        refresh();

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(form(), BorderLayout.EAST);
    }

    private JPanel form() {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JComboBox<Patient> patients = new JComboBox<>(patientService.list().toArray(new Patient[0]));
        JComboBox<Doctor> doctors = new JComboBox<>(doctorService.list().toArray(new Doctor[0]));
        JTextField specialty = new RoundedTextField(12);
        JTextField date = new RoundedTextField(12);
        date.setToolTipText("yyyy-MM-dd");
        JTextField time = new RoundedTextField(12);
        time.setToolTipText("HH:mm");

        panel.add(labeled("Patient", patients));
        panel.add(labeled("Doctor", doctors));
        panel.add(labeled("Specialty (auto-assign)", specialty));
        panel.add(labeled("Date (yyyy-MM-dd)", date));
        panel.add(labeled("Time", time));

        RoundedButton auto = new RoundedButton("Auto Assign");
        auto.addActionListener(e -> {
            try {
                LocalDate d = LocalDate.parse(date.getText());
                LocalTime t = LocalTime.parse(time.getText());
                Doctor doc = appointmentService.autoAssign(specialty.getText(), d, t);
                if (doc == null) {
                    JOptionPane.showMessageDialog(this, "No doctor available", "Notice", JOptionPane.WARNING_MESSAGE);
                } else {
                    doctors.setSelectedItem(doc);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        RoundedButton save = new RoundedButton("Book Appointment");
        save.addActionListener(e -> {
            try {
                Patient patient = (Patient) patients.getSelectedItem();
                Doctor doctor = (Doctor) doctors.getSelectedItem();
                if (patient == null || doctor == null) {
                    throw new IllegalArgumentException("Please add/select patient and doctor");
                }
                Appointment appointment = new Appointment(patient.getId(), doctor.getId(),
                        LocalDate.parse(date.getText()), LocalTime.parse(time.getText()));
                appointmentService.schedule(appointment);
                refresh();
                JOptionPane.showMessageDialog(this, "Appointment booked");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        RoundedButton complete = new RoundedButton("Mark Completed");
        complete.addActionListener(e -> updateStatus(AppointmentStatus.COMPLETED));
        RoundedButton cancel = new RoundedButton("Cancel");
        cancel.addActionListener(e -> updateStatus(AppointmentStatus.CANCELED));
        RoundedButton delay = new RoundedButton("Delayed");
        delay.addActionListener(e -> updateStatus(AppointmentStatus.DELAYED));

        panel.add(auto);
        panel.add(save);
        panel.add(complete);
        panel.add(cancel);
        panel.add(delay);
        return panel;
    }

    private JPanel labeled(String label, java.awt.Component component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        javax.swing.JLabel l = new javax.swing.JLabel(label);
        l.setForeground(ColorPalette.TEXT_SECONDARY);
        wrapper.add(l, BorderLayout.NORTH);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private void refresh() {
        model.setRowCount(0);
        List<Appointment> appointments = appointmentService.list();
        for (Appointment a : appointments) {
            Optional<Patient> patient = patientService.findById(a.getPatientId());
            Optional<Doctor> doctor = doctorService.findById(a.getDoctorId());
            model.addRow(
                    new Object[] { a.getId(), patient.map(Patient::getFullName).orElse(""), doctor.map(Doctor::getFullName).orElse(""),
                            a.getDate(), a.getTime(), a.getStatus() });
        }
    }

    private void updateStatus(AppointmentStatus status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment in the table first");
            return;
        }
        String id = (String) model.getValueAt(selectedRow, 0);
        appointmentService.updateStatus(id, status);
        refresh();
    }
}

