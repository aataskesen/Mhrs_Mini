package tr.mhrs.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tr.mhrs.auth.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // TC ile kullanıcı bul
    Optional<User> findByTcNo(String tcNo);
    
    // Email ile kullanıcı bul
    Optional<User> findByEmail(String email);
    
    // TC kullanımda mı?
    boolean existsByTcNo(String tcNo);
    
    // Email kullanımda mı?
    boolean existsByEmail(String email);
    
    // Son giriş zamanını güncelle
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, 
                        @Param("loginTime") LocalDateTime loginTime);
}
