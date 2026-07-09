(ns kotoba.securities.export-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [kotoba.securities :as sec]
            [kotoba.securities.export :as ex]))

(deftest csv-export
  (let [csv (ex/trades->csv [(sec/trade "T1" "acct-1" "SUB-A" :buy 100 10)])]
    (is (re-find #"trade_id,account,instrument,side,quantity,price,currency" csv))
    (is (re-find #"T1,acct-1,SUB-A,buy,100,10" csv))))

(deftest json-export-escapes-every-c0-control-character
  ;; RFC 8259 requires EVERY control character U+0000-U+001F to be
  ;; escaped, not just \ " and \n -- a trade id containing a raw tab or
  ;; other control byte would otherwise be copied through raw, producing
  ;; invalid JSON (verified against Python's strict json module).
  (let [ts [(sec/trade (str "T" (char 9) "1" (char 1) "x") "acct-1" "SUB-A" :buy 100 10)]
        j (ex/trades->json ts)]
    (is (str/includes? j "\"trade_id\":\"T\\t1\\u0001x\""))))
