DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(55) NOT NULL,
  email VARCHAR(55) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS compilation (
  id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  events INT(55)[],
  pinned BOOLEAN NOT NULL,
  title VARCHAR(55),
  CONSTRAINT pk_compilation PRIMARY KEY (id)
);