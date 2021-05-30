create table if not exists tbl_customer (
  id bigint not null unique,
  tin varchar not null unique
);

-- insert into
--   tbl_customer (id, tin)
-- values
--   (1, 10000000000),
--   (2, 20000000000),
--   (3, 30000000000),
--   (4, 40000000000);

create table if not exists tbl_loan_transaction (
  customer_id bigint not null,
  type varchar not null,
  amount numeric(10, 2) not null,
  constraint customer_fk foreign key(customer_id) references tbl_customer(id) on delete cascade
);

-- insert into
--   tbl_loan_transaction (customer_id, type, amount)
-- values
--   (1, 'loan', 1000.50),
--   (1, 'interest', 1.50),
--   (1, 'interest_repayment', 1.50),
--   (1, 'loan', 7800.00),
--   (2, 'loan', 5800.00),
--   (2, 'loan_repayment', 5200.30),
--   (3, 'loan', 400.00),
--   (4, 'loan', 1400.00),
--   (3, 'interest', 1.50),
--   (4, 'interest', 1.50),
--   (3, 'interest_repayment', 1.50),
--   (3, 'loan_repayment', 400.00);

select
  p.tin as tin,
  p.portfolio as portfolio,
  round(p.portfolio / p.total * 100, 1) as pct
from
  (
    select
      distinct c.tin as tin,
      sum(
        l.amount * case
          when l.type in ('loan', 'interest') then 1
          when l.type in ('loan_repayment', 'interest_repayment') then -1
          else 0
        end
      ) over (partition by c.id) as portfolio,
      sum(
        l.amount * case
          when l.type in ('loan', 'interest') then 1
          when l.type in ('loan_repayment', 'interest_repayment') then -1
          else 0
        end
      ) over () as total
    from
      tbl_customer as c
      inner join tbl_loan_transaction as l on c.id = l.customer_id
  ) as p
where
  p.portfolio != 0
order by
  p.tin;