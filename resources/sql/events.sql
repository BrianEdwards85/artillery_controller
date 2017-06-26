-- name: get-scene-events-sql
SELECT scene, idx, device, addr, pin, delay
  FROM artillery.scene_events
 WHERE NOT removed AND scene = :scene
 ORDER BY idx

--name: append-scene-event<!
INSERT INTO artillery.scene_events (scene, idx, device, addr, pin, delay)
     SELECT :scene, COALESCE(MAX(idx) + 1, 0), :device, :addr, :pin, :delay
       FROM artillery.scene_events
      WHERE scene = :scene

--name: push-scene-events!
UPDATE artillery.scene_events
   SET idx = idx + 1
 WHERE idx >= :idx AND scene = :scene

--name: insert-scene-event<!
INSERT INTO artillery.scene_events (scene, idx, device, addr, pin, delay)
     VALUES (:scene, :idx, :device, :addr, :pin, :delay)

--name: remove-scene-event!
UPDATE artillery.scene_events
   SET removed = TRUE
 WHERE idx = :idx AND scene = :scene
