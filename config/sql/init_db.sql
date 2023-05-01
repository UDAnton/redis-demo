CREATE TABLE IF NOT EXISTS users
(
    id         INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name       varchar(255) NOT NULL,
    birth_year DATE         NOT NULL,
    email      varchar(255) NOT NULL
) ENGINE = InnoDB;
