package by.bsu.fpmi.chat;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Override;



@WebServlet (urlPatterns = {"/chat"},
            asyncSupported = true)
public class MyServlet extends HttpServlet {
    public static Logger logger = Logger.getLogger(MyServlet.class.getName());
    @Override
    public void init() throws ServletException {
        try {
            Functions.loadHistory();
            ConnectionManager.checkDB();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
           logger.error(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("status");
        if ("new".equals(data)) {
            try {
                String history = Functions.formResponse(DBChanges.selectAll(Functions.DB));
                PrintWriter out = response.getWriter();
                out.print(history);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            AsyncContext ac = request.startAsync();
            Functions.getRequests().put(Functions.addKey(),ac);
            ac.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent asyncEvent) throws IOException {
                }

                @Override
                public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                    Functions.deleteRequest(asyncEvent.getAsyncContext());
                    asyncEvent.getAsyncContext().complete();
                }

                @Override
                public void onError(AsyncEvent asyncEvent) throws IOException {
                }

                @Override
                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                }
            });

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Post request");
        try(BufferedReader br = request.getReader()) {
            String data = br.readLine();
            logger.info(data);
            try {
                JSONObject json = Functions.stringToJson(data);
                Message m = Functions.jsonToMessage(json);
                if ("new".equals(m.getStatus())) {
                    DBChanges.add(Functions.DB, m);
                    DBChanges.add(Functions.DBCHANGES, m);
                } else {
                    if ("edit".equals(m.getStatus())){
                        DBChanges.update(m);
                        DBChanges.add(Functions.DBCHANGES, m);
                    } else {
                        if ("delete".equals(m.getStatus())){
                            DBChanges.delete(Integer.parseInt(m.getMessageId()));
                            DBChanges.add(Functions.DBCHANGES, m);
                        }
                    }
                }
                XMLStorage.addData(m);
                Functions.startResponse();
                DBChanges.deleteAll(Functions.DBCHANGES);
            } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(e);
            }
        }
    }
    //Not used because of js strange behaviour
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Put request");
        try(BufferedReader br = request.getReader()) {
            String data = br.readLine();
            logger.info(data);
            try {
                JSONObject json = Functions.stringToJson(data);
                Message m = Functions.jsonToMessage(json);
                DBChanges.update(m);
                DBChanges.add(Functions.DBCHANGES, m);
                Functions.startResponse();
                MessageStorage.getStorage().clear();
            } catch (ParseException e){//| ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(e);
            }
        }
    }
    //Not used because of js strange behaviour
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Delete request");
        try(BufferedReader br = request.getReader()) {
            String data = br.readLine();
            logger.info(data);
            try {
                JSONObject json = Functions.stringToJson(data);
                Message m = Functions.jsonToMessage(json);
                DBChanges.delete(Integer.parseInt(m.getMessageId()));
                DBChanges.add(Functions.DBCHANGES, m);
                Functions.startResponse();
                MessageStorage.getStorage().clear();
            } catch (ParseException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(e);
            }
        }
    }

    @Override
    public void destroy() {
        DBChanges.deleteAll(Functions.DBCHANGES);
        super.destroy();
    }
}