package br.com.rts.eventmanager.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(final Exception ex) {
        if (ex instanceof AccessDeniedException) {
            throw (AccessDeniedException) ex;
        }

        log.error("Erro não tratado no sistema: ", ex);

        final ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", 500);
        modelAndView.addObject("error", "Erro Interno do Servidor");
        modelAndView.addObject("message", "Ocorreu um erro inesperado no sistema. Por favor, tente novamente mais tarde.");
        return modelAndView;
    }
}
