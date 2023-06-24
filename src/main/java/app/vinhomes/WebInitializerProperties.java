package app.vinhomes;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

@Component
public class WebInitializerProperties implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //super.onStartup(servletContext);
        System.out.println("setup servlet context");
        servletContext.addListener(new CustomerSessionListener());
    }
}
