package com.yc.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.yc.bean.Actor;
import com.yc.bean.Movie;
import com.yc.bean.MovieImage;
import com.yc.bean.MovieType;
import com.yc.bean.Type;
import com.yc.service.impl.MovieActorServiceImpl;
import com.yc.service.impl.MovieImageServiceImpl;
import com.yc.service.impl.MovieServiceImpl;
import com.yc.service.impl.MovieTypeServiceImpl;
import com.yc.service.impl.TypeServiceImpl;
import com.yc.util.Utils;
import com.yc.vo.Result;

@Controller
public class FrameController {
	
	@Resource
	MovieServiceImpl msi;
	@Resource
	TypeServiceImpl tsi;
	@Resource
	MovieTypeServiceImpl mtsi;
	@Resource
	MovieImageServiceImpl misi;
	@Resource
	MovieActorServiceImpl masi;
	
	/**
	 * 打开后台页面
	 * @return
	 */
	@RequestMapping("manage")
	public String manage() {
		return "manage/frame";
	}
	
	/**
	 * 获得全部电影信息
	 * @param model
	 * @param current
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@RequestMapping("allMovie")
	public ModelAndView getAllMovie(ModelAndView model,@RequestParam(defaultValue="1") int current) throws IllegalArgumentException, IllegalAccessException {
		model.setViewName("manage/allMovie");
		
		PageInfo<Movie> l = msi.findAllMoive(current,null,null);
		
		List<Map<String,Object>> list = new ArrayList<>();
		for(Movie m:l.getList()) {
			Map<String,Object> m1 = new HashMap<>();
			String type = "";
			Utils.transformBeanToMap(m, m1);
			for(MovieType mt:m.getType()) {
				
				type+=tsi.findTypeByTypeID(mt.getTypeId()).getName()+"   ";
			}
			
			m1.put("type", type);
			list.add(m1);
		}
		List<Type> typeList = tsi.findAllType();
		int total = msi.findTotal(null,null);
		if(total % 5 == 0) {
			total /= 5;
		}else {
			total = (total / 5) + 1;
		}
		model.addObject("movieList",list);
		model.addObject("typeList",typeList);
		model.addObject("total",total);
		return model;
	}
	
	/**
	 * 分类查询
	 * @param model
	 * @param current
	 * @param sname
	 * @param sTime
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 */
	@RequestMapping("getAllMovieBypage")
	@ResponseBody
	public List<Map<String,Object>> getAllMovieBypage(ModelAndView model,@RequestParam(defaultValue="1") int current,@RequestParam(name="sname") String sname,@RequestParam(name="sTime") String sTime) throws IllegalArgumentException, IllegalAccessException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if(sTime != null && sTime.trim().length() > 0) {
			date = sdf.parse(sTime);
		}
		int total = msi.findTotal(sname,date);
		if(total % 5 == 0) {
			total /= 5;
		}else {
			total = (total / 5) + 1;
		}
		Map<String,Object> tmap = new HashMap();
		tmap.put("total", total);
		PageInfo<Movie> l = msi.findAllMoive(current,sname,date);
		List<Map<String,Object>> list = new ArrayList<>();
		for(Movie m:l.getList()) {
			Map<String,Object> m1 = new HashMap<>();
			String type = "";
			Utils.transformBeanToMap(m, m1);
			for(MovieType mt:m.getType()) {
				type+=tsi.findTypeByTypeID(mt.getTypeId()).getName()+"   ";
			}
			m1.put("type", type);
			list.add(m1);
		}
		list.add(tmap);
		return list;
	}
	
	/**
	 添加电影
	 * @return
	 */
	@RequestMapping("addMovie")
	public String addMovie() {
		return "manage/addMovie";
	}
	
	/**
	 * 获得电影详细信息
	 * @param model
	 * @param movieId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@RequestMapping("getMovieDetail")
	@ResponseBody
	public Map<String,Object> getMovieDetail(Model model,int movieId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Map<String, Object> map = msi.findMovieDetailsByMovieId(movieId);
		List<Actor> actorList= (List<Actor>) map.get("actorList");
		if(actorList.size() > 0) {
			String actorlist = "";
			for (Actor actor : actorList) {
				actorlist += actor.getAname()+"，";
			}
			actorlist = actorlist.substring(0,actorlist.lastIndexOf("，"));
			map.put("actorList", actorlist);
		}
		return map;
	}
	
	/**
	 * 修改电影信息	
	 * @param movie
	 * @param typeList
	 * @param actor
	 * @return
	 */
	@RequestMapping("alterMovie")
	@ResponseBody
	public Result alterMovie(Movie movie,@RequestParam(value = "typeList[]")String[] typeList,@RequestParam(value = "actor")String actor) {
		int r = msi.updateMovie(movie);
		mtsi.updateType(movie, typeList);
		masi.update(actor, movie);
		Result re;
		if(r > 0) {
			re = new Result(1, "修改成功");
		}else {
			re = new Result(0, "修改失败");
		}
		
		return re;
	}
	/**
	 * 上传电影封面
	 */
 	@PostMapping("ImgUpload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,@RequestParam(name="movieId") String MovieId) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        System.err.println("id="+MovieId+",path="+file.getOriginalFilename());
        MovieImage mi = new MovieImage();
        mi.setImage(file.getOriginalFilename());
        mi.setMovieId(Integer.parseInt(MovieId));
        misi.update(mi);
        String fileName = file.getOriginalFilename();
        String filePath = "D:/upload/";
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            return "上传成功";
        } catch (IOException e) {
        }
        return "上传失败！";
    }
}
