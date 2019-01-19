CREATE TABLE COMICS (
  id IDENTITY NOT NULL PRIMARY KEY,
  path CLOB NOT NULL,
  title VARCHAR(255) NOT NULL,
  series VARCHAR(255) NOT NULL,
  number VARCHAR(10) NOT NULL,
  position VARCHAR(6) NOT NULL,
  volume VARCHAR(255),
  summary CLOB,
  notes CLOB,
  year SMALLINT NOT NULL,
  month SMALLINT NOT NULL,
  writer CLOB,
  penciller CLOB,
  inker CLOB,
  colorist CLOB,
  letterer CLOB,
  cover_artist CLOB,
  editor CLOB,
  publisher VARCHAR(255) NOT NULL,
  web CLOB,
  page_count SMALLINT,
  manga BOOLEAN,
  characters CLOB,
  teams CLOB
);

CREATE TABLE PREFERENCES (
  id IDENTITY NOT NULL PRIMARY KEY,
  key VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  value VARCHAR(255),
  comment CLOB
);

INSERT INTO PREFERENCES (key, name, value, comment) VALUES
  ('comics.path', 'Path', '../sample', 'Path to you comic library');
