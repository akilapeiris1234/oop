package medicare.ui.panels;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import medicare.service.ReportService;
import medicare.ui.ColorPalette;
import medicare.ui.components.RoundedPanel;

/** Dashboard cards showing key counts. */
public class DashboardPanel extends JPanel {
    private final ReportService reportService;

    public DashboardPanel(ReportService reportService) {
        this.reportService = reportService;
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new GridLayout(1, 3, 16, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        add(statCard("Total Patients", () -> String.valueOf(reportService.totalPatients())));
        add(statCard("Total Doctors", () -> String.valueOf(reportService.totalDoctors())));
        add(statCard("Appointments Today", () -> String.valueOf(reportService.appointmentsToday())));
    }

    private RoundedPanel statCard(String title, ValueSupplier supplier) {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        JLabel valueLabel = new JLabel(supplier.value());
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        valueLabel.setFont(valueLabel.getFont().deriveFont(26f));
        panel.add(titleLabel);
        panel.add(valueLabel);
        return panel;
    }

    @FunctionalInterface
    interface ValueSupplier {
        String value();
    }
}

