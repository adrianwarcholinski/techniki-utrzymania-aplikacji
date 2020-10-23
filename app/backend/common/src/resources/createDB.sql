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


begin;
insert into personal_data values(1, 'Admin', 'Administrator');
insert into personal_data values(2, 'Cyrus', 'Olson');
insert into personal_data values(3, 'Jonh', 'Smith');
insert into personal_data values(4, 'Jan', 'Kowalski');
insert into personal_data values(5, 'Jakub', 'Flaszka');


--selenium
insert into personal_data values(18, 'seleniumBlocked', 'seleniumBlocked');
insert into personal_data values(19, 'SeleniumUnBlocked', 'SeleniumUnBlocked');
insert into personal_data values(20, 'SeleniumCustomer', 'SeleniumCustomer');
insert into personal_data values(21, 'SeleniumNonCustomer', 'SeleniumNonCustomer');
insert into personal_data values(22, 'Selenium', 'Seleniumowy');
insert into personal_data values(23, 'Selenium', 'Seleniumowy-Two');


insert into account values(1, 'admin', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'ssbd2020.01@gmail.com', true, true, null, null, 0, 1, null);
insert into account values(2, 'colson', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'colson@example.com', true, true, null, null, 0, 1, null);
insert into account values(3, 'jsmith', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'jsmith@example.com', true, true, null, null, 0, 1, null);
insert into account values(4, 'jkowalski', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'jkowalski@example.com', true, true, null, null, 0, 1, null);
insert into account values(5, 'jakub', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'test2@ssbd01.pl', true, true, null, null, 0, 1, null);

---------selenium
insert into account values(18, 'seleniumBlocked', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'ewt90560@zzrgg.com', true, false, null, null, 0, 1, null);
insert into account values(19, 'seleniumUnblocked', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'nefap68840@gilfun.com', true, true, null, null, 0, 1, null);
insert into account values(20, 'seleniumCustomer', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test@gilfun.com', true, true, null, null, 0, 1, null);
insert into account values(21, 'seleniumNonCustomer', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=' ,'odp31764@eoopy.com', true, true, null, null, 0, 1, null);
insert into account values(22, 'selenium', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test123@gilfun.com', true, true, null, null, 0 ,1, null );
insert into account values(23, 'selenium2', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test1234@gilfun.com', true, true, null, null, 0, 1, null);



--admin
insert into access_level values (1,1,'ROLE_CUSTOMER', true, 1);
insert into access_level values (2,1,'ROLE_EMPLOYEE', true, 1);
insert into access_level values (3,1,'ROLE_ADMIN', true, 1);

--colson
insert into access_level values (4,2,'ROLE_CUSTOMER',true,1);

--jsmith
insert into access_level values (5, 3, 'ROLE_EMPLOYEE', true, 1);

--jkowalski
insert into access_level values (6,4, 'ROLE_ADMIN', true, 1);

--jakub
insert into access_level values (7,5,'ROLE_CUSTOMER', true, 1);
insert into access_level values (8,5,'ROLE_EMPLOYEE', true, 1);
insert into access_level values (9,5,'ROLE_ADMIN', true, 1);


--selenium
insert into access_level values (53, 18, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (54, 19, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (55, 20, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (56, 20, 'ROLE_ADMIN', true, 1);
insert into access_level values (57, 21, 'ROLE_EMPLOYEE', true, 1);
insert into access_level values (58, 22, 'ROLE_ADMIN', true, 1);
insert into access_level values (59, 22, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (60, 23, 'ROLE_ADMIN', true, 1);
----------------------------------------------


--klienci
insert into customer_data values(1, '123456789');
insert into customer_data values(4,'223456789');
insert into customer_data values(7,'163456789');

--pracownicy
insert into employee_data values(2, '348438393');
insert into employee_data values(5,'987654321');
insert into employee_data values(8, '234567821');

--administratorzy
insert into admin_data values(3, 'ABC-DEF-GHI1');
insert into admin_data values(6, 'JVM-JDK-MVN3');
insert into admin_data values(9, 'ABC-CDE-EFG4');



----selenium
insert into customer_data values(53, '512905812');
insert into customer_data values(54, '581250129');
insert into customer_data values(55, '581905812');
insert into admin_data values(56, 'aaa-bbb-cccc');
insert into employee_data values(57, '230678849');
insert into admin_data values(58, 'abc-def-zxcv');
insert into customer_data values(59, '581905997');
insert into admin_data values(60, '525-515-gs55');


insert into weapon_category values(1, 'RIFLE', 1);
insert into weapon_category values(2, 'PISTOL', 1);
insert into weapon_category values(3, 'SNIPER_RIFLE', 1);
insert into weapon_model values(1, 'Colt Navy model 1851', 'amerykański sześciostrzałowy rewolwer kapiszonowy, wersja pośrednia pomiędzy Dragoonem, a Pocketem. Został skonstruowany przez Samuela Colta w roku 1847.', 9.14, 6, 1, true, 1);
insert into weapon_model values(2, 'Nagant wz. 1895', ' belgijski rewolwer kalibru 7,62 mm, potocznie zwany nagan od rosyjskiej pisowni i wymowy fonetycznej.', 7.62, 6, 1, true, 1);
insert into weapon_model values(3, 'Beretta M9', 'Pistolet samopowtarzalny, licencyjna wersja pistoletu Beretta 92F.', 9, 15, 1, true, 1);
insert into weapon_model values(4, 'Desert Eagle', 'Pistolet na nabój rewolwerowy .357 Magnum z lufami długości: 6, 8, 10 i 14 cali. Broń została zaprojektowana w 1979 roku przez amerykańską firmę Magnum Research, Inc. założoną w St. Paul w stanie Minnesota. Następnie w wyniku porozumienia z Israel Military Industries była m.in. produkowana na licencji.', 12.7, 9, 1, true, 1);
insert into average_rate values(1,null);
insert into average_rate values(2,null);
insert into average_rate values(3,null);
insert into average_rate values(4,null);
insert into weapon values(1, 'DE-1-A2017-02-11-LKI23MDW', 4, true, 1);
insert into weapon values(2, 'DE-1-A2017-02-12-MFW02MDW', 4, true, 1);
insert into weapon values(3, 'B9-1-A2018-02-11-LKI23MDW', 3, true, 1);
insert into weapon values(4, 'B9-1-A2019-03-11-P6V23MDW', 3, true, 1);
insert into weapon values(5, 'CN-1-A1999-05-15-LKI23MDW', 1, true, 1);
insert into weapon values(6, 'CN-2-A2013-02-12-MFW02MDW', 1, true, 1);
insert into weapon values(7, 'NA-1-A2018-02-11-LKI23MDW', 2, true, 1);
insert into weapon values(8, 'NA-2-A2019-03-11-P6V23MDW', 2, true, 1);
insert into alley_difficulty_level values(1, 'EASY', 1);
insert into alley_difficulty_level values(2, 'MEDIUM',1);
insert into alley_difficulty_level values(3, 'HARD',1);
insert into alley values(1, 'testowa aleja', 'aleja do celów testowych', 1, true, 1);
insert into alley values(2, 'testowa aleja 2', 'aleja do celów testowych', 1, true, 1);


insert into generator values('AccessLevelEntity', 100);
insert into generator values('AccountEntity', 100);
insert into generator values('AlleyEntity', 100);
insert into generator values('OpinionEntity', 100);
insert into generator values('ReservationEntity', 100);
insert into generator values('WeaponModelEntity', 100);
insert into generator values('WeaponEntity', 100);
insert into generator values('ExpiredTokenEntity', 100);
commit;