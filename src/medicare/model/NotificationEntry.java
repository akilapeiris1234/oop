package medicare.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** A saved notification message for a doctor or patient. */
public class NotificationEntry implements Serializable {
    private final String id;
    private final String message;
    private final String recipientId;
    private final LocalDateTime createdAt;

    public NotificationEntry(String message, String recipientId) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.recipientId = recipientId;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

