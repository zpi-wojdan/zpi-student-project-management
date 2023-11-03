INSERT INTO faculty
VALUES('W04N', 'Wydział Informatyki i Telekomunikacji');

INSERT INTO department
VALUES
    ('K34W04ND03', 'Katedra Telekomunikacji i Teleinformatyki', 'W04N'),
    ('K46W04ND03', 'Katedra Sztucznej Inteligencji', 'W04N'),
    ('K30W04ND03', 'Katedra Informatyki Technicznej', 'W04N'),
    ('K44W04ND03', 'Katedra Informatyki i Inżynierii Systemów', 'W04N'),
    ('K68W04ND03', 'Katedra Podstaw Informatyki', 'W04N'),
    ('K28W04ND03', 'Katedra Automatyki, Mechatroniki i Systemów Sterowania', 'W04N'),
    ('K32W04ND03', 'Katedra Systemów i Sieci Komputerowych', 'W04N'),
    ('K45W04ND03', 'Katedra Informatyki Stosowanej', 'W04N'),
    ('W4N/KP/ZOS', 'Zespół Obsługi Studentów', 'W04N'),
    ('W4N/KP/ZON', 'Zespół Obsługi Kształcenia', 'W04N'),
    ('W4N/KP/ZJK', 'Zespół Jakości Kształcenia', 'W04N'),
    ('W4N/KP/ZUI', 'Zespół Utrzymania Infrastruktury i Wsparcia Informatycznego', 'W04N'),
    ('W4N/KP/SK', 'Sekretariat Katedr', 'W04N');

INSERT INTO study_field
VALUES
    ('INS', 'Inżynieria Systemów'),
    ('CBE', 'Cyberbezpieczeństwo'),
    ('INA', 'Informatyka algorytmiczna'),
    ('INF', 'Informatyka'),
    ('ISA', 'Informatyczne systemy automatyki'),
    ('IST', 'Informatyka stosowana'),
    ('ITE', 'Informatyka techniczna'),
    ('SZT', 'Sztuczna inteligencja'),
    ('TAI', 'Zaufane systemy sztucznej inteligencji'),
    ('TEL', 'Telekomunikacja'),
    ('TIN', 'Teleinformatyka');

INSERT INTO study_cycle (id, name)
VALUES
    (0, '2020/21-Z'),
    (1, '2021/22-Z'),
    (2, '2022/23-Z'),
    (3, '2023/24-Z');

INSERT INTO specialization (abbreviation, name, study_field_abbreviation)
VALUES
    ('CBD', 'Bezpieczeństwo danych', 'CBE'),
    ('CBS', 'Bezpieczeństwo sieci teleinformatycznych', 'CBE'),
    ('CEN', 'Bezpieczeństwo w energetyce', 'CBE'),
    ('CIK', 'Bezpieczeństwo infrastruktury krytycznej', 'CBE'),
    ('CSI', 'Bezpieczeństwo systemów informatycznych', 'CBE'),
    ('ALG', 'Algorytmika', 'INA'),
    ('CCS', 'Cryptography and computer security', 'INA'),
