INSERT INTO faculty
VALUES('W04N', 'Wydział Informatyki i Telekomunikacji');

INSERT INTO department
VALUES('K34W04ND03', 'Katedra Telekomunikacji i Teleinformatyki', 'W04N');

INSERT INTO study_field
VALUES ('IST', 'Inżynieria Systemów Teleinformatycznych');

INSERT INTO program (id, name, study_field_abbreviation)
VALUES (1, 'W04-ISTP-000P-OSIW7', 'IST');


INSERT INTO study_cycle (id, name)
VALUES
    (1, '2023/24-Z'),
    (2, '2022/23-Z'),
    (3, '2021/22-Z'),
    (4, '2020/21-Z');


INSERT INTO student (mail, name, surname, index, program, teaching_cycle, role, status)
VALUES
    ('123456@student.pwr.edu.pl', 'John', 'Doe', '123456', 1, 1, 'student', 'STU'),
    ('234567@student.pwr.edu.pl', 'Alice', 'Smith', '234567', 1, 1, 'student', 'STU'),
    ('345678@student.pwr.edu.pl', 'Michael', 'Johnson', '345678', 1, 1, 'student', 'STU'),
    ('456789@student.pwr.edu.pl', 'Sarah', 'Williams', '456789', 1, 1, 'student', 'STU'),
    ('567890@student.pwr.edu.pl', 'David', 'Brown', '567890', 1, 1, 'student', 'STU'),
    ('678901@student.pwr.edu.pl', 'Jennifer', 'Lee', '678901', 1, 1, 'student', 'STU'),
    ('789012@student.pwr.edu.pl', 'Christopher', 'Taylor', '789012', 1, 1, 'student', 'STU'),
    ('890123@student.pwr.edu.pl', 'Jessica', 'Harris', '890123', 1, 1, 'student', 'STU'),
    ('901234@student.pwr.edu.pl', 'Matthew', 'Clark', '901234', 1, 1, 'student', 'STU'),
    ('012345@student.pwr.edu.pl', 'Emily', 'Anderson', '012345', 1, 1, 'student', 'STU'),
    ('987654@student.pwr.edu.pl', 'Daniel', 'Lewis', '987654', 1, 1, 'student', 'STU'),
    ('876543@student.pwr.edu.pl', 'Olivia', 'Ward', '876543', 1, 1, 'student', 'STU'),
    ('765432@student.pwr.edu.pl', 'Andrew', 'Scott', '765432', 1, 1, 'student', 'STU'),
    ('654321@student.pwr.edu.pl', 'Sophia', 'Baker', '654321', 1, 1, 'student', 'STU'),
    ('543210@student.pwr.edu.pl', 'William', 'Taylor', '543210', 1, 1, 'student', 'STU'),
    ('432109@student.pwr.edu.pl', 'Ava', 'Green', '432109', 1, 1, 'student', 'STU'),
    ('321098@student.pwr.edu.pl', 'Michael', 'Wright', '321098', 1, 1, 'student', 'STU'),
    ('210987@student.pwr.edu.pl', 'Olivia', 'Young', '210987', 1, 1, 'student', 'STU'),
    ('109876@student.pwr.edu.pl', 'Daniel', 'King', '109876', 1, 1, 'student', 'STU'),
    ('987601@student.pwr.edu.pl', 'Sophia', 'Cooper', '987601', 1, 1, 'student', 'STU'),
    ('876502@student.pwr.edu.pl', 'William', 'Khan', '876502', 1, 1, 'student', 'STU'),
    ('765403@student.pwr.edu.pl', 'Ava', 'Bryant', '765403', 1, 1, 'student', 'STU'),
    ('654304@student.pwr.edu.pl', 'Michael', 'Evans', '654304', 1, 1, 'student', 'STU'),
    ('543205@student.pwr.edu.pl', 'Olivia', 'Fisher', '543205', 1, 1, 'student', 'STU'),
    ('432106@student.pwr.edu.pl', 'Daniel', 'Nelson', '432106', 1, 1, 'student', 'STU'),
    ('321007@student.pwr.edu.pl', 'Sophia', 'Wells', '321007', 1, 1, 'student', 'STU'),
    ('210908@student.pwr.edu.pl', 'William', 'Rose', '210908', 1, 1, 'student', 'STU'),
    ('099809@student.pwr.edu.pl', 'Ava', 'Chapman', '099809', 1, 1, 'student', 'STU'),
    ('988710@student.pwr.edu.pl', 'Michael', 'Gilbert', '988710', 1, 1, 'student', 'STU'),
    ('877611@student.pwr.edu.pl', 'Olivia', 'Thornton', '877611', 1, 1, 'student', 'STU'),
    ('766512@student.pwr.edu.pl', 'Daniel', 'Malone', '766512', 1, 1, 'student', 'STU'),
    ('655413@student.pwr.edu.pl', 'Sophia', 'Saunders', '655413', 1, 1, 'student', 'STU'),
    ('544314@student.pwr.edu.pl', 'William', 'Vargas', '544314', 1, 1, 'student', 'STU'),
    ('433215@student.pwr.edu.pl', 'Ava', 'Maldonado', '433215', 1, 1, 'student', 'STU'),
    ('322116@student.pwr.edu.pl', 'Michael', 'Santos', '322116', 1, 1, 'student', ''),
    ('211017@student.pwr.edu.pl', 'Olivia', 'Moran', '211017', 1, 1, 'student', ''),
    ('100918@student.pwr.edu.pl', 'Daniel', 'Haynes', '100918', 1, 1, 'student', 'STU');



