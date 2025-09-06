-- V1: İller ve İlçeler Tabloları
-- Created: 29.08.2025
-- Description: MHRS sistemi için Türkiye il ve ilçe yapısı

-- İLLER TABLOSU
CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    plate_code INTEGER UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- İLÇELER TABLOSU  
CREATE TABLE districts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);

-- İL VERİLERİ (Test için birkaç tane)
INSERT INTO cities (name, plate_code) VALUES 
('Adana', 1),
('Adıyaman', 2),
('Afyonkarahisar', 3),
('Ağrı', 4),
('Amasya', 5),
('Ankara', 6),
('Antalya', 7),
('İstanbul', 34),
('İzmir', 35);

-- İLÇE VERİLERİ (Test için)
INSERT INTO districts (name, city_id) VALUES 
-- Adana İlçeleri
('Seyhan', 1),
('Yüreğir', 1),
('Çukurova', 1),
-- Ankara İlçeleri  
('Çankaya', 6),
('Keçiören', 6),
('Mamak', 6),
-- İstanbul İlçeleri
('Beşiktaş', 8),
('Kadıköy', 8),
('Şişli', 8);
