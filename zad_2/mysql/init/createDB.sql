CREATE USER 'ssbd01' IDENTIFIED BY 'P@ssw0rd';
CREATE USER 'ssbd01mok' IDENTIFIED BY 'P@ssw0rd';
CREATE USER 'ssbd01mor' IDENTIFIED BY 'P@ssw0rd';
CREATE USER 'ssbd01payara' IDENTIFIED BY 'P@ssw0rd';
CREATE USER 'ssbd01admin' IDENTIFIED BY 'P@ssw0rd';


create table account
(
    id                                bigint                not null,
    login                             varchar(20)           not null,
    password                          varchar(115)          not null,
    email                             varchar(50)           not null,
    verified                          boolean default false not null,
    active                            boolean default false not null,
    last_successful_authentication    timestamp,
    last_unsuccessful_authentication  timestamp,
    unsuccessful_authentication_count bigint  default 0,
    version                           bigint                not null,
    last_used_ip_address              varchar(40),
    constraint account_pk
        primary key (id),
    constraint account_login_key
        unique (login),
    constraint account_email_key
        unique (email)
);

create table personal_data
(
    id      bigint      not null,
    name    varchar(20) not null,
    surname varchar(50) not null,
    constraint personal_data_pk
        primary key (id),
    constraint personal_data_id_fkey
        foreign key (id) references account(id)
);


# alter table account
#     add constraint account_proper_email
#         check (email LIKE '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');

# alter table account
#     add constraint last_used_ip_address_check
#         check (((last_used_ip_address)::text ~*
#             '(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$'::text) OR
#                ((last_used_ip_address)::text ~*
#                    '([0-9A-Fa-f]{0,4}:){2,7}([0-9A-Fa-f]{1,4}$|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\.|$)){4})$'::text));

create table access_level
(
    id         bigint      not null,
    id_account bigint      not null,
    level      varchar(20) not null,
    active     boolean     not null,
    version    bigint      not null,
    constraint access_level_id
        primary key (id),
    constraint access_level_id_account_level_key
        unique (id_account, level),
    constraint access_level_id_account_fkey
        foreign key (id_account) references account(id)
);


create index access_level_id_account
    on access_level (id_account);

create table admin_data
(
    id          bigint      not null,
    card_number varchar(12) not null,
    constraint admin_data_pk
        primary key (id),
    constraint admin_data_card_number_key
        unique (card_number),
    constraint admin_data_id_fkey
        foreign key (id) references access_level(id)
);



create table customer_data
(
    id           bigint     not null,
    phone_number varchar(9) not null,
    constraint customer_data_pk
        primary key (id),
    constraint customer_data_phone_number_key
        unique (phone_number),
    constraint customer_data_id_fkey
        foreign key (id) references access_level(id)
);


create table employee_data
(
    id                bigint     not null,
    work_phone_number varchar(9) not null,
    constraint employee_data_pk
        primary key (id),
    constraint employee_data_work_phone_number_key
        unique (work_phone_number),
    constraint employee_data_id_fkey
        foreign key (id) references access_level(id)
);

create table alley_difficulty_level
(
    id      bigint      not null,
    name    varchar(20) not null,
    version bigint      not null,
    constraint alley_difficulty_level_pk
        primary key (id),
    constraint alley_difficulty_level_name_key
        unique (name)
);


create table alley
(
    id                        bigint       not null,
    name                      varchar(50)  not null,
    description               varchar(400) not null,
    id_alley_difficulty_level bigint       not null,
    active                    boolean      not null,
    version                   bigint       not null,
    constraint alley_pk
        primary key (id),
    constraint alley_name_key
        unique (name),
    constraint alley_id_alley_difficulty_level_fkey
        foreign key (id_alley_difficulty_level) references alley_difficulty_level (id)
);

create index alley_alley_difficulty_level_id
    on alley (id_alley_difficulty_level);

create table weapon_category
(
    id      bigint      not null,
    name    varchar(50) not null,
    version bigint      not null,
    constraint weapon_category_pk
        primary key (id),
    constraint weapon_category_name_key
        unique (name)
);


create table weapon_model
(
    id                 bigint      not null,
    name               varchar(20) not null,
    description        varchar(400),
    caliber_mm         numeric     not null,
    magazine_capacity  integer     not null,
    id_weapon_category bigint      not null,
    active             boolean     not null,
    version            bigint      not null,
    constraint weapon_model_pk
        primary key (id),
    constraint weapon_model_name_key
        unique (name),
    constraint weapon_model_id_weapon_category_fkey
        foreign key (id_weapon_category) references weapon_category(id)
);


create index weapon_model_weapon_category_id
    on weapon_model (id_weapon_category);

