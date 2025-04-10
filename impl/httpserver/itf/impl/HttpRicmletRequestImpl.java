package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	private Map<String, String> m_args = new HashMap<>();
	private Map<String, String> m_cookies = new HashMap<>();

	public Map<String, String> getM_cookies() {
		return m_cookies;
	}

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		parseArguments();

		String line;
		while ((line = br.readLine()) != null && !line.isEmpty()) {
			if (line.startsWith("Cookie: ")) {
				String cookiesLine = line.substring("Cookie: ".length());
				String[] cookies = cookiesLine.split(";");
				for (String cookie : cookies) {
					String[] kv = cookie.trim().split("=");
					if (kv.length == 2) {
						m_cookies.put(kv[0], kv[1]);
					}
				}
			}
		}

	}

	private void parseArguments() {
		if (m_ressname.contains("?")) {
			String[] parts = m_ressname.split("\\?", 2);
			m_ressname = parts[0]; // garder uniquement le chemin sans les arguments
			String query = parts[1];
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				String[] kv = pair.split("=");
				if (kv.length == 2) {
					m_args.put(kv[0], kv[1]);
				}
			}
		}
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArg(String name) {
		return m_args.get(name);
	}

	@Override
	public String getCookie(String name) {
		return m_cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		String path = this.getRessname();
		System.out.println("Trying to access file: " + path);
		
		if(path.startsWith("/ricmlets")) {
			try {
				String ressname = path.substring("/ricmlets/".length());
				String clsname = ressname.replace('/', '.');
				httpserver.itf.HttpRicmlet ricmlet = m_hs.getInstance(clsname);
				ricmlet.doGet(this, (HttpRicmletResponse) resp);
				System.out.println("File: " + path + "sent successfully");

			} catch (ClassNotFoundException  e) {
				e.printStackTrace();
				resp.setReplyError(404, "Rcimlet not found");
			}
			
		}

	}

}
