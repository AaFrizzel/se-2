package de.freerider.restapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import org.springframework.http.ResponseEntity;

import de.freerider.datamodel.Customer;
import de.freerider.repository.CustomerRepository;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.core.type.TypeReference;


//@RestController 
class CustomersController implements CustomersAPI {

    @Autowired
    private ApplicationContext context;
	//
	@Autowired
	private final ObjectMapper objectMapper;
	//
	@Autowired
	private final HttpServletRequest request;
	//	
	@Autowired
    private CustomerRepository customerRepository;

	private Long newId = null;


	/**
	 * Constructor.
	 * 
	 * @param objectMapper entry point to JSON tree for the Jackson library
	 * @param request HTTP request object
	 */
    private CustomersController( ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}
    /**
	 * GET /people
	 * 
	 * Return JSON Array of people (compact).
	 * 
	 * @return JSON Array of people
	 */
    @Override
    public ResponseEntity<List<?>> getCustomers() {
        ResponseEntity<List<?>> re = null;
        System.err.println( request.getMethod() + " " + request.getRequestURI() );   
        try {
			ArrayNode arrayNode = peopleAsJSON();
			ObjectReader reader = objectMapper.readerFor( new TypeReference<List<ObjectNode>>() { } );
			List<String> list = reader.readValue( arrayNode );
			//
			re = new ResponseEntity<List<?>>( list, HttpStatus.OK );

		} catch( IOException e ) {
			re = new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
        return re;
    }
    /**
	 * GET /people/pretty
	 * 
	 * Return JSON Array of people (pretty printed with indentation).
	 * 
	 * @return JSON Array of people
	 */
    @Override
    public ResponseEntity<?> getCustomer(long id) {
        ResponseEntity<String> re = null;
		System.err.println( request.getMethod() + " " + request.getRequestURI() );   
		try {
			ArrayNode arrayNode = peopleAsJSON();
			
			if(arrayNode.get((int) id-1) != null) {
			String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( arrayNode.get((int) id -1) );
			re = new ResponseEntity<String>( pretty, HttpStatus.OK );
            }else {
				re = new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}
			return re;
		}catch( IOException e ) {
			re = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * 
	 * @param kvpairs Map of Strings, with Customer attributes
	 * @return Container with Customer or null.
	 * 
	 * Handle Multiple Customer Objects
	 * 	for(int i =1; i <= kvpairs.keySet().size(); i++){
	 * 		if(!(i%4 == 0)){
	 * 			
	 * über keyset iterieren und customer parameter zuweisen...
	 * 		}
	 * }
	 */
	private Optional<Customer> accept(Map<String, Object> kvpairs ){
		String[] a = {"id", "name", "first", "contacts"};
		Long idx = null;
		//check if id = null
		if(kvpairs.get(a[0]) != null)
		idx =  Long.valueOf((Integer) kvpairs.get(a[0]));
	
		String name = (String) kvpairs.get(a[1]);
		String first = (String) kvpairs.get(a[2]);
		String contacts = (String) kvpairs.get(a[3]);
		Customer customer = new Customer();
		//check if essential parameters are empty or null
		if(name != null && name != "" && first != null && first != ""){
			//set id, if id is provided, create new Id if not
			if(idx != null && idx > 0){
				Long id =idx;
				customer.setId(id).setName(first, name);	
			}else{
				//check if current newId has been used before & set correct ID
				if(newId == null){
				newId = Collections.max(((Map<Long, Customer>) customerRepository.findAll()).keySet());
				customer.setId(newId+2).setName(name, first);
				newId= newId+2;
				}else{
					newId = newId+1;
					customer.setId(newId).setName(name, first);

				}
				}
				//digest contacts and add 
			if(contacts != null && contacts != ""){
				String[] contactLi= contacts.split(";");
				for(String contact : contactLi){
				customer.addContact(contact);
				}
			}
		}else{
			System.err.println("At least one essential Argument was missing to produce a Customer Object\n" + "ID: " + idx + "name: " + name + ", first: " + first);	
			customer = null; 
			}	
		return Optional.ofNullable(customer);
	}
	 
	

    private ArrayNode peopleAsJSON() {
		
		ArrayNode arrayNode = objectMapper.createArrayNode();
		
		((Map<Long, Customer>) customerRepository.findAll()).forEach((k, c) -> {
			StringBuffer sb = new StringBuffer();
			c.contacts.forEach( contact -> sb.append( sb.length()==0? "" : "; " ).append( contact ) );
			arrayNode.add(
				objectMapper.createObjectNode()
					.put( "name", c.getLastName() )
					.put( "first", c.getFirstName() )
					.put( "contacts", sb.toString() )
			);
		});
		return arrayNode;
	}

	private ArrayNode customerAsJSON(Customer customer) {
		ArrayNode arrayNode = objectMapper.createArrayNode();
        StringBuffer sb = new StringBuffer();
        customer.getContacts().forEach(contact -> sb.append(sb.length() == 0 ? "" : "; " ).append(contact));
		arrayNode.add(
            objectMapper.createObjectNode()
                .put("id", customer.getId())
                .put("name", customer.getLastName())
                .put("first",customer.getFirstName())                    
				.put("contacts", sb.toString())
            );
            return arrayNode;
    }
	
	/**
	 * @para each JsonMapData contains Data for one potential Customer
	 * @para optCustLi: all accepted optional Customer Objects
	 * 
	 */
	@Override
	public ResponseEntity<List<?>> postCustomers(Map<String, Object>[] jsonMap) {
		System.err.println( "POST /customers" );
		if( jsonMap == null ){
		System.err.println("Status 400 (bad request) - empty JSON map");

		    return new ResponseEntity<>( null, HttpStatus.BAD_REQUEST );
		}
		//
		Map<String, Object> jsonMapData = new HashMap<>();
		ArrayList<Optional<Customer>> optCustLi = new ArrayList<>();
		
		
		System.out.println( "[{" );
		for( Map<String, Object> kvpairs : jsonMap ) {
		    kvpairs.keySet().forEach( key -> {
		    Object value = kvpairs.get( key );
		    System.out.println( "  [ " + key + ", " + value + " ]" );
		    jsonMapData.put(key, value);	
		    });

			Optional<Customer> customer = accept(jsonMapData);
			optCustLi.add(customer);
		}
		System.out.println( "}]" );

		//process all opt. Customer Objects
		for(Optional<Customer> c : optCustLi){
		if(c.isEmpty()){
			System.err.println("Status 400 (bad request) - empty customer object");

			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}else{
			Customer custObj = c.get();
			//check if ID already exists
			if(customerRepository.existsById(custObj.getId())) {
				try {
					ArrayNode arrayNode = customerAsJSON(custObj);
					ObjectReader reader = objectMapper.readerFor(new TypeReference<List<ObjectNode>>() {});
					List<String> list = reader.readValue(arrayNode);
					System.err.println("Status 409 (conflict)");

					return new ResponseEntity<List<?>>( list, HttpStatus.CONFLICT ); // status 409
				} catch(Exception e) {
					e.printStackTrace();
					}
				}
				else {
					customerRepository.save(custObj);
				}
			}
		}	
			
			//
		return new ResponseEntity<>( null, HttpStatus.CREATED ); 
		
	}
	
	@Override
	public ResponseEntity<List<?>> putCustomers(Map<String, Object>[] jsonMap) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResponseEntity<?> deleteCustomer(long id) {
		System.err.println( "DELETE /customers/" + id ); 
		if(id >= 0 && customerRepository.existsById(id)){
			customerRepository.deleteById(id);
			System.err.println("Status 202 (accepted)");

			return new ResponseEntity<>( null, HttpStatus.ACCEPTED );  // status 202 
		}else{
			System.err.println("Status 404 (not found)");
			return new ResponseEntity<>( null, HttpStatus.NOT_FOUND );  // status 202
			
		}
	}
	
	

} 


