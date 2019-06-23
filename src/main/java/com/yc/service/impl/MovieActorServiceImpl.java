package com.yc.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yc.bean.Actor;
import com.yc.bean.Movie;
import com.yc.bean.MovieActor;
import com.yc.bean.MovieActorExample;
import com.yc.dao.MovieActorMapper;
import com.yc.service.MovieActorService;

@Service
public class MovieActorServiceImpl implements MovieActorService{
	@Resource
	MovieActorMapper mam;
	@Resource
	ActorServiceImpl asi;

	@Override
	public List<MovieActor> getActorByMovieId(int movieId) {
		MovieActorExample mae = new MovieActorExample();
		mae.createCriteria().andMovieIdEqualTo(movieId);
		List<MovieActor> selectByExample = mam.selectByExample(mae);
		return selectByExample;
	}

	@Override
	public void update(String actorList,Movie movie) {
		MovieActorExample mae = new MovieActorExample();
		mae.createCriteria().andMovieIdEqualTo(movie.getMovieId());
		mam.deleteByExample(mae);
		String[] split = actorList.split("，");
		for (String s : split) {
			int i  = asi.find(s);
			if( i > 0) {
				MovieActor ma = new MovieActor();
				ma.setMovieId(movie.getMovieId());
				ma.setActorId(i);
				mam.insert(ma);
			}else {
				MovieActor ma = new MovieActor();
				ma.setMovieId(movie.getMovieId());
				Actor a = new Actor();
				a.setAname(s);
				asi.add(a);
				int id = asi.find(s);
				ma.setActorId(id);
				mam.insert(ma);
			}
		}
	}
}