(ns cap10morgan.parse-date-time-test
  (:require [clojure.test :refer :all]
            [cap10morgan.parse-date-time :refer :all])
  (:import (java.time.format DateTimeFormatter)
           (java.time LocalDate OffsetDateTime ZonedDateTime ZoneId Year
                      YearMonth LocalDateTime LocalTime OffsetTime)))

(deftest parse-date-time-single-formatter-test
  (let [formatter (DateTimeFormatter/ofPattern "yyyy[-MM[-dd['T'HH:mm:ss[VV]]]]")]
    (testing "full OffsetDateTime parses"
      (is (= (OffsetDateTime/of 2021 12 28 11 50 0 0 (ZoneId/of "-05:00"))
             (parse-date-time formatter "2021-12-28T11:50:00-05:00"))))
    (testing "full ZonedDateTime with named TZ parses"
      (is (= (ZonedDateTime/of 2021 12 28 11 50 0 0 (ZoneId/of "America/New_York"))
             (parse-date-time formatter "2021-12-28T11:50:00America/New_York"))))
    (testing "year alone parses"
      (is (= (Year/of 2021)
             (parse-date-time formatter "2021"))))
    (testing "year-month parses"
      (is (= (YearMonth/of 2021 12)
             (parse-date-time formatter "2021-12"))))
    (testing "date alone parses"
      (is (= (LocalDate/of 2021 12 28)
             (parse-date-time formatter "2021-12-28"))))
    (testing "date and time w/ no TZ parses"
      (is (= (LocalDateTime/of 2021 12 28 13 15 0 0)
             (parse-date-time formatter "2021-12-28T13:15:00"))))))

(deftest parse-date-time-multiple-formatters-test
  (let [formatters [(DateTimeFormatter/ofPattern "yyyy")
                    (DateTimeFormatter/ofPattern "yyyy-MM")
                    (DateTimeFormatter/ofPattern "yyyy-MM-dd")
                    (DateTimeFormatter/ofPattern "HH:mm")
                    (DateTimeFormatter/ofPattern "HH:mm:ss")
                    (DateTimeFormatter/ofPattern "HH:mm:ssVV")
                    (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssVV")]]
    (testing "year alone parses"
      (is (= (Year/of 2021)
             (parse-date-time formatters "2021"))))
    (testing "year-month parses"
      (is (= (YearMonth/of 2021 12)
             (parse-date-time formatters "2021-12"))))
    (testing "date alone parses"
      (is (= (LocalDate/of 2021 12 28)
             (parse-date-time formatters "2021-12-28"))))
    (testing "time w/o seconds parses"
      (is (= (LocalTime/of 14 1)
             (parse-date-time formatters "14:01"))))
    (testing "time w/ seconds parses"
      (is (= (LocalTime/of 14 1 42)
             (parse-date-time formatters "14:01:42"))))
    (testing "time w/ TZ parses"
      (is (= (OffsetTime/of 14 1 42 0 (ZoneId/of "-05:00"))
             (parse-date-time formatters "14:01:42-05:00"))))
    (testing "full zoned datetime parses"
      (is (= (ZonedDateTime/of 2021 12 28 14 7 0 0 (ZoneId/of "America/Denver"))
             (parse-date-time formatters "2021-12-28T14:07:00America/Denver"))))))
