@echo off
set PGPASSFILE=pgpass.txt
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE car_rental;"
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d car_rental -f setup_postgresql.sql
echo Database setup complete!
pause