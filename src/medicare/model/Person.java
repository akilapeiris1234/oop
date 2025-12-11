package medicare.model;

import java.io.Serializable;
import java.util.UUID;

/** Common details shared by patients and doctors. */
public abstract class Person implements Serializable {
    private final String id;
    private String fullName;
    private String contactNumber;
    private String address;

    protected Person(String fullName, String contactNumber, String address) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

