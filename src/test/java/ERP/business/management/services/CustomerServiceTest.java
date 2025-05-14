package ERP.business.management.services;

import ERP.business.management.dto.CustomerDTO;
import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import ERP.business.management.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        customer = Customer.builder()
                .id(customerId)
                .name("customer1")
                .email("customer1@test.com")
                .phone("123456789")
                .address("address 1")
                .customerType(CustomerType.CPF)
                .build();

        customerDTO = CustomerDTO.builder()
                .id(customerId)
                .name("customer1")
                .email("customer1@test.com")
                .phone("123456789")
                .address("address 1")
                .customerType(CustomerType.CPF)
                .build();
    }

    @Test
    void findAll_ShouldReturnAllCustomers() {

        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDTO> result = customerService.findAll();

        assertEquals(1, result.size());
        assertEquals(customerDTO.getId(), result.get(0).getId());
        assertEquals(customerDTO.getName(), result.get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findById_WithExistingId_ShouldReturnCustomer() {

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Optional<CustomerDTO> result = customerService.findById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customerDTO.getId(), result.get().getId());
        assertEquals(customerDTO.getName(), result.get().getName());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {

        UUID nonExistingId = UUID.randomUUID();
        when(customerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Optional<CustomerDTO> result = customerService.findById(nonExistingId);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void findByCustomerType_ShouldReturnCustomersOfType() {

        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByCustomerType(CustomerType.CPF)).thenReturn(customers);

        List<CustomerDTO> result = customerService.findByCustomerType(CustomerType.CPF);

        assertEquals(1, result.size());
        assertEquals(CustomerType.CPF, result.get(0).getCustomerType());
        verify(customerRepository, times(1)).findByCustomerType(CustomerType.CPF);
    }

    @Test
    void create_ShouldSaveAndReturnCustomer() {

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.create(customerDTO);

        assertNotNull(result);
        assertEquals(customerDTO.getName(), result.getName());
        assertEquals(customerDTO.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void update_WithExistingId_ShouldUpdateAndReturnCustomer() {

        CustomerDTO updatedDTO = CustomerDTO.builder()
                .id(customerId)
                .name("customer1")
                .email("customer1@test.com")
                .phone("987654321")
                .address("address 1")
                .customerType(CustomerType.CNPJ)
                .build();

        Customer updatedCustomer = Customer.builder()
                .id(customerId)
                .name("customer1Up")
                .email("customer1up@test.com")
                .phone("987654321")
                .address("address 1")
                .customerType(CustomerType.CNPJ)
                .build();

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Optional<CustomerDTO> result = customerService.update(customerId, updatedDTO);

        assertTrue(result.isPresent());
        assertEquals("customer1Up", result.get().getName());
        assertEquals("customer1up@test.com", result.get().getEmail());
        assertEquals(CustomerType.CNPJ, result.get().getCustomerType());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void update_WithNonExistingId_ShouldReturnEmpty() {

        UUID nonExistingId = UUID.randomUUID();
        when(customerRepository.existsById(nonExistingId)).thenReturn(false);

        Optional<CustomerDTO> result = customerService.update(nonExistingId, customerDTO);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).existsById(nonExistingId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void delete_WithExistingId_ShouldReturnTrue() {

        when(customerRepository.existsById(customerId)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(customerId);

        boolean result = customerService.delete(customerId);

        assertTrue(result);
        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void delete_WithNonExistingId_ShouldReturnFalse() {

        UUID nonExistingId = UUID.randomUUID();
        when(customerRepository.existsById(nonExistingId)).thenReturn(false);

        boolean result = customerService.delete(nonExistingId);

        assertFalse(result);
        verify(customerRepository, times(1)).existsById(nonExistingId);
        verify(customerRepository, never()).deleteById(any(UUID.class));
    }
}