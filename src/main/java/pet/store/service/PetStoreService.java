package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.park.controller.model.PetStoreData;
import pet.park.controller.model.PetStoreData.PetStoreCustomer;
import pet.park.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;

	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;


	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
		if (petStoreId == null) {
			petStore = findOrCreatePetStore(petStoreId);
		} else {
			petStore = findPetStoreById(petStoreId);
			if (petStore == null) {
				throw new NoSuchElementException("Pet store is not found with ID=" + petStoreId);
			}
		}
		copyPetStoreFields(petStore, petStoreData);
		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);

	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		// dont add customer & employee **
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
	}

	@Transactional(readOnly = true)
	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId).orElse(null);
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		if (Objects.isNull(petStoreId)) {
			// if causing problems to return
			petStore = new PetStore();
			// return new PetStore();
		} else {
			// IF CAUSES PROBLEMS replace petStore = return
			petStore = findPetStoreById(petStoreId);
		}
		return petStore;
	}
	
//EMPLOYEE
	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		
		copyEmployeeFields (employee, petStoreEmployee);
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		Employee dbEmployee = employeeDao.save(employee);
		
		return new PetStoreEmployee(dbEmployee);

		}
		
	//	Employee dbEmployee = employeeDao.save(employee);
	//	return new PetStoreEmployee(dbEmployee);
	
	
	//petStoreEmployee => employee
		private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
			employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
			employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
			employee.setEmployeeId(petStoreEmployee.getEmployeeId());
			employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
			employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		}
		
	@Transactional(readOnly = true)
	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
	//	return employeeDao.findById(employeeId).orElse(null);
		Employee employee = employeeDao.findById(employeeId).orElseThrow(()-> 
		new NoSuchElementException("Employee with ID=" + employeeId + " was not found."));
		
		if(employee.getPetStore().getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException("Employee with ID=" + employeeId + 
					" is not an employee at this pet store with ID=" + petStoreId);
		}
		return employee;
}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		if(Objects.isNull(employeeId)) {
			return new Employee();
		}
		return findEmployeeById(petStoreId, employeeId);
	//	Employee employee;
	//	} else {
	//		employee = findEmployeeById(employeeId);
	//	}
	//	return employee;
}



//CUSTOMER
@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
	PetStore petStore = findPetStoreById(petStoreId);
	Long customerId = petStoreCustomer.getCustomerId();
	Customer customer = findOrCreateCustomer(petStoreId, customerId);
	
	copyCustomerFields(customer, petStoreCustomer);
	
	customer.getPetStores().add(petStore);
	petStore.getCustomers().add(customer);
	
	Customer dbCustomer = customerDao.save(customer);
	return new PetStoreCustomer(dbCustomer);
	
}

private Customer findCustomerById(Long petStoreId, Long customerId) {
	Customer customer = customerDao.findById(customerId).orElseThrow(() 
			-> new NoSuchElementException("Customer with ID=" + customerId + " was not found"));
	
	boolean found = false;
	
	for(PetStore petStore : customer.getPetStores()) {
		//if(petStore.getPetStoreId() != petStoreId) {
		if(petStore.getPetStoreId() == petStoreId) {
			found = true;
			break;
		}
		
		}
	if(!found) {
		throw new IllegalArgumentException("Customer with ID=" + 
				customerId + " is not a customer at this pet store with ID=" + petStoreId );
	}
	return customer;
	}

	
	//return customerDao.findById(customerId).orElse(null);
	

private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {	
	if(Objects.isNull(customerId)) {
		return new Customer();
	} 
	return findCustomerById(petStoreId, customerId);
}
	//	Long customerId = petStoreCustomer.getCustomerId();
	//	Customer customer = findOrCreateCustomer(petStoreCustomer.getCustomerId(), customerId);
	//	if(customerId == null)	{
	//		customer = findOrCreateCustomer(petStoreId, customerId);
	//	} else {
	//		customer = findCustomerById(customerId);
	//		throw new NoSuchElementException("Customer is not found with ID=" + customerId);
	///	} copyCustomerFields (customer, petStoreCustomer);
	//	Customer dbCustomer = customerDao.save(customer);
	//return new PetStoreCustomer(dbCustomer);
	//}

private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
	customer.setCustomerId(petStoreCustomer.getCustomerId());
	customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
	customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
	customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
}




	

@Transactional (readOnly = true)
public List<PetStoreData> retrieveAllPetStores() {
	List<PetStore> petStores = petStoreDao.findAll();
	
	List<PetStoreData> result = new LinkedList<>();
		for (PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			
			psd.getPetStoreCustomers().clear();
			psd.getPetStoreEmployee().clear();
			
			result.add(psd);
		}
		return result;
}

@Transactional(readOnly = true)
public PetStoreData retrievePetStoreById(Long petStoreId) {
	return new PetStoreData(findPetStoreById(petStoreId));
}

public void deletePetStoreById(Long petStoreId) {
	PetStore petStore = findPetStoreById(petStoreId);
	petStoreDao.delete(petStore);
	
}





}
