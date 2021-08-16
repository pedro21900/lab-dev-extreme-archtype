--**********************************
--Ajuste do NLS_CHARACTERSET
--**********************************
connect sys/oracle as sysdba;
shutdown;
startup restrict;
Alter database character set INTERNAL_USE WE8ISO8859P1;
shutdown immediate;
startup;
connect system/oracle

--**********************************
--Tuning OracleXE
--**********************************
alter system set filesystemio_options=directio scope=spfile;
alter system set disk_asynch_io=false scope=spfile;

--**********************************
--Esquema labdevextremearchetype
--**********************************

create tablespace labdevextremearchetype datafile '/u01/app/oracle/oradata/XE/labdevextremearchetype01.dbf' size 100M online;
create tablespace idx_labdevextremearchetype datafile '/u01/app/oracle/oradata/XE/idx_labdevextremearchetype01.dbf' size 100M;
create user labdevextremearchetype identified by labdevextremearchetype default tablespace labdevextremearchetype temporary tablespace temp;
grant resource to labdevextremearchetype;
grant connect to labdevextremearchetype;
grant create view to labdevextremearchetype;
grant create procedure to labdevextremearchetype;
grant create materialized view to labdevextremearchetype;
alter user labdevextremearchetype default role connect, resource;
exit;
