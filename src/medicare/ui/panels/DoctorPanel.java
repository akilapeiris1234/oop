package medicare.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import medicare.model.Doctor;
import medicare.service.DoctorService;
import medicare.ui.ColorPalette;
import medicare.ui.components.RoundedButton;
import medicare.ui.components.RoundedPanel;
import medicare.ui.components.RoundedTextField;

/** Doctor screen with table plus add, update, delete, and schedule fields. */
public class DoctorPanel extends JPanel {
    private final DoctorService doctorService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField nameField = new RoundedTextField(12);
    private final JTextField specialtyField = new RoundedTextField(12);
    private final JTextField contactField = new RoundedTextField(12);
    private final JTextField addressField = new RoundedTextField(12);
    private final JTextField daysField = new RoundedTextField(12);
    private final JTextField timesField = new RoundedTextField(12);
    private final JTextField maxPerDayField = new RoundedTextField(12);
    private String selectedId;

    public DoctorPanel(DoctorService doctorService) {
        this.doctorService = doctorService;
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        model = new DefaultTableModel(new Object[] { "ID", "Name", "Specialty", "Days", "Slots", "Max/Day" }, 0);
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID visually
        table.getSelectionModel().addListSelectionListener(e -> loadSelected());
        refresh();

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(form(), BorderLayout.EAST);
    }

    private JPanel form() {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        daysField.setToolTipText("Comma separated days e.g. MONDAY,TUESDAY");
        timesField.setToolTipText("Comma separated 24h times e.g. 09:00,10:00");

        panel.add(labeled("Full Name", nameField));
        panel.add(labeled("Specialty", specialtyField));
        panel.add(labeled("Contact", contactField));
        panel.add(labeled("Address", addressField));
        panel.add(labeled("Working Days", daysField));
        panel.add(labeled("Time Slots", timesField));
        panel.add(labeled("Max Appointments/Day", maxPerDayField));

        RoundedButton save = new RoundedButton("Add Doctor");
        save.addActionListener(e -> {
            try {
                Set<String> daySet = Arrays.stream(daysField.getText().split(",")).map(String::trim)
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet());
                List<LocalTime> slots = Arrays.stream(timesField.getText().split(",")).map(String::trim)
                        .map(LocalTime::parse).collect(Collectors.toList());
                Doctor doctor = new Doctor(nameField.getText(), specialtyField.getText(), contactField.getText(),
                        addressField.getText(), daySet, slots, Integer.parseInt(maxPerDayField.getText()));
                doctorService.add(doctor);
                refresh();
                JOptionPane.showMessageDialog(this, "Doctor added");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);

        RoundedButton update = new RoundedButton("Update Doctor");
        update.addActionListener(e -> updateSelected());
        panel.add(update);

        RoundedButton delete = new RoundedButton("Delete Doctor");
        delete.addActionListener(e -> deleteSelected());
        panel.add(delete);
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
        List<Doctor> doctors = doctorService.list();
        model.setRowCount(0);
        doctors.forEach(d -> model.addRow(new Object[] { d.getId(), d.getFullName(), d.getSpecialty(),
                d.getWorkingDays(), d.getTimeSlots(), d.getMaxAppointmentsPerDay() }));
        selectedId = null;
        table.clearSelection();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        selectedId = (String) model.getValueAt(modelRow, 0);
        nameField.setText((String) model.getValueAt(modelRow, 1));
        specialtyField.setText((String) model.getValueAt(modelRow, 2));
        daysField.setText(model.getValueAt(modelRow, 3).toString().replaceAll("[\\[\\]]", ""));
        timesField.setText(model.getValueAt(modelRow, 4).toString().replaceAll("[\\[\\]]", ""));
        maxPerDayField.setText(String.valueOf(model.getValueAt(modelRow, 5)));
        contactField.setText(""); // contact/address not stored in table; reload from service
        addressField.setText("");
        doctorService.findById(selectedId).ifPresent(d -> {
            contactField.setText(d.getContactNumber());
            addressField.setText(d.getAddress());
        });
    }

    private void updateSelected() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a doctor from the table first");
            return;
        }
        try {
            Doctor doctor = doctorService.findById(selectedId).orElseThrow();
            doctor.setFullName(nameField.getText());
            doctor.setSpecialty(specialtyField.getText());
            doctor.setContactNumber(contactField.getText());
            doctor.setAddress(addressField.getText());
            Set<String> daySet = Arrays.stream(daysField.getText().split(",")).map(String::trim)
                    .map(String::toUpperCase).collect(Collectors.toSet());
            List<LocalTime> slots = Arrays.stream(timesField.getText().split(",")).map(String::trim)
                    .map(LocalTime::parse).collect(Collectors.toList());
            doctor.setWorkingDays(daySet);
            doctor.setTimeSlots(slots);
            doctor.setMaxAppointmentsPerDay(Integer.parseInt(maxPerDayField.getText()));
            doctorService.update(doctor);
            refresh();
            JOptionPane.showMessageDialog(this, "Doctor updated");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a doctor from the table first");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected doctor?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            doctorService.remove(selectedId);
            refresh();
            JOptionPane.showMessageDialog(this, "Doctor deleted");
        }
    }
}

