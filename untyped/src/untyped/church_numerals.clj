(ns untyped.church-numerals
  (:require [untyped.church-booleans :refer :all]
            [untyped.church-pairs    :refer :all]))

(def c0 (fn [s] (fn [z] z)))
(def c1 (fn [s] (fn [z] (s z))))
(def c2 (fn [s] (fn [z] (s (s z)))))
(def c3 (fn [s] (fn [z] (s (s (s z))))))
(def c4 (fn [s] (fn [z] (s (s (s (s z)))))))
(def c5 (fn [s] (fn [z] (s (s (s (s (s z))))))))

(def c-succ
  (fn [n]
    (fn [s]
      (fn [z]
        (s ((n s) z))))))

(def c-plus
  (fn [m]
    (fn [n]
      (fn [s]
        (fn [z]
          ((m s) ((n s) z)))))))

(def c-times
  (fn [m]
    (fn [n]
      ((m (c-plus n)) c0))))

(def c-is-zero?
  (fn [m] ((m (fn [x] c-false)) c-true)))

(def zz
  ((c-pair c0) c0))

(def ss
  (fn [p]
    ((c-pair (c-second p)) ((c-plus c1) (c-second p)))))

(def c-pred
  (fn [m]
    (c-first ((m ss) zz))))

(def c-equal?
  (fn [m]
    (fn [n]
      ((c-and (c-is-zero? ((m c-pred) n)))
       (c-is-zero? ((n c-pred) m))))))
