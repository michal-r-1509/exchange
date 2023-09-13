package com.michal.converter.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping(path = "/error")
    public String handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : 0;

        if (statusCode == HttpStatus.NOT_FOUND.value()){
            return "error-404.html";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "error-500.html";
        }

        return "error.html";
    }
}
