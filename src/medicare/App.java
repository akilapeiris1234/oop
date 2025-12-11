package medicare;

import javax.swing.SwingUtilities;

/** Starts the program and opens the MediCare Plus window. */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MediCareApp().start());
    }
}

