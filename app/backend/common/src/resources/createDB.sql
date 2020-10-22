SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

drop table if exists personal_data cascade;
drop table if exists account cascade;
drop table if exists access_level cascade;
drop table if exists auth_view cascade;
drop table if exists admin_data cascade;
drop table if exists employee_data cascade;
drop table if exists customer_data cascade;
drop table if exists reservation cascade;
drop table if exists alley cascade;
drop table if exists weapon cascade;
drop table if exists weapon_model cascade;
drop table if exists opinion cascade;
drop table if exists weapon_category cascade;
drop table if exists alley_difficulty_level cascade;
drop table if exists average_rate cascade;
drop table if exists generator cascade;
drop table if exists expired_token cascade;



create table personal_data
(
    id bigint constraint personal_data_pk primary key,
    name character varying(20) not null constraint personal_data_name CHECK (name ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.''-]{1,20}$'),
    surname character varying(50) not null constraint personal_data_surname CHECK (surname ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.''-]{1,50}$')
);

create table account
(
    id bigint constraint account_pk primary key,
    login character varying(20) not null unique constraint login_format CHECK (login ~* '^[a-zA-Z0-9]{1,20}$'),
    password character varying(115) not null,
    email character varying(50) not null unique constraint account_proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
    verified boolean not null default false,
    active boolean not null default false,
    last_successful_authentication timestamp default null,
    last_unsuccessful_authentication timestamp default null,
    unsuccessful_authentication_count bigint default 0,
    version bigint not null,
    last_used_ip_address character varying(40),
    constraint last_used_ip_address_check CHECK(last_used_ip_address ~* '(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$' OR last_used_ip_address ~* '([0-9A-Fa-f]{0,4}:){2,7}([0-9A-Fa-f]{1,4}$|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\.|$)){4})$' )
);

alter table personal_data add foreign key (id) references account(id) deferrable initially deferred;
alter table account add foreign key (id) references personal_data(id) deferrable initially deferred;


create table access_level
(
    id bigint constraint access_level_id primary key,
    id_account bigint not null,
    level character varying(20) not null,
    active boolean not null,
    version bigint not null,
    unique (id_account, level),
    FOREIGN KEY (id_account) references account(id)
);

create table admin_data
(
    id bigint constraint admin_data_pk primary key,
    card_number character varying(12) not null unique constraint card_number_format CHECK (card_number ~* '^([\w]{3})+(-[\w]{3})+(-[\w]{4})')
);

alter table only admin_data add foreign key (id) references access_level(id) deferrable initially deferred;

create table customer_data
(
    id bigint constraint customer_data_pk primary key,
    phone_number character varying(9) not null unique constraint phone_number_format CHECK (phone_number ~* '^\d+$')
);

alter table only customer_data add foreign key (id) references access_level(id) deferrable initially deferred;

create table employee_data
(
    id bigint constraint employee_data_pk primary key,
    work_phone_number character varying(9) not null unique constraint work_phone_number_format CHECK (work_phone_number ~* '^\d+$')
);


alter table only employee_data add foreign key (id) references access_level(id) deferrable initially deferred;

create view auth_view as
SELECT account.login, account.password, access_level.level FROM access_level join account on account.id = access_level.id_account WHERE account.verified = true AND account.active = true AND access_level.active = true;

-------------------------------------------------------------------------------------------------------------------z------------------


create table alley_difficulty_level
(
    id bigint constraint alley_difficulty_level_pk primary key,
    name character varying(20) not null unique constraint alley_difficulty_level_name_format CHECK (name ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż-]{1,20}$'),
    version bigint not null
);

create table alley
(
    id bigint constraint alley_pk primary key,
    name character varying(50) not null unique constraint alley_name_format CHECK (name ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,. 0-9''-]{1,50}$'),
    description character varying(400) not null constraint alley_description_format CHECK (description ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9 ,.|\n\s$''-]'),
    id_alley_difficulty_level bigint not null,
    active boolean not null,
    version bigint not null,
    FOREIGN KEY (id_alley_difficulty_level) references alley_difficulty_level(id)
);

create table weapon_category
(
    id bigint constraint weapon_category_pk primary key,
    name character varying(50) not null unique constraint weapon_category_name CHECK (name ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9 ,._''-]'),
    version bigint not null
);

