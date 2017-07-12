package org.gottfaust.WWDC.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.gottfaust.WWDC.controller.interfaces.IHateos;
import org.gottfaust.WWDC.model.DataJSONError;
import org.gottfaust.WWDC.model.UnknownJSONError;
import org.gottfaust.WWDC.model.builders.DataJSONErrorBuilder;
import org.gottfaust.WWDC.model.builders.UnknownJSONErrorBuilder;
import org.gottfaust.WWDC.model.enums.JSONErrorType;
import org.gottfaust.WWDC.model.exceptions.WWDCAuthException;
import org.gottfaust.WWDC.model.exceptions.WWDCDatabaseException;
import org.gottfaust.WWDC.model.exceptions.WWDCSubmissionException;
import org.gottfaust.WWDC.model.factories.JSONErrorFactory;
import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

import static org.gottfaust.WWDC.constants.Constants.REALLY_REALLY_BAD;


@ControllerAdvice
public class ErrorAdvisor {

    /** The log4j Logger **/
    private static final Logger LOGGER = Logger.getLogger(ErrorAdvisor.class);

    /**
     * This is the database exception handler, any database exceptions will end up here
     * @param ex Exception
     * @return ResponseEntity with IHateos
     */
    @ExceptionHandler(WWDCDatabaseException.class)
    ResponseEntity<IHateos> handleDatabaseException(WWDCDatabaseException ex) {

        //Log the error
        LOGGER.fatal("DATABASE EXCEPTION", ExceptionUtils.getRootCause(ex));

        //Setup the json error return and return it
        String title = "Database Error";
        HashMap links = new HashMap();
        links.put("self", ex.getSelf());
        DataJSONErrorBuilder builder = new DataJSONErrorBuilder(ex.getMessage(), title, links);
        DataJSONError error = (DataJSONError) JSONErrorFactory.buildJSONError(builder, JSONErrorType.DATASOURCE);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This is the submission exception handler, any submission exceptions will end up here
     * @param ex Exception
     * @return ResponseEntity with IHateos
     */
    @ExceptionHandler(WWDCSubmissionException.class)
    ResponseEntity<IHateos> handleSubmissionException(WWDCSubmissionException ex) {

        //Log the error
        LOGGER.fatal("SUBMISSION EXCEPTION", ExceptionUtils.getRootCause(ex));

        //Setup the json error return and return it
        String title = "Submission Error";
        HashMap links = new HashMap();
        links.put("self", ex.getSelf());
        DataJSONErrorBuilder builder = new DataJSONErrorBuilder(ex.getMessage(), title, links);
        DataJSONError error = (DataJSONError) JSONErrorFactory.buildJSONError(builder, JSONErrorType.DATA);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This is the authorization exception handler, any authorization exceptions will end up here
     * @param ex Exception
     * @return ResponseEntity with IHateos
     */
    @ExceptionHandler(WWDCAuthException.class)
    ResponseEntity<IHateos> handleAuthException(WWDCAuthException ex) {

        //Log the error
        LOGGER.fatal("AUTH EXCEPTION", ExceptionUtils.getRootCause(ex));

        //Setup the json error return and return it
        String title = "Authentication Error";
        HashMap links = new HashMap();
        links.put("self", ex.getSelf());
        DataJSONErrorBuilder builder = new DataJSONErrorBuilder(ex.getMessage(), title, links);
        DataJSONError error = (DataJSONError) JSONErrorFactory.buildJSONError(builder, JSONErrorType.DATA);
        return new ResponseEntity(error, HttpStatus.FORBIDDEN);
    }

    /**
     * This is the default exception handler, any unknown exceptions will end up here
     * @param ex Exception
     * @return ResponseEntity with IHateos
     */
    @ExceptionHandler
    ResponseEntity<IHateos> handleUnknownException(Exception ex) {

        //Log the error
        LOGGER.fatal(REALLY_REALLY_BAD, ex);

        //Setup the json error return and return it
        String title = "Unexpected Error";
        String description = "An unexpected error has occurred.";
        HashMap links = new HashMap();
        UnknownJSONErrorBuilder builder = new UnknownJSONErrorBuilder(description, title, links);
        UnknownJSONError error = (UnknownJSONError) JSONErrorFactory.buildJSONError(builder, JSONErrorType.UNKNOWN);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
