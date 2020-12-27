(ns crawl.state
  "
  The parts of GameState
  zoo - a map of uuid to Creature
  status - one of:
  * :created
  * :running"
  )



(defrecord GameState [zoo status])

(def current (atom nil))

(def history (atom []))

(add-watch current :history 
           (fn [key atm old-state new-state] 
             (swap! history conj new-state)))

(reset! current (->GameState {} :created))

(defn alter-state [f]
  (swap! current f))




;; ## State
;; functions ending with -> return a new state

;; (defrecord Transient [messages errors])

;; (defn create-transient [] (Transient. [] []))

;; (defrecord GameState [context running mode combatants zoo adventurer transient moves] )

;; (defn default-start-state [context adventurer]
;;   (let [transient (create-transient)]
;;     (->GameState context true :move nil { (:id adventurer) adventurer } (:id adventurer) transient 0)))


;; ;;### Utility functions

;; (defn apply-delta [[lx ly] [dx dy]]
;;   [(+ lx dx) (+ ly dy)])

;; ;;### context

;; (defn catalog-ids [{:keys [context adventurer zoo]}]
;;   (let [adv (zoo adventurer)]
;;     (remove #{(:pid adv)} (keys (:catalog context)))))

;; (defn prototype [{:keys [context]} pid]
;;   (-> context :catalog pid))

;; ;;### Running

;; (defn running? [{:keys [running]}]
;;   running)
  
;; (defn running->final [state]
;;   (merge state {:running :final}))

;; (defn running->post-final [state]
;;   (merge state {:running nil}))

;; ;; ### Mode

;; (defn current-mode [{:keys [mode]}]
;;   mode)

;; (defn current-mode-> [state new-mode]
;;     (assoc state :mode new-mode))
;; ;; ### Zoo

;; (defn zoo-append-> [{:keys [zoo] :as state} {:keys [id] :as monster} ]
;;   (assoc-in state [:zoo id] monster))

;; (defn monster-move-> [state id delta]
;;   (update-in state [:zoo id :location] apply-delta delta))

;; (defn monster-for [{:keys [zoo]} id]
;;   (zoo id))

;; ;; ### Combatants

;; (defn combatants-set-> [state & combs]
;;   (assoc state :combatants  combs))

;; (defn rotate 
;;   ([coll] (rotate 1 coll))
;;   ([num coll]
;;     (if (< num 1)
;;       coll
;;       (concat (drop num coll) (take num coll)))))

;; (defn combatants-next-> 
;;   "Make the next combatant"
;;   ([{:keys [combatants] :as state}]
;;      (assoc state :combatants (rotate combatants)))

;;   ([{:keys [combatants] :as state} num]
;;      (assoc state :combatants (rotate num combatants))))

;; (defn combatants [{:keys [combatants]}]
;;   combatants)

;; (defn combatants-as-monsters [{:keys [combatants] :as state}]
;;   (map (partial monster-for state) combatants))


;; ;; ### Adventurer

;; (defn adventurer [{:keys [adventurer]}]
;;   adventurer)

;; ;; ### Transient

;; (defn message-add-> [state & message]
;;   (update-in state [:transient :messages] conj (apply str message)))

;; (defn messages [{:keys [transient]}]
;;   (:messages transient))

;; (defn error-add-> [state error]
;;   (update-in state [:transient :errors] conj error))

;; (defn transient-clear-> [state]
;;   (assoc state :transient (create-transient)))

;; ;; ## Moves

;; (defn moves-next-> [state]
;;   (update-in state [:moves] inc))

;; (defn moves [{:keys [moves]}]
;;   moves)

;; ;; ## Pre and Post

;; (defn pre-> [state]
;;   (-> state
;;       transient-clear->
;;       moves-next->))

;; (defn post-> [state]
;;   state)
