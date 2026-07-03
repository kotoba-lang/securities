(ns kotoba.securities.export
  "Operator-facing export for a securities/fund actor.

  Renders positions, trades and fund NAVs to CSV and JSON for audit and
  downstream reporting. Pure data -> text: no network."
  (:require [clojure.string :as str]))

(defn- csv-cell [v]
  (let [s (str (if (nil? v) "" v))]
    (if (re-find #"[\",\n]" s)
      (str "\"" (str/replace s "\"" "\"\"") "\"")
      s)))

(defn- csv-row [vals] (str/join "," (map csv-cell vals)))

(defn- json-str [v]
  (-> (str (if (nil? v) "" v))
      (str/replace "\\" "\\\\")
      (str/replace "\"" "\\\"")
      (str/replace "\n" "\\n")))

(defn trades->csv [trades]
  (str/join "\n"
    (cons (csv-row ["trade_id" "account" "instrument" "side" "quantity" "price" "currency"])
          (for [t trades]
            (csv-row [(:trade/id t) (:trade/account t) (:trade/instrument t)
                      (name (or (:trade/side t) :unspecified))
                      (:trade/quantity t) (:trade/price t) (:trade/currency t)])))))

(defn positions->csv [positions]
  (str/join "\n"
    (cons (csv-row ["position_id" "holder" "instrument" "quantity"])
          (for [p positions]
            (csv-row [(:position/id p) (or (:position/holder p) "")
                      (:position/instrument p) (:position/quantity p)])))))

(defn trades->json [trades]
  (str "["
       (str/join ","
                 (for [t trades]
                   (str "{\"trade_id\":\"" (json-str (:trade/id t)) "\","
                        "\"side\":\"" (name (or (:trade/side t) :unspecified)) "\","
                        "\"quantity\":" (or (:trade/quantity t) 0) "}")))
       "]"))
