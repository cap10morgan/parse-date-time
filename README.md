# parse-date-time

The new-in-Java-8 java.time library is certainly an improvement over what came
before, but there are still some rough edges; especially for Clojure
programmers. DateTimeFormatter.parseBest is one of the roughest as its heavy
reliance on the method reference operator (::) in the TemporalQueries you have
to provide are awkward to replicate in Clojure interop (to say the least).

It's also a great example of types getting in the way rather than helping the
programmer. The formatter you're calling .parseBest on already has good data
shape / type information embedded into it. But in Java you also have to specify
a non-orthogonal array of types that it's allowed to parse the string into. And
you just have to hope you've covered every type your formatter is capable of
producing.

But this is Clojure! We don't care about types, we care about data!

So this library lets you parse strings into date(time)s using only formatters.
It uses .parseBest under the hood but lets it return any java.time type with a
from(TemporalAccessor ta) static method. This is better because the allowed
data and implicit types (date vs. date time vs. local / zoned, etc.) are
already fully encoded in the formatter.

## Usage

```clojure
(require '[cap10morgan.parse-date-time :refer [parse-date-time]])
(import '(java.time DateTimeFormatter))

(parse-date-time DateTimeFormatter/ISO_ZONED_DATE_TIME
                 "2021-12-28T11:12:00-05:00")
```

The above will return an instance of `java.time.ZonedDateTime`.

Things get more interesting with optionals in your formatter:

```clojure
(parse-date-time (DateTimeFormatter/ofPattern "yyyy-MM-dd['T'HH:mm:ss[Z]]")
                 "2021-12-28")
```

The above will return an instance of `java.time.LocalDate` because only the
`yyyy-MM-dd` portion of the pattern matched.


## License

Copyright © 2021 Wes Morgan

Distributed under the Eclipse Public License version 1.0.
