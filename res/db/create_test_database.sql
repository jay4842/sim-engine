drop database sim_test;
create database sim_test;
use sim_test;
create table sim_id_refs(
ID int NOT NULL,
Primary key (ID)
);
create table sim_steps(
STEP int NOT NULL,
sim_id int,
entity_count int,
avg_entity_count float,
varient_counts varchar(32),
Primary key (STEP),
Foreign key (sim_id) references sim_id_refs (ID)
);
CREATE TABLE entity_info_at_step (
    step_id INT NOT NULL,
    sim_id INT,
    entity_id INT,
    gps VARCHAR(11),
    PRIMARY KEY (entity_id , step_id),
    FOREIGN KEY (sim_id)
        REFERENCES sim_id_refs (ID),
    FOREIGN KEY (step_id)
        REFERENCES sim_steps (STEP)
);

CREATE TABLE entity_info(
entity_id INT NOT NULL,
hp int,
maxHp int,
xp int,
maxXp int,
dmg int,
speed int,
sense int,
energy int,
maxEnergy int,
entityType int,
PRIMARY KEY (entity_id),
foreign key (entity_id) references entity_info_at_step (entity_id)
);

CREATE TABLE entity_type_info(
entityType int NOT NULL,
deathChance float,
replicationChance float,
hue int,
PRIMARY KEY (entityType)
);

CREATE TABLE personality_ref(
id int auto_increment NOT NULL,
personalityName varchar(32),
chance double,
extro_min double,
extro_max double,
agree_min double,
agree_max double,
ambit_min double,
ambit_max double,
neuro_min double,
neuro_max double,
creat_min double,
creat_max double,
PRIMARY KEY (id)
);
