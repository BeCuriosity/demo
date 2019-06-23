package com.yc.bean;

public class MovieType {
    private Integer id;

    private Integer typeId;

    private Integer movieId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

	@Override
	public String toString() {
		return "MovieType [id=" + id + ", typeId=" + typeId + ", movieId=" + movieId + "]";
	}
    
    
}