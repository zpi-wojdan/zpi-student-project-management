INSERT INTO faculty(abbreviation, name)
VALUES('W04N', 'Wydział Informatyki i Telekomunikacji');

INSERT INTO department(code, name, faculty_id)
VALUES
    ('K34W04ND03', 'Katedra Telekomunikacji i Teleinformatyki', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K46W04ND03', 'Katedra Sztucznej Inteligencji', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K30W04ND03', 'Katedra Informatyki Technicznej', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K44W04ND03', 'Katedra Informatyki i Inżynierii Systemów', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K68W04ND03', 'Katedra Podstaw Informatyki', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K28W04ND03', 'Katedra Automatyki, Mechatroniki i Systemów Sterowania', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K32W04ND03', 'Katedra Systemów i Sieci Komputerowych', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('K45W04ND03', 'Katedra Informatyki Stosowanej', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W4N/KP/ZOS', 'Zespół Obsługi Studentów', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W4N/KP/ZON', 'Zespół Obsługi Kształcenia', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W4N/KP/ZJK', 'Zespół Jakości Kształcenia', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W4N/KP/ZUI', 'Zespół Utrzymania Infrastruktury i Wsparcia Informatycznego', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W4N/KP/SK', 'Sekretariat Katedr', (SELECT id FROM faculty WHERE abbreviation = 'W04N'));

INSERT INTO study_field(abbreviation, name, faculty_id)
VALUES
    ('INS', 'Inżynieria Systemów', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('CBE', 'Cyberbezpieczeństwo', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('INA', 'Informatyka algorytmiczna', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('INF', 'Informatyka', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('ISA', 'Informatyczne systemy automatyki', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('IST', 'Informatyka stosowana', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('ITE', 'Informatyka techniczna', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('SZT', 'Sztuczna inteligencja', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('TAI', 'Zaufane systemy sztucznej inteligencji', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('TEL', 'Telekomunikacja', (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('TIN', 'Teleinformatyka', (SELECT id FROM faculty WHERE abbreviation = 'W04N'));


INSERT INTO study_cycle (name)
VALUES
    ('2020/21-Z'),
    ('2021/22-Z'),
    ('2022/23-Z'),
    ('2023/24-Z');

INSERT INTO specialization (abbreviation, name, study_field_id)
VALUES
    ('CBD', 'Bezpieczeństwo danych', (SELECT id FROM study_field WHERE abbreviation = 'CBE')),
    ('CBS', 'Bezpieczeństwo sieci teleinformatycznych', (SELECT id FROM study_field WHERE abbreviation = 'CBE')),
    ('CEN', 'Bezpieczeństwo w energetyce', (SELECT id FROM study_field WHERE abbreviation = 'CBE')),
    ('CIK', 'Bezpieczeństwo infrastruktury krytycznej', (SELECT id FROM study_field WHERE abbreviation = 'CBE')),
    ('CSI', 'Bezpieczeństwo systemów informatycznych', (SELECT id FROM study_field WHERE abbreviation = 'CBE')),
    ('ALG', 'Algorytmika', (SELECT id FROM study_field WHERE abbreviation = 'INA')),
    ('CCS', 'Cryptography and computer security', (SELECT id FROM study_field WHERE abbreviation = 'INA')),
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
    ('IKA', 'Komputerowe systemy sterowania', (SELECT id FROM study_field WHERE abbreviation = 'ISA')),
    ('IPS', 'Inteligentne systemy przemysłu 4.0', (SELECT id FROM study_field WHERE abbreviation = 'ISA')),
    ('IZI', 'Zastosowania inżynierii komputerowej', (SELECT id FROM study_field WHERE abbreviation = 'ISA')),
    ('IZT', 'Zastosowania technologii informacyjnych', (SELECT id FROM study_field WHERE abbreviation = 'ISA')),
    ('COE', 'Computer engineering', (SELECT id FROM study_field WHERE abbreviation = 'IST')), /*  !!!  */
    ('DAN', 'Danologia', (SELECT id FROM study_field WHERE abbreviation = 'IST')),    /*  !!!  */
    ('IOP', 'Inżynieria oprogramowania', (SELECT id FROM study_field WHERE abbreviation = 'IST')),    /*  !!!  */
    ('PSI', 'Projektowanie systemów informatycznych', (SELECT id FROM study_field WHERE abbreviation = 'IST')),
    ('ZTI', 'Zastosowania specjalistycznych technologii informatycznych', (SELECT id FROM study_field WHERE abbreviation = 'IST')),
    ('ACS', 'Advanced computer science', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('AIC', 'Advanced informatics and control', (SELECT id FROM study_field WHERE abbreviation = 'ITE')), /*  !!!  */
    ('IGM', 'Grafika i systemy multimedialne', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('IMT', 'Systemy informatyki w medycynie', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('INE', 'Internet engineering', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('INS', 'Inżynieria systemów informatycznych', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('INT', 'Inżynieria internetowa', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('ISK', 'Systemy i sieci komputerowe', (SELECT id FROM study_field WHERE abbreviation = 'ITE')),
    ('TEM', 'Telekomunikacja mobilna', (SELECT id FROM study_field WHERE abbreviation = 'TEL')),
    ('TIM', 'Teleinformatyka i multimedia', (SELECT id FROM study_field WHERE abbreviation = 'TEL')),
    ('TMU', 'Multimedia w telekomunikacji', (SELECT id FROM study_field WHERE abbreviation = 'TEL')),
    ('TSI', 'Sieci teleinformatyczne', (SELECT id FROM study_field WHERE abbreviation = 'TEL')),
    ('TSM', 'Teleinformatyczne sieci mobilne', (SELECT id FROM study_field WHERE abbreviation = 'TEL')),
    ('TIP', 'Projektowanie sieci teleinformatycznych', (SELECT id FROM study_field WHERE abbreviation = 'TIN')),
    ('TIU', 'Utrzymanie sieci teleinformatycznych', (SELECT id FROM study_field WHERE abbreviation = 'TIN'));


INSERT INTO program (name, study_field_id, specialization_id, faculty_id)
VALUES
    ('W04-ISTP-000P-OSIW7', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),  /*  ?   */
    ('W04-ISTA-000P-OSIW7', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),  /*  ?   */
    ('W04-CBEP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'CBE'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-CBEP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'CBE'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-INAP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'INA'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-INAP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'INA'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-INAP-CCSA-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'INA'), (SELECT id FROM specialization WHERE abbreviation = 'CCS'), (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2021/22-Z   */
    ('W04-INFP-000A-OSMW3', (SELECT id FROM study_field WHERE abbreviation = 'INF'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-INFP-IOPP-OSMW3', (SELECT id FROM study_field WHERE abbreviation = 'INF'), (SELECT id FROM specialization WHERE abbreviation = 'IOP'), (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2021/22-Z   */
    ('W04-ISAP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'ISA'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-ISAP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'ISA'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2022/23-Z   */
    ('W04-ISTP-000A-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2021\22-Z   */
    ('W04-ISTP-000A-OSME4', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')), /*  2021\22-Z   */
    ('W04-ITEP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'ITE'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2021/22-Z   */
    ('W04-ITEP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'ITE'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2021/22-Z   */
    ('W04-SZTP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'SZT'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2022/23-Z   */
    ('W04-TAIP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'TAI'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2022/23-Z   */
    ('W04-TELP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'TEL'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2022/23-Z   */
    ('W04-TELP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'TEL'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2022/23-Z   */
    ('W04-TINP-000P-OSIE7', (SELECT id FROM study_field WHERE abbreviation = 'TIN'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),   /*  2022/23-Z   */
    ('W04-TINP-000P-OSME3', (SELECT id FROM study_field WHERE abbreviation = 'TIN'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N'));   /*  2022/23-Z   */

INSERT INTO title (name)
VALUES
    ('mgr inż.'),
    ('dr'),
    ('dr hab.'),
    ('prof'),
    ('prof. dr hab.'),
    ('mgr');

INSERT INTO status (name)
VALUES
    ('Draft'),
    ('Pending approval'),
    ('Rejected'),
    ('Approved'),
    ('Assigned'),
    ('Closed');

INSERT INTO role (name)
VALUES
    ('student'),
    ('supervisor'),
    ('approver'),
    ('admin');


INSERT INTO student (mail, name, surname, index, role_id, status)
VALUES
    ('123456@student.pwr.edu.pl', 'John', 'Doe', '123456', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('234567@student.pwr.edu.pl', 'Alice', 'Smith', '234567', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('345678@student.pwr.edu.pl', 'Michael', 'Johnson', '345678', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('456789@student.pwr.edu.pl', 'Sarah', 'Williams', '456789', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('567890@student.pwr.edu.pl', 'David', 'Brown', '567890', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('678901@student.pwr.edu.pl', 'Jennifer', 'Lee', '678901', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('789012@student.pwr.edu.pl', 'Christopher', 'Taylor', '789012', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('890123@student.pwr.edu.pl', 'Jessica', 'Harris', '890123', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('901234@student.pwr.edu.pl', 'Matthew', 'Clark', '901234', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('123450@student.pwr.edu.pl', 'Emily', 'Anderson', '012345', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('987654@student.pwr.edu.pl', 'Daniel', 'Lewis', '987654', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('876543@student.pwr.edu.pl', 'Olivia', 'Ward', '876543', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('765432@student.pwr.edu.pl', 'Andrew', 'Scott', '765432', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('654321@student.pwr.edu.pl', 'Sophia', 'Baker', '654321', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('543210@student.pwr.edu.pl', 'William', 'Taylor', '543210', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('432109@student.pwr.edu.pl', 'Ava', 'Green', '432109', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('321098@student.pwr.edu.pl', 'Michael', 'Wright', '321098', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('210987@student.pwr.edu.pl', 'Olivia', 'Young', '210987', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('109876@student.pwr.edu.pl', 'Daniel', 'King', '109876', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('987601@student.pwr.edu.pl', 'Sophia', 'Cooper', '987601', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('876502@student.pwr.edu.pl', 'William', 'Khan', '876502', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('765403@student.pwr.edu.pl', 'Ava', 'Bryant', '765403', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('654304@student.pwr.edu.pl', 'Michael', 'Evans', '654304', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('543205@student.pwr.edu.pl', 'Olivia', 'Fisher', '543205', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('432106@student.pwr.edu.pl', 'Daniel', 'Nelson', '432106', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('321007@student.pwr.edu.pl', 'Sophia', 'Wells', '321007', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('210908@student.pwr.edu.pl', 'William', 'Rose', '210908', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('998090@student.pwr.edu.pl', 'Ava', 'Chapman', '998090', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('988710@student.pwr.edu.pl', 'Michael', 'Gilbert', '988710', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('877611@student.pwr.edu.pl', 'Olivia', 'Thornton', '877611', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('766512@student.pwr.edu.pl', 'Daniel', 'Malone', '766512', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('655413@student.pwr.edu.pl', 'Sophia', 'Saunders', '655413', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('544314@student.pwr.edu.pl', 'William', 'Vargas', '544314', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('433215@student.pwr.edu.pl', 'Ava', 'Maldonado', '433215', (SELECT id FROM role WHERE name = 'student'), 'STU'),
    ('322116@student.pwr.edu.pl', 'Michael', 'Santos', '322116', (SELECT id FROM role WHERE name = 'student'), ''),
    ('211017@student.pwr.edu.pl', 'Olivia', 'Moran', '211017', (SELECT id FROM role WHERE name = 'student'), ''),
    ('100918@student.pwr.edu.pl', 'Daniel', 'Haynes', '100918', (SELECT id FROM role WHERE name = 'student'), 'STU');

INSERT INTO employee (mail, name, surname, title, department_id)
VALUES
    ('john.doe@pwr.edu.pl', 'John', 'Doe', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('alice.smith@pwr.edu.pl', 'Alice', 'Smith', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('michael.johnson@pwr.edu.pl', 'Michael', 'Johnson', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('sarah.williams@pwr.edu.pl', 'Sarah', 'Williams', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('david.brown@pwr.edu.pl', 'David', 'Brown', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('jennifer.lee@pwr.edu.pl', 'Jennifer', 'Lee', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('christopher.taylor@pwr.edu.pl', 'Christopher', 'Taylor', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('jessica.harris@pwr.edu.pl', 'Jessica', 'Harris', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('matthew.clark@pwr.edu.pl', 'Matthew', 'Clark', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('emily.anderson@pwr.edu.pl', 'Emily', 'Anderson', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('daniel.lewis@pwr.edu.pl', 'Daniel', 'Lewis', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('olivia.ward@pwr.edu.pl', 'Olivia', 'Ward', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('andrew.scott@pwr.edu.pl', 'Andrew', 'Scott', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('sophia.baker@pwr.edu.pl', 'Sophia', 'Baker', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('william.taylor@pwr.edu.pl', 'William', 'Taylor', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('ava.green@pwr.edu.pl', 'Ava', 'Green', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('olivia.young@pwr.edu.pl', 'Olivia', 'Young', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('daniel.king@pwr.edu.pl', 'Daniel', 'King', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('sophia.cooper@pwr.edu.pl', 'Sophia', 'Cooper', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('william.khan@pwr.edu.pl', 'William', 'Khan', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('ava.bryant@pwr.edu.pl', 'Ava', 'Bryant', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('michael.evans@pwr.edu.pl', 'Michael', 'Evans', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('olivia.fisher@pwr.edu.pl', 'Olivia', 'Fisher', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('daniel.nelson@pwr.edu.pl', 'Daniel', 'Nelson', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('sophia.wells@pwr.edu.pl', 'Sophia', 'Wells', (SELECT id FROM title WHERE name = 'dr hab.'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('william.rose@pwr.edu.pl', 'William', 'Rose', (SELECT id FROM title WHERE name = 'prof'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('ava.chapman@pwr.edu.pl', 'Ava', 'Chapman', (SELECT id FROM title WHERE name = 'mgr'), (SELECT id FROM department WHERE code = 'K34W04ND03'));

INSERT INTO employee_role (employee_id, role_id)
VALUES
    ((SELECT id FROM employee WHERE mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'alice.smith@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'michael.johnson@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'sarah.williams@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'david.brown@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'jennifer.lee@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'christopher.taylor@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'jessica.harris@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'matthew.clark@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'emily.anderson@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'daniel.lewis@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'olivia.ward@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'andrew.scott@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'sophia.baker@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'william.taylor@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'ava.green@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'olivia.young@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'daniel.king@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'sophia.cooper@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'william.khan@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'ava.bryant@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'michael.evans@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'olivia.fisher@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'daniel.nelson@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'daniel.nelson@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'approver')),
    ((SELECT id FROM employee WHERE mail = 'sophia.wells@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = 'sophia.wells@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'approver')),
    ((SELECT id FROM employee WHERE mail = 'sophia.wells@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin')),
    ((SELECT id FROM employee WHERE mail = 'william.rose@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'approver')),
    ((SELECT id FROM employee WHERE mail = 'ava.chapman@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin'));

INSERT INTO Thesis (name_pl, name_en, description_pl, description_en, num_people, supervisor, cycle_id, status, creation_time, comment_id)
VALUES
    ('Mobilna aplikacja dla miłośników starych zamków', 'Mobile application for lovers of old castles', 'Opis1', 'Description1', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Wieloosobowa i wielopoziomowa gra komputerowa', 'Multiplayer, and multi-level computer game', 'Opis2', 'Description2', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'emily.anderson@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Mobilna aplikacja dla miłośników astronomii', 'Mobile application for lovers and collectors of antiques', 'Opis3', 'Description3', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'jennifer.lee@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Mobilna aplikacja dla miłośników i kolekcjonerów staroci', 'Mobile application for lovers and collectors of antiques', 'Opis4', 'Description4', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Opis5', 'Description5', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Opis6', 'Description6', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'david.brown@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Opis7', 'Description7', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Rytmiczna gra komputerowa wykorzystująca "walking piano" w rzeczywistości rozszerzonej', 'Rhythm video game using walking piano in augmented reality', 'Opis8', 'Description8', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'michael.johnson@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Aplikacja rzeczywistości rozszerzonej wspomagająca grę na instrumencie muzycznym', 'Augmented Reality application that supports playing musical instrument', 'Opis9', 'Description9', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('System wspierający dobór recenzentów artykułów', 'System supporting articles reviewers selection', 'Opis10', 'Description10', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Draft'), NOW(), NULL),
    ('Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Opis11', 'Description11', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Opis12', 'Description12', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Opis13', 'Description13', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Opis14', 'Description14', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Opis15', 'Description15', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Opis16', 'Description16', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Aplikacja do zarządzania wydatkami grupowymi i osobistymi', 'Application to manage group and personal expenses', 'Opis17', 'Description17', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Aplikacja webowa wspomagająca przeprowadzanie sesji gry RPG "Mafia" z możliwością gry zdalnej', 'Web application supporting conducting of RPG game "Mafia" session, with option to play remotely', 'Opis18', 'Description18', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Opis19', 'Description19', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Pending approval'), NOW(), NULL),
    ('System zarządzania relacjami studentów z pracownikami uczelni', 'Student relation management', 'Opis20', 'Description20', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Rejected'), NOW(), NULL),
    ('SRM: System zarządzania relacjami z naukowcami', 'SRM: Scientists Relationship Management System', 'Opis21', 'Description21', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Rejected'), NOW(), NULL),
    ('System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Opis22', 'Description22', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Rejected'), NOW(), NULL),
    ('System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Opis23', 'Description23', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Rejected'), NOW(), NULL),
    ('System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Opis24', 'Description24', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Rejected'), NOW(), NULL),
    ('System wspomagający integrację oraz komunikację webowych i mobilnych aplikacji IoT', 'System for communication and integration of IoT applications with mobile and web services', 'Opis25', 'Description25', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Approved'), NOW(), NULL),
    ('Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Opis26', 'Description26', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Approved'), NOW(), NULL),
    ('Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Opis27', 'Description27', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Approved'), NOW(), NULL),
    ('Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Opis28', 'Description28', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Approved'), NOW(), NULL),
    ('Program do analizy wydajności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Opis29', 'Description29', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Approved'), NOW(), NULL),
    ('System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Opis30', 'Description30', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Assigned'), NOW(), NULL),
    ('System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Opis31', 'Description31', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Assigned'), NOW(), NULL),
    ('System do zarządzania i monitorowania upraw hydroponicznych', 'System for managing and monitoring hydroponic crops', 'Opis32', 'Description32', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Assigned'), NOW(), NULL),
    ('Aplikacja do ewidencji świadczonych usług oraz pracy', 'Application for accounting provided services and work', 'Opis33', 'Description33', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Assigned'), NOW(), NULL),
    ('Zaprojektowanie i zaimplementowanie systemu do automatyzacji zarządzania wynajmem nieruchomości', 'Design and implementation of an automated management system for property rental', 'Opis34', 'Description34', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Assigned'), NOW(), NULL),
    ('Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Opis35', 'Description35', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('System wspomagający rozpoznawania obrazów', 'Image recognition support system', 'Opis36', 'Description36', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('System obsługi rodzinnych ogrodów działkowych', 'System of service for family allotment gardens', 'Opis37', 'Description37', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Opis38', 'Description38', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('System wspierający organizację grupowych aktywności sportowych', 'System supporting the organization of group sports activities', 'Opis39', 'Description39', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('System wspierający organizację konkursu Polish Project Excellence Award', 'System for Polish Project Excellence Award', 'Opis40', 'Description40', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Opis41', 'Description41', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('System obsługi stołówek szkolnych', 'School canteen management system', 'Opis42', 'Description42', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL),
    ('Aplikacja do zarządzania wydatkami grupowymi i osobistymi', 'Application to manage group and personal expenses', 'Opis43', 'Description43', 5, (SELECT e.id FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'), (SELECT id FROM status WHERE name = 'Closed'), NOW(), NULL);


INSERT INTO program_thesis (thesis_id, program_id)
VALUES
    ((SELECT id FROM thesis WHERE description_pl = 'Opis1'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis2'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis3'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis4'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis5'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis6'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis7'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis8'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis9'),  (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis10'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis11'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis12'), (SELECT id FROM program WHERE name = 'W04-ISTA-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis13'), (SELECT id FROM program WHERE name = 'W04-ISTA-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis14'), (SELECT id FROM program WHERE name = 'W04-ISTA-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis15'), (SELECT id FROM program WHERE name = 'W04-ISTA-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis16'), (SELECT id FROM program WHERE name = 'W04-ISTA-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis17'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis18'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis19'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis20'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis21'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis22'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis23'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis24'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis25'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis26'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis27'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis28'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis29'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis30'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis31'), (SELECT id FROM program WHERE name = 'W04-INAP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis32'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis33'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis34'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis35'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis36'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis37'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis38'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis39'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis40'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis41'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis42'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7')),
    ((SELECT id FROM thesis WHERE description_pl = 'Opis43'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'));

INSERT INTO employee(mail, name, surname, title, department_id)
VALUES
    ('260452@student.pwr.edu.pl', 'Piotr', 'Wojdan', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('260466@student.pwr.edu.pl', 'Marta', 'Rzepka', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('260464@student.pwr.edu.pl', 'Zuzanna', 'Sikorska', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03')),
    ('255356@student.pwr.edu.pl', 'Jakub', 'Krupiński', (SELECT id FROM title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03'));

INSERT INTO employee_role(employee_id, role_id)
VALUES
    ((SELECT id FROM employee WHERE mail = '260452@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin')),
    ((SELECT id FROM employee WHERE mail = '260466@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin')),
    ((SELECT id FROM employee WHERE mail = '260464@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin')),
    ((SELECT id FROM employee WHERE mail = '255356@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin'));

INSERT INTO employee_role(employee_id, role_id)
VALUES
    ((SELECT id FROM employee WHERE mail = '260452@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = '260466@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = '260464@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor')),
    ((SELECT id FROM employee WHERE mail = '255356@student.pwr.edu.pl'), (SELECT id FROM role WHERE name = 'supervisor'));



INSERT INTO student_program_cycle (student_id, program_id, cycle_id)
VALUES
    ((SELECT id FROM student WHERE mail = '123456@student.pwr.edu.pl'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM student WHERE mail = '123456@student.pwr.edu.pl'), (SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM student WHERE mail = '998090@student.pwr.edu.pl'), (SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'));


INSERT INTO program_cycle (program_id, cycle_id)
VALUES
    ((SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TAIP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TELP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INAP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-CBEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INFP-IOPP-OSMW3'), (SELECT id FROM study_cycle WHERE name = '2020/21-Z')),

    ((SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TAIP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TELP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INAP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-CBEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INFP-IOPP-OSMW3'), (SELECT id FROM study_cycle WHERE name = '2021/22-Z')),

    ((SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TAIP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TELP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INAP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-CBEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INFP-IOPP-OSMW3'), (SELECT id FROM study_cycle WHERE name = '2022/23-Z')),

    ((SELECT id FROM program WHERE name = 'W04-ISTP-000P-OSIW7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-ITEP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TAIP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-TELP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INAP-000P-OSME3'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-CBEP-000P-OSIE7'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z')),
    ((SELECT id FROM program WHERE name = 'W04-INFP-IOPP-OSMW3'), (SELECT id FROM study_cycle WHERE name = '2023/24-Z'));


INSERT INTO comment (author_id, content, creation_time, thesis_id)
VALUES
    ((SELECT id FROM employee WHERE mail = 'john.doe@pwr.edu.pl' LIMIT 1), 'This is a great idea!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Mobilna aplikacja dla miłośników starych zamków'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'alice.smith@pwr.edu.pl'LIMIT 1), 'Interesting concept. Can we discuss further?', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Mobilna aplikacja dla miłośników starych zamków'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'john.doe@pwr.edu.pl'LIMIT 1), 'Looking forward to the progress!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Wieloosobowa i wielopoziomowa gra komputerowa'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'emily.anderson@pwr.edu.pl'LIMIT 1), 'I have some suggestions for improvement.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Wieloosobowa i wielopoziomowa gra komputerowa'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'william.taylor@pwr.edu.pl'LIMIT 1), 'This simulation system is needed.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System do symulacji ruchu drogowego'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'john.doe@pwr.edu.pl'LIMIT 1), 'I support this project!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System do symulacji ruchu drogowego'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'william.taylor@pwr.edu.pl'LIMIT 1), 'Lets discuss the details.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Symulator pracy robota sprzątającego w środowisku wirtualnym'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'john.doe@pwr.edu.pl'LIMIT 1), 'Great potential in this simulator.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Symulator pracy robota sprzątającego w środowisku wirtualnym'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'william.khan@pwr.edu.pl'LIMIT 1), 'I am interested in working on this project.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'michael.evans@pwr.edu.pl'LIMIT 1), 'Lets coordinate our efforts.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System do symulacji ruchu drogowego'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'olivia.fisher@pwr.edu.pl'LIMIT 1), 'Congratulations on completing the project!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System wspomagający rozpoznawania obrazów'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'daniel.nelson@pwr.edu.pl'LIMIT 1), 'Well done on the garden system!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System obsługi rodzinnych ogrodów działkowych'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'sophia.cooper@pwr.edu.pl'LIMIT 1), 'Im sorry, but I think there are better ideas.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'william.khan@pwr.edu.pl'LIMIT 1), 'Unfortunately, this doesnt align with our goals.', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'ava.bryant@pwr.edu.pl'LIMIT 1), 'Great work on the IoT system!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'System wspomagający integrację oraz komunikację webowych i mobilnych aplikacji IoT'LIMIT 1)),
    ((SELECT id FROM employee WHERE mail = 'sophia.cooper@pwr.edu.pl'LIMIT 1), 'Congratulations on the simulator!', NOW(), (SELECT id FROM thesis WHERE name_pl = 'Symulator pracy robota sprzątającego w środowisku wirtualnym'LIMIT 1));


INSERT INTO deadline(name_pl, name_en, deadline_date)
VALUES
    ('Zgłaszanie tematów przez prowadzących', 'Submitting topics by supervisors', '2024-04-01'),
    ('Rozpatrywanie tematów przez zatwierdzających', 'Reviewing topics by approvers', '2024-04-10'),
    ('Rezerwowanie tematów przez studentów', 'Reserving topics by students', '2024-05-15'),
    ('Zaniesienie wydrukowanej i podpisanej deklaracji tematu do dziekanatu', 'Submitting signed declaration to the department secretary', '2024-05-31');
