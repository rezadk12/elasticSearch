import basic.HtmlPage;
import operation.ElasticOperation;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {

    private ElasticOperation elasticOperation;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("username");
        long startTime = System.currentTimeMillis();
        List<HtmlPage> result=elasticOperation.searchQuery(query);
        long endTime = System.currentTimeMillis();
        request.setAttribute("result",result);
        request.setAttribute("time",endTime-startTime);
        RequestDispatcher disp = request.getRequestDispatcher("result.jsp");
        disp.forward(request, response);


    }
    public void init()
    {
         elasticOperation=new ElasticOperation();
        elasticOperation.makeConnection();
        System.out.println("create!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    public void destroy()  {
        try {
            elasticOperation.closeConnection();
            System.out.println("destroy!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }catch (Exception e){
            e.printStackTrace();
        }
       }
}