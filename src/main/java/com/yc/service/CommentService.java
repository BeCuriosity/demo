package com.yc.service;

import java.util.List;

import com.yc.bean.Comments;

public interface CommentService {
	List<Comments>getCommonts(int movieId);
}
