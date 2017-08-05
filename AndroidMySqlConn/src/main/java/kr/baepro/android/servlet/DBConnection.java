package kr.baepro.android.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kr.baepro.util.RequestUtil;

//@WebServlet("/DBConnection")
public class DBConnection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	JSONObject json = new JSONObject();
	RequestUtil requestUtil = new RequestUtil();
    
    public DBConnection() {
        super();
    }

	protected void doGetLogin(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		System.out.println("doGetLogin(); method is called.");

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String id = request.getParameter("id");
		String pwd = request.getParameter("pwd");
		System.out.println("request id : " + id);
		System.out.println("request pwd : " + pwd);
		try {
			readUser(id, pwd);
			//System.out.println(json.getJSONArray("rows").length());
			//System.out.println("json.toStirng() : " + json.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().write(json.toString());
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		System.out.println("doGet() method is called.");
		doGetList(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
	
		//doGet(request, response);
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		System.out.println("doPost() method is called.");
		
		String mode = request.getParameter("mode");
		
		switch(mode) {
		case "LOGIN":
			System.out.println("mode : " + mode);
			doGetLogin(request, response);
			break;
			
		case "REGISTER":
			System.out.println("mode : " + mode);
			doGetRegister(request, response);
			break;
			
//		case "LIST":
//			System.out.println("mode : " + mode);
//			doGetList(request, response);
//			break;
			
		case "DELETE":
			System.out.println("mode : " + mode);
			doGetDelete(request, response);
			break;
			
		default :	
			System.out.println("default");
			break;
		}
		
	}
	
	protected void doGetDelete(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		System.out.println("doGetDelete() method is called.");
		
		String id = request.getParameter("id");
		System.out.println("id : " + id);
		
		try {
			deleteUser(id);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().write(json.toString());
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGetRegister(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException  {

		System.out.println("doGetRegister() method is called.");
		
		String id = request.getParameter("id");
		String pwd = request.getParameter("pwd");
		String name = request.getParameter("name");
		String age = request.getParameter("age");
		//System.out.println("id=" + id + ", pwd=" + pwd + ", name=" + name + ", age=" + age);

		try {
			createUser(id, pwd, name, age);
			//System.out.println("json.toStirng() : " + json.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().write(json.toString());
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	protected void doGetList(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException  {
		
		System.out.println("doGetList() method is called.");
		
		try {
			userList();
			//System.out.println(json.getJSONArray("rows").length());
			System.out.println("json.toStirng() : " + json.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().write(json.toString());
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private JSONObject createUser(String id, String pwd, String name, String age) 
			throws ClassNotFoundException, SQLException {
		
		System.out.println("createUser() method is called.");
		
		Connection conn = getConnection();
		PreparedStatement ps = null;
		int rs = 0;
		Boolean result = true;
		
		String sql = "insert into user (id, pwd, name, age) values (?, ?, ?, ?);";
		try {
			//System.out.println("createUser() : mysql connected.");
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, pwd);
			ps.setString(3, name);
			ps.setString(4, age);
			rs = ps.executeUpdate();
			
			json.put("success", result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
				//System.out.println("createUser() : mysql closed.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	private JSONObject userList() throws ClassNotFoundException, SQLException {
		
		System.out.println("userList() method is called.");
		
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean result = null;
		
		String sql = "select id, pwd, name, age from user;";
		try {
			//System.out.println("userList() : mysql connected.");
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			List<Map<String, String>> list = new ArrayList();
			Map<String, String> map = null;
			
			int i = 0;
			while (rs.next()) {
				map = new HashMap();
				map.put("id", rs.getString("id"));
				map.put("pwd", rs.getString("pwd"));
				map.put("name", rs.getString("name"));
				map.put("age", rs.getString("age"));

				list.add(map);
				//System.out.println("list.size : " + list.size());
				
				json.put("success", true);
				json.put("rows", list);
				//System.out.println(json.toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
				rs.close();
				//System.out.println("userList() : mysql closed.");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json;
		
	}
	
	private JSONObject readUser(String id, String pwd) throws ClassNotFoundException, SQLException {

		System.out.println("readUser() method is called.");
		
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean result = null;
		
		String sql = "select * from user where id=? and pwd=?";
		try {
			//System.out.println("readUser() : mysql connected.");
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, pwd);
			rs = ps.executeQuery();
			
			List<Map<String, String>> list = new ArrayList();
			Map<String, String> map = null;
			
			int i = 0;
			while (rs.next()) {
				map = new HashMap();
				map.put("id", rs.getString("id"));
				map.put("pwd", rs.getString("pwd"));

				list.add(map);
				//System.out.println("list.size : " + list.size());
			}
			if(list.size()!=0) {
				result = true;
			} else {
				result = false;
			}
			json.put("success", result);
			json.put("rows", list);
			//System.out.println(list);//[{id=baepro, pwd=1234}]
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
				rs.close();
				//System.out.println("readUser() : mysql closed.");
				
				//json.put("success", true);
				//System.out.println(json.toString());//{"success":true,"rows":[{"id":"baepro","pwd":"1234"}]}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	private JSONObject deleteUser(String id) throws ClassNotFoundException, SQLException {

		System.out.println("deleteUser() method is called.");
		
		Connection conn = getConnection();
		PreparedStatement ps = null;
		int rs = 0;
		
		String sql = "delete from user where id=?";
		try {
			System.out.println("deleteUser() : mysql connected.");
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
				System.out.println("deleteUser() : mysql closed.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return json;
		
	}
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "9967015");
		
		return c;
	}

}
