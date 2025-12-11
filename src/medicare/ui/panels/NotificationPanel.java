package medicare.ui.panels;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import medicare.model.Doctor;
import medicare.model.NotificationEntry;
import medicare.model.Patient;
import medicare.service.DoctorService;
import medicare.service.NotificationService;
import medicare.service.PatientService;
import medicare.ui.ColorPalette;

/** Shows the saved notifications for doctors and patients. */
public class NotificationPanel extends JPanel {
    private final NotificationService notificationService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DefaultTableModel model;

    public NotificationPanel(NotificationService notificationService, PatientService patientService,
            DoctorService doctorService) {
        this.notificationService = notificationService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        model = new DefaultTableModel(new Object[] { "Recipient", "Message", "Created" }, 0);
        JTable table = new JTable(model);
        add(new JLabel("Notification Log"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        List<NotificationEntry> entries = notificationService.list();
        for (NotificationEntry n : entries) {
            String name = patientService.findById(n.getRecipientId()).map(Patient::getFullName)
                    .orElseGet(() -> doctorService.findById(n.getRecipientId()).map(Doctor::getFullName).orElse("Recipient"));
            model.addRow(new Object[] { name, n.getMessage(), n.getCreatedAt() });
        }
    }
}

