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


CREATE TABLE edges (
   edgeID CHAR(21) NOT NULL PRIMARY KEY,
   node1 CHAR(10) NOT NULL,
   node2 CHAR(10) NOT NULL,
   FOREIGN KEY (node1) REFERENCES nodes(nodeID) ON DELETE CASCADE,
   FOREIGN KEY (node2) REFERENCES nodes(nodeID) ON DELETE CASCADE
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
      FOREIGN KEY (serviceType) REFERENCES service (serviceType));

CREATE TABLE translator (
      t_employeeID INT NOT NULL PRIMARY KEY,
      FOREIGN KEY (t_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE);

CREATE TABLE language (
      t_employeeID INT NOT NULL,
      language VARCHAR(255) NOT NULL,
      CONSTRAINT LANG_PK PRIMARY KEY (t_employeeID, language),
      FOREIGN KEY (t_employeeID) REFERENCES translator (t_employeeID) ON DELETE CASCADE);

CREATE TABLE laundry(
      l_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,
      PRIMARY KEY(l_employeeID));

CREATE TABLE sanitation(
    sanitationEmployeeID INT NOT NULL REFERENCES employees(employeeID) ON DELETE CASCADE,
    PRIMARY KEY(sanitationEmployeeID)
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
      FOREIGN KEY (username) REFERENCES credential(username),
      FOREIGN KEY (doctorID) REFERENCES employees(employeeID) ON DELETE CASCADE);

/*TODO add the employees table */
CREATE TABLE wheelchairEmployee(
      w_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,
      PRIMARY KEY(w_employeeID));
CREATE TABLE emotionalSupporter(
      l_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,
      PRIMARY KEY(l_employeeID));

CREATE TABLE IT(
      IT_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,
      PRIMARY KEY(IT_employeeID));

CREATE TABLE location (
      doctor INT NOT NULL REFERENCES doctors(doctorID) ON DELETE CASCADE,
      nodeID char(10) NOT NULL REFERENCES nodes(nodeID) ON DELETE CASCADE,
      priority INT NOT NULL GENERATED ALWAYS AS IDENTITY,
      PRIMARY KEY (doctor, nodeID));

CREATE TABLE request(
      requestID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
      timeRequested TIMESTAMP NOT NULL,
      timeCompleted TIMESTAMP,
      reqNotes VARCHAR(255),
      compNotes VARCHAR(255),
      assigned_eID INT REFERENCES employees(employeeID) ON DELETE SET NULL,
      serviceType VARCHAR(255) NOT NULL REFERENCES service(serviceType),
      nodeID CHAR(10) REFERENCES nodes(nodeID) ON DELETE SET NULL,
      status CHAR(4) NOT NULL CONSTRAINT STAT_CK CHECK (status IN ('OPEN', 'DENY', 'DONE')));

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
    patient VARCHAR(255) NOT NULL
);

CREATE TABLE sanitationRequests(
    requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,
    size VARCHAR(7) NOT NULL,
    sanitationType VARCHAR(255),
    danger VARCHAR(7),
    CONSTRAINT size_ck CHECK (LOWER(size) IN ('small', 'medium', 'large', 'unknown')),
    CONSTRAINT danger_ck CHECK (LOWER(danger) IN ('low', 'medium', 'high', 'unknown'))
);

/*TODO: Add request table */
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

INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!');
INSERT INTO service VALUES ('Laundry', '00:00', '00:00', 'Make a request for laundry services!');
INSERT INTO service VALUES ('Medicine', '00:00', '00:00', 'Request medicine delivery!');
INSERT INTO service VALUES ('Sanitation', '00:00', '00:00', 'Request sanitation service!');
/*TODO: Insert your service tuple */
INSERT INTO service VALUES ('Wheelchair', '00:00', '00:00', 'Request a wheelchair!');
INSERT INTO service VALUES ('Emotional Support', '00:00', '00:00', 'Request emotional support, please?!');
INSERT INTO service VALUES ('IT', '00:00', '00:00', 'Make a request for IT services!');

CREATE TRIGGER doc_delete AFTER DELETE ON doctors
REFERENCING OLD AS oldRow
FOR EACH ROW MODE DB2SQL
DELETE FROM credential WHERE credential.username = oldRow.username;


