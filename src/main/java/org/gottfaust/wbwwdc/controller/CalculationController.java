package org.gottfaust.wbwwdc.controller;

import org.gottfaust.wbwwdc.controller.interfaces.IWBWWDCController;
import org.gottfaust.wbwwdc.controller.services.CalculationService;
import org.gottfaust.wbwwdc.model.builders.CalculationResponseBuilder;
import org.gottfaust.wbwwdc.model.exceptions.WBWWDCAuthException;
import org.gottfaust.wbwwdc.model.factories.ResponseFactory;
import org.gottfaust.wbwwdc.model.submissions.CalculationSubmission;
import org.jboss.logging.Logger;
import org.gottfaust.wbwwdc.model.exceptions.WBWWDCDatabaseException;
import org.gottfaust.wbwwdc.model.exceptions.WBWWDCSubmissionException;
import org.gottfaust.wbwwdc.model.interfaces.IWBWWDCResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CalculationController implements IWBWWDCController {

    /** The Queue Service **/
    private CalculationService service = new CalculationService();

    /** The log4j Logger **/
    private static final Logger LOGGER = Logger.getLogger(CalculationController.class);

    @RequestMapping(value = "/api/calculate", method = RequestMethod.POST)
    public ResponseEntity getQueue(HttpServletRequest request,
                                   @RequestBody(required = false) CalculationSubmission submission)
            throws WBWWDCAuthException,
            WBWWDCSubmissionException,
            WBWWDCDatabaseException
    {
        //The IHateos self link
        String self = "/api/queue";

        //Validate the submission
        if(submission != null && !submission.validate()){
            throw new WBWWDCSubmissionException(self, "The submission was invalid.");
        }

        //Setup the builder
        CalculationResponseBuilder builder = new CalculationResponseBuilder();
        builder.self = self;

        //Setup the final response
        IWBWWDCResponse response = ResponseFactory.buildResponse(builder);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
