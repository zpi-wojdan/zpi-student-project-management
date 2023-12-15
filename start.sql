INSERT INTO faculty(abbreviation, name)
VALUES('W04N', 'Wydział Informatyki i Telekomunikacji');

INSERT INTO title (name, num_theses)
VALUES
    ('N/A', 0),
    ('mgr', 1),
    ('mgr inż.', 1),
    ('dr', 2),
    ('dr hab.', 2),
    ('prof', 2),
    ('prof. dr hab.', 2);

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

INSERT INTO study_field(abbreviation, name, faculty_id)
VALUES
    ('IST', 'Informatyka stosowana', (SELECT id FROM faculty WHERE abbreviation = 'W04N'));

INSERT INTO program (name, study_field_id, specialization_id, faculty_id)
VALUES
    ('W04-ISTP-000P-OSIW7', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N')),
    ('W04-ISTA-000P-OSIW7', (SELECT id FROM study_field WHERE abbreviation = 'IST'), NULL, (SELECT id FROM faculty WHERE abbreviation = 'W04N'))

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

INSERT INTO employee (mail, name, surname, title, num_theses, department_id)
VALUES
    ('jan.kowalski@pwr.edu.pl', 'Jan', 'Kowalski', (select id from title WHERE name = 'dr'), (select num_theses from title WHERE name = 'dr'), (SELECT id FROM department WHERE code = 'K34W04ND03'));
    
INSERT INTO employee_role (employee_id, role_id)
VALUES
    ((SELECT id FROM employee WHERE mail = 'jan.kowalski@pwr.edu.pl'), (SELECT id FROM role WHERE name = 'admin'));