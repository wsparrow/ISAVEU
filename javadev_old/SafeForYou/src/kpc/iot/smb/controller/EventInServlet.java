package kpc.iot.smb.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import kpc.iot.smb.data.dao.ActionDAO;
import kpc.iot.smb.data.dao.EventDAO;
import kpc.iot.smb.data.dao.HrDAO;
import kpc.iot.smb.data.vo.TbActionIdVO;
import kpc.iot.smb.data.vo.TbEventVO;
import kpc.iot.smb.data.vo.TbHrVO;
import kpc.iot.smb.fcm.Data;
import kpc.iot.smb.fcm.FCMData;
import kpc.iot.smb.fcm.FCMDataTo;
import kpc.iot.smb.util.Action;

public class EventInServlet extends Action{
	String issue;
	String temp;
	String smoke;
	String gyro;
	String fire;

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/plain;charset=utf-8");
		issue = request.getParameter("issue");
		String module_id = request.getParameter("module_id");
		temp = request.getParameter("temp");
		smoke = request.getParameter("smoke");
		gyro = request.getParameter("gyro");
		fire = request.getParameter("fire");
//		String reqContentType = request.getContentType();
		//ISSUE Process
		// 0 -> DB, 1 -> Rasp,DB, 2->Rasp,DB, 3->Rasp,DB,

