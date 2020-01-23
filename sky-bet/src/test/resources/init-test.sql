CREATE SCHEMA roulette;

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



CREATE SCHEMA resulting;

CREATE TABLE resulting.selection (
     openbet_id varchar2(100) primary key,
     external_id varchar2(100) not null,
     event_id varchar2(100) not null
);

CREATE TABLE resulting.legs (
    leg_id varchar2(100) primary key,
    stuff varchar2(25) not null
);

CREATE TABLE resulting.selection_legs (
    openbet_id varchar2(100) references resulting.selection,
    leg_id varchar2(100) references resulting.legs,
    primary key(openbet_id, leg_id)
);

CREATE TABLE resulting.event_legs (
    event_id varchar2(100),
    leg_id varchar2(100) references resulting.legs,
    primary key(event_id, leg_id)
);


CREATE TABLE resulting.leg_state (
    event_id varchar2(100) not null,
    leg_id varchar2(100) references resulting.legs,
    result varchar2(20) not null,
    confirmed tinyint not null
);



COMMIT;