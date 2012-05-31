package gr.forthnet.nms.svc.rrd.remote.util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@WebServlet(name = "GraphsTest", urlPatterns = {"/GraphsTest"})
public class GraphsTest extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// then get the writer and write the response data
		PrintWriter out = response.getWriter();

		StringBuilder body = new StringBuilder();
		
		body.append("<html>");
		body.append("<body>");

		for (int i = 21124500; i <= 21124700; i++) {
			body.append(String.format("<img src='/svc_rrd_remote/rest/%d/fetchGraph?group=bw&timespan=0&titleX=Bandwidth&titleY=bps'>", i));
		}

		body.append("</body>");
		body.append("</html>");

		out.println(body);
		out.close();
	}
}
