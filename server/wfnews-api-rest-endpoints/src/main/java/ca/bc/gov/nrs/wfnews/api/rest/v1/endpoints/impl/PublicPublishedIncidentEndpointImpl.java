package ca.bc.gov.nrs.wfnews.api.rest.v1.endpoints.impl;

import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.nrs.common.service.ConflictException;
import ca.bc.gov.nrs.common.service.ForbiddenException;
import ca.bc.gov.nrs.common.service.NotFoundException;
import ca.bc.gov.nrs.wfnews.api.rest.v1.endpoints.PublicPublishedIncidentEndpoint;
import ca.bc.gov.nrs.wfnews.api.rest.v1.parameters.PagingQueryParameters;
import ca.bc.gov.nrs.wfnews.api.rest.v1.parameters.validation.ParameterValidator;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.PublishedIncidentListResource;
import ca.bc.gov.nrs.wfnews.service.api.v1.IncidentsService;
import ca.bc.gov.nrs.wfone.common.model.Message;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.BaseEndpointsImpl;

public class PublicPublishedIncidentEndpointImpl extends BaseEndpointsImpl implements PublicPublishedIncidentEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(PublicPublishedIncidentEndpointImpl.class);
	
	@Autowired
	private IncidentsService incidentsService;
	
	@Autowired
	private ParameterValidator parameterValidator;
	
	@Override
	public Response getPublishedIncidentList(String pageNumber, String pageRowCount) throws NotFoundException, ForbiddenException, ConflictException {
		Response response = null;
		
		try {
			PagingQueryParameters parameters = new PagingQueryParameters();
			
			Integer pageNum = null;
			Integer rowCount = null;
			
			if (pageNumber!=null)pageNum = Integer.parseInt(pageNumber);
			if (pageRowCount!=null)rowCount = Integer.parseInt(pageRowCount);

			parameters.setPageNumber(pageNumber);
			parameters.setPageRowCount(pageRowCount);
			
			List<Message> validationMessages = this.parameterValidator.validatePagingQueryParameters(parameters);

			if (!validationMessages.isEmpty()) {
				response = Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
			}else {
				PublishedIncidentListResource results = incidentsService.getPublishedIncidentList(pageNum, rowCount, getFactoryContext());

				GenericEntity<PublishedIncidentListResource> entity = new GenericEntity<PublishedIncidentListResource>(results) {
					/* do nothing */
				};

				response = Response.ok(entity).tag(results.getUnquotedETag()).build();
			}
				
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		return response;
	}
}