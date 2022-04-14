package account.model;

public enum UserRole {
    USER,
    ACCOUNTANT,
    AUDITOR,
    ADMINISTRATOR;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
