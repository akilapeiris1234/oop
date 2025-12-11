package medicare.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.YearMonth;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import medicare.model.Doctor;
import medicare.model.Patient;
import medicare.service.AppointmentService;
import medicare.service.DoctorService;
import medicare.service.PatientService;
import medicare.service.ReportService;
import medicare.ui.ColorPalette;
import medicare.ui.components.RoundedButton;
import medicare.ui.components.RoundedPanel;
import medicare.ui.components.RoundedTextField;

/** Monthly report screen that counts appointments by doctor and patient. */
public class ReportPanel extends JPanel {
    private final ReportService reportService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DefaultTableModel doctorModel = new DefaultTableModel(new Object[] { "Doctor", "Appointments" }, 0);
    private final DefaultTableModel patientModel = new DefaultTableModel(new Object[] { "Patient", "Appointments" }, 0);
    private final JLabel totals = new JLabel();

    public ReportPanel(ReportService reportService, PatientService patientService, DoctorService doctorService,
            AppointmentService appointmentService) {
        this.reportService = reportService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));
        add(content(), BorderLayout.CENTER);
    }

    private JPanel content() {
        JPanel wrapper = new JPanel(new BorderLayout(12, 12));
        wrapper.setOpaque(false);

        RoundedPanel controls = new RoundedPanel();
        controls.setLayout(new GridLayout(1, 0, 8, 8));
        controls.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField month = new RoundedTextField(8);
        month.setToolTipText("yyyy-MM");
        RoundedButton run = new RoundedButton("Generate Report");
        run.addActionListener(e -> generate(month.getText()));

        controls.add(new JLabel("Month (yyyy-MM)"));
        controls.add(month);
        controls.add(run);
        controls.add(totals);

        wrapper.add(controls, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(1, 2, 12, 12));
        tables.setOpaque(false);
        tables.add(new JScrollPane(new JTable(doctorModel)));
        tables.add(new JScrollPane(new JTable(patientModel)));
        wrapper.add(tables, BorderLayout.CENTER);
        return wrapper;
    }

    private void generate(String monthInput) {
        try {
            YearMonth ym = YearMonth.parse(monthInput);
            ReportService.ReportSummary summary = reportService.monthly(ym);
            totals.setText(String.format("Total: %d, Completed: %d, Canceled: %d", summary.totalAppointments,
                    summary.completedAppointments, summary.canceledAppointments));
            fillTable(doctorModel, summary.appointmentsByDoctor, true);
            fillTable(patientModel, summary.appointmentsByPatient, false);
        } catch (Exception ex) {
            totals.setText("Invalid month");
        }
    }

    private void fillTable(DefaultTableModel model, Map<String, Long> data, boolean doctor) {
        model.setRowCount(0);
        data.forEach((id, count) -> {
            String name = doctor ? doctorService.findById(id).map(Doctor::getFullName).orElse("Doctor")
                    : patientService.findById(id).map(Patient::getFullName).orElse("Patient");
            model.addRow(new Object[] { name, count });
        });
    }
}

