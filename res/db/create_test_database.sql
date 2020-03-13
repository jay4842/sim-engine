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
Primary key (STEP, sim_id),
Foreign key (sim_id) references sim_id_refs (ID)
);

CREATE TABLE entity_type_info(
entityType int NOT NULL,
deathChance float,
replicationChance float,
hue int,
PRIMARY KEY (entityType)
);

CREATE TABLE entity_info_at_step (
    step_id INT NOT NULL,
    sim_id INT,
    entity_id INT,
    gps VARCHAR(11),
    PRIMARY KEY (entity_id, step_id, sim_id),
    FOREIGN KEY (sim_id) REFERENCES sim_id_refs (ID),
    FOREIGN KEY (step_id) REFERENCES sim_steps (STEP)
);

CREATE TABLE entity_info(
entity_id INT NOT NULL,
step_id INT NOT NULL,
sim_id INT NOT NULL,
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
PRIMARY KEY (entity_id, step_id, sim_id),
foreign key (step_id, entity_id, sim_id) references entity_info_at_step (step_id, entity_id, sim_id),
foreign key (entityType) references entity_type_info(entityType)
);

CREATE TABLE personality_ref(
id int auto_increment NOT NULL,
personalityName varchar(32) NOT NULL,
chance double NOT NULL,
extro_min double NOT NULL,
extro_max double NOT NULL,
agree_min double NOT NULL,
agree_max double NOT NULL,
ambit_min double NOT NULL,
ambit_max double NOT NULL,
neuro_min double NOT NULL,
neuro_max double NOT NULL,
creat_min double NOT NULL,
creat_max double NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE personality(
ref_id int NOT NULL,
id int auto_increment NOT NULL,
step_id int NOT NULL,
entity_id int NOT NULL,
sim_id int NOT NULL,
extro double NOT NULL,
afree double NOT NULL,
ambit double NOT NULL,
neuro double NOT NULL,
creat double NOT NULL,
PRIMARY KEY (id, step_id, sim_id),
foreign key(ref_id) references personality_ref(id),
foreign key(entity_id, step_id, sim_id) references entity_info_at_step(entity_id, step_id, sim_id)
);

CREATE TABLE inventory(
id int auto_increment NOT NULL,
step_id int NOT NULL,
entity_id int NOT NULL,
sim_id int NOT NULL,
inventory_size int NOT NULL,
inventory_var varchar(128),
PRIMARY KEY (id, step_id, sim_id),
foreign key(entity_id, step_id, sim_id) references entity_info_at_step(entity_id, step_id, sim_id)
);

CREATE TABLE ItemRef(
ref_id int NOT NULL,
ref_name varchar(32),
ref_type varchar(32),
image_path varchar(32),
properties varchar(128),
drop_min int NOT NULL,
drop_max int NOT NULL,
primary key (ref_id)
);

CREATE TABLE Item(
item_id int NOT NULL,
ref_id int NOT NULL,
list_id int NOT NULL,
sim_id int NOT NULL,
step_id int NOT NULL,
dur int,
amt int,
namespace varchar(32),
primary key (item_id, sim_id, step_id),
foreign key (sim_id, step_id) references sim_steps(sim_id, STEP),
foreign key (ref_id) references ItemRef(ref_id)
);

