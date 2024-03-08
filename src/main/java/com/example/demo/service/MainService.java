package com.example.demo.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repo.MainRepository;
import com.example.demo.repo.SJRepository;
import com.example.demo.repo.SidoRepository;
import com.example.demo.repo.TPRepository;
import com.example.demo.vo.GHVo;
import com.example.demo.vo.SJVO;
import com.example.demo.vo.SidoVo;
import com.example.demo.vo.TPVO;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class MainService {

	@Autowired
	private MainRepository mainRepo;
	@Autowired
	private SidoRepository sidoRepo;
	@Autowired
	private SJRepository sjRepo;
	@Autowired
	private TPRepository tpRepo;
	
	public void makeGHData() {
		List<SidoVo> sidoList = sidoRepo.findAll();
			System.out.println(sidoList.size());
		for(int i = 0 ; i<sidoList.size(); i++) {
			getData(sidoList.get(i).getSidoCode(), sidoList.get(i).getSidoName());
		
		}
	
	}
	
	public void getData(String sidoCode, String sidoName) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
System.out.println("????");
		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCCP09");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType2")));
			menu.click();

			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20160413");

			// 'electionCode' 요소 대기
			WebElement selectBox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionCode")));
			// 'electionCode' 요소를 Select 객체로 변환
			Select select2 = new Select(selectBox2);
			Thread.sleep(200);
			// 'electionCode' 요소에 값을 입력
			select2.selectByValue("2");

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoCode);

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			// 'cont_table' 요소 대기 및 결과 반환
			WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table01")));
			Thread.sleep(200);
			WebElement tbody = table.findElement(By.tagName("tbody"));
			int whenCount = 20;
			String kindSungu = "국회의원";
			String city = sidoName;

			String sungugoo = "";
			int posiblePeople = 0;
			int realPeople = 0;
			List<String> jungdang = new ArrayList<>();
			List<String> hubo= new ArrayList<>();
			List<String> dpSu = new ArrayList<>();
			int muhyo = 0;
			int giveUp = 0 ; 
			List<GHVo> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int i = 0; i < rows.size(); i++) {
				
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().equals(" ") ) {
					sungugoo = "";
					posiblePeople = 0;
					realPeople = 0;
					jungdang = new ArrayList<>();
					hubo= new ArrayList<>();
					dpSu = new ArrayList<>();
					muhyo = 0;
					giveUp = 0 ; 
					
					count=0;
				}

				for (int j = 0; j < cells.size(); j++) {
					WebElement cell = cells.get(j);
					if(cell.getText().isEmpty())continue;
					if(count ==0 ) {  // 첫줄일때
						if(j==0) {
							sungugoo =cell.getText();
							continue;
						}
						if(j<4)continue;  // 빈칸임
						if(j == cells.size()-3)break; // 계 이후는 쓸모없음
						
					      	WebElement strongElement = cell.findElement(By.tagName("strong"));
					        String[] strongTextParts = strongElement.getText().split("\n");
					        String party = strongTextParts[0]; // 정당
					        String name = strongTextParts[1]; // 이름
					        jungdang.add(party);
					     
					        hubo.add(name);
						
					}else if(count ==1) { //두번째 줄일때
						if(j<2)continue;
						if(j==2) {
							posiblePeople = Integer.parseInt(deleteComma(cell.getText()));
							continue;
						}
						if(j==3) {
							realPeople = Integer.parseInt(deleteComma(cell.getText()));
							continue;
						}
						if(j<cells.size()-3) {
							dpSu.add(deleteComma(cell.getText()));
							continue;
						}
						if(j==cells.size()-2) {
							muhyo = Integer.parseInt(deleteComma(cell.getText()));
						}else if(j==cells.size()-1) {
							giveUp = Integer.parseInt(deleteComma(cell.getText()));
						}												
					}else {
						break;
					}
				}
				if(count==1) { 
					 fillEmptyValues(jungdang, hubo, dpSu);
					GHVo vo = GHVo.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)
							.tusu(realPeople)
							.jungdang(jungdang)
							.hubo(hubo)
							.dpSu(dpSu)
							.mutusu(muhyo)
							.gigwonsu(giveUp)
							.build();
					mainRepo.save(vo);
					//list.add(vo);
					count ++;
				}else if(count==0) count++;

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}


	public void insertGH( List<GHVo> list) {
		for(int i  = 0 ;  i<list.size() ; i++) {

			mainRepo.save(list.get(i));
		}
	}
	public void insertSJ( List<SJVO> list) {
		for(int i  = 0 ;  i<list.size() ; i++) {
	
			sjRepo.save(list.get(i));
		}
	}
	public void insertTP( List<GHVo> list) {
		for(int i  = 0 ;  i<list.size() ; i++) {
			System.out.println(list.get(i).getHubo().size());
			mainRepo.save(list.get(i));
		}
	}
	public String deleteComma(String str) {
		String numberOnly = str.replaceAll("[^0-9]", "");
		return numberOnly;
	}
	public List<GHVo> getAllData(){
		return mainRepo.findAll();
	}
	
	public List<SJVO> getSJVOData(){
		return sjRepo.findAll();
	}
	public List<TPVO> getTPVOData(){
		return tpRepo.findAll();
	}
    public  void fillEmptyValues(List<String> jungdang, List<String> hubo, List<String> dpSu) {
        fillEmptyValues(jungdang, 35);
        fillEmptyValues(hubo, 35);
        fillEmptyValues(dpSu, 35);
    }
    public  void fillEmptyValues(List<String> list, int size) {
        int emptySlots = size - list.size();
        for (int i = 0; i < emptySlots; i++) {
            list.add("");
       
        }
    }

    public  void fillEmptyValuesInt(List<Integer> list, int size) {
        int emptySlots = size - list.size();
        for (int i = 0; i < emptySlots; i++) {
            list.add(0);
        }
    }
    
    public void makeBRData() {
    	
    	List<SidoVo> sidoList = sidoRepo.findAll();
	
	for(int i = 0 ; i<sidoList.size(); i++) {
		getBRData(sidoList.get(i).getSidoCode(), sidoList.get(i).getSidoName());
	
	}
    }
  public void makeDTData() {
	  List<SidoVo> sidoList = sidoRepo.findAll();
		for(int i = 0 ; i<sidoList.size(); i++) {
			getDTData(sidoList.get(i).getSidoCode(), sidoList.get(i).getSidoName());
		
		}
    	
    }
  public void makeJBData() {
	  List<SidoVo> sidoList = sidoRepo.findAll();
		for(int i = 0 ; i<sidoList.size(); i++) {
			getJBData(sidoList.get(i).getSidoCode(), sidoList.get(i).getSidoName());
		
		}
    	
    }
  public void makeSJData() {
	  List<SidoVo> sidoList = sidoRepo.findAll();
		for(int i = 0 ; i<sidoList.size(); i++) {
			getSJData(sidoList.get(i).getSidoCode(), sidoList.get(i).getSidoName());
		
		}
    	
    }
  public void makeTPData() {
	  List<SidoVo> sidoList = sidoRepo.findAll();
		
	 
		  getTPData(sidoList);
		
		
		
		
		
    	
    }
	public void getJBData(String sidoCode, String sidoName) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCCP09");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType4")));
			menu.click();

			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20060531");

			// 'electionCode' 요소 대기
			WebElement selectBox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionCode")));
			// 'electionCode' 요소를 Select 객체로 변환
			Select select2 = new Select(selectBox2);
			Thread.sleep(200);
			// 'electionCode' 요소에 값을 입력
			select2.selectByValue("3");

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoCode);

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			// 'cont_table' 요소 대기 및 결과 반환
			WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table01")));
			Thread.sleep(200);
			
			WebElement thead = table.findElement(By.tagName("thead"));
			List<WebElement> thRows = thead.findElements(By.tagName("tr"));
			WebElement thirdTr = thRows.get(2);
			List<WebElement> thElements = thirdTr.findElements(By.tagName("th"));
		
			List<String> jungdang = new ArrayList<>();
			List<String> hubo= new ArrayList<>();
			for (int i = 0; i < thElements.size()-1; i++) {
			    WebElement th = thElements.get(i);
			    //WebElement strongElement = th.findElement(By.tagName("strong"));
			
			     String[] strongTextParts = th.getText().split("\n");
			        String party = strongTextParts[0]; // 정당
			        String name = strongTextParts[1]; // 이름
			    jungdang.add(party);
		        hubo.add(name);
			}
			
			
			WebElement tbody = table.findElement(By.tagName("tbody"));
			int whenCount = 4;
			String kindSungu = "지방선거";
			String city = sidoName;

			String sungugoo = "";
			int posiblePeople = 0;
			int realPeople = 0;

			List<String> dpSu = new ArrayList<>();
			int muhyo = 0;
			int giveUp = 0 ; 
			List<GHVo> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int i = 2; i < rows.size(); i++) {
				
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().equals(" ") ) {
					sungugoo = "";
					posiblePeople = 0;
					realPeople = 0;
		
					dpSu = new ArrayList<>();
					muhyo = 0;
					giveUp = 0 ; 
					
				
				}else continue;
				
				 sungugoo = cells.get(0).getText();
				 posiblePeople = Integer.parseInt(deleteComma(cells.get(1).getText()));
				 realPeople = Integer.parseInt(deleteComma(cells.get(2).getText()));
				for (int j = 3; j < cells.size(); j++) {
					WebElement cell = cells.get(j);
					if(j<cells.size()-3) {
						dpSu.add(deleteComma(cell.getText()));
						continue;
					}
					if(j==cells.size()-2) {
						muhyo = Integer.parseInt(deleteComma(cell.getText()));
					}else if(j==cells.size()-1) {
						giveUp = Integer.parseInt(deleteComma(cell.getText()));
					}	
					
				}
					 fillEmptyValues(jungdang, hubo, dpSu);
					GHVo vo = GHVo.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)
							.tusu(realPeople)
							.jungdang(jungdang)
							.hubo(hubo)
							.dpSu(dpSu)
							.mutusu(muhyo)
							.gigwonsu(giveUp)
							.build();
					mainRepo.save(vo);
					//list.add(vo);
				
				}

			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	public void getDTData(String sidoCode, String sidoName) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCCP09");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType1")));
			menu.click();

			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20021219");

			// 'electionCode' 요소 대기
			WebElement selectBox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionCode")));
			// 'electionCode' 요소를 Select 객체로 변환
			Select select2 = new Select(selectBox2);
			Thread.sleep(200);
			// 'electionCode' 요소에 값을 입력
			select2.selectByValue("1");

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoCode);

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			// 'cont_table' 요소 대기 및 결과 반환
			WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table01")));
			Thread.sleep(200);
			
			WebElement thead = table.findElement(By.tagName("thead"));
			List<WebElement> thRows = thead.findElements(By.tagName("tr"));
			WebElement thirdTr = thRows.get(2);
			List<WebElement> thElements = thirdTr.findElements(By.tagName("th"));
		
			List<String> jungdang = new ArrayList<>();
			List<String> hubo= new ArrayList<>();
			for (int i = 0; i < thElements.size()-1; i++) {
			    WebElement th = thElements.get(i);
			    //WebElement strongElement = th.findElement(By.tagName("strong"));
			
			     String[] strongTextParts = th.getText().split("\n");
			        String party = strongTextParts[0]; // 정당
			        String name = strongTextParts[1]; // 이름
			    jungdang.add(party);
		        hubo.add(name);
			}
			
			
			WebElement tbody = table.findElement(By.tagName("tbody"));
			int whenCount = 16;
			String kindSungu = "대통령";
			String city = sidoName;

			String sungugoo = "";
			int posiblePeople = 0;
			int realPeople = 0;

			List<String> dpSu = new ArrayList<>();
			int muhyo = 0;
			int giveUp = 0 ; 
			List<GHVo> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int i = 2; i < rows.size(); i++) {
				
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().equals(" ") ) {
					sungugoo = "";
					posiblePeople = 0;
					realPeople = 0;
		
					dpSu = new ArrayList<>();
					muhyo = 0;
					giveUp = 0 ; 
					
				
				}else continue;
				
				 sungugoo = cells.get(0).getText();
				 posiblePeople = Integer.parseInt(deleteComma(cells.get(1).getText()));
				 realPeople = Integer.parseInt(deleteComma(cells.get(2).getText()));
				for (int j = 3; j < cells.size(); j++) {
					WebElement cell = cells.get(j);
					if(j<cells.size()-3) {
						dpSu.add(deleteComma(cell.getText()));
						continue;
					}
					if(j==cells.size()-2) {
						muhyo = Integer.parseInt(deleteComma(cell.getText()));
					}else if(j==cells.size()-1) {
						giveUp = Integer.parseInt(deleteComma(cell.getText()));
					}	
					
				}
					 fillEmptyValues(jungdang, hubo, dpSu);
					GHVo vo = GHVo.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)
							.tusu(realPeople)
							.jungdang(jungdang)
							.hubo(hubo)
							.dpSu(dpSu)
							.mutusu(muhyo)
							.gigwonsu(giveUp)
							.build();
					mainRepo.save(vo);
					//list.add(vo);
				
				}

			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	public void getBRData(String sidoCode, String sidoName) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCCP09");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType2")));
			menu.click();

			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20040415");

			// 'electionCode' 요소 대기
			WebElement selectBox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionCode")));
			// 'electionCode' 요소를 Select 객체로 변환
			Select select2 = new Select(selectBox2);
			Thread.sleep(200);
			// 'electionCode' 요소에 값을 입력
			select2.selectByValue("7");

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoCode);

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			// 'cont_table' 요소 대기 및 결과 반환
			WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table01")));
			Thread.sleep(200);
			
			WebElement thead = table.findElement(By.tagName("thead"));
			List<WebElement> thRows = thead.findElements(By.tagName("tr"));
			WebElement thirdTr = thRows.get(2);
			List<WebElement> thElements = thirdTr.findElements(By.tagName("th"));
		
			List<String> jungdang = new ArrayList<>();
			List<String> hubo= new ArrayList<>();
			for (int i = 0; i < thElements.size()-1; i++) {
			    WebElement th = thElements.get(i);
			    //WebElement strongElement = th.findElement(By.tagName("strong"));
			    String value = th.getText();
			    jungdang.add(value);
		        hubo.add(value);
			}
			
			
			WebElement tbody = table.findElement(By.tagName("tbody"));
			int whenCount = 17;
			String kindSungu = "비례의원";
			String city = sidoName;

			String sungugoo = "";
			int posiblePeople = 0;
			int realPeople = 0;

			List<String> dpSu = new ArrayList<>();
			int muhyo = 0;
			int giveUp = 0 ; 
			List<GHVo> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int i = 2; i < rows.size(); i++) {
				
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().equals(" ") ) {
					sungugoo = "";
					posiblePeople = 0;
					realPeople = 0;
		
					dpSu = new ArrayList<>();
					muhyo = 0;
					giveUp = 0 ; 
					
				
				}else continue;
				
				 sungugoo = cells.get(0).getText();
				 posiblePeople = Integer.parseInt(deleteComma(cells.get(1).getText()));
				 realPeople = Integer.parseInt(deleteComma(cells.get(2).getText()));
				for (int j = 3; j < cells.size(); j++) {
					WebElement cell = cells.get(j);
					if(j<cells.size()-3) {
						dpSu.add(deleteComma(cell.getText()));
						continue;
					}
					if(j==cells.size()-2) {
						muhyo = Integer.parseInt(deleteComma(cell.getText()));
					}else if(j==cells.size()-1) {
						giveUp = Integer.parseInt(deleteComma(cell.getText()));
					}	
					
				}
					 fillEmptyValues(jungdang, hubo, dpSu);
					GHVo vo = GHVo.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)
							.tusu(realPeople)
							.jungdang(jungdang)
							.hubo(hubo)
							.dpSu(dpSu)
							.mutusu(muhyo)
							.gigwonsu(giveUp)
							.build();
					mainRepo.save(vo);
					//list.add(vo);
				
				}

			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	
	public void getSJData(String sidoCode, String sidoName) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCAP01");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType4")));
			menu.click();

			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20140604");

	

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoCode);
			
			// 'electionCode' 요소 대기
			WebElement selectBox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dateCode")));
			// 'electionCode' 요소를 Select 객체로 변환
			Select select2 = new Select(selectBox2);
			Thread.sleep(200);
			// 'electionCode' 요소에 값을 입력
			select2.selectByValue("3");

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			// 'cont_table' 요소 대기 및 결과 반환
			WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table01")));
			Thread.sleep(200);
			

		
			
		
			
			
			WebElement tbody = table.findElement(By.tagName("tbody"));
			int whenCount = 6;
			String kindSungu = "지방선거";
			//String day = "1일차";
			String day = "2일차누계";
			String city = sidoName;
			List<String> time = new ArrayList<>();
			int posiblePeople = 0;
			String sungugoo = "";
		

			List<String> dpSu = new ArrayList<>();
	 
			List<SJVO> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int i = 2; i < rows.size(); i++) {
				
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().contains("%") ) {
					sungugoo = "";
		
					dpSu = new ArrayList<>();
					time = new ArrayList<>();			
				}else continue;
				
				 sungugoo = cells.get(0).getText();
				 posiblePeople = Integer.parseInt(deleteComma(cells.get(1).getText()));
				for (int j = 2; j < cells.size(); j++) {
					WebElement cell = cells.get(j);
				
						dpSu.add(deleteComma(cell.getText()));
				
					
				}
		
					SJVO vo = SJVO.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)						
							.dpSu(dpSu)	
							.일차(day)
							.build();
					sjRepo.save(vo);
					//list.add(vo);
				
				}

			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	public void getTPData(List<SidoVo> sidoList) {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("http://info.nec.go.kr/main/showDocument.xhtml?electionId=0000000000&topMenuId=VC&secondMenuId=VCVP01");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			


			// 'electionType2' 요소 대기 및 클릭
			WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionType2")));
			menu.click();
			for(int i = 0 ; i<sidoList.size() ;i++) {
			// 'electionName' 요소 대기
			WebElement selectBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("electionName")));
			// 'electionName' 요소를 Select 객체로 변환
			Select select1 = new Select(selectBox);
			// 'electionName' 요소에 값을 입력
			select1.selectByValue("20040415");

	

			// 'cityCode' 요소 대기
			WebElement selectBox3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cityCode")));
			// 'cityCode' 요소를 Select 객체로 변환
			Select select3 = new Select(selectBox3);
			Thread.sleep(200);
			// 'cityCode' 요소에 값을 입력
			select3.selectByValue(sidoList.get(i).getSidoCode());
			
			

			// 'searchBtn' 요소 대기 및 클릭
			WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", searchButton);

			
			WebElement contTable = driver.findElement(By.className("cont_table"));
			List<WebElement> tables = contTable.findElements(By.tagName("table"));
			// 두 번째 테이블 선택
			WebElement secondTable = tables.get(1);
			
			// 'cont_table' 요소 대기 및 결과 반환
		
			Thread.sleep(200);
			

		
			
		
			
			
			WebElement tbody = secondTable.findElement(By.tagName("tbody"));
			int whenCount = 17;
			String kindSungu = "국회의원";
	
			String city = sidoList.get(i).getSidoName();
	
			int posiblePeople = 0;
			String sungugoo = "";
		

			List<String> dpSu = new ArrayList<>();
	 
			List<SJVO> list = new ArrayList<>();

			int count  =0 ;
			List<WebElement> rows = tbody.findElements(By.tagName("tr"));
			for (int j = 2; j < rows.size(); j++) {
				
				WebElement row = rows.get(j);
				List<WebElement> cells = row.findElements(By.tagName("td"));
		
			
				if(!cells.get(0).getText().contains("%") ) {
					sungugoo = "";
		
					dpSu = new ArrayList<>();
							
				}else continue;
				
				 sungugoo = cells.get(0).getText();
				 String[] posPeo = cells.get(1).getText().split("\n");
				 posiblePeople = Integer.parseInt(deleteComma(posPeo[0]));
				for (int  z= 2; z < cells.size()-1; z++) {
					WebElement cell = cells.get(z);
			        String[] splitStr = cell.getText().split("\n");
						dpSu.add(deleteComma(splitStr[0]));
								
				}
		
					TPVO vo = TPVO.builder()
							.선거(kindSungu)
							.언제(whenCount)
							.sggName(sungugoo)
							.시도(city)
							.sunsu(posiblePeople)						
							.dpSu(dpSu)	

							.build();
					tpRepo.save(vo);
					//list.add(vo);
				
				}

			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
    
}