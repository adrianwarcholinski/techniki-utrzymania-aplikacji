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