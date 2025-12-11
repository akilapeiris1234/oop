package medicare.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import medicare.model.Patient;
import medicare.service.PatientService;
import medicare.ui.ColorPalette;
import medicare.ui.components.RoundedButton;
import medicare.ui.components.RoundedPanel;
import medicare.ui.components.RoundedTextField;

/** Patient screen with table plus add, update, and delete actions. */
public class PatientPanel extends JPanel {
    private final PatientService patientService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField nameField = new RoundedTextField(12);
    private final JTextField nicField = new RoundedTextField(12);
    private final JTextField genderField = new RoundedTextField(12);
    private final JTextField ageField = new RoundedTextField(12);
    private final JTextField contactField = new RoundedTextField(12);
    private final JTextField addressField = new RoundedTextField(12);
    private final JTextArea historyArea = new JTextArea(3, 12);
    private String selectedId;

    public PatientPanel(PatientService patientService) {
        this.patientService = patientService;
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        model = new DefaultTableModel(
                new Object[] { "ID", "Name", "NIC/Passport", "Gender", "Age", "Contact", "Address", "History" }, 0);
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

        historyArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(labeled("Full Name", nameField));
        panel.add(labeled("NIC/Passport", nicField));
        panel.add(labeled("Gender", genderField));
        panel.add(labeled("Age", ageField));
        panel.add(labeled("Contact", contactField));
        panel.add(labeled("Address", addressField));
        panel.add(labeled("Medical History", new JScrollPane(historyArea)));

        RoundedButton save = new RoundedButton("Add Patient");
        save.addActionListener(e -> {
            try {
                Patient patient = new Patient(nameField.getText(), nicField.getText(), genderField.getText(),
                        Integer.parseInt(ageField.getText()), contactField.getText(), addressField.getText(),
                        historyArea.getText());
                patientService.add(patient);
                refresh();
                JOptionPane.showMessageDialog(this, "Patient added");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);

        RoundedButton update = new RoundedButton("Update Patient");
        update.addActionListener(e -> updateSelected());
        panel.add(update);

        RoundedButton delete = new RoundedButton("Delete Patient");
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
        List<Patient> patients = patientService.list();
        model.setRowCount(0);
        patients.forEach(p -> model.addRow(
                new Object[] { p.getId(), p.getFullName(), p.getNicOrPassport(), p.getGender(), p.getAge(),
                        p.getContactNumber(), p.getAddress(), p.getMedicalHistory() }));
        selectedId = null;
        table.clearSelection();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        // model index accounts for hidden ID column
        int modelRow = table.convertRowIndexToModel(row);
        selectedId = (String) model.getValueAt(modelRow, 0);
        nameField.setText((String) model.getValueAt(modelRow, 1));
        nicField.setText((String) model.getValueAt(modelRow, 2));
        genderField.setText((String) model.getValueAt(modelRow, 3));
        ageField.setText(String.valueOf(model.getValueAt(modelRow, 4)));
        contactField.setText((String) model.getValueAt(modelRow, 5));
        addressField.setText((String) model.getValueAt(modelRow, 6));
        historyArea.setText((String) model.getValueAt(modelRow, 7));
    }

    private void updateSelected() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a patient from the table first");
            return;
        }
        try {
            Patient patient = patientService.findById(selectedId).orElseThrow();
            patient.setFullName(nameField.getText());
            patient.setNicOrPassport(nicField.getText());
            patient.setGender(genderField.getText());
            patient.setAge(Integer.parseInt(ageField.getText()));
            patient.setContactNumber(contactField.getText());
            patient.setAddress(addressField.getText());
            patient.setMedicalHistory(historyArea.getText());
            patientService.update(patient);
            refresh();
            JOptionPane.showMessageDialog(this, "Patient updated");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a patient from the table first");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected patient?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            patientService.remove(selectedId);
            refresh();
            JOptionPane.showMessageDialog(this, "Patient deleted");
        }
    }
}

