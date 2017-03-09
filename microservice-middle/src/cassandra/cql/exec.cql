CREATE KEYSPACE microservice WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3};

CREATE TABLE microservice.movie (
	id bigint,
	title text,
	PRIMARY KEY (id));

CREATE TABLE microservice.movie_by_title (
	id bigint,
	title text,
	PRIMARY KEY (id, title));