--     ('AIC', 'Advanced informatics and control', 'INF'), /*  !!!  */
--     ('COE', 'Computer engineering', 'INF'), /*  !!!  */
--     ('DAN', 'Danologia', 'INF'), /*  !!!  */
--     ('IGM', 'Grafika i systemy multimedialne', 'INF'),
--     ('IMT', 'Systemy informatyki w medycynie', 'INF'),
--     ('INE', 'Internet engineering', 'INF'),
--     ('INS', 'Inżynieria systemów informatycznych', 'INF'),
--     ('INT', 'Inżynieria internetowa', 'INF'),
--     ('IOP', 'Inżynieria oprogramowania', 'INF'),    /*  !!!  */
--     ('ISK', 'Systemy i sieci komputerowe', 'INF'),
    ('IKA', 'Komputerowe systemy sterowania', 'ISA'),
    ('IPS', 'Inteligentne systemy przemysłu 4.0', 'ISA'),
    ('IZI', 'Zastosowania inżynierii komputerowej', 'ISA'),
    ('IZT', 'Zastosowania technologii informacyjnych', 'ISA'),
    ('COE', 'Computer engineering', 'IST'), /*  !!!  */
    ('DAN', 'Danologia', 'IST'),    /*  !!!  */
    ('IOP', 'Inżynieria oprogramowania', 'IST'),    /*  !!!  */
    ('PSI', 'Projektowanie systemów informatycznych', 'IST'),
    ('ZTI', 'Zastosowania specjalistycznych technologii informatycznych', 'IST'),
    ('ACS', 'Advanced computer science', 'ITE'),
    ('AIC', 'Advanced informatics and control', 'ITE'), /*  !!!  */
    ('IGM', 'Grafika i systemy multimedialne', 'ITE'),
    ('IMT', 'Systemy informatyki w medycynie', 'ITE'),
    ('INE', 'Internet engineering', 'ITE'),
    ('INS', 'Inżynieria systemów informatycznych', 'ITE'),
    ('INT', 'Inżynieria internetowa', 'ITE'),
    ('ISK', 'Systemy i sieci komputerowe', 'ITE'),
    ('TEM', 'Telekomunikacja mobilna', 'TEL'),
    ('TIM', 'Teleinformatyka i multimedia', 'TEL'),
    ('TMU', 'Multimedia w telekomunikacji', 'TEL'),
    ('TSI', 'Sieci teleinformatyczne', 'TEL'),
    ('TSM', 'Teleinformatyczne sieci mobilne', 'TEL'),
    ('TIP', 'Projektowanie sieci teleinformatycznych', 'TIN'),
    ('TIU', 'Utrzymanie sieci teleinformatycznych', 'TIN');


INSERT INTO program (id, name, study_field_abbreviation, specialization_abbreviation, faculty_abbreviation)
VALUES
    (0, 'W04-ISTP-000P-OSIW7', 'IST', NULL,  'W04N'),  /*  ?   */
    (1, 'W04-ISTA-000P-OSIW7', 'IST', NULL, 'W04N'),  /*  ?   */
    (2, 'W04-CBEP-000P-OSIE7', 'CBE', NULL, 'W04N'), /*  2022/23-Z   */
    (3, 'W04-CBEP-000P-OSME3', 'CBE', NULL, 'W04N'), /*  2022/23-Z   */
    (4, 'W04-INAP-000P-OSIE7', 'INA', NULL, 'W04N'), /*  2022/23-Z   */
    (5, 'W04-INAP-000P-OSME3', 'INA', NULL, 'W04N'), /*  2022/23-Z   */
    (6, 'W04-INAP-CCSA-OSME3', 'INA', 'CCS', 'W04N'), /*  2021/22-Z   */
    (7, 'W04-INFP-000A-OSMW3', 'INF', NULL, 'W04N'), /*  2022/23-Z   */
    (8, 'W04-INFP-IOPP-OSMW3', 'INF', 'IOP', 'W04N'), /*  2021/22-Z   */
    (9, 'W04-ISAP-000P-OSIE7', 'ISA', NULL, 'W04N'), /*  2022/23-Z   */
    (10, 'W04-ISAP-000P-OSME3', 'ISA', NULL, 'W04N'), /*  2022/23-Z   */
    (11, 'W04-ISTP-000A-OSIE7', 'IST', NULL, 'W04N'), /*  2021\22-Z   */
    (12, 'W04-ISTP-000A-OSME4', 'IST', NULL, 'W04N'), /*  2021\22-Z   */
    (13, 'W04-ITEP-000P-OSIE7', 'ITE', NULL, 'W04N'),   /*  2021/22-Z   */
    (14, 'W04-ITEP-000P-OSME3', 'ITE', NULL, 'W04N'),   /*  2021/22-Z   */
    (15, 'W04-SZTP-000P-OSME3', 'SZT', NULL, 'W04N'),   /*  2022/23-Z   */
    (16, 'W04-TAIP-000P-OSME3', 'TAI', NULL, 'W04N'),   /*  2022/23-Z   */
    (17, 'W04-TELP-000P-OSIE7', 'TEL', NULL, 'W04N'),   /*  2022/23-Z   */
    (18, 'W04-TELP-000P-OSME3', 'TEL', NULL, 'W04N'),   /*  2022/23-Z   */
    (19, 'W04-TINP-000P-OSIE7', 'TIN', NULL, 'W04N'),   /*  2022/23-Z   */
    (20, 'W04-TINP-000P-OSME3', 'TIN', NULL, 'W04N');   /*  2022/23-Z   */



