package medicare.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Saves and loads lists to small .dat files using Java serialization. */
public class FileRepository<T> {
    private final Path filePath;

    public FileRepository(Path dataDir, String fileName) {
        this.filePath = dataDir.resolve(fileName);
        try {
            Files.createDirectories(dataDir);
            if (Files.notExists(filePath)) {
                saveAll(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to prepare storage", e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized List<T> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public synchronized void saveAll(List<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist data", e);
        }
    }

    public synchronized void update(Consumer<List<T>> modifier) {
        List<T> data = loadAll();
        modifier.accept(data);
        saveAll(data);
    }
}

