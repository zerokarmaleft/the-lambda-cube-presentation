object ChurchNumerals {
  import ChurchBooleans._

  sealed trait Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] <: Up

    type gt = Match[False, False, True, Bool]
    type ge = Match[False, True, True, Bool]
    type eq = Match[False, True, False, Bool]
    type le = Match[True, True, False, Bool]
    type lt = Match[True, False, False, Bool]
  }
  sealed trait GT extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfGT
  }
  sealed trait LT extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfLT
  }
  sealed trait EQ extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfEQ
  }

  trait Fold[-Elem, Value] {
    type Apply[E <: Elem, V <: Value] <: Value

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] <: Type
  }

  sealed trait Nat {
    type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] <: Up

    type Compare[N <: Nat] <: Comparison

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] <: Type
  }
  sealed trait _0 extends Nat {
    type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] = IfZero

    type Compare[N <: Nat] = N#Match[ConstLT, EQ, Comparison]

    type ConstLT[A] = LT

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = Init
  }
  sealed trait Succ[N <: Nat] extends Nat {
    type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] = NonZero[N]

    type Compare[O <: Nat] = O#Match[N#Compare, GT, Comparison]

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = F#Apply[Succ[N], N#FoldR[Init, Type, F]]
  }
  object Nat {
    type Add[A <: Nat, B <: Nat] = A#FoldR[B, Nat, Inc]

    type Inc = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Succ[Acc]
    }

    type Mult[A <: Nat, B <: Nat] = A#FoldR[_0, Nat, Sum[B]]

    type Sum[By <: Nat] = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Add[By, Acc]
    }

    type Fact[A <: Nat] = A#FoldR[_1, Nat, Prod]

    type Prod = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Mult[N, Acc]
    }

    type Exp[A <: Nat, B <: Nat] = B#FoldR[_1, Nat, ExpFold[A]]
    type ExpFold[By <: Nat] = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Mult[By, Acc]
    }

    type Mod[A <: Nat, B <: Nat] = A#FoldR[_0, Nat, ModFold[B]]
    type ModFold[By <: Nat] = Fold[Nat, Nat] {
      type Wrap[Acc <: Nat] = By#Compare[Acc]#eq
      type Apply[N <: Nat, Acc <: Nat] = Wrap[Succ[Acc]]#If[_0, Succ[Acc], Nat]
    }

    type Eq[A <: Nat, B <: Nat] = A#Compare[B]#eq

    def toInt[N <: Nat](implicit n: NatRep[N]): Int = n.value

    class NatRep[N <: Nat](val value: Int)
    implicit val zeroRep: NatRep[_0] = new NatRep(0)
  }

  type  _1 = Succ[_0]
  type  _2 = Succ[_1]
  type  _3 = Succ[_2]
  type  _4 = Succ[_3]
  type  _5 = Succ[_4]
  type  _6 = Succ[_5]
  type  _7 = Succ[_6]
  type  _8 = Succ[_7]
  type  _9 = Succ[_8]
  type _10 = Succ[_9]

  type Is0[A <: Nat] = A#Match[ConstFalse, True, Bool]
  type ConstFalse[A] = False

}
