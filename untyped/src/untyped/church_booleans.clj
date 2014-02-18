(ns untyped.church-booleans)

(def c-true  (fn [t] (fn [f] t)))
(def c-false (fn [t] (fn [f] f)))

(def c-test
  (fn [b]
    (fn [v]
      (fn [w]
        ((b v) w)))))

(def c-and
  (fn [b1]
    (fn [b2]
      ((b1 b2) c-false))))

(def c-or
  (fn [b1]
    (fn [b2]
      ((b1 c-true) b2))))

(def c-not
  (fn [b]
    ((b c-false) c-true)))