INSERT INTO role (name)
VALUES
    ('student'),
    ('supervisor'),
    ('approver'),
    ('admin');


INSERT INTO student (mail, name, surname, index, role_id, status)
VALUES
    ('123456@student.pwr.edu.pl', 'John', 'Doe', '123456', 1, 'STU'),
    ('234567@student.pwr.edu.pl', 'Alice', 'Smith', '234567', 1, 'STU'),
    ('345678@student.pwr.edu.pl', 'Michael', 'Johnson', '345678', 1, 'STU'),
    ('456789@student.pwr.edu.pl', 'Sarah', 'Williams', '456789', 1, 'STU'),
    ('567890@student.pwr.edu.pl', 'David', 'Brown', '567890', 1, 'STU'),
    ('678901@student.pwr.edu.pl', 'Jennifer', 'Lee', '678901', 1, 'STU'),
    ('789012@student.pwr.edu.pl', 'Christopher', 'Taylor', '789012', 1, 'STU'),
    ('890123@student.pwr.edu.pl', 'Jessica', 'Harris', '890123', 1, 'STU'),
    ('901234@student.pwr.edu.pl', 'Matthew', 'Clark', '901234', 1, 'STU'),
    ('123450@student.pwr.edu.pl', 'Emily', 'Anderson', '012345', 1, 'STU'),
    ('987654@student.pwr.edu.pl', 'Daniel', 'Lewis', '987654', 1, 'STU'),
    ('876543@student.pwr.edu.pl', 'Olivia', 'Ward', '876543', 1, 'STU'),
    ('765432@student.pwr.edu.pl', 'Andrew', 'Scott', '765432', 1, 'STU'),
    ('654321@student.pwr.edu.pl', 'Sophia', 'Baker', '654321', 1, 'STU'),
    ('543210@student.pwr.edu.pl', 'William', 'Taylor', '543210', 1, 'STU'),
    ('432109@student.pwr.edu.pl', 'Ava', 'Green', '432109', 1, 'STU'),
    ('321098@student.pwr.edu.pl', 'Michael', 'Wright', '321098', 1, 'STU'),
    ('210987@student.pwr.edu.pl', 'Olivia', 'Young', '210987', 1, 'STU'),
    ('109876@student.pwr.edu.pl', 'Daniel', 'King', '109876', 1, 'STU'),
    ('987601@student.pwr.edu.pl', 'Sophia', 'Cooper', '987601', 1, 'STU'),
    ('876502@student.pwr.edu.pl', 'William', 'Khan', '876502', 1, 'STU'),
    ('765403@student.pwr.edu.pl', 'Ava', 'Bryant', '765403', 1, 'STU'),
    ('654304@student.pwr.edu.pl', 'Michael', 'Evans', '654304', 1, 'STU'),
    ('543205@student.pwr.edu.pl', 'Olivia', 'Fisher', '543205', 1, 'STU'),
    ('432106@student.pwr.edu.pl', 'Daniel', 'Nelson', '432106', 1, 'STU'),
    ('321007@student.pwr.edu.pl', 'Sophia', 'Wells', '321007', 1, 'STU'),
    ('210908@student.pwr.edu.pl', 'William', 'Rose', '210908', 1, 'STU'),
    ('998090@student.pwr.edu.pl', 'Ava', 'Chapman', '099809', 1, 'STU'),
    ('988710@student.pwr.edu.pl', 'Michael', 'Gilbert', '988710', 1, 'STU'),
    ('877611@student.pwr.edu.pl', 'Olivia', 'Thornton', '877611', 1, 'STU'),
    ('766512@student.pwr.edu.pl', 'Daniel', 'Malone', '766512', 1, 'STU'),
    ('655413@student.pwr.edu.pl', 'Sophia', 'Saunders', '655413', 1, 'STU'),
    ('544314@student.pwr.edu.pl', 'William', 'Vargas', '544314', 1, 'STU'),
    ('433215@student.pwr.edu.pl', 'Ava', 'Maldonado', '433215', 1, 'STU'),
    ('322116@student.pwr.edu.pl', 'Michael', 'Santos', '322116', 1, ''),
    ('211017@student.pwr.edu.pl', 'Olivia', 'Moran', '211017', 1, ''),
    ('100918@student.pwr.edu.pl', 'Daniel', 'Haynes', '100918', 1, 'STU');

