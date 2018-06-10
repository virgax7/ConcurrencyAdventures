package spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/spring")
public class FooController {

    public FooController() {
        System.out.println("FooController() from " + Thread.currentThread().getName());
    }

    @GetMapping
    public String getFoo() {
        return "Foo";
    }
}
