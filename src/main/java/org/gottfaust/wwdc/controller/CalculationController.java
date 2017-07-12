package org.gottfaust.wwdc.controller;

import org.gottfaust.wwdc.controller.interfaces.IwwdcController;
import org.gottfaust.wwdc.controller.services.CalculationService;
import org.gottfaust.wwdc.model.builders.CalculationResponseBuilder;
import org.gottfaust.wwdc.model.exceptions.wwdcAuthException;
import org.gottfaust.wwdc.model.factories.ResponseFactory;
import org.gottfaust.wwdc.model.submissions.CalculationSubmission;
import org.jboss.logging.Logger;
import org.gottfaust.wwdc.model.exceptions.wwdcDatabaseException;
import org.gottfaust.wwdc.model.exceptions.wwdcSubmissionException;
import org.gottfaust.wwdc.model.interfaces.IwwdcResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CalculationController implements IwwdcController {

    /** The Queue Service **/
    private CalculationService service = new CalculationService();

    /** The log4j Logger **/
    private static final Logger LOGGER = Logger.getLogger(CalculationController.class);

    @RequestMapping(value = "/api/calculate", method = RequestMethod.POST)
    public ResponseEntity getQueue(HttpServletRequest request,
                                   @RequestBody(required = false) CalculationSubmission submission)
            throws wwdcAuthException,
            wwdcSubmissionException,
            wwdcDatabaseException
    {
        //The IHateos self link
        String self = "/api/queue";

        //Validate the submission
        if(submission != null && !submission.validate()){
            throw new wwdcSubmissionException(self, "The submission was invalid.");
        }

        //Setup the builder
        CalculationResponseBuilder builder = new CalculationResponseBuilder();
        builder.self = self;

        //Setup the final response
        IwwdcResponse response = ResponseFactory.buildResponse(builder);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
