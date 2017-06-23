-- name: get-scene-events-sql
SELECT scene, idx, device, addr, pin, delay
  FROM artillery.scene_events
 WHERE NOT removed AND scene = :scene
 ORDER BY idx

--name: add-scene-event<!
INSERT INTO artillery.scene_events (scene, idx, device, addr, pin, delay)
     SELECT :scene, COALESCE(MAX(idx) + 1, 0), :device, :addr, :pin, :delay
       FROM artillery.scene_events
      WHERE scene = :scene