create table average_rate
(
    weapon_model_id bigint not null,
    value           numeric,
    constraint average_rate_pk
        primary key (weapon_model_id),
    constraint average_rate_weapon_model_id_fkey
        foreign key (weapon_model_id) references weapon_model(id)
);


alter table average_rate
    add constraint value_check
        check (((value >= 1) AND (value <= 5)) OR (value IS NULL));

create table weapon
(
    id              bigint      not null,
    serial_number   varchar(25) not null,
    id_weapon_model bigint      not null,
    active          boolean     not null,
    version         bigint      not null,
    constraint weapon_pk
        primary key (id),
    constraint weapon_serial_number_key
        unique (serial_number),
    constraint weapon_id_weapon_model_fkey
        foreign key (id_weapon_model) references weapon_model (id)
);


create index weapon_weapon_model_id
    on weapon (id_weapon_model);

create table reservation
(
    id                 bigint               not null,
    reservation_number bigint               not null,
    id_customer        bigint               not null,
    id_alley           bigint               not null,
    id_weapon          bigint               not null,
    start_date         timestamp            not null,
    end_date           timestamp            not null,
    active             boolean default true not null,
    version            bigint               not null,
    constraint reservation_pk
        primary key (id),
    constraint reservation_reservation_number_key
        unique (reservation_number),
    constraint reservation_id_customer_fkey
        foreign key (id_customer) references customer_data(id),
    constraint reservation_id_alley_fkey
        foreign key (id_alley) references alley(id),
    constraint reservation_id_weapon_fkey
        foreign key (id_weapon) references weapon(id)
);


create index reservation_weapon_id
    on reservation (id_weapon);

create index reservation_alley_id
    on reservation (id_alley);

create index reservation_account_id
    on reservation (id_customer);

alter table reservation
    add constraint date_check
        check (start_date < end_date);


#test
create table opinion
(
    id               bigint       not null,
    opinion_number   bigint       not null,
    id_weapon_model  bigint       not null,
    id_customer_data bigint       not null,
    content          varchar(200) not null,
    rate             integer      not null,
    version          bigint       not null,
    constraint opinion_pk
        primary key (id),
    constraint opinion_opinion_number_key
        unique (opinion_number),
    constraint opinion_id_weapon_model_fkey
        foreign key (id_weapon_model) references weapon_model(id),
    constraint opinion_id_customer_data_fkey
        foreign key (id_customer_data) references customer_data(id)
);

create index opinion_weapon_model_id
    on opinion (id_weapon_model);

create index opinion_customer_data_id
    on opinion (id_customer_data);

alter table opinion
    add constraint rate_range
        check ((rate >= 1) AND (rate <= 5));

create table expired_token
(
    id    bigint       not null,
    token varchar(250) not null,
    constraint expired_tokens
        primary key (id),
    constraint expired_token_token_key
        unique (token)
);


create table generator
(
    class_name varchar(32),
    id_range   bigint
);


create view auth_view(login, password, level) as
SELECT account.login,
       account.password,
       access_level.level
FROM access_level
         JOIN account ON account.id = access_level.id_account
WHERE account.verified = true
  AND account.active = true
  AND access_level.active = true;


GRANT ALL PRIVILEGES ON TABLE auth_view TO 'ssbd01admin';
GRANT SELECT ON TABLE auth_view TO 'ssbd01payara';

GRANT ALL PRIVILEGES ON TABLE account TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE account TO ssbd01mok;
GRANT SELECT ON TABLE account TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE personal_data TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE personal_data TO ssbd01mok;
GRANT SELECT ON TABLE personal_data TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE access_level TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE access_level TO ssbd01mok;
GRANT SELECT ON TABLE access_level TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE admin_data TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE admin_data TO ssbd01mok;

GRANT ALL PRIVILEGES ON TABLE customer_data TO ssbd01admin;
GRANT SELECT,INSERT, UPDATE ON TABLE customer_data TO ssbd01mok;
GRANT SELECT ON TABLE customer_data TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE employee_data TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE employee_data TO ssbd01mok;

GRANT ALL PRIVILEGES ON TABLE reservation TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE reservation TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE weapon TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE weapon TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE weapon_category TO ssbd01admin;
GRANT SELECT ON TABLE weapon_category TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE weapon_model TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE weapon_model TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE opinion TO ssbd01admin;
GRANT SELECT, INSERT, DELETE, UPDATE ON TABLE opinion TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE alley TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE alley TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE alley_difficulty_level TO ssbd01admin;
GRANT SELECT ON TABLE alley_difficulty_level TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE average_rate TO ssbd01admin;
GRANT SELECT, INSERT, UPDATE ON TABLE average_rate TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE generator TO ssbd01;
GRANT ALL PRIVILEGES ON TABLE generator TO ssbd01admin;
GRANT SELECT,UPDATE ON TABLE generator TO ssbd01mok;
GRANT SELECT,UPDATE ON TABLE generator TO ssbd01mor;