INSERT INTO employee (mail, name, surname, title, department_code)
VALUES
    ('john.doe@pwr.edu.pl', 'John', 'Doe', 'dr', 'K34W04ND03'),
    ('alice.smith@pwr.edu.pl', 'Alice', 'Smith', 'dr hab.', 'K34W04ND03'),
    ('michael.johnson@pwr.edu.pl', 'Michael', 'Johnson', 'prof', 'K34W04ND03'),
    ('sarah.williams@pwr.edu.pl', 'Sarah', 'Williams', 'dr', 'K34W04ND03'),
    ('david.brown@pwr.edu.pl', 'David', 'Brown', 'mgr', 'K34W04ND03'),
    ('jennifer.lee@pwr.edu.pl', 'Jennifer', 'Lee', 'dr hab.', 'K34W04ND03'),
    ('christopher.taylor@pwr.edu.pl', 'Christopher', 'Taylor', 'prof', 'K34W04ND03'),
    ('jessica.harris@pwr.edu.pl', 'Jessica', 'Harris', 'mgr', 'K34W04ND03'),
    ('matthew.clark@pwr.edu.pl', 'Matthew', 'Clark', 'dr', 'K34W04ND03'),
    ('emily.anderson@pwr.edu.pl', 'Emily', 'Anderson', 'dr hab.', 'K34W04ND03'),
    ('daniel.lewis@pwr.edu.pl', 'Daniel', 'Lewis', 'prof', 'K34W04ND03'),
    ('olivia.ward@pwr.edu.pl', 'Olivia', 'Ward', 'dr', 'K34W04ND03'),
    ('andrew.scott@pwr.edu.pl', 'Andrew', 'Scott', 'dr hab.', 'K34W04ND03'),
    ('sophia.baker@pwr.edu.pl', 'Sophia', 'Baker', 'prof', 'K34W04ND03'),
    ('william.taylor@pwr.edu.pl', 'William', 'Taylor', 'mgr', 'K34W04ND03'),
    ('ava.green@pwr.edu.pl', 'Ava', 'Green', 'dr', 'K34W04ND03'),
    ('olivia.young@pwr.edu.pl', 'Olivia', 'Young', 'dr hab.', 'K34W04ND03'),
    ('daniel.king@pwr.edu.pl', 'Daniel', 'King', 'prof', 'K34W04ND03'),
    ('sophia.cooper@pwr.edu.pl', 'Sophia', 'Cooper', 'mgr', 'K34W04ND03'),
    ('william.khan@pwr.edu.pl', 'William', 'Khan', 'dr', 'K34W04ND03'),
    ('ava.bryant@pwr.edu.pl', 'Ava', 'Bryant', 'dr hab.', 'K34W04ND03'),
    ('michael.evans@pwr.edu.pl', 'Michael', 'Evans', 'prof', 'K34W04ND03'),
    ('olivia.fisher@pwr.edu.pl', 'Olivia', 'Fisher', 'mgr', 'K34W04ND03'),
    ('daniel.nelson@pwr.edu.pl', 'Daniel', 'Nelson', 'dr', 'K34W04ND03'),
    ('sophia.wells@pwr.edu.pl', 'Sophia', 'Wells', 'dr hab.', 'K34W04ND03'),
    ('william.rose@pwr.edu.pl', 'William', 'Rose', 'prof', 'K34W04ND03'),
    ('ava.chapman@pwr.edu.pl', 'Ava', 'Chapman', 'mgr', 'K34W04ND03');

