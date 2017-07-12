package org.gottfaust.WWDC.controller;

import org.gottfaust.WWDC.controller.interfaces.IWWDCController;
import org.gottfaust.WWDC.controller.services.CalculationService;
import org.gottfaust.WWDC.model.builders.CalculationResponseBuilder;
import org.gottfaust.WWDC.model.exceptions.WWDCAuthException;
import org.gottfaust.WWDC.model.factories.ResponseFactory;
import org.gottfaust.WWDC.model.submissions.CalculationSubmission;
import org.jboss.logging.Logger;
import org.gottfaust.WWDC.model.exceptions.WWDCDatabaseException;
import org.gottfaust.WWDC.model.exceptions.WWDCSubmissionException;
import org.gottfaust.WWDC.model.interfaces.IWWDCResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CalculationController implements IWWDCController {

    /** The Queue Service **/
    private CalculationService service = new CalculationService();

    /** The log4j Logger **/
    private static final Logger LOGGER = Logger.getLogger(CalculationController.class);

    @RequestMapping(value = "/api/calculate", method = RequestMethod.POST)
    public ResponseEntity getQueue(HttpServletRequest request,
                                   @RequestBody(required = false) CalculationSubmission submission)
            throws WWDCAuthException,
            WWDCSubmissionException,
            WWDCDatabaseException
    {
        //The IHateos self link
        String self = "/api/queue";

        //Validate the submission
        if(submission != null && !submission.validate()){
            throw new WWDCSubmissionException(self, "The submission was invalid.");
        }

        //Setup the builder
        CalculationResponseBuilder builder = new CalculationResponseBuilder();
        builder.self = self;

        //Setup the final response
        IWWDCResponse response = ResponseFactory.buildResponse(builder);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
