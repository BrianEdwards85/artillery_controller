-- name: insert-scene<!
INSERT INTO artillery.scene (id,description)
VALUES (:id,:description)

-- name: get-scenes-sql
SELECT id, description
  FROM artillery.scene
 WHERE NOT removed

-- name: get-all-scenes-sql
SELECT id, description
  FROM artillery.scene

-- name: remove-scene-sql!
UPDATE artillery.scene
   SET removed = true
 WHERE id = :id

