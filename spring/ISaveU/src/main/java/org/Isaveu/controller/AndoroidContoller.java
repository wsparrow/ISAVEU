package org.Isaveu.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.Isaveu.domain.LocationByFireExVO;
import org.Isaveu.domain.LocationByIssueVO;
import org.Isaveu.domain.TbHrVO;
import org.Isaveu.service.FireExService;
import org.Isaveu.service.HrService;
import org.Isaveu.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/Android/*")
public class AndoroidContoller {

	@Resource(name = "org.Isaveu.service.HrService")
	HrService hService;

	@Resource(name = "org.Isaveu.service.LocationService")
	LocationService lService;

	@Resource(name = "org.Isave.service.FireExService")
	FireExService fService;

	@ResponseBody
	@RequestMapping(value = "/Login.do")
	public Map<String, String> androidLogin(@ModelAttribute TbHrVO hrvo, @RequestParam("u_id") String id,
			@RequestParam("u_pw") String pw, @RequestParam(value = "u_instancekey", defaultValue = "") String fcm)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("Request /Android/Login.do");
		if ("".equals(id) || "".equals(pw)) {
			System.out.println("Android Login: " + id + " : Fail");
			map.put("access", "0");
			map.put("name", "0");
			map.put("profile", "0");
			map.put("email", "0");
			return map;
		} else {
			ArrayList<TbHrVO> list = new ArrayList<TbHrVO>();
			list = hService.getHrListId(id);
			if (list.size() != 0) {
				hrvo = list.get(0);
				System.out.println("Android Login: " + id + " : SUCCESS");
				if (id.equals(hrvo.getId()) && pw.equals(hrvo.getPw())) {
					if ("".equals(hrvo.getFcm())) {
						System.out.println(id + " : FCM UPDATE");
						System.out.println(fcm);
						hrvo.setFcm(fcm);
						hService.fcmUpdate(hrvo);
						map.put("access", "1");
						map.put("name", hrvo.getName().toString());
						map.put("profile", hrvo.getProfile().toString());
						map.put("email", hrvo.getEmail().toString());
						map.put("fcm", hrvo.getFcm().toString());
						return map;
					} else {
						if (fcm.equals(hrvo.getFcm())) {
							System.out.println(id + " :  FCM EQUALS");
							map.put("access", "1");
							map.put("name", hrvo.getName().toString());
							map.put("profile", hrvo.getProfile().toString());
							map.put("email", hrvo.getEmail().toString());
							map.put("fcm", hrvo.getFcm().toString());
							return map;
						} else {
							System.out.println(id + " : FCM UPDATE");
							System.out.println(fcm);
							hrvo.setFcm(fcm);
							hService.fcmUpdate(hrvo);
							map.put("access", "1");
							map.put("name", hrvo.getName().toString());
							map.put("profile", hrvo.getProfile().toString());
							map.put("email", hrvo.getEmail().toString());
							map.put("fcm", hrvo.getFcm().toString());
							return map;
						}
					}

				} else {
					System.out.println("Android Login: " + id + " : SUCCESS");
					map.put("access", "0");
					map.put("name", "0");
					map.put("profile", "0");
					map.put("email", "0");
					return map;
				}
			} else {
				System.out.println("Android Login: " + id + " : SUCCESS");
				map.put("access", "0");
				map.put("name", "0");
				map.put("profile", "0");
				map.put("email", "0");
				return map;
			}
		}
	}

	@ResponseBody
	@RequestMapping(value = "/Streaming.do")
	public Map<String, String> AndroidStreming(@RequestParam("act_st") String act_st) {
		System.out.println("Request /Android/Streaming.do");
		Map<String, String> map = new HashMap<String, String>();

		if (act_st.equals("1")) {
			map.put("streamingServer_url", "http://192.168.0.13:5001/stream/1/");
			map.put("streaming_access", "http://192.168.0.13:5000/video_stream");
		} else {
			map.put("streamingServer_url", "http://192.168.0.13:5001/stream/0/");
			map.put("streaming_access", "http://192.168.0.13:5000/video_stream");
		}
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/DisasterCheck.do")
	public Map<String, Object> AndroiDisasterCheck(@RequestParam("loc") String loc) {
		System.out.println("Request /Android//DisasterCheck.do");
		Map<String, Object> map = new HashMap<String, Object>();
		String count = "";
		try {
			count = lService.locationCount(loc);
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<LocationByIssueVO> list = new ArrayList<LocationByIssueVO>();

		int countInt = Integer.parseInt(count);
		try {
			list = new ArrayList<LocationByIssueVO>();
			for (int i = 0; i < countInt; i++) {
				list.addAll(lService.AndroidDisasterCheck(String.valueOf(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		map.put("floor", loc);
		map.put("list", list);
		System.out.println(map);
		return map;

	}

	@ResponseBody
	@RequestMapping(value = "/feRestart.do")
	public Map<String, String> AndroidfeRestart(@RequestParam("loc") String loc) {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("Request /Android/feRestart.do");
		String url = "http://192.168.0.61:5002/feRestart/";
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(url, String.class);
		System.out.println(result);

		try {
			fService.updatefireExStatus(loc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("access", "ok");
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/IamgeGet.do", method = RequestMethod.GET)
	private void AndoridIamgeGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("Request /Android/IamgeGet.do");
		String ImageId = request.getParameter("imageID");
		String path = "C:\\Users\\user\\Documents\\ISAVEU\\spring\\ISaveU\\src\\main\\resources\\static\\eventImage\\"
				+ ImageId;
		System.out.println(path);
		response.setContentType("image/png");
		response.setHeader("Content-Transfer-Encoding", "binary");

		File file = new File(path);
		FileInputStream input = new FileInputStream(file);
		OutputStream output = response.getOutputStream();
		byte[] buffer = new byte[8 * 1024];
		try {
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			output.flush();
			// System.out.println("Response 완료!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			input.close();
			output.close();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/locationFireEx.do")
	public Map<String, Object> locationFireEx(@ModelAttribute LocationByFireExVO location,
			@RequestParam("loc") String loc) throws Exception {
		System.out.println("Request /Android/locationFireEx.do");
		ArrayList<LocationByFireExVO> list = new ArrayList<LocationByFireExVO>();
		Map<String, Object> map = new HashMap<String, Object>();

		list = lService.locationByFireEx();
		map.put("floor", loc);
		map.put("list", list);

		return map;
	}

}
