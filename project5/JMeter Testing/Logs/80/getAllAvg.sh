#!/bin/bash

mkdir results;

cp Master/JDBCMovieListNoPool10HTTP80.txt ./JDBCMLNoPool.txt
cp Master/JDBCMovieListNoPS10HTTP80.txt ./JDBCMLNOPS.txt
cp Master/JDBCMovieList1HTTP80.txt ./JDBCML1.txt
cp Master/JDBCMovieList10HTTP80.txt ./JDBCML10.txt
cp Master/SearchServletMovieList1HTTP80.txt ./SearchServletML1.txt
cp Master/SearchServletMovieList10HTTP80.txt ./SearchServletML10.txt
cp Master/SearchServletMovieListNoPS10HTTP80.txt ./SearchServletMLNOPS.txt
cp Master/SearchServletMovieListNoPool10HTTP80.txt ./SearchServletMLNoPool.txt

cat Slave/JDBCMovieListNoPool.txt >> JDBCMLNoPool.txt
cat Slave/JDBCMovieListNoPS.txt >> JDBCMLNOPS.txt
cat Slave/JDBCMovieList1.txt >> JDBCML1.txt
cat Slave/JDBCMovieList10.txt >> JDBCML10.txt
cat Slave/SearchServletMovieList1.txt >> SearchServletML1.txt
cat Slave/SearchServletMovieList10.txt >> SearchServletML10.txt
cat Slave/SearchServletMovieListNoPS.txt >> SearchServletMLNOPS.txt
cat Slave/SearchServletMovieListNoPool.txt >> SearchServletMLNoPool.txt

python3 findAvg.py < JDBCMLNoPool.txt > results/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMLNOPS.txt > results/JDBCNoPSResult.txt
python3 findAvg.py < JDBCML1.txt > results/JDBCMLResult1.txt
python3 findAvg.py < JDBCML10.txt > results/JDBCMLResult10.txt
python3 findAvg.py < SearchServletML1.txt > results/SearchMLResult1.txt
python3 findAvg.py < SearchServletML10.txt > results/SearchMLResult10.txt
python3 findAvg.py < SearchServletMLNOPS.txt > results/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMLNoPool.txt > results/SearchMLNoPoolResult.txt

rm *.txt
