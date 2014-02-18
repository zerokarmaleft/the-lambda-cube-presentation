(ns untyped.church-pairs
  (:require [untyped.church-booleans :refer :all]))

(def c-pair   (fn [f] (fn [s] (fn [b] ((b f) s)))))
(def c-first  (fn [p] (p c-true)))
(def c-second (fn [p] (p c-false)))
