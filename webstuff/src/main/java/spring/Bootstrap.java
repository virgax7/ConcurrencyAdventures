package spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Configuration
@ComponentScan(basePackages = "spring")
public class Bootstrap implements ApplicationContextAware,WebApplicationInitializer{

    private ApplicationContext applicationContext;

    public Bootstrap() {
        System.out.println("Bootstrap initialized by thread " + Thread.currentThread().getName());
        System.out.println(this.applicationContext);
    }
    @Override
    public void onStartup(final ServletContext container) throws ServletException {
        System.out.println("Bootstrap.onStartup() from" + Thread.currentThread().getName());
        System.out.println(applicationContext);
        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(Bootstrap.class);
        container.addListener(new ContextLoaderListener(rootContext));

        final AnnotationConfigWebApplicationContext servletContext = new AnnotationConfigWebApplicationContext();
        servletContext.register(Bootstrap.class);
        ServletRegistration.Dynamic dispatcher = container.addServlet("springDispatcher", new DispatcherServlet(servletContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/spring");
    }

    @Bean
    public String foo() {
        System.out.println("Bootstrap.foo() called by thread " + Thread.currentThread().getName());
        System.out.println(applicationContext);
        return "foo";
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

