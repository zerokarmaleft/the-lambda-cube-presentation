type Cbool a = a -> a -> a

ctrue, cfalse :: Cbool a
ctrue t f = t
cfalse t f = f

testBool :: Cbool Bool -> Bool
testBool b = b True False

cnot :: Cbool (Cbool a) -> Cbool a
cnot b = b cfalse ctrue

cand :: Cbool (Cbool a) -> Cbool a -> Cbool a
cand b1 b2 = b1 b2 cfalse

cor :: Cbool (Cbool a) -> Cbool a -> Cbool a
cor b1 b2 = b1 ctrue b2

type Cnum a = (a -> a) -> (a -> a)

f = (+ 1)

c0 :: Cnum a
c0 f = id

c1 :: Cnum a
c1 f = f

c2 :: Cnum a
c2 f = f . f

testNum :: Cnum Int -> Int
testNum n = n (+ 1) 0

csucc :: Cnum a -> Cnum a
csucc n = \x -> x . n x
