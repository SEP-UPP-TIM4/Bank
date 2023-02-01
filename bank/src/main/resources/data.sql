INSERT INTO accounts(
    id, address, amount, currency, name, number, password, reserved_amount)
VALUES ('547c7ba9-fc3e-4e48-ab78-6e1ab8e220f0', 'Address', 100000.0, 1, 'merchant', '318928310212356784', '$2a$10$i9gbDSXwPUh99J5Vw9zIveeDdTduv/ZzN3ydURP0pkHDpckqABpOi', 0.0);

INSERT INTO accounts(
    id, address, amount, currency, name, number, password, reserved_amount)
VALUES ('8e3416d8-6b2b-4ef4-acc0-489b8a57a745', 'Address', 100000.0, 1, 'issuer', '312348310212387654', '$2a$10$i9gbDSXwPUh99J5Vw9zIveeDdTduv/ZzN3ydURP0pkHDpckqABpOi', 0.0);

-- INSERT INTO accounts(
--     id, address, amount, currency, name, number, password, reserved_amount)
-- VALUES ('8e3416d8-6b2b-4ef4-acc0-489b8a57a755', 'Address', 100000.0, 1, 'issuer2', '312348310212387655', '$2a$10$i9gbDSXwPUh99J5Vw9zIveeDdTduv/ZzN3ydURP0pkHDpckqABpOi', 0.0);


INSERT INTO credit_card(
    id, cvv, expiration_date, pan, account_id)
VALUES ('a8e2f208-b667-44e7-ae3a-9d0e044658b4', '012', '01/01/2025', '9bVyTZkZiSRq7zbCM80L2YklKlCX19Qm', '547c7ba9-fc3e-4e48-ab78-6e1ab8e220f0');
--                                                                           4024007166390947
INSERT INTO credit_card(
    id, cvv, expiration_date, pan, account_id)
VALUES ('a8e2f208-b667-44e7-ae3a-9d0e044658b5', '015', '01/01/2025', '9bVyTZkZiSSPoMTY3+LVkoklKlCX19Qm', '8e3416d8-6b2b-4ef4-acc0-489b8a57a745');
--                                                                            4024007186929724
-- INSERT INTO credit_card(
--     id, cvv, expiration_date, pan, account_id)
-- VALUES ('a8e2f208-b667-44e7-ae3a-9d0e044658b6', '015', '01/01/2025', '1235567891011215', '8e3416d8-6b2b-4ef4-acc0-489b8a57a755');