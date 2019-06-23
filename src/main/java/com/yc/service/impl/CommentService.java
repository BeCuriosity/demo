package com.yc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yc.bean.Comments;
import com.yc.bean.CommentsExample;
import com.yc.dao.CommentsMapper;

@Service
public class CommentService implements com.yc.service.CommentService{
	
	@Autowired
	CommentsMapper commentsMapper;

	@Override
	public List<Comments> getCommonts(int movieId) {
		
		CommentsExample example = new CommentsExample();
		example.createCriteria().andMovieIdEqualTo(movieId);
		List<Comments> list = commentsMapper.selectByExample(example);
		return list;
	}
}
