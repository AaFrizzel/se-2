package de.freerider.restapi.dto;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import de.freerider.restapi.dto.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;


@RequestMapping( "${app.api.endpoints.customers}" )

public interface CustomersDTOAPI {


    /**
     * 2.) Erstellen Sie im Paket restapi neu: 
        Das Interface CustomersDTOAPI.java mit den GET, POST, PUT und DELETE Methoden des 
        Resource Sets /customers nach dem Bsp. CustomersAPI.java jetzt mit DTO‐Klasse:  
     */

     /*
	 * Swagger API doc annotations:
	 */
	@Operation(
		summary = "Return all customers from repository.",		// appears as text in API short-list
		description = "Return all customers from repository.",	// appears as text inside API
		tags={ "customers-dto-controller" }	// appears in swagger-ui URL: http://localhost:8080/swagger-ui/index.html#/customers-controller
		)
	@ApiResponses( value = {	// also auto-derived by Swagger
		@ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
		@ApiResponse( responseCode = "401", description = "Unauthorized"),
		@ApiResponse( responseCode = "403", description = "Forbidden"),
		// to remove "404" from docs, set SwaggerConfig::Docket.useDefaultResponseMessages(true) // ->false
		@ApiResponse( responseCode = "404", description = "Not Found")
	})

	/*
	 * Spring REST Controller annotation:
	 */
	@RequestMapping(
		method = RequestMethod.GET,
		value = "",	// relative to interface @RequestMapping
		produces = { "application/json" }
	)
    

    /**
     * 
     * @return list of Customers DTOs
     */
    ResponseEntity<List<CustomerDTO>> getCustomers();

    /*
	 * Swagger API doc annotations:
	 */
	@Operation(
		summary = "Return customer with {id} from repository.",
		description = "Return customer with {id} from repository.",
		tags={ "customers-dto-controller" }
		)

	/*
	 * Spring REST Controller annotation:
	 */
	@RequestMapping(
		method=RequestMethod.GET,
		value="{id}",	// relative to interface @RequestMapping
		produces={ "application/json" }
	)
    
    
    /**
     * 
     * @param id 
     * @return singel Customer DTO
     */
    ResponseEntity<CustomerDTO> getCustomer( @PathVariable("id") long id ); 




    /*
	 * Swagger API doc annotations:
	 */
	@Operation(
		summary = "Add new customers to repository.",
		description = "Add new customers to repository.",
		tags={ "customers-dto-controller" }
		)

	/*
	 * Spring REST Controller annotation:
	 */
	@RequestMapping(
		method = RequestMethod.POST,
		value = ""	// relative to interface @RequestMapping
	)
    /**
     * 
     * @param dtos
     * @return
     */
    ResponseEntity<List<CustomerDTO>> postCustomers( @RequestBody List<CustomerDTO> dtos ); 
    
    

    /*
	 * Swagger API doc annotations:
	 */
	@Operation(
		summary = "Update existing customers in repository.",
		description = "Update existing customers in repository.",
		tags={ "customers-dto-controller" }
		)

	/*
	 * Spring REST Controller annotation:
	 */
	@RequestMapping(
		method = RequestMethod.PUT,
		value = ""	// relative to interface @RequestMapping
	)
    
    /**
     * 
     * @param dtos
     * @return
     */
    ResponseEntity<List<CustomerDTO>> putCustomers( @RequestBody List<CustomerDTO> dtos ); 
    
    

    /*
	 * Swagger API doc annotations:
	 */
	@Operation(
		summary = "Delete customer by its id from repository.",
		description = "Delete customer by its id from repository.",
		tags={ "customers-dto-controller" }
		)

	/*
	 * Spring REST Controller annotation:
	 */
	@RequestMapping(
		method = RequestMethod.DELETE,
		value = "{id}"	// relative to interface @RequestMapping
	)
    
    /**
     * 
     * @param id
     * @return
     */
    ResponseEntity<?> deleteCustomer( @PathVariable("id") long id ); 
}
