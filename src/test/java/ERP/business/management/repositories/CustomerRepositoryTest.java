package ERP.business.management.repositories;

import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void shouldSavedCustomer() {
        Customer customer = Customer.builder()
                .name("CustomerTest")
                .email("test123@test.com")
                .phone("921111111110")
                .address("test address, 111")
                .customerType(CustomerType.CPF)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertEquals("CustomerTest", savedCustomer.getName());
        assertEquals("test123@test.com", savedCustomer.getEmail());
        assertEquals(CustomerType.CPF, savedCustomer.getCustomerType());
    }

    @Test
    public void shouldFindCustomerById() {

        Customer customer = Customer.builder()
                .name("CustomerFind")
                .email("findcustomertest123@test.com")
                .phone("921111111110")
                .address("find address test, 111")
                .customerType(CustomerType.CPF)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals(CustomerType.CPF, savedCustomer.getCustomerType());
    }

    @Test
    public void shouldReturnEmptyWhenCustomerNotFound() {

        Optional<Customer> foundCustomer = customerRepository.findById(UUID.randomUUID());

        assertTrue(foundCustomer.isPresent());
    }

    @Test
    public void shouldFindCustomersByType() {

        Customer cpfCustomer1 = Customer.builder()
                .name("customer1")
                .email("customer1@test.com")
                .phone("92000000001")
                .address("address 1")
                .customerType(CustomerType.CPF)
                .build();

        Customer cpfCustomer2 = Customer.builder()
                .name("customer2")
                .email("customer2@test.com")
                .phone("92000000002")
                .address("address 2")
                .customerType(CustomerType.CPF)
                .build();

        Customer cnpjCustomer3 = Customer.builder()
                .name("customer3")
                .email("customer3@test.com")
                .phone("92000000003")
                .address("address 3")
                .customerType(CustomerType.CNPJ)
                .build();

        customerRepository.save(cpfCustomer1);
        customerRepository.save(cpfCustomer2);
        customerRepository.save(cnpjCustomer3);

        List<Customer> cpfCustomers = customerRepository.findByCustomerType(CustomerType.CPF);
        List<Customer> cnpjCustomers = customerRepository.findByCustomerType(CustomerType.CNPJ);

        assertEquals(2, cpfCustomers.size());
        assertEquals(1, cnpjCustomers.size());

        assertTrue(cpfCustomers.stream().allMatch(customer -> customer.getCustomerType() == CustomerType.CPF));
        assertTrue(cnpjCustomers.stream().allMatch(customer -> customer.getCustomerType() == CustomerType.CNPJ));
    }

    @Test
    public void shouldDeleteCustomer() {

        Customer customer = Customer.builder()
                .name("byebye customer")
                .email("byebye123@teste.com")
                .phone("92111111115")
                .address("bye address, 999")
                .customerType(CustomerType.CPF)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        UUID customerId = savedCustomer.getId();

        customerRepository.deleteById(customerId);

        Optional<Customer> deletedCustomer = customerRepository.findById(customerId);

        assertTrue(deletedCustomer.isEmpty());
    }
}