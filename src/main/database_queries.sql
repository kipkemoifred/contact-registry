
CREATE DATABASE contact_registry;


CREATE TABLE contact_registry.contacts
(     id INT PRIMARY KEY AUTO_INCREMENT,
      full_name VARCHAR(100) NOT NULL UNIQUE,
      phone_number VARCHAR(15) NOT NULL UNIQUE,
      email VARCHAR(100) NOT NULL UNIQUE,
      id_number VARCHAR(20) NOT NULL UNIQUE,
      dob DATE NOT NULL,
      gender ENUM('Male', 'Female', 'Other') NOT NULL,
      organization VARCHAR(255) NOT NULL,
      masked_name VARCHAR(100) NOT NULL,
      masked_phone_number VARCHAR(100) NOT NULL,
      hashed_phone_number VARCHAR(100) NOT NULL
);
