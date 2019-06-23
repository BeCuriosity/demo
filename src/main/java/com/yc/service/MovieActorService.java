package com.yc.service;

import java.util.List;

import com.yc.bean.Actor;
import com.yc.bean.Movie;
import com.yc.bean.MovieActor;

public interface MovieActorService {
	String director_role = "director";
	String actor_role = "actor";
	
	List<MovieActor> getActorByMovieId(int movieId);
	
	void update(String actorList,Movie movie);
}
