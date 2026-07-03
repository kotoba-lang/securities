(ns kotoba.securities.export-test
  (:require [clojure.test :refer [deftest is]]
            [kotoba.securities :as sec]
            [kotoba.securities.export :as ex]))

(deftest csv-export
  (let [csv (ex/trades->csv [(sec/trade "T1" "acct-1" "SUB-A" :buy 100 10)])]
    (is (re-find #"trade_id,account,instrument,side,quantity,price,currency" csv))
    (is (re-find #"T1,acct-1,SUB-A,buy,100,10" csv))))
