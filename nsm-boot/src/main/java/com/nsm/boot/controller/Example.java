package com.nsm.boot.controller; /**
 * Created by nieshuming on 2018/9/17
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Example {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

}
