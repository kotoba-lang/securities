(ns kotoba.securities-test
  (:require [clojure.test :refer [deftest is]]
            [kotoba.securities :as sec]))

(deftest position-test
  (is (= "P1" (:position/id (sec/position "P1" "HoldCo" "SUB-A" 1000)))))

(deftest position-value-test
  (is (= 5000 (sec/position-value (sec/position "P1" "HoldCo" "SUB-A" 1000 :price 5)))))

(deftest trade-test
  (is (= :buy (:trade/side (sec/trade "T1" "acct-1" "SUB-A" :buy 100 10))))
  (is (nil? (sec/trade "T1" "acct-1" "SUB-A" :short 100 10))))

(deftest trade-notional-test
  (is (= 1000 (sec/trade-notional (sec/trade "T1" "acct-1" "SUB-A" :buy 100 10)))))

(deftest fund-nav-test
  (is (= 10.0 (:nav/per-share (sec/fund-nav "F1" 1000000 100000))))
  (is (nil? (sec/fund-nav "F1" 1000000 0))))

(deftest mandate-breach-test
  (let [m (sec/mandate "M1" "F1" {:max-single-issuer-pct 10})]
    (is (sec/breaches-mandate? m 15))
    (is (not (sec/breaches-mandate? m 5)))))

(deftest validate-trade-test
  (is (true? (:securities/valid? (sec/validate-trade (sec/trade "T1" "acct-1" "SUB-A" :buy 100 10)))))
  (is (= :unknown-side (:securities/error (sec/validate-trade {:trade/id "T1" :trade/side :short}))))
  (is (= :non-positive-quantity (:securities/error (sec/validate-trade {:trade/id "T1" :trade/side :buy :trade/quantity 0}))))
  (is (= :not-a-map (:securities/error (sec/validate-trade "x")))))

(deftest validate-position-test
  (is (true? (:securities/valid? (sec/validate-position (sec/position "P1" "HoldCo" "SUB-A" 1000)))))
  (is (= :missing-id (:securities/error (sec/validate-position {}))))
  (is (= :not-a-map (:securities/error (sec/validate-position "x")))))
