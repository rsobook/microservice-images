package si.fri.rsobook.images.api.servlet;


import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;
import si.fri.rsobook.images.ImageBean;
import si.fri.rsobook.images.exception.UploadException;
import si.fri.rsobook.images.models.Image;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet( name = "ImageServlet", urlPatterns = {"/api/v1/images", "/api/v1/images/*"})
@RequestScoped
@MultipartConfig
public class ImagesServlet extends HttpServlet {

    private Pattern regExIdPattern = Pattern.compile("/api/v1/images/(.*)");
    private ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private ImageBean imageBean;

    @Inject
    @Metric(name = "imagesUploadedCounter")
    private Counter imagesUploadedCounter;

    @Inject
    @Metric(name = "imagesFailedCounter")
    private Counter imagesFailedCounter;


    @Metered(name = "imageUpload")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Image img = imageBean.createImage(req.getPart("image"));
            System.out.println(img);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", "/api/v1/images/" + img.getId());
            resp.setHeader("Url", img.getUrl());
            resp.getWriter().println("Image successfully uploaded");
            imagesUploadedCounter.inc();
        } catch (UploadException e) {
            e.printStackTrace();
            resp.setStatus(e.errorCode);
            resp.getWriter().println("ERROR: " + e.getMessage());
            imagesFailedCounter.inc();
        }
        resp.flushBuffer();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String id = getIdFromUri(req.getRequestURI());

        if (id != null && id.length() > 0) {
            // api/v1/images/{id}
            System.out.println("requested image " + id);
            Image img = imageBean.getImage(id);
            if (img != null) {
                String json = objectMapper.writeValueAsString(img);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("{'error': 404}");
            }

        } else {
            String json = objectMapper.writeValueAsString(imageBean.getAllImages());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(json);
        }
        resp.flushBuffer();
    }

    private String getIdFromUri(String uri) {
        String id = null;
        if (uri != null) {
            Matcher regExMatcher = regExIdPattern.matcher(uri);
            if (regExMatcher.find())
                id = regExMatcher.group(1);
        }

        return id;
    }
}