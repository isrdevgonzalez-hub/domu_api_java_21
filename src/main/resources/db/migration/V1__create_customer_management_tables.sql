CREATE TABLE client (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_type VARCHAR(20) NOT NULL,
    business_name VARCHAR(200) NOT NULL,
    rut VARCHAR(12) NOT NULL,
    description VARCHAR(500),
    business_activity VARCHAR(150),
    email VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(255),
    district VARCHAR(120),
    city VARCHAR(120),
    region VARCHAR(120),
    postal_code VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT uk_client_rut UNIQUE (rut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_client_business_name ON client (business_name);
CREATE INDEX idx_client_status ON client (status);

CREATE TABLE person (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(120) NOT NULL,
    paternal_surname VARCHAR(120) NOT NULL,
    maternal_surname VARCHAR(120),
    rut VARCHAR(12),
    email VARCHAR(255),
    phone VARCHAR(50),
    position_name VARCHAR(120),
    origin VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (id),
    CONSTRAINT uk_person_rut UNIQUE (rut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_person_email ON person (email);
CREATE INDEX idx_person_status ON person (status);

CREATE TABLE app_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    person_id BIGINT,
    username VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uk_app_user_username UNIQUE (username),
    CONSTRAINT uk_app_user_person UNIQUE (person_id),
    CONSTRAINT fk_app_user_person FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE client_contact (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    contact_type VARCHAR(30) NOT NULL,
    is_primary BOOLEAN NOT NULL,
    receives_quotes BOOLEAN NOT NULL,
    receives_invoices BOOLEAN NOT NULL,
    notes VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_client_contact PRIMARY KEY (id),
    CONSTRAINT uk_client_contact_client_person UNIQUE (client_id, person_id),
    CONSTRAINT fk_client_contact_client FOREIGN KEY (client_id) REFERENCES client (id),
    CONSTRAINT fk_client_contact_person FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_client_contact_client_id ON client_contact (client_id);
CREATE INDEX idx_client_contact_person_id ON client_contact (person_id);
CREATE INDEX idx_client_contact_status ON client_contact (status);
