CREATE TABLE customer (
    customer_id SERIAL NOT NULL PRIMARY KEY, 
    username VARCHAR(20) NOT NULL,
    password VARCHAR(20) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    date_added DATE NOT NULL
);

CREATE TABLE country (
    country_id SERIAL NOT NULL PRIMARY KEY,
    country_name VARCHAR(30) NOT NULL
);

CREATE TABLE address (
    address_id SERIAL NOT NULL PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customer(customer_id),
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    zip_code VARCHAR(6) NOT NULL,
    country_id INT NOT NULL REFERENCES country(country_id)
);