		switch (issue) {
		case "1":
			System.out.println("화재경보");
//			System.out.println( "reqContentType : " + reqContentType );
			imageGet(issue);
			break;
		case "2":
			System.out.println("지진경보");
//			System.out.println( "reqContentType : " + reqContentType );
			imageGet(issue);
			break;
		case "3":
			System.out.println("지진 + 화재경보");
//			System.out.println( "reqContentType : " + reqContentType );
			imageGet(issue);
			break;
		default:
			EventDAO dao = new EventDAO();
			TbEventVO vo = new TbEventVO();
			vo.setModule_id(module_id);
			vo.setTemp(Float.parseFloat(temp));
			vo.setSmoke(Float.parseFloat(smoke));
			vo.setGyro(Float.parseFloat(gyro));
			vo.setFire(Float.parseFloat(fire));
			vo.setIssue(issue);
			dao.insertEvent(vo);
			System.out.println("InsertEvent Succes");
			break;
		}
	}
	public void imageGet(String issue){
		String url;
		switch (issue) {
		case "1":
			try {
				url = "http://192.168.0.13:5001/cam/1";
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				int responseCode = con.getResponseCode();
				InputStream input = con.getInputStream();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				transData(input);
				break;
			}catch (IOException e) {
				e.printStackTrace();
			}

		case "2":
			try {
				url = "http://192.168.0.13:5001/cam/2";
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				int responseCode = con.getResponseCode();
				InputStream input = con.getInputStream();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				transData(input);
				break;
			}catch (IOException e) {
				e.printStackTrace();
			}
		case "3":
			try {
				url = "http://192.168.0.13:5001/cam/3";
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				int responseCode = con.getResponseCode();
				InputStream input = con.getInputStream();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				transData(input);
				break;
			}catch (IOException e) {
				e.printStackTrace();
			}
		default:
			break;
		}
	}
	
	public void transData(InputStream input) {
		// data transform
		byte[] buffer = new byte[8 * 1024];
		try {
			Date date = new Date();
			SimpleDateFormat transFomat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String fileName = transFomat.format(date);
			String serverName = "C:\\workspace\\SaveForYou\\javadev\\SafeForYou\\WebContent\\img\\Event\\";
			String fileExtension = ".png";
			String androidPass = "http://192.168.0.35:8088/SafeForYou/AndoridIamgeGet.do?imageID=";
		
			String DbName = serverName + fileName + fileExtension;

			OutputStream output = new FileOutputStream(DbName);
			try {
				int bytesRead;
				while ((bytesRead = input.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
				System.out.println("Transform 완료!");
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				output.close();
				ActionDAO dao = new ActionDAO();
				TbActionIdVO vo = new TbActionIdVO();
				vo.setUrl(DbName);
				dao.insertPicture(vo);
				System.out.println(androidPass + fileName + fileExtension);
				androidSend(androidPass + fileName + fileExtension);
				
				
			} 
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					input.close();
			} 	catch (IOException e) {
					e.printStackTrace();
			}
		} 	
	}
	
	public void androidSend(String fileName) {
		// TODO Auto-generated constructor stub
//				String url = "https://fcm.googleapis.com/fcm/send"; 
//				FCMDataTo fcmData = new FCMDataTo();
//				Data data = new Data();
//				data.setTitle("[I Save You]긴급상황 발생");
//				data.setContent_1(fileName);
//				data.setContent_2("http://192.168.0.13:5001/stream/1");
//				data.setContent_3("http://192.168.0.13:5000/video_stream");
//				fcmData.setData(data);
//				fcmData.setTo("foSJVNORz8Y:APA91bEMxsYGGEEGlqnxPa3Gh3OB25evSPs5nR5yfbmxvEBbZvMn4aoo3L0Cn78bImFNVFSCEchn60Ii_-HQVjUapqkAeHeNo_roY4yUVeUgHIH2V20SaSdo3nFcQerbyrfjXPrxpImX");
				
				// DB에 전체 SELECT
				ArrayList<TbHrVO> arrayList = new ArrayList<TbHrVO>();
				TbHrVO vo = new TbHrVO();
				HrDAO dao = new HrDAO();
				arrayList = dao.getHrListAll();
				ArrayList<String> fcmList = new ArrayList<String>();
				for (TbHrVO tbHrVO : arrayList) {
					fcmList.add(tbHrVO.getFcm());
				}
				Gson gson = new Gson();
				JsonElement reglist = gson.toJsonTree(fcmList);
				String url = "https://fcm.googleapis.com/fcm/send"; 
				FCMData fcmData = new FCMData();
				Data data = new Data();
				data.setTitle("[I Save You]긴급상황 발생");
				data.setContent_1(fileName);
				fcmData.setData(data);
				fcmData.setRegistration_ids(reglist);
				
				String params = gson.toJson(fcmData);
				System.out.println(params);
				try {
					String returnData = sendPost(url, params);
					System.out.println(returnData);
				}catch(Exception e) {
					System.out.println("e : " + e);
				}		
			}
			
			public String sendPost(String url, String parameters) throws Exception { 
		        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		                public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}
		                public void checkClientTrusted(X509Certificate[] certs, String authType){}
		                public void checkServerTrusted(X509Certificate[] certs, String authType){}
		        }};
		        SSLContext sc = SSLContext.getInstance("TLS");
		        sc.init(null, trustAllCerts, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		   
		       URL obj = new URL(url);
		       HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		   
		       //reuqest header
		       con.setRequestMethod("POST");
		       con.setRequestProperty("Content-Type", "application/json");
		       con.setRequestProperty("Authorization", "key=AAAA91-0IQE:APA91bEvPIXCvITxVpcVaxysasJzU4wjuTNT29zkgmRv6ayxLe0U1iIgO0zIvImluA4_5AczoDfZrlFZluTuVBqFM_JBvyjqkH6R9k2bBoMSQaNOPlTOVnjHYTFwjSjMuVt0-nusaVRJ");
		       String urlParameters = parameters;
		   
		       //post request
		       con.setDoOutput(true);
		       DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		       wr.write(urlParameters.getBytes("UTF-8"));
		       wr.flush();
		       wr.close();

		       int responseCode = con.getResponseCode();     
		       System.out.println("Post parameters : " + urlParameters);
		       System.out.println("Response Code : " + responseCode);
		   
		       StringBuffer response = new StringBuffer();
		   
		       if(responseCode == 200){   
		              BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
		              String inputLine;
		              while ((inputLine = in.readLine()) != null) {
		                     response.append(inputLine);
		              }
		              in.close();   
		       }
		       //result
		       System.out.println(response.toString());
		       return response.toString();
		}
			
			@WebServlet("/Dispatcher")
			public class Dispatcher extends HttpServlet {
				private static final long serialVersionUID = 1L;
			       
			    public Dispatcher() {
			        super();
			    }

				protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
					process(req, resp);
				}

				protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
					process(req, resp);
				}
				
				protected void process(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException {
					//이번에는 이전 예제와는 다르게 Ajax요청이 오면 응답해줄꺼다.
					//이전에는 그냥 내가 원하는 페이지로 json을 가져가는 거였다면?
					//지금은 요청한 놈한테 return만 해주면 되기 때문에
					//PrintWriter out = resp.getWriter();
					//이걸 사용하면 된다.
					
					//주소요청 http://localhost:8000/JsonAjax/Dispatcher
					//Get방식
					//process()함수 호출
					//JSON만들기
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("temp",temp);
					jsonObj.put("smoke",smoke);
					jsonObj.put("fire",fire);
					jsonObj.put("gyro",gyro);
					jsonObj.put("msg", "success");
					
					PrintWriter out = resp.getWriter();
					out.print(jsonObj);
					System.out.println(out);
				}

			}
}
