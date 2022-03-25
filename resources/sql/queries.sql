-- :name create-order! :! :n
-- :doc creates a new order record
INSERT INTO orders
(article_imt, color, size, article_wb, chrt_id, barcode, trademark, order_id, wbsticker, wbsticker_encoded, wbsticker_id, wbsticker_id_parts_A, wbsticker_id_parts_B)
VALUES (:article_imt, :color, :size, :article_wb, :chrt_id, :barcode, :trademark, :order_id, :wbsticker, :wbsticker_encoded, :wbsticker_id, :wbsticker_id_parts_A, :wbsticker_id_parts_B)

-- :name get-order :? :1
-- :doc retrieves a order record given the order_id
SELECT * FROM orders
WHERE order_id = :order_id

-- :name get-orders :? :*
-- :doc selects all available orders
select * from orders ORDER BY id DESC;

-- :name get-today-orders :? :*
-- :doc selects today orders
select * from orders WHERE t > TIMESTAMP 'today' ORDER BY id DESC;

-- :name get-yesterday-orders :? :*
-- :doc selects whis week orders
select * from orders where t::date = current_date - 1 ORDER BY id DESC;

-- :name delete-order! :! :n
-- :doc deletes a order record given the id
DELETE FROM orders
WHERE id = :id

-- :name delete-old-orders! :n
-- :doc deletes old orders
DELETE FROM orders WHERE ROWID IN (SELECT ROWID FROM orders ORDER BY ROWID DESC LIMIT -1 OFFSET 200)