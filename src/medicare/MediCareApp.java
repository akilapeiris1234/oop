package medicare;

import medicare.service.AppointmentService;
import medicare.service.DoctorService;
import medicare.service.NotificationService;
import medicare.service.PatientService;
import medicare.service.ReportService;
import medicare.ui.MainFrame;

/** Builds the services and shows the main window. */
public class MediCareApp {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public MediCareApp() {
        this.patientService = new PatientService();
        this.doctorService = new DoctorService();
        this.notificationService = new NotificationService();
        this.appointmentService = new AppointmentService(patientService, doctorService, notificationService);
        this.reportService = new ReportService(patientService, doctorService, appointmentService);
    }

    public void start() {
        new MainFrame(patientService, doctorService, appointmentService, notificationService, reportService).setVisible(true);
    }
}