create table weapon_model
(
    id bigint constraint weapon_model_pk primary key,
    name character varying(20) not null unique constraint weapon_model_name CHECK (name ~* '^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.''-]'),
    description character varying(400) constraint weapon_model_description_format CHECK (description ~* '^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9 ,.:|\n\s$''-]'),
    caliber_mm decimal not null,
    magazine_capacity integer not null,
    id_weapon_category bigint not null,
    active boolean not null,
    version bigint not null,
    FOREIGN KEY (id_weapon_category) references weapon_category(id)
);

create table average_rate
(
    weapon_model_id bigint constraint average_rate_pk primary key,
    value decimal constraint value_check check(value BETWEEN 1 AND 5 OR value IS NULL)
);

alter table weapon_model add FOREIGN KEY (id) references average_rate(weapon_model_id) deferrable initially deferred;
alter table average_rate add FOREIGN KEY (weapon_model_id) references weapon_model(id) deferrable initially deferred;

create table weapon
(
    id bigint constraint weapon_pk primary key,
    serial_number character varying(25) not null unique constraint weapon_serial_number_format CHECK (serial_number ~ '^^[A-Z0-9]{2}-[A-Z0-9]-[A-Z0-9]{5}-[A-Z0-9]{2}-[A-Z0-9]{2}-[A-Z0-9]{8}$'),
    id_weapon_model bigint not null,
    active boolean not null,
    version bigint not null,
    FOREIGN KEY (id_weapon_model) references weapon_model(id)
);

create table reservation
(
    id bigint constraint reservation_pk primary key,
    reservation_number bigint not null unique,
    id_customer bigint not null,
    id_alley bigint not null,
    id_weapon bigint not null,
    start_date timestamp not null,
    end_date timestamp not null,
    active boolean not null default true,

    version bigint not null,
    FOREIGN KEY (id_customer) references customer_data(id),
    FOREIGN KEY (id_alley) references alley(id),
    FOREIGN KEY (id_weapon) references weapon(id),
    constraint date_check CHECK(start_date < end_date)
);

create table opinion
(
    id bigint constraint opinion_pk primary key,
    opinion_number bigint unique not null,
    id_weapon_model bigint not null,
    id_customer_data bigint not null,
    content character varying(200) not null constraint content_format CHECK (content ~* '^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż,.()|\n\s$''-]'),
    rate int not null constraint rate_range CHECK(rate BETWEEN 1 AND 5),
    version bigint not null,
    FOREIGN KEY (id_weapon_model) references weapon_model(id),
    FOREIGN KEY (id_customer_data) references customer_data(id)

);

create table expired_token
(
    id bigint constraint expired_tokens primary key,
    token character varying(250) not null unique
);

-------------------------------------------------------------------------------------------------------------------------------------
create table generator
(
    class_name character varying(32),
    id_range bigint
);

--------------------------------------------------------------------------------------------------------------------------------------

create index access_level_id_account on access_level using btree (id_account);
create index opinion_weapon_model_id on opinion using btree(id_weapon_model);
create index reservation_weapon_id on reservation using btree(id_weapon);
create index reservation_alley_id on reservation using btree(id_alley);
create index reservation_account_id on reservation using btree(id_customer);
create index weapon_model_weapon_category_id on weapon_model using btree(id_weapon_category);
create index weapon_weapon_model_id on weapon using btree(id_weapon_model);
create index alley_alley_difficulty_level_id on alley using btree(id_alley_difficulty_level);
create index opinion_customer_data_id on opinion using btree(id_customer_data);

alter table personal_data owner to ssbd01;
alter table account owner to ssbd01;
alter table access_level owner to ssbd01;
alter table auth_view owner to ssbd01;
alter table admin_data owner to ssbd01;
alter table customer_data owner to ssbd01;
alter table employee_data owner to ssbd01;
alter table reservation owner to ssbd01;
alter table weapon owner to ssbd01;
alter table weapon_model owner to ssbd01;
alter table weapon_category owner to ssbd01;
alter table opinion owner to ssbd01;
alter table alley owner to ssbd01;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

REVOKE ALL ON TABLE auth_view FROM PUBLIC;
REVOKE ALL ON TABLE auth_view FROM ssbd01admin;
GRANT ALL ON TABLE auth_view TO ssbd01admin;
grant select on table auth_view to ssbd01payara;

REVOKE ALL ON TABLE account FROM PUBLIC;
REVOKE ALL ON TABLE account FROM ssbd01admin;
GRANT ALL ON TABLE account TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE account TO ssbd01mok;
GRANT SELECT ON TABLE account TO ssbd01mor;