INSERT INTO employee_role (mail, role_id)
VALUES
    ('john.doe@pwr.edu.pl', '2'),
    ('alice.smith@pwr.edu.pl', '2'),
    ('michael.johnson@pwr.edu.pl', '2'),
    ('sarah.williams@pwr.edu.pl', '2'),
    ('david.brown@pwr.edu.pl', '2'),
    ('jennifer.lee@pwr.edu.pl', '2'),
    ('christopher.taylor@pwr.edu.pl', '2'),
    ('jessica.harris@pwr.edu.pl', '2'),
    ('matthew.clark@pwr.edu.pl', '2'),
    ('emily.anderson@pwr.edu.pl', '2'),
    ('daniel.lewis@pwr.edu.pl', '2'),
    ('olivia.ward@pwr.edu.pl', '2'),
    ('andrew.scott@pwr.edu.pl', '2'),
    ('sophia.baker@pwr.edu.pl', '2'),
    ('william.taylor@pwr.edu.pl', '2'),
    ('ava.green@pwr.edu.pl', '2'),
    ('olivia.young@pwr.edu.pl', '2'),
    ('daniel.king@pwr.edu.pl', '2'),
    ('sophia.cooper@pwr.edu.pl', '2'),
    ('william.khan@pwr.edu.pl', '2'),
    ('ava.bryant@pwr.edu.pl', '2'),
    ('michael.evans@pwr.edu.pl', '2'),
    ('olivia.fisher@pwr.edu.pl', '2'),
    ('daniel.nelson@pwr.edu.pl', '2'),
    ('daniel.nelson@pwr.edu.pl', '3'),
    ('sophia.wells@pwr.edu.pl', '2'),
    ('sophia.wells@pwr.edu.pl', '3'),
    ('sophia.wells@pwr.edu.pl', '4'),
    ('william.rose@pwr.edu.pl', '3'),
    ('ava.chapman@pwr.edu.pl', '4');

INSERT INTO Thesis (thesis_id, name_pl, name_en, description, num_people, supervisor, cycle_id, status)
VALUES
    ('1', 'Mobilna aplikacja dla miłośników starych zamków', 'Mobile application for lovers of old castles', 'Description1', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 1, 'OPEN'),
    ('2', 'Wieloosobowa i wielopoziomowa gra komputerowa', 'Multiplayer, and multi-level computer game', 'Description2', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'emily.anderson@pwr.edu.pl'), 1, 'OPEN'),
    ('3', 'Mobilna aplikacja dla miłośników astronomii', 'Mobile application for lovers and collectors of antiques', 'Description3', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'jennifer.lee@pwr.edu.pl'), 1, 'CLOSED'),
    ('4', 'Mobilna aplikacja dla miłośników i kolekcjonerów staroci', 'Mobile application for lovers and collectors of antiques', 'Description4', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 1, 'OPEN'),
    ('5', 'System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Description5', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 1, 'OPEN'),
    ('6', 'System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Description6', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'david.brown@pwr.edu.pl'), 1, 'CLOSED'),
    ('7', 'System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Description7', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 1, 'OPEN'),
    ('8', 'Rytmiczna gra komputerowa wykorzystująca "walking piano" w rzeczywistości rozszerzonej', 'Rhythm video game using walking piano in augmented reality', 'Description8', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.johnson@pwr.edu.pl'), 1, 'OPEN'),
    ('9', 'Aplikacja rzeczywistości rozszerzonej wspomagająca grę na instrumencie muzycznym', 'Augmented Reality application that supports playing musical instrument', 'Description9', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 1, 'CLOSED'),
    ('10', 'System wspierający dobór recenzentów artykułów', 'System supporting articles reviewers selection', 'Description10', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), 1, 'OPEN'),
    ('11', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description11', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 1, 'OPEN'),
    ('12', 'System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Description12', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), 1, 'CLOSED'),
    ('13', 'System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Description13', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), 1, 'OPEN'),
    ('14', 'Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Description14', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 1, 'OPEN'),
    ('15', 'Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Description15', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), 1, 'CLOSED'),
    ('16', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description16', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 1, 'OPEN'),
    ('17', 'Aplikacja do zarządzania wydatkami grupowymi i osobistymi', 'Application to manage group and personal expenses', 'Description17', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 1, 'OPEN'),
    ('18', 'Aplikacja webowa wspomagająca przeprowadzanie sesji gry RPG "Mafia" z możliwością gry zdalnej', 'Web application supporting conducting of RPG game "Mafia" session, with option to play remotely', 'Description18', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 1, 'CLOSED'),
    ('19', 'Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Description19', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 1, 'OPEN'),
    ('20', 'System zarządzania relacjami studentów z pracownikami uczelni', 'Student relation management', 'Description20', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), 1, 'OPEN'),
    ('21', 'SRM: System zarządzania relacjami z naukowcami', 'SRM: Scientists Relationship Management System', 'Description21', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), 1, 'CLOSED'),
    ('22', 'System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Description22', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 1, 'OPEN'),
    ('23', 'System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Description23', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 1, 'OPEN'),
    ('24', 'System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Description24', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 1, 'CLOSED'),
    ('25', 'System wspomagający integrację oraz komunikację webowych i mobilnych aplikacji IoT', 'System for communication and integration of IoT applications with mobile and web services', 'Description25', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 1, 'OPEN'),
    ('26', 'Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Description26', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 1, 'OPEN'),
    ('27', 'Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Description27', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 1, 'CLOSED'),
    ('28', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description28', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 1, 'OPEN'),
    ('29', 'Program do analizy wydajności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description29', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), 1, 'OPEN'),
    ('30', 'System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Description30', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 1, 'CLOSED'),
    ('31', 'System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Description31', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), 1, 'OPEN'),
    ('32', 'System do zarządzania i monitorowania upraw hydroponicznych', 'System for managing and monitoring hydroponic crops', 'Description32', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), 1, 'OPEN'),
    ('33', 'Aplikacja do ewidencji świadczonych usług oraz pracy', 'Application for accounting provided services and work', 'Description33', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), 1, 'CLOSED'),
    ('34', 'Zaprojektowanie i zaimplementowanie systemu do automatyzacji zarządzania wynajmem nieruchomości', 'Design and implementation of an automated management system for property rental', 'Description34', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 1, 'OPEN'),
    ('35', 'Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Description35', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), 1, 'OPEN'),
    ('36', 'System wspomagający rozpoznawania obrazów', 'Image recognition support system', 'Description36', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), 1, 'CLOSED'),
    ('37', 'System obsługi rodzinnych ogrodów działkowych', 'System of service for family allotment gardens', 'Description37', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), 1, 'OPEN'),
    ('38', 'Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Description38', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), 1, 'OPEN'),
    ('39', 'System wspierający organizację grupowych aktywności sportowych', 'System supporting the organization of group sports activities', 'Description39', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 1, 'CLOSED'),
    ('40', 'System wspierający organizację konkursu Polish Project Excellence Award', 'System for Polish Project Excellence Award', 'Description40', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), 1, 'OPEN'),
    ('41', 'Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Description41', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), 1, 'OPEN'),
    ('42', 'System obsługi stołówek szkolnych', 'School canteen management system', 'Description42', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), 1, 'CLOSED'),
    ('43', 'Aplikacja do zarządzania wydatkami grupowymi i osobistymi', 'Application to manage group and personal expenses', 'Description43', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), 1, 'OPEN');


