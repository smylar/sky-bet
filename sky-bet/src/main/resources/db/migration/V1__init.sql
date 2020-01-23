
CREATE TABLE roulette.bets (
  id bigint auto_increment,
  customer_id bigint not null,
  game_id bigint not null,
  bet_type varchar(20) not null,
  number_selection varchar(100),
  bet_value bigint not null,
  bet_status varchar(20) not null,
  winning_number int,
  winnings bigint,
  PRIMARY KEY (id)
);

CREATE TABLE roulette.games (
  id bigint,
  status varchar(20) not null,
  PRIMARY KEY (id)
);

INSERT INTO roulette.games VALUES (1, 'OPEN');


COMMIT;