GRANT ALL PRIVILEGES ON TABLE expired_token TO ssbd01;
GRANT ALL PRIVILEGES ON TABLE expired_token TO ssbd01admin;
GRANT SELECT, INSERT ON TABLE expired_token TO ssbd01mok;

begin;

insert into account values(1, 'admin', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'ssbd2020.01@gmail.com', true, true, null, null, 0, 1, null);
insert into account values(2, 'colson', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'colson@example.com', true, true, null, null, 0, 1, null);
insert into account values(3, 'jsmith', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'jsmith@example.com', true, true, null, null, 0, 1, null);
insert into account values(4, 'jkowalski', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'jkowalski@example.com', true, true, null, null, 0, 1, null);
insert into account values(5, 'jakub', 'PBKDF2WithHmacSHA256:2048:EQOtAaLGuoJFq8ScfjFWQ2eWdL8TqAydMd6hx+LZH+c=:Zzxrl0YcjtxQIESA4i8NYmdCGEPNaJYq2Z2WhHPkFgo=', 'test2@ssbd01.pl', true, true, null, null, 0, 1, null);
insert into account values(18, 'seleniumBlocked', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'ewt90560@zzrgg.com', true, false, null, null, 0, 1, null);
insert into account values(19, 'seleniumUnblocked', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'nefap68840@gilfun.com', true, true, null, null, 0, 1, null);
insert into account values(20, 'seleniumCustomer', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test@gilfun.com', true, true, null, null, 0, 1, null);
insert into account values(21, 'seleniumNonCustomer', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=' ,'odp31764@eoopy.com', true, true, null, null, 0, 1, null);
insert into account values(22, 'selenium', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test123@gilfun.com', true, true, null, null, 0 ,1, null );
insert into account values(23, 'selenium2', 'PBKDF2WithHmacSHA256:2048:eyU8/uUvT4yS3422+9/4pl215ysDkwGs5pez0jXYq/w=:L7d/cPe2WPdeVu/pDNv8sKPfy8Mcfbmq9HIn+zhtp8Q=', 'test1234@gilfun.com', true, true, null, null, 0, 1, null);


insert into personal_data values(1, 'Admin', 'Administrator');
insert into personal_data values(2, 'Cyrus', 'Olson');
insert into personal_data values(3, 'Jonh', 'Smith');
insert into personal_data values(4, 'Jan', 'Kowalski');
insert into personal_data values(5, 'Jakub', 'Flaszka');



insert into personal_data values(18, 'seleniumBlocked', 'seleniumBlocked');
insert into personal_data values(19, 'SeleniumUnBlocked', 'SeleniumUnBlocked');
insert into personal_data values(20, 'SeleniumCustomer', 'SeleniumCustomer');
insert into personal_data values(21, 'SeleniumNonCustomer', 'SeleniumNonCustomer');
insert into personal_data values(22, 'Selenium', 'Seleniumowy');
insert into personal_data values(23, 'Selenium', 'Seleniumowy-Two');


insert into access_level values (1,1,'ROLE_CUSTOMER', true, 1);
insert into access_level values (2,1,'ROLE_EMPLOYEE', true, 1);
insert into access_level values (3,1,'ROLE_ADMIN', true, 1);

insert into access_level values (4,2,'ROLE_CUSTOMER',true,1);

insert into access_level values (5, 3, 'ROLE_EMPLOYEE', true, 1);

insert into access_level values (6,4, 'ROLE_ADMIN', true, 1);


insert into access_level values (7,5,'ROLE_CUSTOMER', true, 1);
insert into access_level values (8,5,'ROLE_EMPLOYEE', true, 1);
insert into access_level values (9,5,'ROLE_ADMIN', true, 1);


insert into access_level values (53, 18, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (54, 19, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (55, 20, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (56, 20, 'ROLE_ADMIN', true, 1);
insert into access_level values (57, 21, 'ROLE_EMPLOYEE', true, 1);
insert into access_level values (58, 22, 'ROLE_ADMIN', true, 1);
insert into access_level values (59, 22, 'ROLE_CUSTOMER', true, 1);
insert into access_level values (60, 23, 'ROLE_ADMIN', true, 1);


insert into customer_data values(1, '123456789');
insert into customer_data values(4,'223456789');
insert into customer_data values(7,'163456789');

insert into employee_data values(2, '348438393');
insert into employee_data values(5,'987654321');
insert into employee_data values(8, '234567821');

insert into admin_data values(3, 'ABC-DEF-GHI1');
insert into admin_data values(6, 'JVM-JDK-MVN3');
insert into admin_data values(9, 'ABC-CDE-EFG4');


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

