package tr.mhrs.appointment_service.entity;

public enum AppointmentStatus {
    SCHEDULED("Planlandı"),          // Randevu alındı
    CONFIRMED("Onaylandı"),          // Randevu onaylandı
    COMPLETED("Tamamlandı"),         // Muayene tamamlandı
    CANCELLED("İptal Edildi"),       // Randevu iptal edildi
    NO_SHOW("Gelmedi"),             // Hasta gelmedi
    RESCHEDULED("Ertelendi");       // Randevu ertelendi

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
