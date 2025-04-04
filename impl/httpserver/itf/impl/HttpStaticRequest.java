package httpserver.itf.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.File;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	public void process(HttpResponse resp) throws Exception {
	// TO COMPLETE
		String resourcePath = this.getRessname();
		/*if (resourcePath.startsWith("/")) {
		    resourcePath = resourcePath.substring(1);
		}*/
		File file = new File(m_hs.getFolder(), resourcePath);
		System.out.println("Trying to access file: " + file.getAbsolutePath());


		if(file.isDirectory()) {
			file = new File(file, DEFAULT_FILE);
		}

		if (!file.exists() || !file.isFile()) {
			resp.setReplyError(404, "File Not Found");
			return;
		}
		
		resp.setReplyOk();
		resp.setContentLength((int) file.length());
		resp.setContentType(HttpRequest.getContentType(file.getName()));


		PrintStream ps = resp.beginBody();

		
		try(FileInputStream fileStream = new FileInputStream(file);) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead=fileStream.read(buffer) )!= -1) {
                ps.write(buffer, 0, bytesRead);
            }
            
            System.out.println("File " + file.getName() + " sent sucessfully");
	}

		
	}

}
