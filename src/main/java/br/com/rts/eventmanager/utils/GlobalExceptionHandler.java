package br.com.rts.eventmanager.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResourceFoundException(final NoResourceFoundException ex) {
        log.warn("Recurso estático não encontrado: {}", ex.getMessage());

        final ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", 404);
        modelAndView.addObject("error", "Não Encontrado");
        modelAndView.addObject("message", "O recurso estático solicitado não foi encontrado.");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(final Exception ex) throws Exception {
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
