# untyped

Sample code walkthrough implementing Church Booleans and Church Numerals. I put
this together for a talk entitled "The Lambda Cube" at TulsaLambdaLounge.

## Setup

This walkthrough is written in Clojure. Not much Clojure-specific knowledge is
required to understand Church encodings, but getting a working REPL to
interactively execute expressions is slightly more involved than something like
`sudo apt-get install clojure`.

1. Follow the instructions for (installing
   Leiningen)[https://github.com/technomancy/leiningen#installation].

2. Clone this repository. `$ git clone https://github.com/zerokarmaleft/the-lambda-cube-presentation`
    
3. `cd` into the `untyped/` directory.

4. Launch a REPL with `lein repl`. After launching the REPL, you should see
   a prompt like this:

```
nREPL server started on port 52742 on host 127.0.0.1
REPL-y 0.3.0
Clojure 1.5.1
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

user=> 
```

## Walkthrough

### Church Booleans

First, we'll load up the namespace (module) for Church Booleans.

```
user=> (use 'untyped.church-booleans)
nil
```

The `nil` signifies that the namespace was successfully loaded. Any problems
would result in an exception message.

In
[church_booleans.clj](https://github.com/zerokarmaleft/the-lambda-cube-presentation/untyped/src/untyped/church_booleans.clj),
there are two `def` expressions binding the symbols `c-true` and `c-false`
(Church-encodings of `true` and `false`, to avoid name-clashing the underlying
Boolean primitives) to strange-looking function definitions. Let's look at
those:

```clojure
(def c-true  (fn [t] (fn [f] t)))
(def c-false (fn [t] (fn [f] f)))
```

In the purely untyped lambda calculus, *everything* is a function. Church
Booleans are just functions that accept two arguments and depending on the truth
value, return the first argument or the second argument. So why aren't the
functions defined to accept two arguments instead of nesting two functions? It
turns out the untyped lambda calculus at its core doesn't provide multi-argument
(a.k.a. multi-arity) functions. However, it's possible to express them using
*higher-order functions* - a function that either accepts a function as an
argument or a function that returns a function as a result. Using higher-order
functions in this way is called
[currying](http://en.wikipedia.org/wiki/Currying).

Let's try evaluating `c-true`:

```
user=> (c-true "hello")
#<church_booleans$c_true$fn__1187 untyped.church_booleans$c_true$fn__1187@36399ad5>
```

The return value enclosed in `#<>` is Clojure's way of representing a function.
You can see some under-the-hood information about where this function was
defined (`#<church_booleans$`), what symbol it was bound to (`$c_true$`), and
that it is indeed a function (`$fn__1187` - this may vary on your own machine).

So, the REPL returns a function because we need to apply the resulting function
to an additional argument:

```
user=> ((c-true "hello") "goodbye")
"hello"
```

In the body of the nested anonymous function inside `c-true`, you can see that
it simply returns the value bound to `t`, the first argument. And in our example
above, we see that calling `c-true` in currying style returns the string
`"hello"`. Likewise, `c-false` returns the second argument:

```
user=> ((c-false "hello") "goodbye")
"goodbye"
```

Next, let's build some operations to work with these primitives. First up,
`c-test`, a sort of if-then-else construct:

```clojure
(def c-test
  (fn [b]
    (fn [v]
      (fn [w]
        ((b v) w)))))
```

Three nested anonymous functions, which you should now see as one curried
function that accepts three arguments. The first argument, `b`, is the Boolean
value we want to test for truthiness. The second and third arguments, `v` and
`w`, are values we want to return depending on the truthiness of `b`. If `b` is
`c-true`, the expression `((b v) w)` will evaluate to `v`. If `b` is `c-false`,
the expression `((b v) w` will evaluate to `w`.

Testing in the REPL:

```
user=> (((c-test c-true) "hello") "goodbye")
"hello"
user=> (((c-test c-false) "hello") "goodbye")
"goodbye"
```

We can define Boolean operators to combine arbitrarily complex Boolean
expressions in the same way as `c-test`.

```clojure
(def c-and
  (fn [b1]
    (fn [b2]
      ((b1 b2) c-false))))
```

`c-and` is a curried function that accepts two Boolean arguments. If `b1` is
`c-true`, then the expression `((b1 b2) c-false)` will reduce to `b2`. If `b2`
is `c-true`, then both `b1` and `b2` are truthy, hence returning `c-true` is
logically correct. If `b2` is `c-false`, then `b1` and `b2` are not both truthy.
Likewise, returning `c-false` is logically correct. If `b1` is `c-false`, then
the expression `((b1 b2) c-false)` short-circuits reducing `b2`, and simply
returns `c-false`.

```clojure
(def c-or
  (fn [b1]
    (fn [b2]
      ((b1 c-true) b2))))
```

`c-or` is a curried function that accepts two Boolean arguments. If `b1` is
`c-false`, then the expression `((b1 c-true) b2)` will reduce to `b2`. If `b2`
is `c-true`, then at least one of `b1` and `b2` are truthy, hence returning
`c-true` is logically correct. If `b2` is `c-false`, then `b1` and `b2` are both
not truthy. Likewise, returning `c-false` is logically correct. If `b1` is true,
then the expression `((b1 c-true) b2)` short-circuits reducing `b2`, and simply
returns `c-true`.

```clojure
(def c-not
  (fn [b]
    ((b c-false) c-true)))
```

`c-not` should be fairly self-explanatory at this point. It is a function that
accepts a single Boolean argument. It evalutes the argument and depending on its
truthiness, returns `c-false` or `c-true`. `c-false` and `c-true` are passed as
the first and second argument, respectively, to negate the truthiness of `b`.

### Church Numerals

Natural numbers can also be expressed in the untyped lambda calculus in a peculiar way.
First, let's load up the namespace:

```
user=> (use 'untyped.church-numerals)
nil
```

Looking inside
(church_numerals.clj)[https://github.com/zerokarmaleft/the-lambda-cube-presentation/untyped/src/untyped/church_numerals.clj],
we see definitions for the first handful of natural numbers, starting with `c0`,
which is the Church representation of 0:

```clojure
(def c0 (fn [s] (fn [z] z)))
```

`c0` is a curried function that expects an internal successor function to be
passed as `s`, which is to be applied to the argument `z`. The application count
of `s` to `z` is the representation of a number. In the expression `(fn [z] z)`
for `c0`, we obviously see that `s` is not applied to `z` at all. It is applied
precisely 0 times. For `c1`, `c2`, `c3`, `c4`, and `c5`, we clearly see that the
application count of `s` to `z` corresponds to the number we want to represent.

```clojure
(def c1 (fn [s] (fn [z] (s z)))) ;; one s
(def c2 (fn [s] (fn [z] (s (s z))))) ;; two s
(def c3 (fn [s] (fn [z] (s (s (s z)))))) ;; three s
(def c4 (fn [s] (f [z] (s (s (s (s z))))))) ;; four s
(def c5 (fn [s] (fn [z] (s (s (s (s (s z)))))))) ;; five s, ah...ah...ah!
```

Obviously, we don't have all day to define numbers manually in this fashion. We
can do better by combining `c0` and a external successor function (a successor
function not used inside the representation, but a public function that works
with Church numerals) to build up larger numbers.

```clojure
(def c-succ
  (fn [n]
    (fn [s]
      (fn [z]
        (s ((n s) z))))))
```

`c-succ` is a curried function that accepts a single Church numeral as an
argument and returns a new Church numeral in the form of `(fn [s] (fn [z]
<expression>))`, just as 0-5 are represented above. We can see that the
successor is adding one additional outer application of `s` in the expression
`(s ((n s) z))`, but what's going on inside? The inner expression `((n s) z)` is
a way of saying "apply the internal successor function `s`, `n` times to `z`.

## License

The MIT License (MIT)

Copyright © 2014 Edward Cho

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
