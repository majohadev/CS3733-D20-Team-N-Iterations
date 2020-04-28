CREATE TABLE nodes (
  nodeID CHAR(10) NOT NULL PRIMARY KEY,
  xcoord INT NOT NULL,
  ycoord INT NOT NULL,
  floor INT NOT NULL,
  building VARCHAR(255) NOT NULL,
  nodeType CHAR(4) NOT NULL CONSTRAINT TYPE_CK CHECK (nodeType IN ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')),
  longName VARCHAR(255) NOT NULL,
  shortName VARCHAR(255) NOT NULL,
  teamAssigned CHAR(1) NOT NULL
  );

  CREATE TABLE kiosk (
  nodeID char(10) NOT NULL PRIMARY KEY REFERENCES nodes(nodeID) ON DELETE CASCADE,
  angle INT NOT NULL
  );


CREATE TABLE edges (
   edgeID CHAR(21) NOT NULL PRIMARY KEY,
   node1 CHAR(10) NOT NULL,
   node2 CHAR(10) NOT NULL,
   CONSTRAINT FK_edgeN1 FOREIGN KEY (node1) REFERENCES nodes(nodeID) ON DELETE CASCADE,
   CONSTRAINT FK_edgeN2 FOREIGN KEY (node2) REFERENCES nodes(nodeID) ON DELETE CASCADE
   );


CREATE TABLE service (
  serviceType VARCHAR(255) NOT NULL PRIMARY KEY,
  timeStart CHAR(5),
  timeEnd CHAR(5),
  description VARCHAR(255));

CREATE TABLE employees (
      employeeID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      serviceType VARCHAR(255) NOT NULL,
      CONSTRAINT FK_empSERV FOREIGN KEY (serviceType) REFERENCES service (serviceType));

CREATE TABLE translator (
      t_employeeID INT NOT NULL PRIMARY KEY,
      CONSTRAINT FK_trans FOREIGN KEY (t_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE language (
      t_employeeID INT NOT NULL,
      language VARCHAR(255) NOT NULL,
      CONSTRAINT LANG_PK PRIMARY KEY (t_employeeID, language),
      CONSTRAINT FK_lang FOREIGN KEY (t_employeeID) REFERENCES translator (t_employeeID) ON DELETE CASCADE);

CREATE TABLE laundry(
      l_employeeID INT NOT NULL,
      PRIMARY KEY(l_employeeID),
      CONSTRAINT FK_laund FOREIGN KEY (l_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE sanitation(
    sanitationEmployeeID INT NOT NULL,
    PRIMARY KEY(sanitationEmployeeID),
    CONSTRAINT FK_san FOREIGN KEY (sanitationEmployeeID) REFERENCES employees(employeeID) ON DELETE CASCADE
);

CREATE TABLE credential(
    username VARCHAR(255) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    access VARCHAR(6) NOT NULL CONSTRAINT ACC_CK CHECK (access IN ('ADMIN', 'DOCTOR'))
);

CREATE TABLE doctors (
      doctorID INT NOT NULL PRIMARY KEY,
      field VARCHAR(255) NOT NULL,
      username VARCHAR(255) NOT NULL UNIQUE,
      CONSTRAINT doc_user FOREIGN KEY (username) REFERENCES credential(username),
      CONSTRAINT doc_id FOREIGN KEY (doctorID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE flower(
    flowerName VARCHAR(255) PRIMARY KEY,
    price INT NOT NULL
);

/*TODO add the employees table */

CREATE TABLE wheelchairEmployee(
      w_employeeID INT NOT NULL,
      PRIMARY KEY(w_employeeID),
      CONSTRAINT wheel_id FOREIGN KEY (w_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE emotionalSupporter(
      l_employeeID INT NOT NULL,
      PRIMARY KEY(l_employeeID),
      CONSTRAINT emo_ID FOREIGN KEY (l_employeeID)REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE flowerDeliverer(
    f_employeeID INT NOT NULL REFERENCES employees(employeeID) on DELETE  CASCADE,
    PRIMARY KEY(f_employeeID)
);


CREATE TABLE IT(
      IT_employeeID INT NOT NULL,
      PRIMARY KEY(IT_employeeID),
      CONSTRAINT it_id FOREIGN KEY (IT_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE location (
      doctor INT NOT NULL,
      nodeID char(10) NOT NULL,
      priority INT NOT NULL GENERATED ALWAYS AS IDENTITY,
      PRIMARY KEY (doctor, nodeID),
      CONSTRAINT loc_doc FOREIGN KEY (doctor) REFERENCES doctors(doctorID) ON DELETE CASCADE,
      CONSTRAINT loc_node FOREIGN KEY (nodeID) REFERENCES nodes(nodeID) ON DELETE CASCADE);

CREATE TABLE request(
      requestID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
      timeRequested TIMESTAMP NOT NULL,
      timeCompleted TIMESTAMP,
      reqNotes VARCHAR(255),
      compNotes VARCHAR(255),
      assigned_eID INT,
      serviceType VARCHAR(255) NOT NULL,
      nodeID CHAR(10),
      status CHAR(4) NOT NULL CONSTRAINT STAT_CK CHECK (status IN ('OPEN', 'DENY', 'DONE')),
      CONSTRAINT req_emp FOREIGN KEY (assigned_eID) REFERENCES employees(employeeID) ON DELETE SET NULL,
      CONSTRAINT req_serv FOREIGN KEY (serviceType) REFERENCES service(serviceType),
      CONSTRAINT req_node FOREIGN KEY (nodeID) REFERENCES nodes(nodeID) ON DELETE SET NULL);

CREATE TABLE lrequest(
              requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE);

CREATE TABLE trequest(
                requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
                language VARCHAR(255) NOT NULL);


CREATE TABLE medicineRequests(
    requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
    medicineName VARCHAR(255),
    dosage FLOAT,
    units VARCHAR(3),
    patient VARCHAR(255) NOT NULL,
    CONSTRAINT units_ck CHECK (LOWER(units) IN ('mg', 'g', 'cc'))
);

CREATE TABLE sanitationRequests(
    requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
    size VARCHAR(7) NOT NULL,
    sanitationType VARCHAR(255),
    danger VARCHAR(7),
    CONSTRAINT size_ck CHECK (LOWER(size) IN ('small', 'medium', 'large', 'unknown')),
    CONSTRAINT danger_ck CHECK (LOWER(danger) IN ('low', 'medium', 'high', 'unknown'))
);

CREATE TABLE wrequest(
              requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
              needsAssistance VARCHAR(255) NOT NULL);
CREATE TABLE erequest(
                requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
                supportType VARCHAR(255) NOT NULL);

CREATE TABLE ITrequest(
              requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
              device VARCHAR(255) NOT NULL,
              problem VARCHAR(255) NOT NULL);

CREATE TABLE flowerRequest(
            requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
            patientName VARCHAR(255) NOT NULL,
            visitorName VARCHAR(255) NOT NULL,
            creditNum CHAR(19));

CREATE TABLE flowertoflower(
            relationID INT NOT NULL PRIMARY KEY generated always as identity,
            flowerName VARCHAR(255) NOT NULL references  flower(flowerName) on DELETE CASCADE,
            requestID INT NOT NULL references flowerRequest(requestID) on DELETE CASCADE
);


INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!');
INSERT INTO service VALUES ('Laundry', '00:00', '00:00', 'Make a request for laundry services!');
INSERT INTO service VALUES ('Medicine', '00:00', '00:00', 'Request medicine delivery!');
INSERT INTO service VALUES ('Sanitation', '00:00', '00:00', 'Request sanitation service!');
INSERT INTO service VALUES ('Wheelchair', '00:00', '00:00', 'Request a wheelchair!');
INSERT INTO service VALUES ('Emotional Support', '00:00', '00:00', 'Request emotional support, please?!');
INSERT INTO service VALUES ('IT', '00:00', '00:00', 'Make a request for IT services!');
INSERT INTO service VALUES ('Flower', '00:00', '00:00', 'Make a request for Flower Delivery services');

INSERT INTO credential VALUES ('Gaben', 'MoolyFTW', 'ADMIN');



CREATE TRIGGER doc_delete AFTER DELETE ON doctors
REFERENCING OLD AS oldRow
FOR EACH ROW MODE DB2SQL
DELETE FROM credential WHERE credential.username = oldRow.username;

CREATE TRIGGER rset_kiosk AFTER INSERT ON kiosk
REFERENCING NEW AS newRow
FOR EACH ROW MODE DB2SQL
DELETE FROM kiosk WHERE NOT nodeID = newRow.nodeID;