REVOKE ALL ON TABLE personal_data FROM PUBLIC;
REVOKE ALL ON TABLE personal_data FROM ssbd01admin;
GRANT ALL ON TABLE personal_data TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE personal_data TO ssbd01mok;
GRANT SELECT ON TABLE personal_data TO ssbd01mor;


REVOKE ALL ON TABLE access_level FROM PUBLIC;
REVOKE ALL ON TABLE access_level FROM ssbd01admin;
GRANT ALL ON TABLE access_level TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE access_level TO ssbd01mok;
GRANT SELECT ON TABLE access_level TO ssbd01mor;


REVOKE ALL ON TABLE admin_data FROM PUBLIC;
REVOKE ALL ON TABLE admin_data FROM ssbd01admin;
GRANT ALL ON TABLE admin_data TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE admin_data TO ssbd01mok;

REVOKE ALL ON TABLE customer_data FROM PUBLIC;
REVOKE ALL ON TABLE customer_data FROM ssbd01admin;
GRANT ALL ON TABLE customer_data TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE customer_data TO ssbd01mok;
GRANT SELECT ON TABLE customer_data TO ssbd01mor;

REVOKE ALL ON TABLE employee_data FROM PUBLIC;
REVOKE ALL ON TABLE employee_data FROM ssbd01admin;
GRANT ALL ON TABLE employee_data TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE employee_data TO ssbd01mok;


REVOKE ALL ON TABLE reservation FROM PUBLIC;
REVOKE ALL ON TABLE reservation FROM ssbd01admin;
GRANT ALL ON TABLE reservation TO ssbd01admin;
GRANT SELECT,INSERT,UPDATE ON TABLE reservation TO ssbd01mor;

REVOKE ALL ON TABLE weapon FROM PUBLIC;
REVOKE ALL ON TABLE weapon FROM ssbd01admin;
GRANT ALL ON TABLE weapon TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE weapon TO ssbd01mor;

REVOKE ALL ON TABLE weapon_category FROM PUBLIC;
REVOKE ALL ON TABLE weapon_category FROM ssbd01admin;
GRANT ALL ON TABLE weapon_category TO ssbd01admin;
GRANT SELECT ON TABLE weapon_category TO ssbd01mor;

REVOKE ALL ON TABLE weapon_model FROM PUBLIC;
REVOKE ALL ON TABLE weapon_model FROM ssbd01admin;
GRANT ALL ON TABLE weapon_model TO ssbd01admin;
GRANT SELECT,INSERT,UPDATE ON TABLE weapon_model TO ssbd01mor;

REVOKE ALL ON TABLE opinion FROM PUBLIC;
REVOKE ALL ON TABLE opinion FROM ssbd01admin;
GRANT ALL ON TABLE opinion TO ssbd01admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE opinion TO ssbd01mor;

REVOKE ALL ON TABLE alley FROM PUBLIC;
REVOKE ALL ON TABLE alley FROM ssbd01admin;
GRANT ALL ON TABLE alley TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE alley TO ssbd01mor;


REVOKE ALL ON TABLE alley_difficulty_level FROM PUBLIC;
REVOKE ALL ON TABLE alley_difficulty_level FROM ssbd01admin;
GRANT ALL ON TABLE alley_difficulty_level TO ssbd01admin;
GRANT SELECT ON TABLE alley_difficulty_level TO ssbd01mor;


REVOKE ALL ON TABLE average_rate FROM PUBLIC;
REVOKE ALL ON TABLE average_rate FROM ssbd01admin;
GRANT ALL ON TABLE average_rate TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE average_rate TO ssbd01mor;


REVOKE ALL ON TABLE generator FROM PUBLIC;
REVOKE ALL ON TABLE generator FROM ssbd01;
GRANT ALL ON TABLE generator TO ssbd01;
GRANT ALL ON TABLE generator TO ssbd01admin;
GRANT SELECT,UPDATE ON TABLE generator TO ssbd01mok;
GRANT SELECT,UPDATE ON TABLE generator TO ssbd01mor;


REVOKE ALL ON TABLE expired_token FROM PUBLIC;
REVOKE ALL ON TABLE expired_token FROM ssbd01;
GRANT ALL ON TABLE expired_token TO ssbd01;
GRANT ALL ON TABLE expired_token TO ssbd01admin;
GRANT SELECT, INSERT ON TABLE expired_token TO ssbd01mok;