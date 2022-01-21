package de.freerider.restapi.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.freerider.datamodel.Customer;
import de.freerider.repository.CustomerRepository;
import de.freerider.restapi.dto.CustomerDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
class CustomersDTOController implements CustomersDTOAPI{

    @Autowired
    private ApplicationContext context;

	
	private final HttpServletRequest request;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public CustomersDTOController (HttpServletRequest request) {
		this.request = request;
	}

    /**
     * serves all customers as List of DTOs
     */
    @Override
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        System.err.println(request.getMethod() + " " +request.getRequestURI());
        List<CustomerDTO> li = new ArrayList<>();
        for(Customer c : customerRepository.findAll()){
            li.add(new CustomerDTO(c));
        }
        return new ResponseEntity<List<CustomerDTO>>(li, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CustomerDTO> getCustomer(long id) {
        System.err.println(request.getMethod() + " " +request.getRequestURI());
        ResponseEntity<CustomerDTO> re = null;
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty())
        re = new ResponseEntity<CustomerDTO>(HttpStatus.NOT_FOUND);
        else{
            Customer c = customer.get();
            CustomerDTO custDTO = new CustomerDTO(c);
            re = new ResponseEntity<CustomerDTO>(custDTO, HttpStatus.OK);
        }
        return re;
    }

    @Override
    public ResponseEntity<List<CustomerDTO>> postCustomers(List<CustomerDTO> dtos) {
        System.err.println(request.getMethod() + " " +request.getRequestURI());

        if(dtos == null)
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        for(CustomerDTO dto : dtos){ 
            Optional<Customer> c = dto.create();
            if(c.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }else{
                Customer customer = c.get();

                if(customerRepository.existsById(customer.getId())){
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }else{
                    customerRepository.save(customer);
                }    
            }
        }
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /** 
 * PUT /customers 
*/
@Override 
public ResponseEntity<List<CustomerDTO>> putCustomers(@RequestBody List<CustomerDTO> dtos ) { 
// TODO: replace by logger 
  System.err.println( request.getMethod() + " " + request.getRequestURI() ); 
  // 
  dtos.stream().forEach( dto -> { 

    dto.print(); 
    Optional<Customer> customerOpt = dto.create(); 
    CustomerDTO.print( customerOpt ); 
  }); 
  return new ResponseEntity<>( null, HttpStatus.ACCEPTED ); 
}

/**
    @Override
    public ResponseEntity<List<CustomerDTO>> putCustomers(List<CustomerDTO> dtos) {
        System.err.println(request.getMethod() + " " +request.getRequestURI());
        ArrayList<CustomerDTO> notFoundLi = new ArrayList<>();
        for(CustomerDTO dto : dtos){
            dto.print(); 
            System.err.println("put customers printed");

            Optional<Customer> optC = dto.create();
            if(!optC.isEmpty()){
                if(customerRepository.findById(optC.get().getId()).isEmpty()){
                    notFoundLi.add(dto);
                    System.err.println( dto.toString()+ "\n" + request.getMethod() + " " +request.getRequestURI()+"\n"+" --- Not Found in Repository -----");

                }else{
                    Customer c = customerRepository.findById(optC.get().getId()).get();
                    customerRepository.delete(c);
                    customerRepository.save(optC.get());
                }
            }else{
                notFoundLi.add(dto);
            }
        }
        if(notFoundLi.size()==0){
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>(notFoundLi, HttpStatus.NOT_FOUND);
        }
    }
    */ 

    @Override
    public ResponseEntity<?> deleteCustomer(long id) {
        System.err.println(request.getMethod() + " " +request.getRequestURI() + "ID: " + id);
        if(customerRepository.existsById(id)){
        customerRepository.deleteById(id);
        return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
    }
    
    
}