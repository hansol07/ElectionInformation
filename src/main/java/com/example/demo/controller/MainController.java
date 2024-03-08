package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.MainService;
import com.example.demo.vo.GHVo;
import com.example.demo.vo.SJVO;
import com.example.demo.vo.TPVO;



@Controller
public class MainController {

	@Autowired
	private MainService mainService;
	
	@GetMapping("/")
    public String main() {
        return "index";
    }
	@GetMapping("/makeGHdata")
    public String getGHInfo() {
		mainService.makeGHData();
        return "index";
    }
	@GetMapping("/makeBRdata")
    public String getBRInfo() {
		mainService.makeBRData();
        return "index";
    }
	@GetMapping("/makeDTdata")
    public String getDTInfo() {
		mainService.makeDTData();
        return "index";
    }
	@GetMapping("/makeJBdata")
    public String getJBInfo() {
		mainService.makeJBData();
        return "index";
    }
	@GetMapping("/makeSJdata")
    public String getSJInfo() {
		mainService.makeSJData();
        return "index";
    }
	@GetMapping("/makeTPdata")
    public String getTPInfo() {
		mainService.makeTPData();
        return "index";
    }
	@GetMapping("/getGHVo")
	public String getVGData(Model model) {
		List<GHVo> data = mainService.getAllData();
		model.addAttribute("ghvoList",data);
		return "view";
	}
	@GetMapping("/getBRVo")
	public String getBRData(Model model) {
		List<GHVo> data = mainService.getAllData();
		model.addAttribute("ghvoList",data);
		return "view";
	}
	@GetMapping("/getDTVo")
	public String getDTData(Model model) {
		List<GHVo> data = mainService.getAllData();
		model.addAttribute("ghvoList",data);
		return "view";
	}
	@GetMapping("/getJBVo")
	public String getJBData(Model model) {
		List<GHVo> data = mainService.getAllData();
		model.addAttribute("ghvoList",data);
		return "view";
	}@GetMapping("/getSJVo")
	public String getSJData(Model model) {
		List<SJVO> data = mainService.getSJVOData();
		model.addAttribute("sjvoList",data);
		return "sjView";
	}
	@GetMapping("/getTPVo")
	public String getTPData(Model model) {
		List<TPVO> data = mainService.getTPVOData();
		model.addAttribute("tpvoList",data);
		return "tpView";
	}
}