INSERT INTO program_thesis (thesis_id, program_id)
VALUES
    ('1',  0),
    ('2',  0),
    ('3',  0),
    ('4',  0),
    ('5',  0),
    ('6',  1),
    ('7',  1),
    ('8',  1),
    ('9',  1),
    ('10', 1),
    ('11', 1),
    ('12', 0),
    ('13', 0),
    ('14', 1),
    ('15', 1),
    ('16', 1),
    ('17', 1),
    ('18', 1),
    ('19', 1),
    ('20', 1),
    ('21', 1),
    ('22', 0),
    ('23', 0),
    ('24', 0),
    ('25', 0),
    ('26', 0),
    ('27', 0),
    ('28', 1),
    ('29', 1),
    ('30', 1),
    ('31', 1),
    ('32', 1),
    ('33', 1),
    ('34', 1),
    ('35', 0),
    ('36', 0),
    ('37', 0),
    ('38', 0),
    ('39', 0),
    ('40', 0),
    ('41', 0),
    ('42', 1),
    ('43', 1);

INSERT INTO employee(mail, name, surname, title, department_code)
VALUES
    ('260452@student.pwr.edu.pl', 'Piotr', 'Wojdan', 'dr', 'K34W04ND03'),
    ('260466@student.pwr.edu.pl', 'Marta', 'Rzepka', 'dr', 'K34W04ND03'),
    ('260464@student.pwr.edu.pl', 'Zuzanna', 'Sikorska', 'dr', 'K34W04ND03'),
    ('255356@student.pwr.edu.pl', 'Jakub', 'Krupiński', 'dr', 'K34W04ND03');

INSERT INTO employee_role(mail, role_id)
VALUES
    ('260452@student.pwr.edu.pl', '4'),
    ('260466@student.pwr.edu.pl', '4'),
    ('260464@student.pwr.edu.pl', '4'),
    ('255356@student.pwr.edu.pl', '4');
