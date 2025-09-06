package tr.mhrs.appointment_service.repository;

// İMPORTLAR - Neden gerekli?
import org.springframework.data.jpa.repository.JpaRepository;  // Temel CRUD işlemleri için
import org.springframework.data.jpa.repository.Query;           // Özel sorgular yazmak için
import org.springframework.data.repository.query.Param;        // Query parametreleri için
import org.springframework.stereotype.Repository;              // Spring Bean işaretlemesi için
import tr.mhrs.appointment_service.entity.Hospital;          // Hospital entity'miz
import tr.mhrs.appointment_service.entity.HospitalType;      // Enum: DEVLET, OZEL, UNIVERSITE

import java.util.List;     // Çoklu sonuç döndürmek için
import java.util.Optional; // Null-safe tek sonuç için

@Repository  // Spring'e diyoruz ki: "Bu bir veritabanı erişim sınıfı"
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    // JpaRepository<Entity, IDTipi>
    // Bize otomatik olarak şunları sağlar:
    // - save(Hospital h) → hastane kaydet
    // - findById(Long id) → ID ile bul
    // - findAll() → tüm hastaneleri getir
    // - delete(Hospital h) → hastane sil
    // - count() → kaç hastane var

    // 1️⃣ ŞEHİRDEKİ HASTANELERİ BUL
    List<Hospital> findByCityId(Long cityId);
    // Spring otomatik SQL üretir:
    // SELECT * FROM hospitals WHERE city_id = ?
    // Kullanım: hospitalRepository.findByCityId(34L); // İstanbul'un ID'si 34

    // 2️⃣ İLÇEDEKİ HASTANELERİ BUL
    List<Hospital> findByDistrictId(Long districtId);
    // SQL: SELECT * FROM hospitals WHERE district_id = ?
    // Kullanım: hospitalRepository.findByDistrictId(100L); // Kadıköy ID'si

    // 3️⃣ ŞEHİR VE İLÇEYE GÖRE BUL (İKİ KOŞUL BİRDEN)
    List<Hospital> findByCityIdAndDistrictId(Long cityId, Long districtId);
    // "And" kelimesi iki koşulu birleştirir
    // SQL: SELECT * FROM hospitals WHERE city_id = ? AND district_id = ?

    // 4️⃣ HASTANE TİPİNE GÖRE BUL
    List<Hospital> findByHospitalType(HospitalType type);
    // SQL: SELECT * FROM hospitals WHERE hospital_type = ?
    // Kullanım: findByHospitalType(HospitalType.DEVLET)

    // 5️⃣ İSİMLE ARAMA (İçinde geçen)
    List<Hospital> findByNameContainingIgnoreCase(String name);
    // "Containing" = SQL'de LIKE '%...%' demek
    // "IgnoreCase" = büyük/küçük harf önemsiz
    // SQL: SELECT * FROM hospitals WHERE LOWER(name) LIKE LOWER('%input%')
    // Örnek: "Acıbadem" yazınca → "ACIBADEM TAKSİM", "Acıbadem Fulya" bulur

    // 6️⃣ BELİRLİ BİR UZMANLIĞI OLAN HASTANELERİ BUL (KOMPLEKS SORGU)
    @Query("SELECT DISTINCT h FROM Hospital h " +
            "JOIN h.policlinics p " +              // Hastane → Poliklinikler
            "JOIN p.doctors d " +                  // Poliklinik → Doktorlar
            "WHERE d.speciality.id = :specialityId " +  // Doktor uzmanlığı
            "AND d.status = 'ACTIVE'")            // Aktif doktorlar
    List<Hospital> findHospitalsBySpeciality(@Param("specialityId") Long specialityId);
    // Bu sorgu der ki: "Kardiyoloji uzmanı olan hastaneleri bul"
    // DISTINCT = tekrar eden hastaneleri gösterme

    // 7️⃣ İL BAZINDA HASTANE SAYILARI (İSTATİSTİK)
    @Query("SELECT h.city.name, COUNT(h) " +
            "FROM Hospital h " +
            "GROUP BY h.city.name " +
            "ORDER BY COUNT(h) DESC")
    List<Object[]> countHospitalsByCity();
    // GROUP BY = şehirlere göre grupla
    // COUNT(h) = her grupta kaç hastane var
    // Object[] döner: [0]=şehir adı, [1]=hastane sayısı
    // Örnek sonuç: ["İstanbul", 250], ["Ankara", 150]

    // 8️⃣ ID İLE HASTANE BUL (NULL GÜVENLİ)
    Optional<Hospital> findByIdAndHospitalType(Long id, HospitalType type);
    // Optional = sonuç olmayabilir, null yerine Optional.empty() döner
    // Kullanım:
    // Optional<Hospital> hastane = repo.findByIdAndHospitalType(1L, HospitalType.DEVLET);
    // if(hastane.isPresent()) { ... }

    // 9️⃣ AKTİF POLİKLİNİĞİ OLAN HASTANELER
    @Query("SELECT h FROM Hospital h " +
            "WHERE EXISTS (" +
            "  SELECT 1 FROM Policlinic p " +
            "  WHERE p.hospital = h " +
            "  AND p.isActive = true" +
            ")")
    List<Hospital> findHospitalsWithActivePoliclinics();
    // EXISTS = en az bir tane varsa true
    // Alt sorgu poliklinik kontrolü yapar

    // 🔟 EN YAKIN HASTANE (NATIVE SQL ÖRNEĞİ)
    @Query(value = "SELECT * FROM hospitals h " +
            "WHERE h.city_id = :cityId " +
            "ORDER BY h.created_at DESC " +
            "LIMIT :limit",
            nativeQuery = true)  // ← Saf SQL kullan
    List<Hospital> findRecentHospitals(@Param("cityId") Long cityId,
                                       @Param("limit") int limit);
    // nativeQuery = true → JPQL değil, doğrudan SQL yaz
    // LIMIT → PostgreSQL'de ilk N kayıt
}