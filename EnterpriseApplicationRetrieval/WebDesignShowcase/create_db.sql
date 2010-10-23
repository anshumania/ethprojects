CREATE TABLE users (
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(20) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE designs (
    id SERIAL NOT NULL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    title VARCHAR(20) NOT NULL,
    url VARCHAR(200) NOT NULL,
    image_url VARCHAR(250) NOT NULL
);

CREATE TABLE comments (
    id SERIAL NOT NULL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    design_id INT NOT NULL REFERENCES designs(id),
    comment VARCHAR(300) NOT NULL
);