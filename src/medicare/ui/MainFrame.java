package medicare.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import medicare.service.AppointmentService;
import medicare.service.DoctorService;
import medicare.service.NotificationService;
import medicare.service.PatientService;
import medicare.service.ReportService;
import medicare.ui.components.RoundedButton;
import medicare.ui.panels.AppointmentPanel;
import medicare.ui.panels.DashboardPanel;
import medicare.ui.panels.DoctorPanel;
import medicare.ui.panels.NotificationPanel;
import medicare.ui.panels.PatientPanel;
import medicare.ui.panels.ReportPanel;

/** Main window with sidebar navigation and card-swapped screens. */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public MainFrame(PatientService patientService, DoctorService doctorService, AppointmentService appointmentService,
            NotificationService notificationService, ReportService reportService) {
        setTitle("MediCare Plus - Patient Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        getContentPane().setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());

        Map<String, Supplier<JPanel>> screens = new LinkedHashMap<>();
        screens.put("Dashboard", () -> new DashboardPanel(reportService));
        screens.put("Patients", () -> new PatientPanel(patientService));
        screens.put("Doctors", () -> new DoctorPanel(doctorService));
        screens.put("Appointments", () -> new AppointmentPanel(patientService, doctorService, appointmentService));
        screens.put("Notifications", () -> new NotificationPanel(notificationService, patientService, doctorService));
        screens.put("Reports", () -> new ReportPanel(reportService, patientService, doctorService, appointmentService));

        screens.forEach((name, supplier) -> cardPanel.add(supplier.get(), name));

        add(sidebar(screens), BorderLayout.WEST);
        add(new JScrollPane(cardPanel), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel sidebar(Map<String, Supplier<JPanel>> screens) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorPalette.CARD);
        panel.setBorder(new EmptyBorder(24, 16, 24, 16));
        JLabel title = new JLabel("MediCare Plus", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        screens.keySet().forEach(name -> {
            RoundedButton btn = new RoundedButton(name);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            btn.addActionListener(e -> cardLayout.show(cardPanel, name));
            panel.add(btn);
            panel.add(Box.createVerticalStrut(12));
        });
        return panel;
    }
}

