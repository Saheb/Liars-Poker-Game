-- table declarations :
create table player (
    name varchar(128) not null,
    email varchar(128) not null,
    player_id bigint primary key not null auto_increment
);
-- indexes on player
create unique index idx1f5d04c3 on player (email);
create table player_stats (
    games_won int not null,
    rank int not null,
    games_played int not null,
    player_id bigint not null
);
create table game_bet (
    game_id bigint not null,
    bet varchar(128) not null,
    turn_number int not null,
    round_number int not null,
    player_id bigint not null
);
create table player_status (
    name varchar(128) not null,
    game_id bigint not null,
    num_of_cards int not null,
    status varchar(128) not null,
    position int not null,
    player_id bigint not null
);
create table game_status (
    joined_players int not null,
    name varchar(128) not null,
    max_players int not null,
    admin_player bigint not null,
    game_id bigint primary key not null auto_increment,
    status int not null,
    winner_player bigint not null
);
create table round_result (
    player_challenge_id bigint not null,
    game_id bigint not null,
    result varchar(128) not null,
    player_bet_id bigint not null,
    round_number int not null,
    bet_challenged varchar(128) not null
);
create table game_hand (
    game_id bigint not null,
    hand varchar(128) not null,
    round_number int not null,
    player_id bigint not null
);
-- foreign key constraints :
alter table player_status add constraint player_statusFK1 foreign key (player_id) references player(player_id);
alter table player_status add constraint player_statusFK2 foreign key (game_id) references game_status(game_id);
