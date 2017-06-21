CREATE SCHEMA IF NOT EXISTS artillery;

CREATE TABLE IF NOT EXISTS artillery.scene (
       id VARCHAR PRIMARY KEY,
       description VARCHAR  NOT NULL,
       removed BOOLEAN DEFAULT false NOT NULL
);

CREATE TABLE IF NOT EXISTS artillery.scene_events(
       scene VARCHAR NOT NULL,
       idx INT NOT NULL,
       device VARCHAR NOT NULL,
       addr INT NOT NULL,
       pin INT NOT NULL,
       delay INT NOT NULL,
       removed BOOLEAN DEFAULT false NOT NULL,
       PRIMARY KEY (scene, idx),
       FOREIGN KEY (scene) REFERENCES artillery.scene (id)
);
