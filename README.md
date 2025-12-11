## MediCare Plus – Patient Management System

Desktop app built with Java Swing (no external DB) for managing patients, doctors, appointments, notifications, and monthly reports.

### How to run (simple)
1) Open PowerShell and go to the project folder:  
   `cd D:\Oop-2\project-2`
2) Compile everything into `bin`:  
   `javac -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName`
3) Start the app:  
   `java -cp bin medicare.App`

Data is stored in local `.dat` files under `data/` (created automatically).

### What the folders mean
- `model`: simple data classes (patients, doctors, appointments, notifications, status).
- `service`: business rules and storage (add/update/delete, no double booking, duplicates check, reports).
- `ui`: Swing screens and components (main window, panels, rounded buttons/fields/panels).
  - `ui/panels`: each screen (dashboard, patients, doctors, appointments, notifications, reports).
  - `ui/components`: reusable UI pieces and colors.

### src vs bin (what’s inside)
- `src/medicare`: all source code you edit.
  - `model`: patient, doctor, appointment, notification, status enums.
  - `service`: patient/doctor/appointment/notification/report services plus file repository.
  - `ui`: main window, panels, and rounded UI components.
- `bin/medicare`: compiled `.class` files that match the same structure as `src`. These are generated; you don’t edit them. If they get out of date, just delete `bin` and recompile.

### Main features
- Patients: add, update, delete, search in table.
- Doctors: add, update, delete, manage working days and time slots.
- Appointments: book, auto-assign doctor by specialty/availability, prevent double booking, update status.
- Notifications: log of alerts to patients/doctors.
- Reports: monthly counts (total, completed, canceled) plus by doctor and patient; dashboard cards for quick stats.

### Tech choices
- Java Swing with custom rounded controls and light theme.
- File-based storage using Java serialization (no database needed).
- OOP structure with clear layers (model, service, ui).
