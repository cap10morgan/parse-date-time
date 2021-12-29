(ns cap10morgan.parse-date-time
  (:import (java.time LocalDate LocalDateTime LocalTime MonthDay OffsetDateTime
                      OffsetTime Year YearMonth ZonedDateTime)
           (java.time.temporal TemporalQuery)
           (java.time.format DateTimeParseException DateTimeFormatter)))

(set! *warn-on-reflection* true)

(def java-time-classes
  "All unambiguous java.time classes implementing a from(TemporalAccessor ta)
  method with corresponding format pattern characters. For example, ZoneOffset
  is excluded because it can't be differentiated from a LocalTime by the
  formatter pattern alone. And Instant is excluded because there is no formatter
  pattern that can match it.
  Note that the order here must be more specific to less specific because the
  first to match an optional formatter will be returned. OffsetDateTime must
  come before ZonedDateTime or ZonedDateTime will match its strings (which is
  maybe fine and/or preferable?)."
  [OffsetDateTime ZonedDateTime LocalDateTime LocalDate OffsetTime LocalTime
   YearMonth MonthDay Year])

(defmacro gen-temporal-query [time-class]
  `(with-meta
     (reify TemporalQuery (queryFrom [_ ta#]
                            (~(symbol (str time-class "/from")) ta#)))
     {:class ~time-class}))

(defmacro temporal-queries []
  `[~@(for [^Class time-class java-time-classes]
        `(gen-temporal-query ~(.getName time-class)))])

(defn parse-date-time
  "This function attempts to parse the given str arg using any of the supplied
  formatters in the first argument. It will attempt to use any java.time class
  that can hold the result, hopefully minimizing any data loss (i.e. if there's
  a date and time component in the string and in one or more of the formatters,
  it will attempt to return an instance of a class that can represent the date
  and time together)."
  [formatters str]
  (let [formatters' (if (coll? formatters) formatters [formatters])]
    (loop [fs formatters']
      (let [^DateTimeFormatter f (first fs)
            parsed (try
                     (.parseBest f str
                                 (into-array TemporalQuery
                                             (temporal-queries)))
                     (catch DateTimeParseException _ false))]
        (if parsed
          parsed
          (when (< 1 (count fs))
            (recur (rest fs))))))))
