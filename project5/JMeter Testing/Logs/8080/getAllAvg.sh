#!/bin/bash
mkdir results;
python3 findAvg.py < JDBCMovieListNoPool10HTTP8080.txt > results/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMovieListNoPS10HTTP8080.txt > results/JDBCNoPSResult.txt
python3 findAvg.py < JDBCMovieList1HTTP8080.txt  > results/JDBCMLResult1.txt
python3 findAvg.py < JDBCMovieList10HTTP8080.txt > results/JDBCMLResult10.txt
python3 findAvg.py < JDBCMovieList10HTTPS8443.txt > results/JDBCMLResultSecure.txt
python3 findAvg.py < SearchServletMovieList1HTTP8080.txt > results/SearchMLResult1.txt
python3 findAvg.py < SearchServletMovieList10HTTP8080.txt > results/SearchMLResult10.txt
python3 findAvg.py < SearchServletMovieList10HTTPS8843.txt > results/SearchMLResultSecure.txt
python3 findAvg.py < SearchServletMovieListNoPS10HTTP8080.txt > results/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMovieListNoPool10HTTP8080.txt > results/SearchMLNoPoolResult.txt