INSERT INTO employee (mail, name, surname, title, role, department_code)
VALUES
    ('john.doe@pwr.edu.pl', 'John', 'Doe', 'dr', 'teacher', 'K34W04ND03'),
    ('alice.smith@pwr.edu.pl', 'Alice', 'Smith', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('michael.johnson@pwr.edu.pl', 'Michael', 'Johnson', 'prof', 'teacher', 'K34W04ND03'),
    ('sarah.williams@pwr.edu.pl', 'Sarah', 'Williams', 'dr', 'teacher', 'K34W04ND03'),
    ('david.brown@pwr.edu.pl', 'David', 'Brown', 'mgr', 'teacher', 'K34W04ND03'),
    ('jennifer.lee@pwr.edu.pl', 'Jennifer', 'Lee', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('christopher.taylor@pwr.edu.pl', 'Christopher', 'Taylor', 'prof', 'teacher', 'K34W04ND03'),
    ('jessica.harris@pwr.edu.pl', 'Jessica', 'Harris', 'mgr', 'teacher', 'K34W04ND03'),
    ('matthew.clark@pwr.edu.pl', 'Matthew', 'Clark', 'dr', 'teacher', 'K34W04ND03'),
    ('emily.anderson@pwr.edu.pl', 'Emily', 'Anderson', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('daniel.lewis@pwr.edu.pl', 'Daniel', 'Lewis', 'prof', 'teacher', 'K34W04ND03'),
    ('olivia.ward@pwr.edu.pl', 'Olivia', 'Ward', 'dr', 'teacher', 'K34W04ND03'),
    ('andrew.scott@pwr.edu.pl', 'Andrew', 'Scott', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('sophia.baker@pwr.edu.pl', 'Sophia', 'Baker', 'prof', 'teacher', 'K34W04ND03'),
    ('william.taylor@pwr.edu.pl', 'William', 'Taylor', 'mgr', 'teacher', 'K34W04ND03'),
    ('ava.green@pwr.edu.pl', 'Ava', 'Green', 'dr', 'teacher', 'K34W04ND03'),
    ('olivia.young@pwr.edu.pl', 'Olivia', 'Young', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('daniel.king@pwr.edu.pl', 'Daniel', 'King', 'prof', 'teacher', 'K34W04ND03'),
    ('sophia.cooper@pwr.edu.pl', 'Sophia', 'Cooper', 'mgr', 'teacher', 'K34W04ND03'),
    ('william.khan@pwr.edu.pl', 'William', 'Khan', 'dr', 'teacher', 'K34W04ND03'),
    ('ava.bryant@pwr.edu.pl', 'Ava', 'Bryant', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('michael.evans@pwr.edu.pl', 'Michael', 'Evans', 'prof', 'teacher', 'K34W04ND03'),
    ('olivia.fisher@pwr.edu.pl', 'Olivia', 'Fisher', 'mgr', 'teacher', 'K34W04ND03'),
    ('daniel.nelson@pwr.edu.pl', 'Daniel', 'Nelson', 'dr', 'teacher', 'K34W04ND03'),
    ('sophia.wells@pwr.edu.pl', 'Sophia', 'Wells', 'dr hab.', 'teacher', 'K34W04ND03'),
    ('william.rose@pwr.edu.pl', 'William', 'Rose', 'prof', 'teacher', 'K34W04ND03'),
    ('ava.chapman@pwr.edu.pl', 'Ava', 'Chapman', 'mgr', 'teacher', 'K34W04ND03');

INSERT INTO Thesis (thesis_id, name_pl, name_en, description, num_people, supervisor, faculty, field, edu_cycle, status)
VALUES
    ('1', 'Mobilna aplikacja dla miłośników starych zamków', 'Mobile application for lovers of old castles', 'Description1', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 'Faculty1', 'Field1', 'EduCycle1', 'OPEN'),
    ('2', 'Wieloosobowa i wielopoziomowa gra komputerowa', 'Multiplayer, and multi-level computer game', 'Description2', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'emily.anderson@pwr.edu.pl'), 'Faculty2', 'Field2', 'EduCycle2', 'OPEN'),
    ('3', 'Mobilna aplikacja dla miłośników astronomii', 'Mobile application for lovers and collectors of antiques', 'Description3', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'jennifer.lee@pwr.edu.pl'), 'Faculty3', 'Field3', 'EduCycle3', 'CLOSED'),
    ('4', 'Mobilna aplikacja dla miłośników i kolekcjonerów staroci', 'Mobile application for lovers and collectors of antiques', 'Description4', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 'Faculty4', 'Field4', 'EduCycle4', 'OPEN'),
    ('5', 'System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Description5', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 'Faculty5', 'Field5', 'EduCycle5', 'OPEN'),
    ('6', 'System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Description6', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'david.brown@pwr.edu.pl'), 'Faculty6', 'Field6', 'EduCycle6', 'CLOSED'),
    ('7', 'System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Description7', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 'Faculty7', 'Field7', 'EduCycle7', 'OPEN'),
    ('8', 'Rytmiczna gra komputerowa wykorzystująca "walking piano" w rzeczywistości rozszerzonej', 'Rhythm video game using walking piano in augmented reality', 'Description8', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.johnson@pwr.edu.pl'), 'Faculty8', 'Field8', 'EduCycle8', 'OPEN'),
    ('9', 'Aplikacja rzeczywistości rozszerzonej wspomagająca grę na instrumencie muzycznym', 'Augmented Reality application that supports playing musical instrument', 'Description9', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.lewis@pwr.edu.pl'), 'Faculty9', 'Field9', 'EduCycle9', 'CLOSED'),
    ('10', 'System wspierający dobór recenzentów artykułów', 'System supporting articles reviewers selection', 'Description10', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), 'Faculty10', 'Field10', 'EduCycle10', 'OPEN'),
    ('11', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description11', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 'Faculty11', 'Field11', 'EduCycle11', 'OPEN'),
    ('12', 'System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Description12', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.baker@pwr.edu.pl'), 'Faculty12', 'Field12', 'EduCycle12', 'CLOSED'),
    ('13', 'System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Description13', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), 'Faculty13', 'Field13', 'EduCycle13', 'OPEN'),
    ('14', 'Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Description14', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 'Faculty14', 'Field14', 'EduCycle14', 'OPEN'),
    ('15', 'Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Description15', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.taylor@pwr.edu.pl'), 'Faculty15', 'Field15', 'EduCycle15', 'CLOSED'),
    ('16', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description16', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'john.doe@pwr.edu.pl'), 'Faculty16', 'Field16', 'EduCycle16', 'OPEN'),
    ('17', 'Aplikacja do zarządzania wydatkami grupowymi i osobistymi', 'Application to manage group and personal expenses', 'Description17', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 'Faculty17', 'Field17', 'EduCycle17', 'OPEN'),
    ('18', 'Aplikacja webowa wspomagająca przeprowadzanie sesji gry RPG "Mafia" z możliwością gry zdalnej', 'Web application supporting conducting of RPG game "Mafia" session, with option to play remotely', 'Description18', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 'Faculty18', 'Field18', 'EduCycle18', 'CLOSED'),
    ('19', 'Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Description19', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.green@pwr.edu.pl'), 'Faculty19', 'Field19', 'EduCycle19', 'OPEN'),
    ('20', 'System zarządzania relacjami studentów z pracownikami uczelni', 'Student relation management', 'Description20', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), 'Faculty20', 'Field20', 'EduCycle20', 'OPEN'),
    ('21', 'SRM: System zarządzania relacjami z naukowcami', 'SRM: Scientists Relationship Management System', 'Description21', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.young@pwr.edu.pl'), 'Faculty21', 'Field21', 'EduCycle21', 'CLOSED'),
    ('22', 'System wspomagający planowanie wspólnych dojazdów z wykorzystaniem technologii Blockchain', 'A system to support the planning of carpooling using Blockchain technology', 'Description22', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 'Faculty22', 'Field22', 'EduCycle22', 'OPEN'),
    ('23', 'System wspomagający rodzica w organizacji aktywnego spędzania czasu z dzieckiem', 'System to assist the parent in organizing active time with the child', 'Description23', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 'Faculty23', 'Field23', 'EduCycle23', 'OPEN'),
    ('24', 'System wspomagający tworzenie i przeprowadzenie kampanii fundrisingowej dla podmiotów NGO.', 'A system to support the creation and execution of a fundrising campaign for NGO entities', 'Description24', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 'Faculty24', 'Field24', 'EduCycle24', 'CLOSED'),
    ('25', 'System wspomagający integrację oraz komunikację webowych i mobilnych aplikacji IoT', 'System for communication and integration of IoT applications with mobile and web services', 'Description25', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 'Faculty25', 'Field25', 'EduCycle25', 'OPEN'),
    ('26', 'Symulator pracy robota sprzątającego w środowisku wirtualnym', 'Robot vaccum operation simulator in virtual environment', 'Description26', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 'Faculty26', 'Field26', 'EduCycle26', 'OPEN'),
    ('27', 'Rozproszony serwer rozpoznawania mowy z udostępnianiem zasobów obliczeniowych na komputerach użytkowników', 'Distributed speech recognition server using local users computational resources', 'Description27', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 'Faculty27', 'Field27', 'EduCycle27', 'CLOSED'),
    ('28', 'Program do analizy efektywności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description28', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.cooper@pwr.edu.pl'), 'Faculty28', 'Field28', 'EduCycle28', 'OPEN'),
    ('29', 'Program do analizy wydajności instalacji fotowoltaicznej', 'A program for photovoltaic system efficiency analysis', 'Description29', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), 'Faculty29', 'Field29', 'EduCycle29', 'OPEN'),
    ('30', 'System rozpoznawania mowy do współpracy z dowolnym programem w systemie Windows', 'Speech recognition system colaborating with any Windows GUI program', 'Description30', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 'Faculty30', 'Field30', 'EduCycle30', 'CLOSED'),
    ('31', 'System do symulacji ruchu drogowego', 'Road traffic simulation system', 'Description31', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'michael.evans@pwr.edu.pl'), 'Faculty31', 'Field31', 'EduCycle31', 'OPEN'),
    ('32', 'System do zarządzania i monitorowania upraw hydroponicznych', 'System for managing and monitoring hydroponic crops', 'Description32', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), 'Faculty32', 'Field32', 'EduCycle32', 'OPEN'),
    ('33', 'Aplikacja do ewidencji świadczonych usług oraz pracy', 'Application for accounting provided services and work', 'Description33', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), 'Faculty33', 'Field33', 'EduCycle33', 'CLOSED'),
    ('34', 'Zaprojektowanie i zaimplementowanie systemu do automatyzacji zarządzania wynajmem nieruchomości', 'Design and implementation of an automated management system for property rental', 'Description34', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 'Faculty34', 'Field34', 'EduCycle34', 'OPEN'),
    ('35', 'Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Description35', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.king@pwr.edu.pl'), 'Faculty35', 'Field35', 'EduCycle35', 'OPEN'),
    ('36', 'System wspomagający rozpoznawania obrazów', 'Image recognition support system', 'Description36', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'olivia.fisher@pwr.edu.pl'), 'Faculty36', 'Field36', 'EduCycle36', 'CLOSED'),
    ('37', 'System obsługi rodzinnych ogrodów działkowych', 'System of service for family allotment gardens', 'Description37', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), 'Faculty37', 'Field37', 'EduCycle37', 'OPEN'),
    ('38', 'Narzędzie do anotacji ontologicznej zdjęć dwuwymiarowych', 'A tool for 2D image ontological annotation.', 'Description38', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'daniel.nelson@pwr.edu.pl'), 'Faculty38', 'Field38', 'EduCycle38', 'OPEN'),
    ('39', 'System wspierający organizację grupowych aktywności sportowych', 'System supporting the organization of group sports activities', 'Description39', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'ava.bryant@pwr.edu.pl'), 'Faculty39', 'Field39', 'EduCycle39', 'CLOSED'),
    ('40', 'System wspierający organizację konkursu Polish Project Excellence Award', 'System for Polish Project Excellence Award', 'Description40', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), 'Faculty40', 'Field40', 'EduCycle40', 'OPEN'),
    ('41', 'Komunikator internetowy z możliwością udostępniania położenia', 'Internet communicator with location sharing function', 'Description41', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'sophia.wells@pwr.edu.pl'), 'Faculty41', 'Field41', 'EduCycle41', 'OPEN'),
    ('42', 'System obsługi stołówek szkolnych', 'System for school cafeteria', 'Description42', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'jessica.harris@pwr.edu.pl'), 'Faculty42', 'Field42', 'EduCycle42', 'CLOSED'),
    ('43', 'Aplikacja wirtualnej rzeczywistości do ćwiczeń ogólnorozwojowych', 'A virtual reality application for general development exercises', 'Description43', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'jessica.harris@pwr.edu.pl'), 'Faculty43', 'Field43', 'EduCycle43', 'OPEN'),
    ('44', 'System wspomagający naukę gry na akordeonie', 'Accordion learning support system', 'Description44', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'jessica.harris@pwr.edu.pl'), 'Faculty44', 'Field44', 'EduCycle44', 'OPEN'),
    ('45', 'System zarządzania relacjami studentów z pracownikami uczelni', 'Student relation management', 'Description45', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'matthew.clark@pwr.edu.pl'), 'Faculty45', 'Field45', 'EduCycle45', 'CLOSED'),
    ('46', 'System webowy do składania zamówień w restauracji z uwzględnieniem kodu QR', 'Web system for placing orders in a restaurant, including the QR code', 'Description46', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 'Faculty46', 'Field46', 'EduCycle46', 'OPEN'),
    ('47', 'System webowy do wyszukiwania atrakcji turystycznych', 'Web system for searching tourist attractions', 'Description47', 5, (SELECT e.mail FROM Employee e WHERE e.mail = 'william.khan@pwr.edu.pl'), 'Faculty47', 'Field47', 'EduCycle47', 'OPEN');


