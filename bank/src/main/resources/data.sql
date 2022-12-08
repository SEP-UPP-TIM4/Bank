INSERT INTO accounts(
    id, address, amount, currency, name, number, password, reserved_amount)
VALUES ('547c7ba9-fc3e-4e48-ab78-6e1ab8e220f0', 'Address', 100000.0, 1, 'merchant', '3189283102123', '$2a$10$i9gbDSXwPUh99J5Vw9zIveeDdTduv/ZzN3ydURP0pkHDpckqABpOi', 0.0);

INSERT INTO accounts(
    id, address, amount, currency, name, number, password, reserved_amount)
VALUES ('8e3416d8-6b2b-4ef4-acc0-489b8a57a745', 'Address', 100000.0, 1, 'issuer', '3123483102123', '$2a$10$i9gbDSXwPUh99J5Vw9zIveeDdTduv/ZzN3ydURP0pkHDpckqABpOi', 0.0);

INSERT INTO credit_card(
    id, cvv, expiration_date, pan, account_id)
VALUES ('a8e2f208-b667-44e7-ae3a-9d0e044658b4', '012', '01/01/2025', '318238190231312', '547c7ba9-fc3e-4e48-ab78-6e1ab8e220f0');

INSERT INTO credit_card(
    id, cvv, expiration_date, pan, account_id)
VALUES ('a8e2f208-b667-44e7-ae3a-9d0e044658b5', '015', '01/01/2025', '312348190231312', '8e3416d8-6b2b-4ef4-acc0-489b8a57a745');