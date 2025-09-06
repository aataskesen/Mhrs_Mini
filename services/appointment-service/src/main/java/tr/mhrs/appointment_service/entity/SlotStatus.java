package tr.mhrs.appointment_service.entity;

public enum SlotStatus {
    AVAILABLE("Müsait"),           // Randevu alınabilir
    RESERVED("Rezerve"),           // Geçici olarak rezerve edilmiş (5 dakika timeout)
    BOOKED("Dolu"),               // Kesin randevu alınmış
    BLOCKED("Bloke"),             // Sistem tarafından bloke edilmiş
    CANCELLED("İptal"),           // İptal edilmiş
    COMPLETED("Tamamlandı");      // Muayene tamamlandı

    private final String displayName;

    SlotStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
