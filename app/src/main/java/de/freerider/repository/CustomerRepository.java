package de.freerider.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import de.freerider.datamodel.Customer;

import java.util.ArrayList;
import java.util.HashMap;


@Component
public class CustomerRepository implements CrudRepository<Customer, Long> {

    public static HashMap<Long, Customer> savedCustomers = new HashMap<Long, Customer>();

    @Override
    /**
	 * Saves a given entity. Use the returned instance for further operations as
     *  the save operation might have changed the
	 * entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
	 */
    public <S extends de.freerider.datamodel.Customer> S save(S entity) {
        if(entity.getId() >= 0 && entity != null){
            savedCustomers.put(entity.getId(), entity);
            return entity;
        }else{
            if(entity == null){
                throw new IllegalArgumentException("Customer can't be null!");
            }
            return entity;
            
        }
        
    }

    /**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null} nor must it contain {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
	 *         as the {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
	 *           {@literal null}.
	 */
    @Override
    public <S extends de.freerider.datamodel.Customer> Iterable<S> saveAll(Iterable<S> entities) {
        for(S entity : entities){
            if(entity.getId() >= 0 && entity != null){
                savedCustomers.put(entity.getId(), entity);
            }else{
                if(entity == null){
                    throw new IllegalArgumentException("Customer can't be null!");
                }else{
                    System.err.println("Customer ID >= 0 \n Customer ID: " + entity.getId());
                }
            }
        }
        return entities;
    }

    /**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param customer must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
    @Override
    public boolean existsById(Long id) {
        if(id != null){
            if(savedCustomers.get(id) != null){
                return true;
            }else{
                return false;
            }
        }
        throw new IllegalArgumentException("Customer ID can't be null!");
    }

    /**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
    @Override
    public Optional<de.freerider.datamodel.Customer> findById(Long id) {
        
        if(id != null){
            Optional<Customer> re = Optional.ofNullable(savedCustomers.get(id));
            return re;
        } throw new IllegalArgumentException("Customer ID can't be null!");   
    }

    /**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
    @Override
    public Iterable<de.freerider.datamodel.Customer> findAll() {
        ArrayList<Customer> allEntities = new ArrayList<Customer>();
        for(Long id : savedCustomers.keySet()){
            allEntities.add(savedCustomers.get(id));
        } 
        return allEntities;
    }
    /**
	 * Returns all instances of the type {@code T} with the given IDs.
	 * <p>
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * <p>
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be {@literal null} nor contain any {@literal null} values.
	 * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
	 *         {@literal ids}.
	 * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
	 */
    @Override
    public Iterable<de.freerider.datamodel.Customer> findAllById(Iterable<Long> ids) {
        ArrayList<Customer> allEntities = new ArrayList<Customer>();
        if(ids != null){
            for(Long id : ids){
                if(id >= 0 && id != null){
                allEntities.add(savedCustomers.get(id));
                }else{
                    throw new IllegalArgumentException("Customer ID can't be null!");
                }
            } 
        }
        return allEntities;
    }
    /**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
    @Override
    public long count() {
        return savedCustomers.size();
    }
    /**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
	 */
    @Override
    public void deleteById(Long id) {
        if(id != null && id >= 0){
            savedCustomers.remove(id);
        }else{
            throw new IllegalArgumentException("Customer ID can't be null!");
        }      
    }
    /**
	 * Deletes a given entity.
	 *
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
    @Override
    public void delete(de.freerider.datamodel.Customer entity) {
        if(entity != null){
            savedCustomers.remove(entity.getId());
        }        
    }
    /**
	 * Deletes all instances of the type {@code T} with the given IDs.
	 *
	 * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
	 * @since 2.5
	 */
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        if(ids != null){
            for(Long id : ids){
                if(id >= 0 && id != null){
                savedCustomers.remove(id);
                }else{
                    throw new IllegalArgumentException("Customer ID can't be null!");
                }
            } 
        }else{
            throw new IllegalArgumentException("Customer ID can't be null!");
        }  
    }
    /**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
	 */
    @Override
    public void deleteAll(Iterable<? extends de.freerider.datamodel.Customer> entities) {
        if(entities != null){
            for(Customer entity : entities){
                if(entity != null){
                savedCustomers.remove(entity.getId());
                }else{
                    throw new IllegalArgumentException("Customer can't be null!");
                }
            }
        }throw new IllegalArgumentException("Iterable can't be null!");
    }

    @Override
    public void deleteAll() {
        savedCustomers.clear();
        
    }

    
}
