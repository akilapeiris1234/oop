package medicare.service;

import java.nio.file.Paths;
import java.util.List;

import medicare.model.NotificationEntry;

/** Creates notification messages and lists them. */
public class NotificationService {
    private final FileRepository<NotificationEntry> repository;

    public NotificationService() {
        this.repository = new FileRepository<>(Paths.get("data"), "notifications.dat");
    }

    public void push(String message, String recipientId) {
        repository.update(list -> list.add(new NotificationEntry(message, recipientId)));
    }

    public List<NotificationEntry> list() {
        return repository.loadAll();
    }
}

