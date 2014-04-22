# Representation of group payments #


Payments by individual states are recorded in the file `phoros.csv`.  Confidence of reading of name and of amount is recorded in `conflevels.csv`.

**Group** payments are represented as follows:

- each amount paid by a group is recorded in `groupPayments.csv`.  This record associates the group payment (e.g., `urn:cite:phoros:grouppayrec.11`) with an identified group making the payment (e.g., `urn:cite:phoros:paygroup.12`), and records the amount paid, in obols.
- each state contributing to a group payment is recorded in `groupContributions.csv`.   It uses the same references
for the group payment (e.g., `urn:cite:phoros:grouppayrec.11`) and for the identified group making the payment (e.g., `urn:cite:phoros:paygroup.12`) as in `groupPayments.csv`.  In addition, it identifies the state with a URN like `urn:cite:phoros:places.178`, the appearnce of the individual state in the sequence of payments (e.g., `urn:cite:phoros:payrec.697`), and the year of the payment.

**Confidence levels** for group payments are organized as follows:

- confidence of amount paid by a group is recorded in `groupPaymentConfidence.csv`.  The GroupPayment URN (e.g., `urn:cite:phoros:grouppayrec.1`) corresponds to one record in the `groupPayments.csv` file.
- confidence of reading of names is recorded in `groupConfidence.csv`.  The GroupPaymentRecord URN (e.g., `urn:cite:phoros:grouppayrec.1`) and the PlaceUrn (e.g., `:cite:phoros:places.9`) match a pair in the file `groupContributions.csv`.