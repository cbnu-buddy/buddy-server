INSERT INTO party (plan_id, leader_id, leader_pwd, rec_limit, current_rec_num, start_date, duration_month, end_date, bill_date, progress_status)
SELECT
    p.plan_id,
    CONCAT('leader', FLOOR(1 + RAND() * 9)) AS leader_id,
    CONCAT('password', FLOOR(1 + RAND() * 9)) AS leader_pwd,
    (pl.max_member_num - 1) AS rec_limit,
    @currentRecNum := CASE
        WHEN RAND() < 0.5 THEN (pl.max_member_num - 1)
        ELSE FLOOR(0 + RAND() * (pl.max_member_num - 1))
END,
    DATE_ADD(NOW(), INTERVAL -FLOOR(0 + RAND() * 365) DAY) AS start_date,
    FLOOR(1 + RAND() * 12) AS duration_month,
    NULL AS end_date,
    NULL AS bill_date,
    CASE
        WHEN @currentRecNum = (pl.max_member_num - 1) THEN 1
        ELSE 0
END AS progress_status
FROM
    plan p
        INNER JOIN
    (SELECT plan_id, max_member_num FROM plan) AS pl ON p.plan_id = pl.plan_id
ORDER BY RAND()
LIMIT 100;
